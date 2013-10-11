package br.ufu.facom.network.dlontology.exception;

public class DTSException extends Exception {

	private static final long serialVersionUID = 1L;

	public DTSException() {
		super();
	}

	public DTSException(String message) {
		super(message);
	}

	public DTSException(String message, Throwable cause) {
		super(message, cause);
	}

	public DTSException(Throwable cause) {
		super(cause);
	}

}
