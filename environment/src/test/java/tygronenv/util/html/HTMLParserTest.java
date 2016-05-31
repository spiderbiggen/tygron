package tygronenv.util.html;

import org.junit.Test;

/**
 * @author Joshua Slik
 */
public class HTMLParserTest {

	@Test
	public void test01() {
		HTMLParser htmlParser = new HTMLParser("<root><p>Budget overzicht Gemeente</p><table width=\"335\"><tr><td width=\"130\">Inkomen</td><td STYLE=\"text-align: right;\" width=\"140\">� 100.000.000,-</td></tr><tr><td width=\"130\">Kosten</td><td STYLE=\"text-align: right;\" width=\"140\">� 0,-</td><td STYLE=\"text-align: right;\" width=\"70\">-</td></tr></table><table width=\"335\"><tr><td width=\"130\">Winst</td><td STYLE=\"text-align: right;\" width=\"140\">� 100.000.000,-</td></tr><tr><td width=\"130\">Gewenst</td><td STYLE=\"text-align: right;\" width=\"140\">� 100.000,-</td><td STYLE=\"text-align: right;\" width=\"70\">-</td></tr></table><table width=\"335\"><tr><td width=\"130\"></td><td STYLE=\"text-align: right;\" width=\"140\">� 99.900.000,-</td><td STYLE=\"text-align: right;\" width=\"70\"></td></tr></table><p><font color=\"#D0FFFF\">U heeft � 99.900.000,- extra winst gemaakt boven op uw opgave.</font></p><p>Voor de complete begroting klik op de onderstaande knop.</p><p></p><br/><p>Score: 100%</p></root>");
		htmlParser.getItems();
		HTMLTag root = htmlParser.getRoot();
		System.out.println(root.hasMoreThanOneElement("p"));
		System.out.println(root.elements("p"));
		System.out.println(root.getContent("p", 0));

	}

}
