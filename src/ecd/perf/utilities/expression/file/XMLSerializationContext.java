package ecd.perf.utilities.expression.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class XMLSerializationContext
{
	private Document m_doc;
	private Node m_root;
	private Map<Object, Object> m_refMap;
	private XMLSerializer m_rootSerializer;

	public XMLSerializationContext(XMLSerializer rootSerializer, Document doc, Node root)
	{
		m_rootSerializer = rootSerializer;
		m_doc = doc;
		m_root = root;
		m_refMap = new HashMap<Object, Object>();
		m_refMap.put(rootSerializer, new Integer(0));
	}

	private int nextRef()
	{
		Integer i = (Integer) m_refMap.get(m_rootSerializer);
		int nexti = 1 + i.intValue();
		m_refMap.put(m_rootSerializer, new Integer(nexti));
		return nexti;
	}

	public Document getDocument()
	{
		return m_doc;
	}

	public Node getParentElement()
	{
		return m_root;
	}

	public Element createElement(String tagName)
	{
		return m_doc.createElement(tagName);
	}

	public Element createElementNS(String ns, String tagName)
	{
		return m_doc.createElementNS(ns, tagName);
	}

	public XMLSerializationContext createChildContext(Element child)
	{
		XMLSerializationContext ctx = new XMLSerializationContext(m_rootSerializer, m_doc, child);
		ctx.m_refMap = this.m_refMap;
		return ctx;
	}

	public Element getChildElement(Node root, String name)
	{
		return findNamedChild(root, name);
	}

	private Element findNamedChild(Node root, String name)
	{
		Node n = root.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name))
				return (Element) n;
			n = n.getNextSibling();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Object> deserializeList(Node root, String name, List list)
	{
		List<Object> l = (List<Object>) list;
		// retrieve <name>
		Element el = findNamedChild(root, name);
		// retrieve each <entry>, add to list
		Node n = el.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("entry")) { //$NON-NLS-1$
				Object obj = deserializeObject((Element) n, null);
				l.add(obj);
			}
			n = n.getNextSibling();
		}
		return l;
	}


	@SuppressWarnings("unchecked")
	public Map<Object, Object> deserializeMap(Node root, String name, Map map)
	{
		Map<Object, Object> m = (Map<Object, Object>) map;

		// retrieve <name>
		Element el = findNamedChild(root, name);
		// retrieve each <entry>, add to map
		Node n = el.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("entry")) { //$NON-NLS-1$
				Element elKey = findNamedChild(n, "key"); //$NON-NLS-1$
				Element elValue = findNamedChild(n, "value"); //$NON-NLS-1$
				Object objKey = deserializeObject(elKey, null);
				Object objValue = deserializeObject(elValue, null);
				m.put(objKey, objValue);
			}
			n = n.getNextSibling();
		}
		return m;
	}


	public Object[] deserializeArray(Node root, String name, Object[] exemplarArray)
	{
		// retrieve <name>
		Element el = findNamedChild(root, name);
		// retrieve each <entry>, build a sorted tree
		TreeMap<Object, Object> l = new TreeMap<Object, Object>();
		Node n = el.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("entry")) { //$NON-NLS-1$
				Element elKey = findNamedChild(n, "key"); //$NON-NLS-1$
				Element elValue = findNamedChild(n, "value"); //$NON-NLS-1$
				Object objKey = Integer.parseInt((String) deserializeObject(elKey, null));
				Object objValue = deserializeObject(elValue, null);
				l.put(objKey, objValue);
			}
			n = n.getNextSibling();
		}
		// turn sorted tree into array
		return l.values().toArray(exemplarArray);
	}


	public Collection<Object> deserializeCollection(Node root, String name, Collection<Object> c)
	{
		// retrieve <name>
		Element el = findNamedChild(root, name);
		// retrieve each <entry>, build collection
		Node n = el.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("entry")) { //$NON-NLS-1$
				Object obj = deserializeObject((Element) n, null);
				c.add(obj);
			}
			n = n.getNextSibling();
		}
		return c;
	}


	private boolean stringRequiresEncoding(String s)
	{
		// things that happen to start with the magic prefix must be encoded
		if (s.startsWith("B64:")) //$NON-NLS-1$
			return true;
		/*
		 *
		 * check for characters in the string that are not legal in XML (see XML spec on W3.org):
		 * Char	   ::=   	#x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
		 */
		int count = s.length();
		for (int i = 0; i < count; ++i) {
			char b = s.charAt(i);
			if (b < 0x9)
				return true;
			if (b > 0xA && b < 0xD)
				return true;
			if (b > 0xD && b < 0x20)
				return true;
			if (b > 0xd7ff && b < 0xe000)
				return true;
			if (b > 0xfffd && b < 0x10000)
				return true;
			if (b > 0x10FFFF)
				return true;
		}
		return false;
	}

	private String encodeStringToXML(String s) throws IOException
	{
		if (stringRequiresEncoding(s)) {
			// base 64 encode
			return "B64:" + new String(Base64.encode(s.getBytes()));
		}
		return s;
	}

	private String decodeStringFromXML(String s)
	{
		if (s.startsWith("B64:")) { //$NON-NLS-1$
			try {
				s = new String(Base64.decode(s.substring("B64:".length())));
			} catch (Base64DecodingException e) {
			}
		}
		return s;
	}

	private String getNamedText(Node root, String name)
	{
		Element child;
		if (name == null)
			child = (Element) root;
		else
			child = findNamedChild(root, name);
		Node n = child.getFirstChild();
		Text t = (Text) n;
		String s = t.getNodeValue();
		return s;
	}

	private String getNamedTextCatch(Node root, String name)
	{
		String s = null;
		s = getNamedText(root, name);
		return s;
	}

	private String getNamedCDATA(Node root, String name)
	{
		Element child = findNamedChild(root, name);
		Node n = child.getFirstChild();
		CDATASection t = (CDATASection) n;
		String s = t.getData();
		return s;
	}

	public int deserializeInt(Node root, String name)
	{
		int i = Integer.parseInt(getNamedText(root, name));
		return i;
	}

	public int deserializeInt(Node root, String name, int defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		int i = Integer.parseInt(s);
		return i;
	}

	public long deserializeLong(Node root, String name)
	{
		long p = Long.parseLong(getNamedText(root, name));
		return p;
	}

	public long deserializeLong(Node root, String name, long defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		long p = Long.parseLong(s);
		return p;
	}

	public short deserializeShort(Node root, String name)
	{
		short p = Short.parseShort(getNamedText(root, name));
		return p;
	}

	public short deserializeShort(Node root, String name, short defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		short p = Short.parseShort(s);
		return p;
	}

	public float deserializeFloat(Node root, String name)
	{
		float p = Float.parseFloat(getNamedText(root, name));
		return p;
	}

	public float deserializeFloat(Node root, String name, float defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		float p = Float.parseFloat(s);
		return p;
	}

	public double deserializeDouble(Node root, String name)
	{
		double p = Double.parseDouble(getNamedText(root, name));
		return p;
	}

	public double deserializeDouble(Node root, String name, double defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		double p = Double.parseDouble(s);
		return p;
	}

	public char deserializeChar(Node root, String name)
	{
		String s = deserializeString(root, name);
		return s.charAt(0);
	}

	public char deserializeChar(Node root, String name, char defaultValue)
	{
		String s = deserializeString(root, name, null);
		if (s == null)
			return defaultValue;

		return s.charAt(0);
	}

	public byte deserializeByte(Node root, String name)
	{
		String s = deserializeString(root, name);
		return s.getBytes()[0];
	}

	public byte deserializeByte(Node root, String name, byte defaultValue)
	{
		String s = deserializeString(root, name, null);
		if (s == null)
			return defaultValue;

		return s.getBytes()[0];
	}

	public boolean deserializeBoolean(Node root, String name)
	{
		String s = getNamedText(root, name);
		return Boolean.valueOf(s).booleanValue();
	}

	public boolean deserializeBoolean(Node root, String name, boolean defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		return Boolean.valueOf(s).booleanValue();
	}

	public String deserializeString(Node root, String name)
	{
		return deserializeString(root, name, ""); //$NON-NLS-1$
	}

	public String deserializeString(Node root, String name, String defaultValue)
	{
		String s = getNamedTextCatch(root, name);
		if (s == null)
			return defaultValue;

		s = decodeStringFromXML(s);
		return s;
	}

	public String deserializeCDATA(Node root, String name)
	{
		return deserializeCDATA(root, name, ""); //$NON-NLS-1$
	}

	public String deserializeCDATA(Node root, String name, String defaultValue)
	{
			return getNamedCDATA(root, name);
	}

	@SuppressWarnings("unchecked")
	public Object deserializeObject(Element objectRoot, String name)
	{
		Element origRoot = objectRoot;
		if (name != null) {
			objectRoot = findNamedChild(objectRoot, name);
		}
		if (objectRoot == null)
			return null;

		String refNum = objectRoot.getAttribute("ref"); //$NON-NLS-1$
		if (objectRoot.hasAttribute("ref")) { //$NON-NLS-1$
			Object obj = m_refMap.get(refNum);
			return obj;
		}
		String clazzName = objectRoot.getAttribute("class"); //$NON-NLS-1$
		return clazzName;
	}

	public void addAttribute(Element element, String name, int p)
	{
		element.setAttribute(name, Integer.toString(p));
	}

	public void addAttribute(Element element, String name, short p)
	{
		element.setAttribute(name, Short.toString(p));
	}

	public void addAttribute(Element element, String name, long p)
	{
		element.setAttribute(name, Long.toString(p));
	}

	public void addAttribute(Element element, String name, float p)
	{
		element.setAttribute(name, Float.toString(p));
	}

	public void addAttribute(Element element, String name, double p)
	{
		element.setAttribute(name, Double.toString(p));
	}

	public void addAttribute(Element element, String name, byte p)
	{
		element.setAttribute(name, new String(new byte[] { p }));
	}

	public void addAttribute(Element element, String name, char p)
	{
		element.setAttribute(name, new String(new char[] { p }));
	}

	public void addAttribute(Element element, String name, boolean p)
	{
		element.setAttribute(name, Boolean.toString(p));
	}

	public void addAttribute(Element element, String name, String p)
	{
		if (p != null && !"".equals(p)) { //$NON-NLS-1$
			try {
				p = encodeStringToXML(p);
			}
			catch (IOException e) {
			}
			element.setAttribute(name, p);
		}
	}

	public int getAttributeInt(Element element, String name)
	{
		return Integer.parseInt(element.getAttribute(name));
	}

	public short getAttributeShort(Element element, String name)
	{
		return Short.parseShort(element.getAttribute(name));
	}

	public long getAttributeLong(Element element, String name)
	{
		return Long.parseLong(element.getAttribute(name));
	}

	public float getAttributeFloat(Element element, String name)
	{
		return Float.parseFloat(element.getAttribute(name));
	}

	public double getAttributeDouble(Element element, String name)
	{
		return Double.parseDouble(element.getAttribute(name));
	}

	public char getAttributeChar(Element element, String name)
	{
		return element.getAttribute(name).charAt(0);
	}

	public byte getAttributeByte(Element element, String name)
	{
		return element.getAttribute(name).getBytes()[0];
	}

	public boolean getAttributeBoolean(Element element, String name)
	{
		return Boolean.parseBoolean(element.getAttribute(name));
	}

	public String getAttributeString(Element element, String name)
	{
		return getAttributeString(element, name, ""); //$NON-NLS-1$
	}

	public int getAttributeInt(Element element, String name, int defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Integer.parseInt(element.getAttribute(name));
	}

	public short getAttributeShort(Element element, String name, short defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Short.parseShort(element.getAttribute(name));
	}

	public long getAttributeLong(Element element, String name, long defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Long.parseLong(element.getAttribute(name));
	}

	public float getAttributeFloat(Element element, String name, float defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Float.parseFloat(element.getAttribute(name));
	}

	public double getAttributeDouble(Element element, String name, double defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Double.parseDouble(element.getAttribute(name));
	}

	public char getAttributeChar(Element element, String name, char defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return element.getAttribute(name).charAt(0);
	}

	public byte getAttributeByte(Element element, String name, byte defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return element.getAttribute(name).getBytes()[0];
	}

	public boolean getAttributeBoolean(Element element, String name, boolean defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		return Boolean.parseBoolean(element.getAttribute(name));
	}

	public String getAttributeString(Element element, String name, String defaultValue)
	{
		if (!element.hasAttribute(name))
			return defaultValue;

		String s = element.getAttribute(name);
		try {
			s = decodeStringFromXML(s);
			return s;
		}
		catch (Exception e) {
			return defaultValue;
		}
	}
}
