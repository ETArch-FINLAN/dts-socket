package br.ufu.facom.network.dlontology;

import java.nio.ByteBuffer;
import java.util.Arrays;

import br.ufu.facom.network.dlontology.util.Util;
import br.ufu.mehar.dts.Dts.ControlRequest.RequestType;
import br.ufu.mehar.dts.Dts.ControlResponse;

import com.google.protobuf.GeneratedMessage;

public class DTSSocket extends FinSocket {
	
	public static final byte[] ADDR = "DTS\0\0\0".getBytes();
	public static final short ETHERTYPE = 0x0880;

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
		
		byte fullMsg[] = null;
		boolean fragmented = false;
		byte[] recievedAddr = new byte[6];
		byte[] data = new byte[DTSSocket.MAX_FRAME_SIZE];
		
		do {
			
			data = read();
			recievedAddr = Arrays.copyOfRange(data, 6, 12);
			if(Arrays.equals(addr, recievedAddr)) {
				
				fragmented = data[14] == (byte)1;
				
				if(fullMsg == null) {//Se é o primeiro pacote 
					fullMsg = Arrays.copyOfRange(data, 15, data.length);
				} else {
					byte[] payload1 = fullMsg;
					byte[] payload2 = Arrays.copyOfRange(data, 15, data.length);
					byte[] payloadMerged = new byte[payload1.length + payload2.length];
					
					System.arraycopy(payload1, 0, payloadMerged, 0, payload1.length);
					System.arraycopy(payload2, 0, payloadMerged, payload1.length, payload2.length);
					
					fullMsg = payloadMerged;
				}
			}
			
		} while(fragmented == true || !Arrays.equals(addr, recievedAddr));
		
		return fullMsg;
		
	}
	
	protected void send(byte addr[], byte msg[]) {
		
		int headerSize = addr.length + (Short.SIZE/8) + (Byte.SIZE/8);//Address + ethertype + flag isFragmented
		int maxMsgSize = DTSSocket.MAX_FRAME_SIZE - headerSize;
		int offset = 0;
		boolean done = false;
		
		byte curretMessage[];
		ByteBuffer bbuffer;
		
		while(!done) {
		
			if( headerSize + (msg.length - offset) > DTSSocket.MAX_FRAME_SIZE ) {//Se não cabe em um único frame
				
				curretMessage = Arrays.copyOfRange(msg, offset, offset + maxMsgSize);
				
				bbuffer = ByteBuffer.allocate(DTSSocket.MAX_FRAME_SIZE);
				bbuffer.put(addr)
					.putShort(DTSSocket.ETHERTYPE)
					.put((byte) 1)
					.put(curretMessage);
				
				offset += maxMsgSize;
				
			} else {//Se cabe
				
				curretMessage = Arrays.copyOfRange(msg, offset, msg.length);
				
				bbuffer = ByteBuffer.allocate(headerSize + curretMessage.length);
				bbuffer.put(addr)
					.putShort(DTSSocket.ETHERTYPE)
					.put((byte) 0)
					.put(curretMessage);
				
				done = true;
			}
			
			write(bbuffer.array());
		
		}
		
	}
	

	private long getNextId() {
		return ++id;
	}
	
	
	
}
