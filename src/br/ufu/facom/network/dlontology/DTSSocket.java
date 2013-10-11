package br.ufu.facom.network.dlontology;

import java.nio.ByteBuffer;
import java.util.Arrays;

import br.ufu.facom.network.dlontology.util.Util;
import br.ufu.mehar.dts.Dts.ControlRequest.RequestType;
import br.ufu.mehar.dts.Dts.ControlResponse;

import com.google.protobuf.GeneratedMessage;

public class DTSSocket extends FinSocket {

	private long id = 0;

	public ControlResponse callDts(GeneratedMessage generatedMessage, RequestType requestType, String interf) throws Exception {

		ControlResponse respObj = null;
		boolean foundResp = false;
		byte[] addrMac = Util.getAddrMacByInterfaceName(interf);

		Request request = new Request(generatedMessage, requestType, getNextId(), addrMac);

		write(request.toByteArray());

		while (!foundResp) {

			byte[] data = read();

			short len, pos;
			ByteBuffer bbuffers = ByteBuffer.wrap(data);
			if (bbuffers.get(6) != 'D' || bbuffers.get(7) != 'T' || bbuffers.get(8) != 'S') {
				System.out.println("non-dts");
				continue;
			}
			pos = 14;
			byte[] msg;
			
			// Control Response
			len = bbuffers.get(pos++);
			len &= 0xff;
			len |= (bbuffers.get(pos++) << 4);
			msg = Arrays.copyOfRange(data, pos, pos + len);
			pos += len;
			respObj = ControlResponse.parseFrom(msg);

			if (respObj.getRequestId() == request.getControlRequest().getId()) {
				foundResp = true;
			}

		}

		return respObj;

	}
	
	protected byte[] recieveFiltre(byte addr[]) {
		
		while(true) {
			
			byte[] data = read();
			byte[] recievedAddr = Arrays.copyOfRange(data, 6, 12);
			
			if(Arrays.equals(addr, recievedAddr)) {
				return Arrays.copyOfRange(data, 14, data.length - 1);
			}
			
		}
		
	}
	
	protected void send(byte addr[], byte[] msg) {
		
		ByteBuffer bbuffer = ByteBuffer.allocate(addr.length + Short.SIZE + msg.length);
		bbuffer.put(addr)
			.putShort(Util.ETHERTYPE)
			.put(msg);
		
		write(bbuffer.array());
	}
	

	private long getNextId() {
		return ++id;
	}
	
	
	
}
