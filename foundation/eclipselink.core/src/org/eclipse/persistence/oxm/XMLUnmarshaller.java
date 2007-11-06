/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.transform.Source;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.eclipse.persistence.oxm.attachment.*;

/**
 * <p>Class used to unmarshal XML to objects.
 *
 * <p>Create an XMLUnmarshaller from an XMLContext.<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLUnmarshaller unmarshaller = context.createUnmarshaller();<br>
 *  <code>
 *
 * <p>XML can be unmarshalled from the following inputs:<ul>
 * <li>java.io.File</li>
 * <li>java.io.InputStream</li>
 * <li>java.io.Reader</li>
 * <li>java.net.URL</li>
 * <li>javax.xml.transform.Source</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.InputSource</li>
 * </ul>
 *
 * <p>XML that can be unmarshalled is XML which has a root tag that corresponds
 * to a default root element on an XMLDescriptor in the TopLink project associated
 * with the XMLContext.
 *
 * @see org.eclipse.persistence.oxm.XMLContext
 */
public class XMLUnmarshaller {
    public static final int NONVALIDATING = XMLParser.NONVALIDATING;
    public static final int SCHEMA_VALIDATION = XMLParser.SCHEMA_VALIDATION;
    public static final int DTD_VALIDATION = XMLParser.DTD_VALIDATION;
    private XMLContext xmlContext;
    private XMLUnmarshallerHandler xmlUnmarshallerHandler;
    private PlatformUnmarshaller platformUnmarshaller;
    private boolean schemasAreInitialized;
    private XMLUnmarshalListener unmarshalListener;
    private XMLAttachmentUnmarshaller attachmentUnmarshaller;
    private Properties unmarshalProperties;
    private Class unmappedContentHandlerClass;

    protected XMLUnmarshaller(XMLContext xmlContext) {
        setXMLContext(xmlContext);
    }

    private void initialize() {
        DatabaseSession session = xmlContext.getSession(0);
        XMLPlatform xmlPlatform = (XMLPlatform)session.getDatasourceLogin().getDatasourcePlatform();
        platformUnmarshaller = xmlPlatform.newPlatformUnmarshaller(xmlContext);
        platformUnmarshaller.setWhitespacePreserving(false);
        unmarshalProperties = new Properties();

        // Waiting on XDK to fix bug #3697940 to enable this code
        //initializeSchemas();        
        setValidationMode(NONVALIDATING);
    }

    private void initializeSchemas() {
        if (!schemasAreInitialized) {
            HashSet schemas = new HashSet();
            Iterator xmlDescriptors;
            XMLDescriptor xmlDescriptor;
            XMLSchemaReference xmlSchemaReference;
            int numberOfSessions = xmlContext.getSessions().size();
            for (int x = 0; x < numberOfSessions; x++) {
                xmlDescriptors = ((DatabaseSession)xmlContext.getSessions().get(x)).getDescriptors().values().iterator();
                URL schemaURL;
                while (xmlDescriptors.hasNext()) {
                    xmlDescriptor = (XMLDescriptor)xmlDescriptors.next();
                    xmlSchemaReference = (XMLSchemaReference)xmlDescriptor.getSchemaReference();
                    if (null != xmlSchemaReference) {
                        schemaURL = xmlSchemaReference.getURL();
                        if (null != schemaURL) {
                            schemas.add(schemaURL.toString());
                        }
                    }
                }
            }
            schemas.remove(null);
            platformUnmarshaller.setSchemas(schemas.toArray());
            schemasAreInitialized = true;
        }
    }

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLUnmarshaller.
     */
    public XMLContext getXMLContext() {
        return xmlContext;
    }

    /** 
     * Set the XMLContext used by this instance of XMLUnmarshaller.
     */
    public void setXMLContext(XMLContext value) {
        xmlContext =  value;
        initialize();
    }
    
    /**
    * Get the validation mode set on this XMLUnmarshaller
    * By default, the unmarshaller is set to be NONVALIDATING
    * @return the validation mode
    */
    public int getValidationMode() {
        return platformUnmarshaller.getValidationMode();
    }

    /**
     * INTERNAL
     * @param unitOfWork
     */
    public void resolveReferences(AbstractSession unitOfWork) {
        ReferenceResolver resolver = ReferenceResolver.getInstance(unitOfWork);
        if (resolver != null) {
            resolver.resolveReferences(unitOfWork);
        }
    }

    /**
    * Set the validation mode.
    * This method sets the validation mode of the parser to one of the 3 types:
    * NONVALIDATING, DTD_VALIDATION and SCHEMA_VALIDATION.
    * By default, the unmarshaller is set to be NONVALIDATING
    * @param validationMode sets the type of the validation mode to be used
    **/
    public void setValidationMode(int validationMode) {
        if (validationMode == SCHEMA_VALIDATION) {
            initializeSchemas();
        }
        platformUnmarshaller.setValidationMode(validationMode);
    }

    /**
     * Get the EntityResolver set on this XMLUnmarshaller
     * @return the EntityResolver set on this XMLUnmarshaller
     */
    public EntityResolver getEntityResolver() {
        return platformUnmarshaller.getEntityResolver();
    }

    /**
     * Set the EntityResolver on this XMLUnmarshaller
     * @param entityResolver the EntityResolver to set on this XMLUnmarshaller
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        platformUnmarshaller.setEntityResolver(entityResolver);
    }

    /**
     * Get the ErrorHandler set on this XMLUnmarshaller
     * @return the ErrorHandler set on this XMLUnmarshaller
     */
    public ErrorHandler getErrorHandler() {
        return platformUnmarshaller.getErrorHandler();
    }

    /**
     * Set the ErrorHandler on this XMLUnmarshaller
     * @param errorHandler the ErrorHandler to set on this XMLUnmarshaller
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        platformUnmarshaller.setErrorHandler(errorHandler);
    }

    public XMLUnmarshalListener getUnmarshalListener() {
        return this.unmarshalListener;
    }

    public void setUnmarshalListener(XMLUnmarshalListener listener) {
        this.unmarshalListener = listener;
    }

    /**
      * Get the class that will be instantiated to handled unmapped content
      * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
      */
    public Class getUnmappedContentHandlerClass() {
        return this.unmappedContentHandlerClass;
    }

    /**
     * Set the class that will be instantiated to handled unmapped content
     * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
     * @param aClass
     */
    public void setUnmappedContentHandlerClass(Class aClass) {
        this.unmappedContentHandlerClass = aClass;
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the file and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param file The file to unmarshal from
    * @return the object which resulted from unmarshalling the given file
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(File file) throws XMLMarshalException {
        if (file == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(file, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the file and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param file The file to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given file
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(File file, Class clazz) throws XMLMarshalException {
        if ((null == file) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(file, clazz, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputStream and map the XML data into an object.
    * The inputStream must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param inputStream The inputStream to unmarshal from
    * @return the object which resulted from unmarshalling the given inputStream
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputStream inputStream) throws XMLMarshalException {
        if (inputStream == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputStream, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputStream and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param inputStream The inputStream to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given inputStream
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputStream inputStream, Class clazz) throws XMLMarshalException {
        if ((null == inputStream) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputStream, clazz, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the reader and map the XML data into an object.
    * The reader must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param reader The reader to unmarshal from
    * @return the object which resulted from unmarshalling the given reader
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Reader reader) throws XMLMarshalException {
        if (reader == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(reader, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the reader and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param reader The reader to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given reader
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Reader reader, Class clazz) throws XMLMarshalException {
        if ((null == reader) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(reader, clazz, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the url and map the XML data into an object.
    * The url must reference a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param url The url to unmarshal from
    * @return the object which resulted from unmarshalling the given url
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(URL url) throws XMLMarshalException {
        if (url == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(url, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the url and map the XML data into an object.
    * The url must reference a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param url The url to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given url
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(URL url, Class clazz) throws XMLMarshalException {
        if ((null == url) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(url, clazz, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputSource and map the XML data into an object.
    * The inputSource must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param inputSource The inputSource to unmarshal from
    * @return the object which resulted from unmarshalling the given inputSource
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputSource inputSource) throws XMLMarshalException {
        if (inputSource == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputSource, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputSource and map the XML data into an object.
    * The inputSource must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param inputSource The inputSource to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given inputSource
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputSource inputSource, Class clazz) throws XMLMarshalException {
        if ((null == inputSource) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputSource, clazz, this);
    }

    /**
    * PUBLIC:
    * Map the XML node into an object.
    * The node must be a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param node The node to unmarshal from
    * @return the object which resulted from unmarshalling the given node
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Node node) throws XMLMarshalException {
        if (node == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        if ((node.getNodeType() == Node.DOCUMENT_NODE) || (node.getNodeType() == Node.ELEMENT_NODE) || (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)) {
            return platformUnmarshaller.unmarshal(node, this);
        } else {
            throw XMLMarshalException.unmarshalException();
        }
    }

    /**
    * PUBLIC:
    * Map the XML node into an object.
    * The node must be a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param node The node to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given node
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Node node, Class clazz) throws XMLMarshalException {
        if ((null == node) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(node, clazz, this);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the source and map the XML data into an object.
    * The source must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param source The source to unmarshal from
    * @return the object which resulted from unmarshalling the given source
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Source source) throws XMLMarshalException {
        if (source == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(source, this);
    }

    /**
     * Return a properties object for a given instance of the
     * XMLUnmarshaller.
     *
     * @return
     */
    public Properties getProperties() {
        return unmarshalProperties;
    }

    /**
     * Return the property for a given key, if one exists.
     *
     * @parm key
     * @return
     */
    public Object getProperty(Object key) {
        return unmarshalProperties.get(key);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the source and map the XML data into an object.
    * The source must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param source The inputSource to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given source
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Source source, Class clazz) throws XMLMarshalException {
        if ((null == source) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(source, clazz, this);
    }

    public XMLUnmarshallerHandler getUnmarshallerHandler() {
        if (null == xmlUnmarshallerHandler) {
            xmlUnmarshallerHandler = new XMLUnmarshallerHandler(this);
        }
        return xmlUnmarshallerHandler;
    }

    public XMLAttachmentUnmarshaller getAttachmentUnmarshaller() {
        return attachmentUnmarshaller;
    }

    public void setAttachmentUnmarshaller(XMLAttachmentUnmarshaller atu) {
        attachmentUnmarshaller = atu;
    }

    public void setResultAlwaysXMLRoot(boolean alwaysReturnRoot) {
        platformUnmarshaller.setResultAlwaysXMLRoot(alwaysReturnRoot);
    }

    public boolean isResultAlwaysXMLRoot() {
        return platformUnmarshaller.isResultAlwaysXMLRoot();
    }
}
