package br.ufu.facom.network.dlontology;

import br.ufu.facom.network.dlontology.exception.DTSException;
import br.ufu.mehar.dts.Dts.ControlResponse;
import br.ufu.mehar.dts.Dts.ControlRequest.RequestType;
import br.ufu.mehar.dts.Dts.ControlResponse.ReturnStatus;
import br.ufu.mehar.dts.Etcp.EntityRegister;
import br.ufu.mehar.dts.Etcp.EntityUnregister;

public class EntitySocket extends DTSSocket {

	private String title;
	private Boolean registered;
	
	public EntitySocket(){
		
	}
	
	public EntitySocket(String interf, String title) {
		this.interf = interf;
		this.title  = title;
	}

	public String getInterf() {
		return interf;
	}

	public void setInterf(String interf) {
		this.interf = interf;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void register() throws Exception {
		br.ufu.mehar.dts.Etcp.EntityRegister.Builder builder = EntityRegister.newBuilder();
		builder.setTitle(title);
		EntityRegister entityRegister = builder.build();
		
		ControlResponse controlResponse = callDts(entityRegister, RequestType.ETCP_ENTITY_REGISTER, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Failed to register entity.");
		} 
		
		registered = true;
	}
	
	public void unregister() throws Exception {
		
		br.ufu.mehar.dts.Etcp.EntityUnregister.Builder builder = EntityUnregister.newBuilder();
		builder.setTitle(title);
		EntityUnregister entityUnregister = builder.build();
		
		ControlResponse controlResponse = callDts(entityUnregister, RequestType.ETCP_ENTITY_UNREGISTER, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Failed to register entity.");
		} 
		
		registered = false;
		
	}
	
}
