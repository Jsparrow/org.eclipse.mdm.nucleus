package org.eclipse.mdm.connector.boundary;

public class ConnectorServiceException extends RuntimeException {

	private static final long serialVersionUID = 3491930665127242286L;

	public ConnectorServiceException(String message) {
		super(message);
	}

	public ConnectorServiceException(String message, Throwable t) {
		super(message, t);
	}

}
