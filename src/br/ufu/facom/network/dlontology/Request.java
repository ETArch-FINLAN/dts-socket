package br.ufu.facom.network.dlontology;

import java.nio.ByteBuffer;

import br.ufu.mehar.dts.Dts.ControlRequest;
import br.ufu.mehar.dts.Dts.ControlRequest.Builder;
import br.ufu.mehar.dts.Dts.ControlRequest.RequestType;

import com.google.protobuf.GeneratedMessage;

public class Request {

	private ControlRequest controlRequest;
	private GeneratedMessage generatedMessage;
	private byte[] addrMac;
	
	public Request(){
		
	}
	
	public Request(GeneratedMessage generatedMessage, RequestType requestType, long id, byte[] addrMac){
		this.setControlRequest(requestType, id);
		this.setGeneratedMessage(generatedMessage);
		this.setAddrMac(addrMac);
	}

	public ControlRequest getControlRequest() {
		return controlRequest;
	}

	public void setControlRequest(ControlRequest controlRequest) {
		this.controlRequest = controlRequest;
	}
	
	public void setControlRequest(RequestType requestType, long id){
		
		Builder builder = br.ufu.mehar.dts.Dts.ControlRequest.newBuilder();
		builder.setType(requestType);
		builder.setId(id);
		
		this.controlRequest = builder.build();
	}

	public GeneratedMessage getGeneratedMessage() {
		return generatedMessage;
	}

	public void setGeneratedMessage(GeneratedMessage generatedMessage) {
		this.generatedMessage = generatedMessage;
	}
	
	public byte[] getAddrMac() {
		return addrMac;
	}

	public void setAddrMac(byte[] addrMac) {
		this.addrMac = addrMac;
	}

	public byte[] toByteArray(){
		
		byte[] cRequest = controlRequest.toByteArray();
		byte[] messageRequest = generatedMessage.toByteArray();

		ByteBuffer bbuffer = ByteBuffer.allocate(cRequest.length + messageRequest.length + 18);

		byte[] newMessage = bbuffer.put(DTSSocket.ADDR)
				.put(addrMac)
				.putShort(DTSSocket.ETHERTYPE)
				.putShort((short) (((cRequest.length>>8)&0xff) + ((cRequest.length << 8)&0xff00)))
				.put(cRequest)
				.putShort((short) (((messageRequest.length>>8)&0xff) + ((messageRequest.length << 8)&0xff00)))
				.put(messageRequest)
				.array();
		
		return newMessage;
		
	}

}
