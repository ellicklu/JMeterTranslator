package ecd.perf.utilities.xmlStream.core;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import ecd.perf.utilities.xmlStream.IO.Object2XmlWriter;
import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;


/**
 * Read and Write xml file
 * @author EllickLu
 *
 */
public class XmlStream
{
	//conversion context which includes class maps, allias, converters
	private ConversionContext m_conversionContext;

	/**
	 * construct XmlStream
	 *
	 */
	public XmlStream()
	{
		m_conversionContext = new ConversionContext();
		m_conversionContext.setupAliases();
		m_conversionContext.setupConverters();
	}

	/**
	 * Write Object to xml file
	 * @param obj Object to be wriiten
	 * @return xml file stream
	 * @throws XmlStreamException on any exception
	 */
	public String toXML(Object obj) throws XmlStreamException
	{
		//output stream
		Writer stringWriter = new StringWriter();
		//object writer
		Object2XmlWriter writer = new Object2XmlWriter(stringWriter, m_conversionContext);
		//marshaller recursive convert engine
		TreeMarshaller marshaller = new TreeMarshaller(writer, m_conversionContext);
		try {
			//convert object to xml
			marshaller.convertToXml(obj, null);
		}
		catch (ConversionException e) {
			throw new XmlStreamException(e);
		}
		finally {
			//close writer
			writer.flush();
			writer.close();
		}
		//return output stream
		return stringWriter.toString();
	}

	public ConversionContext getConversionContext()
	{
		return m_conversionContext;
	}

	public void setConversionContext(ConversionContext context)
	{
		m_conversionContext = context;
	}
}