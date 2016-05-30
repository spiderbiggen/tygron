package tygronenv.util.html;

/**
 * Class for the exception if an element isn't unique
 *
 * @author Joshua Slik
 *
 */
public class ElementNotUniqueException extends RuntimeException {

	private static final long serialVersionUID = -6041248958884842614L;

	/**
	 * creating a super for the exception that an element isn't unique
	 */
	public ElementNotUniqueException() {
		super();
	}

	/**
	 * Message for a not unique element.
	 *
	 * @param s
	 *            is a string that is shown when an element isn't unique
	 */
	public ElementNotUniqueException(String s) {
		super(s);
	}

}
