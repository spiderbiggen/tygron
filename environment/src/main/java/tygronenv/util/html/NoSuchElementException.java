package tygronenv.util.html;

/**
 * @author Joshua Slik
 */
public class NoSuchElementException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3704509663698448420L;

	/**
	 *
	 */
	public NoSuchElementException() {
		super();
	}

	/**
	 *
	 * @param message
	 *            is the exception message to pass through.
	 */
	public NoSuchElementException(String message) {
		super(message);
	}

}
