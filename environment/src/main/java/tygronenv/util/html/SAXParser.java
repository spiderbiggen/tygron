package tygronenv.util.html;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Joshua Slik
 */
public class SAXParser extends DefaultHandler {

	private HTMLTag root = null;
	private ArrayList<HTMLTag> tagList = new ArrayList<>();
	private boolean inElement = false;

	/**
	 * parsing a local xml root as a string
	 *
	 * @param htmlString
	 *            is a string with the xml in it
	 * @return the xml root
	 */
	public static HTMLTag parseString(final String htmlString) {
		XMLReader xr = null;
		try {
			xr = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		SAXParser handler = new SAXParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		InputStream is = new ByteArrayInputStream(
				htmlString.getBytes(StandardCharsets.UTF_8));
		try {
			xr.parse(new InputSource(is));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (SAXException e) {
			System.err.println("Something went wrong parsing your string.");
			System.err.println(e.getMessage());
		}
		return handler.getHTMLTag();
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////

	public void startDocument() {
		// System.out.println("Start document");
	}

	public void endDocument() {
		// System.out.println("End document");
		this.root = tagList.get(0);
	}

	public void startElement(final String uri, final String name, final String qName,
							 final Attributes atts) {
		// Default behaviour
		// if ("".equals(uri)) {
		// System.out.println("Start element: " + qName);
		// for (int i = 0; i < atts.getLength(); i++) {
		// System.out.println("   Attribute : " + atts.getQName(i) + "=\""
		// + atts.getValue(i) + "\"");
		// }
		// } else {
		// System.out.println("Start element: {" + uri + "}" + name);
		// }

		// My behaviour
		this.inElement = true;
		// Generate HashMap for the attributes
		LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
		for (int i = 0; i < atts.getLength(); i++) {
			attributes.put(atts.getQName(i), atts.getValue(i));
		}

		// Create new XMLTag and push it onto the stack
		HTMLTag current = new HTMLTag(qName, attributes);
		tagList.add(current);
	}

	public void endElement(final String uri, final String name, final String qName) {
		// Default behaviour
		// if ("".equals(uri))
		// System.out.println("End element: " + qName);
		// else
		// System.out.println("End element:   {" + uri + "}" + name);

		// My behaviour

		/*
		 * Element has ended, we are no longer inside an element This has been
		 * added because after endElement is called, the parser calls the
		 * characters() method again, and we don't want it to run, because it
		 * will override the previous content with nothing.
		 */
		this.inElement = false;
		// Pop the current XMLTag off the stack (only if it is not the root
		// element)
		if (tagList.size() > 1) {
			HTMLTag ended = tagList.remove(tagList.size() - 1);

			tagList.get(tagList.size() - 1).addElement(ended);
		}
	}

	public void characters(final char ch[], final int start, final int length) {
		if (this.inElement) {
			// System.out.print("   Characters: \"");
			String content = "";
			for (int i = start; i < start + length; i++) {
				switch (ch[i]) {
					case '\\':
						// System.out.print("\\\\");
						// content = content + "\\\\";
						break;
					case '"':
						// System.out.print("\\\"");
						// content = content + "\\\"";
						break;
					case '\n':
						// System.out.print("\\n");
						// content = content + "\\n";
						break;
					case '\r':
						// System.out.print("\\r");
						// content = content + "\\r";
						break;
					case '\t':
						// System.out.print("\\t");
						// content = content + "\\t";
						break;
					default:
						// System.out.print(ch[i]);
						content = content + ch[i];
						break;
				}
			}
			// System.out.println("Characters: " + content);
			// System.out.println(tagList.get(tagList.size() - 1).getName());
			tagList.get(tagList.size() - 1).setContent(content);
			// System.out.print("\n");
			// System.out.print("\"\n");
		}
	}

	private HTMLTag getHTMLTag() {
		return root;
	}

}
