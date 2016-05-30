package tygronenv.util.html;

import java.util.List;

/**
 * @author Joshua Slik
 */
public class HTMLParser {

	private String html;
	private List items;
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
	public List getItems() {
		if(items == null)
			parseHTML();

		return items;
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
