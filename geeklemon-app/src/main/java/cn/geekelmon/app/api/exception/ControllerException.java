package cn.geekelmon.app.api.exception;

public class ControllerException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControllerException(String msg) {
		super(msg);
	}
}
