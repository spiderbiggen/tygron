package tygronenv.util.html;

import org.junit.Test;

/**
 * @author Joshua Slik
 */
public class HTMLParserTest {

	@Test
	public void test01() {
		HTMLParser htmlParser = new HTMLParser("<p>Hello</p>");
		htmlParser.getItems();
		HTMLTag root = htmlParser.getRoot();
		System.out.println(root.getContent());

	}

}
