package br.ufu.facom.network.dlontology;

import br.ufu.facom.network.dlontology.exception.DTSException;
import br.ufu.facom.network.dlontology.util.SHA256;
import br.ufu.facom.network.dlontology.util.Util;
import br.ufu.mehar.dts.Dts.ControlRequest.RequestType;
import br.ufu.mehar.dts.Dts.ControlResponse;
import br.ufu.mehar.dts.Dts.ControlResponse.ReturnStatus;
import br.ufu.mehar.dts.Etcp.WorkspaceAttach;
import br.ufu.mehar.dts.Etcp.WorkspaceCreate;
import br.ufu.mehar.dts.Etcp.WorkspaceDelete;
import br.ufu.mehar.dts.Etcp.WorkspaceDetach;

public class WorkspaceSocket extends DTSSocket {

	private String title;
	private boolean created;
	private EntitySocket attachedEntity;
	private byte[] hashTitle;
	private byte[] addrMac;
	
	public WorkspaceSocket() {
		
	}
	
	public WorkspaceSocket(String interf, String title) {
		this.interf = interf;
		setTitle(title);
		setAddrMac(Util.getAddrMacByInterfaceName(interf));
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
		if(title != null) {
			this.hashTitle = Util.hexStringToByteArray(SHA256.getHash(title).substring(0, 12));
		} else {
			this.hashTitle = null;
		}
	}

	public Boolean getCreated() {
		return created;
	}

	public EntitySocket getAttachedEntity() {
		return attachedEntity;
	}

	public void createOnDts(EntitySocket entity) throws Exception {
		
		br.ufu.mehar.dts.Etcp.WorkspaceCreate.Builder builder = WorkspaceCreate.newBuilder();
		builder.setWorkspaceTitle(title);
		
		if (entity != null) {
			builder.setEntityTitle(entity.getTitle());
			builder.setAttachToo(true);
		}
		
		WorkspaceCreate workspaceCreate = builder.build();
		
		ControlResponse controlResponse = callDts(workspaceCreate, RequestType.ETCP_WORKSPACE_CREATE, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Workspace creation failed.");
		} 
		
		this.created = true;
		
		if (entity != null) {
			this.attachedEntity = entity;
		}
	}

	public void deleteOnDts() throws Exception {
		
		br.ufu.mehar.dts.Etcp.WorkspaceDelete.Builder builder = WorkspaceDelete.newBuilder();
		builder.setTitle(title);
		
		WorkspaceDelete workspaceDelete = builder.build();
		
		ControlResponse controlResponse = callDts(workspaceDelete, RequestType.ETCP_WORKSPACE_DELETE, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Workspace deletion failed.");
		}
		
		this.created = false;
	}

	public void attach(EntitySocket entity) throws Exception {
		
		br.ufu.mehar.dts.Etcp.WorkspaceAttach.Builder builder = WorkspaceAttach.newBuilder();
		builder.setWorkspaceTitle(title);
		builder.setEntityTitle(entity.getTitle());
		
		WorkspaceAttach workspaceAttach = builder.build();
		
		ControlResponse controlResponse = callDts(workspaceAttach, RequestType.ETCP_WORKSPACE_ATTACH, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Failed to detach from workspace.");
		}
		
		this.attachedEntity = entity;

	}
	
	private void setAddrMac(byte[] addrMac) {
		this.addrMac = addrMac;
	}

	public void detach() throws Exception {
		
		br.ufu.mehar.dts.Etcp.WorkspaceDetach.Builder builder = WorkspaceDetach.newBuilder();
		builder.setWorkspaceTitle(title);
		builder.setEntityTitle(attachedEntity.getTitle());
		
		WorkspaceDetach workspaceDetach = builder.build();
		
		ControlResponse controlResponse = callDts(workspaceDetach, RequestType.ETCP_WORKSPACE_DETACH, interf);
		
		if(!controlResponse.getStatus().equals(ReturnStatus.SUCCESS)) {
			throw new DTSException("Failed to detach from workspace.");
		}
		
		this.attachedEntity = null;

	}
	
	public byte[] recieve() {
		return recieveFiltre(hashTitle);
	}
	
	public void send(byte[] msg) {
		byte[] addr = Util.concat(hashTitle, addrMac);
		send(addr, msg);
	}
	
}
