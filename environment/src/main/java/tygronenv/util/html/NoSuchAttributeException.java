package tygronenv.util.html;

/**
 * Class for the exception that there's no such attribute.
 *
 * @author Joshua Slik
 */
public class NoSuchAttributeException extends RuntimeException {

	private static final long serialVersionUID = -1054927539376716053L;

	/**
	 * Super for no such attribute exception
	 */
	public NoSuchAttributeException() {
		super();
	}

	/**
	 * Super
	 * @param s is a string that is shown when there's no such attribute
	 */
	public NoSuchAttributeException(String s) {
		super(s);
	}

}
