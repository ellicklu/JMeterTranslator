package ecd.perf.utilities.xmlStream.IO;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.jdom.*;

import ecd.perf.utilities.xmlStream.core.ConversionContext;


/**
 * write to xml file with Object's value
 * @author EllickLu
 *
 */
public class Object2XmlWriter extends PrintWriter
{
	//tab to be spared before tags' head
	private int m_tab;

	//xml file head
	private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

	/**
	 * construct writer
	 * @param output destination where the xml stream is written to
	 * @param context conversion context
	 */
	public Object2XmlWriter(Writer output, ConversionContext context)
	{
		super(output);
		m_tab = 0;
		println(XML_VERSION);
	}

	/**
	 * print the body text
	 * @param text body text
	 */
	public void printBodyText(String text)
	{
		StringBuffer sb = new StringBuffer();
		//escape text
		sb.append(XmlIOUtils.getEscaped(text));
		print(sb.toString());
	}

	/**
	 * print tag name and attributes
	 * @param element element to be printed
	 * @param hasBody TRUE: if has children or text, FALSE: if is empty body
	 */
	@SuppressWarnings("unchecked")
	public void startTag(Element element, boolean hasBody)
	{
		m_tab++;
		StringBuffer sb = new StringBuffer();
		sb.append("<"); //$NON-NLS-1$
		sb.append(element.getName());

		//print attributes
		List<Attribute> attributes = element.getAttributes();
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = (Attribute) attributes.get(i);
			sb.append(" "); //$NON-NLS-1$
			sb.append(attribute.getName());
			sb.append("=\""); //$NON-NLS-1$
			sb.append(XmlIOUtils.getEscaped(String.valueOf(attribute.getValue())));
			sb.append("\""); //$NON-NLS-1$
		}

		sb.append((hasBody) ? ">" : "/>"); //$NON-NLS-1$ //$NON-NLS-2$
		print(sb.toString());
		if (!hasBody)
			m_tab--;
	}

	/**
	 * print tag end
	 * @param element
	 */
	public void endTag(Element element)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("</"); //$NON-NLS-1$
		sb.append(element.getName());
		sb.append(">"); //$NON-NLS-1$
		print(sb.toString());
		m_tab--;
	}

	/**
	 * change line
	 */
	public void changeLine()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < m_tab; i++) {
			sb.append("\t"); //$NON-NLS-1$
		}
		print("\n" + sb.toString()); //$NON-NLS-1$
	}
}
