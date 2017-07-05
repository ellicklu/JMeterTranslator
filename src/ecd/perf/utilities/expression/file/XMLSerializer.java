package ecd.perf.utilities.expression.file;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class XMLSerializer
{
	public static final Object COALESCE_PROPERTY = new Object();
	public static final Object EXPAND_ENTITIES_PROPERTY = new Object();
	public static final Object IGNORE_COMMENTS_PROPERTY = new Object();
	public static final Object IGNORE_ELEMENT_WS_PROPERTY = new Object();
	public static final Object NAMESPACE_AWARE_PROPERTY = new Object();
	public static final Object VALIDATING_PROPERTY = new Object();

	private Map m_properties;
	private static Map<Object, Boolean> s_defaultProperties;

	static {
		s_defaultProperties = new HashMap<Object, Boolean>();
		s_defaultProperties.put(NAMESPACE_AWARE_PROPERTY, Boolean.FALSE);
		s_defaultProperties.put(IGNORE_ELEMENT_WS_PROPERTY, Boolean.TRUE);
		s_defaultProperties.put(IGNORE_COMMENTS_PROPERTY, Boolean.TRUE);
	}

	@SuppressWarnings("unchecked")
	public XMLSerializer()
	{
		m_properties = new HashMap();
		m_properties.putAll(s_defaultProperties);
	}

	@SuppressWarnings("unchecked")
	public XMLSerializer(Map properties)
	{
		this();
		m_properties.putAll(properties);
	}

	public Object deserialize(Document doc)
	{
		NodeList nl = doc.getElementsByTagName("document"); //$NON-NLS-1$
		Element root = (Element) (nl.item(0));
		XMLSerializationContext ctx = new XMLSerializationContext(this, doc, root);
		return ctx.deserializeObject(root, null);
	}

	public Object deserialize(InputStream istream) throws SAXException, IOException
	{
		Document doc = loadDocument(istream);
		return deserialize(doc);
	}

	private DocumentBuilderFactory createDBF()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Iterator iter = m_properties.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			if (key instanceof String) {
				try {
					dbf.setAttribute((String) key, m_properties.get(key));
				}
				catch (IllegalArgumentException e) {
					// unrecognized attributes can be silently ignored
					// might not be ideal (since behavior changes...)
					// but we can also try different interpretations...
					try {
						dbf.setAttribute((String) key, Boolean.valueOf((String) m_properties.get(key)));
					}
					catch (IllegalArgumentException e1) {
					}
				}
			}
			else {
				Object value = m_properties.get(key);
				if (key == NAMESPACE_AWARE_PROPERTY)
					dbf.setNamespaceAware(((Boolean) value).booleanValue());
				else if (key == COALESCE_PROPERTY)
					dbf.setCoalescing(((Boolean) value).booleanValue());
				else if (key == IGNORE_COMMENTS_PROPERTY)
					dbf.setIgnoringComments(((Boolean) value).booleanValue());
				else if (key == IGNORE_ELEMENT_WS_PROPERTY)
					dbf.setIgnoringElementContentWhitespace(((Boolean) value).booleanValue());
				else if (key == EXPAND_ENTITIES_PROPERTY)
					dbf.setExpandEntityReferences(((Boolean) value).booleanValue());
				else if (key == VALIDATING_PROPERTY)
					dbf.setValidating(((Boolean) value).booleanValue());
				else
					throw new IllegalArgumentException("Unrecognized DocumentBuilderFactory property: name=" + key //$NON-NLS-1$
							+ " value=" + value); //$NON-NLS-1$
			}
		}

		return dbf;

	}

	public static void parseDocument(InputStream stream, DefaultHandler handler) throws SAXException
	{
		parseDocument(new InputSource(stream), handler);
	}

	public static void parseDocument(String inputString, DefaultHandler handler) throws SAXException
	{
		StringReader reader = new StringReader(inputString);
		parseDocument(new InputSource(reader), handler);
	}

	public static void parseDocument(InputSource source, DefaultHandler handler) throws SAXException
	{
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.setDTDHandler(handler);
		reader.setEntityResolver(handler);
		try {
			reader.parse(source);
		}
		catch (IOException e) {
			throw new SAXException("error parsing XML document", e); //$NON-NLS-1$
		}

	}

	/**
	 * Load a document from an input stream.
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public Document loadDocument(InputStream stream) throws IOException, SAXException
	{
		DocumentBuilderFactory dbf = createDBF();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			// this is serious
			//log.error("unable to configure XML parser", e);

			SAXException ioe = new SAXException("unable to configure XML parser", e); //$NON-NLS-1$
			throw ioe;
		}

		Document doc;

		doc = db.parse(stream);

		return doc;
	}
	
	/**
	 * Use this form of loadDocument when you already know the character encoding of your XML,
	 * such as if the XML is read as a String from a database.
	 * When using this form of loadDocument, the Reader ensures that any XML prolog
	 * such as <?xml encoding="foo"/> is ignored, and the Reader takes care of decoding
	 * using the proper charset.
	 * @param reader
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public Document loadDocument(Reader reader) throws IOException, SAXException
	{
		DocumentBuilderFactory dbf = createDBF();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			// this is serious
			//log.error("unable to configure XML parser", e);

			SAXException ioe = new SAXException("unable to configure XML parser", e); //$NON-NLS-1$
			throw ioe;
		}

		Document doc;

		doc = db.parse(new InputSource(reader));

		return doc;
	}

	/**
	 * Load an XML document from a file.
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public Document loadDocument(String filename) throws IOException, SAXException
	{
		File f = new File(filename);
		if (!f.exists() && !f.canRead())
			throw new IOException("Unable to read " + filename); //$NON-NLS-1$
		FileInputStream fis = new FileInputStream(filename);
		try {
			return loadDocument(fis);
		}
		finally {
			fis.close();
		}
	}

	public Document loadDocumentFromXmlString(String xml) throws IOException, SAXException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		try {
			return loadDocument(bis);
		}
		finally {
			bis.close();
		}
	}

	public String storeDocument(Document doc)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		storeDocument(doc, bos);
		return new String(bos.toByteArray(), Charset.forName("UTF-8"));//$NON-NLS-1$
	}

	public void storeDocument(Document doc, OutputStream ostream)
	{
		TransformerFactory tfac = TransformerFactory.newInstance();
		try {
			Transformer tform = tfac.newTransformer();
			Source src = new DOMSource(doc.getDocumentElement());
			tform.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			tform.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			tform.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // TODO not for Japanese, other I18N? //$NON-NLS-1$
			Result result = new StreamResult(ostream);
			tform.transform(src, result);
		}
		catch (TransformerException e) {
		}
	}

	public void storeDocument(Document doc, String filename)
	{
		TransformerFactory tfac = TransformerFactory.newInstance();
		try {
			Transformer tform = tfac.newTransformer();
			Source src = new DOMSource(doc.getDocumentElement());
			tform.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			tform.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			tform.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // TODO not for Japanese, other I18N? //$NON-NLS-1$
			Result result = new StreamResult(new File(filename));
			tform.transform(src, result);
		}
		catch (TransformerException e) {
		}

	}
	
	public Document createNewDocument() 
	{
		DocumentBuilderFactory dbf = createDBF();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			return doc;
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

}
