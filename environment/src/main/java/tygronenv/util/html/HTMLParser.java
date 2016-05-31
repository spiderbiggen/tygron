package tygronenv.util.html;

import java.util.List;

/**
 * @author Joshua Slik
 */
public class HTMLParser {

	private String html;
	private HTMLTag root;

	/**
	 * Constructor
	 * @param html HTML string
	 */
	public HTMLParser(final String html) {
		this.html = html;
	}

	/**
	 * Getter for the items.
	 * @return items in the HTML.
	 */
	public HTMLTag getItems() {
		if(root == null)
			parseHTML();

		return root;
	}

	/**
	 * Parses the HTML given to the constructor.
	 */
	private void parseHTML() {
		root = SAXParser.parseString(html);
	}

	public HTMLTag getRoot() {
		return root;
	}

}
