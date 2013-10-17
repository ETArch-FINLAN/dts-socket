package br.ufu.facom.network.dlontology;
import java.util.Map;

public class FinSocket {
	
	public static final int MAX_FRAME_SIZE = 1500;
	
	//Descritor do socket em uso.
	private int socket = -1;
	//Nome da interface em uso.
	protected String interf = null;
	//Index da interface em uso na lista de interfaces.
	private int ifIndex = -1;
	
	private boolean promisc = false;
	
	//Carrega a biblioteca libFinSocket.so
    static {  
    	System.loadLibrary("FinSocket");  
    }

	//Métodos nativos da bibliotéca em C
	private native int finOpen();
	private native boolean finClose(int sock);
	private native boolean finWrite(int ifIndex, int socket, byte[] data, int offset, int len);
	private native int finRead(int socket, byte[] data, int offset, int len);
	private native boolean setPromiscousMode(String ifName, int sock);
	private native Map<Integer,String> getNetIfs();
	
	public boolean open(){
		socket = finOpen();
		
		Map<Integer,String> ifs = getNetIfs();
		for(Integer index : ifs.keySet()){
			String name = ifs.get(index); 
			
			if(name.equalsIgnoreCase(interf)){
				ifIndex = index;
				debug("Interface utilizada: " + ifIndex + " - " + interf);
				break;
			}
			
		}
		
		return isOpenned();
	}
	
	/**
	 * Fecha o socket aberto.
	 * @return
	 */
	public boolean close(){
		
		if(finClose(socket)) {
			ifIndex = -1;
			interf = null;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se existe um socket aberto.
	 * @return
	 */
	public boolean isOpenned() {
		return socket >= 0 && ifIndex >=0;  
	}
	
	/**
	 * Printa a mensagem de debug.
	 * @param info
	 */
	private void debug(String info) {
		System.out.println(info);
	}
	
	public void write(byte[] msg) {
		finWrite(ifIndex, socket, msg, 0, msg.length);
	}
	
	public byte[] read() {
		byte bytes[] = null;
		if (isOpenned()) {
			if (!promisc) {
				if (!setPromiscousMode(interf, socket)) {
					System.err.println("Não foi possível colocar a interface em modo promíscuo.");
					return null;
				} else {
					promisc = true;
				}
			}

			bytes = new byte[FinSocket.MAX_FRAME_SIZE];// TODO hard code
			finRead(socket, bytes, 0, FinSocket.MAX_FRAME_SIZE);// TODO hard code
		} else {
			throw new RuntimeException("FinSocket não aberto!");
		}

		return bytes;
	}

} 
