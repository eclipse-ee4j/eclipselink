/**
 * <copyright>
 *
 * Service Data Objects
 * Version 2.1.1
 * Licensed Materials
 *
 * (c) Copyright BEA Systems, Inc., International Business Machines Corporation, 
 * Oracle Corporation, Primeton Technologies Ltd., Rogue Wave Software, SAP AG., 
 * Software AG., Sun Microsystems, Sybase Inc., Xcalia, Zend Technologies, 
 * 2005-2008. All rights reserved.
 *
 * </copyright>
 * 
 */

package commonj.sdo.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.xml.transform.Source;
import javax.xml.transform.Result;

import commonj.sdo.DataObject;
import commonj.sdo.impl.HelperProvider;

/**
 * A helper to convert XML documents into DataObects and 
 * DataObjects into XML documnets.
 */
public interface XMLHelper
{
  /**
   * Creates and returns an XMLDocument from the input String.
   * By default does not perform XSD validation.
   * Same as
   *   load(new StringReader(inputString), null, null);
   * 
   * @param inputString specifies the String to read from
   * @return the new XMLDocument loaded
   * @throws RuntimeException for errors in XML parsing or 
   *   implementation-specific validation.
   */
  XMLDocument load(String inputString);

  /**
   * Creates and returns an XMLDocument from the inputStream.
   * The InputStream will be closed after reading.
   * By default does not perform XSD validation.
   * Same as
   *   load(inputStream, null, null);
   * 
   * @param inputStream specifies the InputStream to read from
   * @return the new XMLDocument loaded
   * @throws IOException for stream exceptions.
   * @throws RuntimeException for errors in XML parsing or 
   *   implementation-specific validation.
   */
  XMLDocument load(InputStream inputStream) throws IOException;
  
  /**
   * Creates and returns an XMLDocument from the inputStream.
   * The InputStream will be closed after reading.
   * By default does not perform XSD validation.
   * @param inputStream specifies the InputStream to read from
   * @param locationURI specifies the URI of the document for relative schema locations
   * @param options implementation-specific options.
   * @return the new XMLDocument loaded
   * @throws IOException for stream exceptions.
   * @throws RuntimeException for errors in XML parsing or 
   *   implementation-specific validation.
   */
  XMLDocument load(InputStream inputStream, String locationURI, Object options) throws IOException;

  /**
   * Creates and returns an XMLDocument from the inputReader.
   * The InputStream will be closed after reading.
   * By default does not perform XSD validation.
   * @param inputReader specifies the Reader to read from
   * @param locationURI specifies the URI of the document for relative schema locations
   * @param options implementation-specific options.
   * @return the new XMLDocument loaded
   * @throws IOException for stream exceptions.
   * @throws RuntimeException for errors in XML parsing or 
   *   implementation-specific validation.
   */
  XMLDocument load(Reader inputReader, String locationURI, Object options) throws IOException;
  
  /**
   * Creates and returns an XMLDocument from the inputSource.
   * The InputSource will be closed after reading.
   * By default does not perform XSD validation.
   * @param inputSource specifies the Source to read from
   * @param locationURI specifies the URI of the document for relative schema locations
   * @param options implementation-specific options.
   * @return the new XMLDocument loaded
   * @throws IOException for stream exceptions.
   * @throws RuntimeException for errors in XML parsing or 
   *   implementation-specific validation.
   */
  XMLDocument load(Source inputSource, String locationURI, Object options) throws IOException;
  
  /**
   * Returns the DataObject saved as an XML document with the specified root element.
   * Same as
   *   StringWriter stringWriter = new StringWriter();
   *   save(createDocument(dataObject, rootElementURI, rootElementName), 
   *     stringWriter, null);
   *   stringWriter.toString();
   *
   * @param dataObject specifies DataObject to be saved
   * @param rootElementURI the Target Namespace URI of the root XML element
   * @param rootElementName the Name of the root XML element
   * @return the saved XML document as a string
   * @throws IllegalArgumentException if the dataObject tree
   *   is not closed or has no container.
   */
  String save(DataObject dataObject, String rootElementURI, String rootElementName);

  /**
   * Saves the DataObject as an XML document with the specified root element.
   * Same as
   *   save(createDocument(dataObject, rootElementURI, rootElementName),
   *     outputStream, null);
   * 
   * @param dataObject specifies DataObject to be saved
   * @param rootElementURI the Target Namespace URI of the root XML element
   * @param rootElementName the Name of the root XML element
   * @param outputStream specifies the OutputStream to write to.
   * @throws IOException for stream exceptions.
   * @throws IllegalArgumentException if the dataObject tree
   *   is not closed or has no container.
   */
  void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws IOException;
  
  /**
   * Serializes an XMLDocument as an XML document into the outputStream.
   * If the DataObject's Type was defined by an XSD, the serialization
   *   will follow the XSD.
   * Otherwise the serialization will follow the format as if an XSD
   *   were generated as defined by the SDO specification.
   * The OutputStream will be flushed after writing.
   * Does not perform validation to ensure compliance with an XSD.
   * @param xmlDocument specifies XMLDocument to be saved
   * @param outputStream specifies the OutputStream to write to.
   * @param options implementation-specific options.
   * @throws IOException for stream exceptions.
   * @throws IllegalArgumentException if the dataObject tree
   *   is not closed or has no container.
   */
  void save(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException;

  /**
   * Serializes an XMLDocument as an XML document into the outputWriter.
   * If the DataObject's Type was defined by an XSD, the serialization
   *   will follow the XSD.
   * Otherwise the serialization will follow the format as if an XSD
   *   were generated as defined by the SDO specification.
   * The OutputStream will be flushed after writing.
   * Does not perform validation to ensure compliance with an XSD.
   * @param xmlDocument specifies XMLDocument to be saved
   * @param outputWriter specifies the Writer to write to.
   * @param options implementation-specific options.
   * @throws IOException for stream exceptions.
   * @throws IllegalArgumentException if the dataObject tree
   *   is not closed or has no container.
   */
  void save(XMLDocument xmlDocument, Writer outputWriter, Object options) throws IOException;

  /**
   * Serializes an XMLDocument as an XML document into the outputResult in a 
   * serialization technology independent format (as specified in 
   * javax.xml.transform).
   * The OutputResult will be flushed after writing.
   * Does not perform validation to ensure compliance with an XSD.
   * @param xmlDocument specifies XMLDocument to be saved
   * @param outputResult specifies Result to be saved
   * @param options implementation-specific options.
   * @throws IOException for stream exceptions.
   * @throws IllegalArgumentException if the dataObject tree
   *   is not closed or has no container.
   */
  void save(XMLDocument xmlDocument, Result outputResult, Object options) throws IOException;
  
  /**
   * Creates an XMLDocument with the specified XML rootElement for the DataObject.
   * @param dataObject specifies DataObject to be saved
   * @param rootElementURI the Target Namespace URI of the root XML element
   * @param rootElementName the Name of the root XML element
   * @return XMLDocument a new XMLDocument set with the specified parameters.
   */
  XMLDocument createDocument(DataObject dataObject, String rootElementURI, String rootElementName);
  
  /**
   * The default XMLHelper.
   */
  XMLHelper INSTANCE = HelperProvider.getXMLHelper();
}
