/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper.delegates;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.SDOXMLDocument;
import org.eclipse.persistence.sdo.helper.SDOUnmappedContentHandler;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOMarshalListener;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOUnmarshalListener;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.types.SDOPropertyType;
import org.eclipse.persistence.sdo.types.SDOTypeType;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.ContentHandlerRecord;
import org.eclipse.persistence.oxm.record.FormattedWriterRecord;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.record.WriterRecord;
import org.eclipse.persistence.sessions.Project;
import org.xml.sax.InputSource;

/**
 * <p><b>Purpose</b>: Helper to XML documents into DataObects and DataObjects into XML documents.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Load methods create commonj.sdo.XMLDocument objects from XML (unmarshal)
 * <li> Save methods create XML from commonj.sdo.XMLDocument and commonj.sdo.DataObject objects (marshal)
 * </ul>
 */
public class SDOXMLHelperDelegate implements SDOXMLHelper {
    private SDOClassLoader loader;
    private XMLContext xmlContext;
    
    private Map<Thread, XMLMarshaller> xmlMarshallerMap;
    private Map<Thread, XMLUnmarshaller> xmlUnmarshallerMap;

    private Project topLinkProject;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOXMLHelperDelegate(HelperContext aContext) {
        this(aContext, Thread.currentThread().getContextClassLoader());
    }

    public SDOXMLHelperDelegate(HelperContext aContext, ClassLoader aClassLoader) {
        aHelperContext = aContext;
        // This ClassLoader is internal to SDO so no inter servlet-ejb container context issues should arise
        loader = new SDOClassLoader(aClassLoader, aContext);
        xmlMarshallerMap = new WeakHashMap<Thread, XMLMarshaller>();
        xmlUnmarshallerMap = new WeakHashMap<Thread, XMLUnmarshaller>();        
    }

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */
    public void setTimeZone(TimeZone timeZone) {       
        getXmlConversionManager().setTimeZone(timeZone);
    }

    /**
     * By setting this flag to true the marshalled date objects marshalled to
     * the XML schema types time and dateTime will be qualified by a time zone.
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified) {
        getXmlConversionManager().setTimeZoneQualified(timeZoneQualified);
    }

    /**
     * Creates and returns an XMLDocument from the input String.
     * By default does not perform XSD validation.
     * Same as
     *   load(new StringReader(inputString), null, null);
     *
     * @param inputString specifies the String to read from
     * @return the new XMLDocument loaded
     * @throws RuntimeException for errors in XML parsing or
     *    implementation-specific validation.
     */
    public XMLDocument load(String inputString) {
        StringReader reader = new StringReader(inputString);
        try {
            return load(reader, null, null);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

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
     *    implementation-specific validation.
     */
    public XMLDocument load(InputStream inputStream) throws IOException {
        return load(inputStream, null, null);
    }

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
     *    implementation-specific validation.
     */
    public XMLDocument load(InputStream inputStream, String locationURI, Object options) throws IOException {
        InputSource inputSource = new InputSource(inputStream);
        return load(inputSource, locationURI, options);
    }

    /**
       * Creates and returns an XMLDocument from the inputSource.
       * The InputSource will be closed after reading.
       * By default does not perform XSD validation.
       * @param inputSource specifies the InputSource to read from
       * @param locationURI specifies the URI of the document for relative schema locations
       * @param options implementation-specific options.
       * @return the new XMLDocument loaded
       * @throws IOException for stream exceptions.
       * @throws RuntimeException for errors in XML parsing or
       *    implementation-specific validation.
       */
    public XMLDocument load(InputSource inputSource, String locationURI, Object options) throws IOException {
        // get XMLUnmarshaller once - as we may create a new instance if this helper isDirty=true
        XMLUnmarshaller anXMLUnmarshaller = getXmlUnmarshaller();
        Object unmarshalledObject = null;
        if (options == null) {
            try {
               unmarshalledObject = anXMLUnmarshaller.unmarshal(inputSource);
            } catch(XMLMarshalException xmlException){
               handleXMLMarshalException(xmlException);               
            }
        } else {
            try {
                DataObject optionsDataObject = (DataObject)options;
                try {
                    SDOType theType = (SDOType)optionsDataObject.get(SDOConstants.TYPE_LOAD_OPTION);
                    try{
                        if (theType != null) {
                            unmarshalledObject = anXMLUnmarshaller.unmarshal(inputSource, theType.getImplClass());
                        }else{
                            unmarshalledObject = anXMLUnmarshaller.unmarshal(inputSource);
                        }
                    } catch(XMLMarshalException xmlException){
                        handleXMLMarshalException(xmlException);               
                    }
                } catch (ClassCastException ccException) {                  
                  throw SDOException.typePropertyMustBeAType(ccException);
                }
            } catch (ClassCastException ccException) {
               throw SDOException.optionsMustBeADataObject(ccException, SDOConstants.ORACLE_SDO_URL ,SDOConstants.XMLHELPER_LOAD_OPTIONS);            
            }
        }

        if (unmarshalledObject instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot)unmarshalledObject;
            XMLDocument xmlDocument = createDocument((DataObject)((XMLRoot)unmarshalledObject).getObject(), ((XMLRoot)unmarshalledObject).getNamespaceURI(), ((XMLRoot)unmarshalledObject).getLocalName());
            if(xmlRoot.getEncoding() != null) {
                xmlDocument.setEncoding(xmlRoot.getEncoding());
            }
            if(xmlRoot.getXMLVersion() != null) {
                xmlDocument.setXMLVersion(xmlRoot.getXMLVersion());
            }
            xmlDocument.setSchemaLocation(xmlRoot.getSchemaLocation());
            xmlDocument.setNoNamespaceSchemaLocation(xmlRoot.getNoNamespaceSchemaLocation());
            return xmlDocument;             
        } else if (unmarshalledObject instanceof DataObject) {
            String localName = ((SDOType)((DataObject)unmarshalledObject).getType()).getXmlDescriptor().getDefaultRootElement();
            if (localName == null) {
                localName = ((SDOType)((DataObject)unmarshalledObject).getType()).getXsdLocalName();
            }
            return createDocument((DataObject)unmarshalledObject, ((DataObject)unmarshalledObject).getType().getURI(), localName);
        } else if (unmarshalledObject instanceof XMLDocument) {
            return (XMLDocument)unmarshalledObject;
        }

        return null;
    }

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
     *    implementation-specific validation.
     */
    public XMLDocument load(Reader inputReader, String locationURI, Object options) throws IOException {
        InputSource inputSource = new InputSource(inputReader);
        return load(inputSource, locationURI, options);
    }

    public XMLDocument load(Source source, String locationURI, Object options) throws IOException {
        // get XMLUnmarshaller once - as we may create a new instance if this helper isDirty=true
        XMLUnmarshaller anXMLUnmarshaller = getXmlUnmarshaller();
        Object unmarshalledObject = null;
        if (options == null) {
            try {
                unmarshalledObject = anXMLUnmarshaller.unmarshal(source);
            } catch(XMLMarshalException xmlException){
                handleXMLMarshalException(xmlException);               
            }
        } else {                     
            try {
                DataObject optionsDataObject = (DataObject)options;
                try {
                    SDOType theType = (SDOType)optionsDataObject.get(SDOConstants.TYPE_LOAD_OPTION);
                    try{
                        if (theType != null) {
                            unmarshalledObject = anXMLUnmarshaller.unmarshal(source, theType.getImplClass());
                        }else{
                            unmarshalledObject = anXMLUnmarshaller.unmarshal(source);
                        }
                    } catch(XMLMarshalException xmlException){
                        handleXMLMarshalException(xmlException);               
                    }
                } catch (ClassCastException ccException) {                  
                  throw SDOException.typePropertyMustBeAType(ccException);
                }
            } catch (ClassCastException ccException) {               
               throw SDOException.optionsMustBeADataObject(ccException, SDOConstants.ORACLE_SDO_URL ,SDOConstants.XMLHELPER_LOAD_OPTIONS);            
            }           
            
        }
        
        if (unmarshalledObject instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot)unmarshalledObject;
            XMLDocument xmlDocument = createDocument((DataObject)((XMLRoot)unmarshalledObject).getObject(), ((XMLRoot)unmarshalledObject).getNamespaceURI(), ((XMLRoot)unmarshalledObject).getLocalName());
            if(xmlRoot.getEncoding() != null) {
                xmlDocument.setEncoding(xmlRoot.getEncoding());
            }
            if(xmlRoot.getXMLVersion() != null) {
                xmlDocument.setXMLVersion(xmlRoot.getXMLVersion());
            }
            xmlDocument.setSchemaLocation(xmlRoot.getSchemaLocation());
            xmlDocument.setNoNamespaceSchemaLocation(xmlRoot.getNoNamespaceSchemaLocation());
            return xmlDocument;             
        } else if (unmarshalledObject instanceof DataObject) {
            DataObject unmarshalledDataObject = (DataObject)unmarshalledObject;
            String localName = ((SDOType)((DataObject)unmarshalledObject).getType()).getXmlDescriptor().getDefaultRootElement();
            if (localName == null) {
                localName = ((SDOType)((DataObject)unmarshalledObject).getType()).getXsdLocalName();
            }
            return createDocument(unmarshalledDataObject, unmarshalledDataObject.getType().getURI(), localName);
        } else if (unmarshalledObject instanceof XMLDocument) {
            return (XMLDocument)unmarshalledObject;
        }
        return null;
    }

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
     *    is not closed or has no container.
     */
    public String save(DataObject dataObject, String rootElementURI, String rootElementName) {
        try {
            StringWriter writer = new StringWriter();
            save(dataObject, rootElementURI, rootElementName, writer, getXmlMarshaller());
            return writer.toString();
        } catch (XMLMarshalException e) {
            throw SDOException.xmlMarshalExceptionOccurred(e, rootElementURI, rootElementName);
        }
    }

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
     *    is not closed or has no container.
     */
    public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws XMLMarshalException, IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, getXmlMarshaller().getEncoding());
        save(dataObject, rootElementURI, rootElementName, writer, getXmlMarshaller());
    }

    public void serialize(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, getXmlMarshaller().getEncoding());
        XMLMarshaller xmlMarshaller = getXmlContext().createMarshaller();
        xmlMarshaller.setMarshalListener(new SDOMarshalListener(xmlMarshaller, (SDOTypeHelper) aHelperContext.getTypeHelper()));
        save(xmlDocument, writer, options, xmlMarshaller);
    }

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
     *    is not closed or has no container.
     */
    public void save(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException {
        if (xmlDocument == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("save", "xmlDocument"));
        }
        
        String encoding = this.getXmlMarshaller().getEncoding();
        if(xmlDocument.getEncoding() != null) {
            encoding = xmlDocument.getEncoding();
        }
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, encoding);
        save(xmlDocument, writer, options);
    }

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
     *    is not closed or has no container.
     */
    public void save(XMLDocument xmlDocument, Writer outputWriter, Object options) throws IOException {
        save(xmlDocument, outputWriter, options, getXmlMarshaller());
    }

    private void save(XMLDocument xmlDocument, Writer outputWriter, Object options, XMLMarshaller anXMLMarshaller) throws IOException {
        if (xmlDocument == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("save", "xmlDocument"));
        }

        // Ask the SDOXMLDocument if we should include the XML declaration in the resulting XML
        anXMLMarshaller.setFragment(!xmlDocument.isXMLDeclaration());
        
        anXMLMarshaller.setEncoding(xmlDocument.getEncoding());
        anXMLMarshaller.setSchemaLocation(xmlDocument.getSchemaLocation());
        anXMLMarshaller.setNoNamespaceSchemaLocation(xmlDocument.getNoNamespaceSchemaLocation());
        
        WriterRecord writerRecord;
        if(anXMLMarshaller.isFormattedOutput()) {
            writerRecord = new FormattedWriterRecord();
        } else {
            writerRecord = new WriterRecord();
        }
        writerRecord.setWriter(outputWriter);
        writerRecord.setMarshaller(anXMLMarshaller);
        
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(xmlDocument.getRootObject());
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObjectRootQName(new QName(xmlDocument.getRootElementURI(), xmlDocument.getRootElementName()));
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setRootMarshalReocrd(writerRecord);
        
        anXMLMarshaller.marshal(xmlDocument, writerRecord);
        outputWriter.flush();
    }

    public void save(XMLDocument xmlDocument, Result result, Object options) throws IOException {
        if (xmlDocument == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("save", "xmlDocument"));
        }

        if (result instanceof StreamResult) {
            StreamResult streamResult = (StreamResult)result;
            Writer writer = streamResult.getWriter();
            if (null == writer) {
                save(xmlDocument, streamResult.getOutputStream(), options);
            } else {
                save(xmlDocument, writer, options);
            }
            
        } else {
            // get XMLMarshaller once - as we may create a new instance if this helper isDirty=true
            XMLMarshaller anXMLMarshaller = getXmlMarshaller();

            // Ask the SDOXMLDocument if we should include the XML declaration in the resulting XML
            anXMLMarshaller.setFragment(!xmlDocument.isXMLDeclaration());
        
            anXMLMarshaller.setEncoding(xmlDocument.getEncoding());
            anXMLMarshaller.setSchemaLocation(xmlDocument.getSchemaLocation());
            anXMLMarshaller.setNoNamespaceSchemaLocation(xmlDocument.getNoNamespaceSchemaLocation());
            ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(xmlDocument.getRootObject());
            ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObjectRootQName(new QName(xmlDocument.getRootElementURI(), xmlDocument.getRootElementName()));
            
            if(result instanceof SAXResult) {
                ContentHandlerRecord marshalRecord = new ContentHandlerRecord();
                marshalRecord.setContentHandler(((SAXResult)result).getHandler());
                marshalRecord.setMarshaller(anXMLMarshaller);
                ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setRootMarshalReocrd(marshalRecord);
                anXMLMarshaller.marshal(xmlDocument, marshalRecord);
            } else if(result instanceof DOMResult) {
                NodeRecord marshalRecord = new NodeRecord();
                marshalRecord.setDOM(((DOMResult)result).getNode());
                marshalRecord.setMarshaller(anXMLMarshaller);
                ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setRootMarshalReocrd(marshalRecord);
                anXMLMarshaller.marshal(xmlDocument, marshalRecord);
            } else {
                StringWriter writer = new StringWriter();
                this.save(xmlDocument, writer, options);
                String xml = writer.toString();
                StreamSource source = new StreamSource(new java.io.StringReader(xml));
                anXMLMarshaller.getTransformer().transform(source, result);
            }
        }

    }

    /**
     * Creates an XMLDocument with the specified XML rootElement for the DataObject.
     * @param dataObject specifies DataObject to be saved
     * @param rootElementURI the Target Namespace URI of the root XML element
     * @param rootElementName the Name of the root XML element
     * @return XMLDocument a new XMLDocument set with the specified parameters.
     */
    public XMLDocument createDocument(DataObject dataObject, String rootElementURI, String rootElementName) {
        SDOXMLDocument document = new SDOXMLDocument();
        document.setRootObject(dataObject);
        document.setRootElementURI(rootElementURI);
        if (rootElementName != null) {
            document.setRootElementName(rootElementName);
        }
        
        Property globalProp = getHelperContext().getXSDHelper().getGlobalProperty(rootElementURI, rootElementName, true);
        if (null != globalProp) {
            document.setSchemaType(((SDOType) globalProp.getType()).getXsdType());
        }
        
        document.setEncoding(SDOXMLDocument.DEFAULT_XML_ENCODING);
        document.setXMLVersion(SDOXMLDocument.DEFAULT_XML_VERSION);

        return document;
    }

    /**
         * INTERNAL:
         * Saves the DataObject as an XML document with the specified root element.
         * Same as
         *   save(createDocument(dataObject, rootElementURI, rootElementName),
         *     writer, null);
         *
         * @param dataObject specifies DataObject to be saved
         * @param rootElementURI the Target Namespace URI of the root XML element
         * @param rootElementName the Name of the root XML element
         * @param writer specifies the Writer to write to.
         * @throws IOException for stream exceptions.
         * @throws IllegalArgumentException if the dataObject tree
         *    is not closed or has no container.
         */
    private void save(DataObject rootObject, String rootElementURI, String rootElementName, Writer writer, XMLMarshaller anXMLMarshaller) throws XMLMarshalException {
        SDOXMLDocument xmlDocument = (SDOXMLDocument)createDocument(rootObject, rootElementURI, rootElementName);
        
        // Ask the SDOXMLDocument if we should include the XML declaration in the resulting XML
        anXMLMarshaller.setFragment(!xmlDocument.isXMLDeclaration());
        WriterRecord writerRecord;
        if(anXMLMarshaller.isFormattedOutput()) {
            writerRecord = new FormattedWriterRecord();
        } else {
            writerRecord = new WriterRecord();
        }
        writerRecord.setWriter(writer);
        writerRecord.setMarshaller(anXMLMarshaller);
        
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(rootObject);
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObjectRootQName(new QName(rootElementURI, rootElementName));
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setRootMarshalReocrd(writerRecord);
        anXMLMarshaller.marshal(xmlDocument, writerRecord);
        try {
            writer.flush();
        } catch(IOException ex) {
            throw XMLMarshalException.marshalException(ex);
        }
    }

    public void setLoader(SDOClassLoader loader) {
        this.loader = loader;
        getXmlConversionManager().setLoader(this.loader);
    }

    public SDOClassLoader getLoader() {
        return loader;
    }

    public void setXmlContext(XMLContext xmlContext) {
        this.xmlContext = xmlContext;
    }

    public synchronized XMLContext getXmlContext() {  
        if (xmlContext == null) {
            xmlContext = new XMLContext(getTopLinkProject());
            XMLConversionManager xmlConversionManager = getXmlConversionManager();
            xmlConversionManager.setLoader(this.loader);
            xmlConversionManager.setTimeZone(TimeZone.getTimeZone("GMT"));
            xmlConversionManager.setTimeZoneQualified(true);
        }
        return xmlContext;
    }
   
    public void initializeDescriptor(XMLDescriptor descriptor){
        AbstractSession theSession = (AbstractSession)getXmlContext().getSession(0);
        //do initialization for new descriptor;        
        descriptor.preInitialize(theSession);
        descriptor.initialize(theSession);
        descriptor.postInitialize(theSession);
        descriptor.getObjectBuilder().initializePrimaryKey(theSession);
        getXmlContext().storeXMLDescriptorByQName(descriptor);                           
    }

    public void addDescriptors(List types) {        
        for (int i = 0; i < types.size(); i++) {
            SDOType nextType = (SDOType)types.get(i);      
            
            if (!nextType.isDataType() && nextType.isFinalized()){            
              XMLDescriptor nextDescriptor = nextType.getXmlDescriptor();
              getTopLinkProject().addDescriptor(nextDescriptor);            
            }
        }        
        for (int i = 0; i < types.size(); i++) {
          SDOType nextType = (SDOType)types.get(i);                
          if (!nextType.isDataType() && nextType.isFinalized()){            
             XMLDescriptor nextDescriptor = nextType.getXmlDescriptor();
             initializeDescriptor(nextDescriptor);          
          }
        }                
    }

    public void setTopLinkProject(Project toplinkProject) {
        this.topLinkProject = toplinkProject;
        this.xmlContext = null;
        this.xmlMarshallerMap.clear();
        this.xmlUnmarshallerMap.clear();
    }

    public Project getTopLinkProject() {
        if (topLinkProject == null) {
            topLinkProject = new Project();
            XMLLogin xmlLogin = new XMLLogin();
            xmlLogin.setEqualNamespaceResolvers(false);
            topLinkProject.setDatasourceLogin(xmlLogin);
            // 200606_changeSummary
            NamespaceResolver nr = new NamespaceResolver();
            SDOTypeHelper sdoTypeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();
            String sdoPrefix = sdoTypeHelper.getPrefix(SDOConstants.SDO_URL);
            nr.put(sdoPrefix, SDOConstants.SDO_URL);
            SDOType changeSummaryType = (SDOType) sdoTypeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
            changeSummaryType.getXmlDescriptor().setNamespaceResolver(nr);
            topLinkProject.addDescriptor(changeSummaryType.getXmlDescriptor());
            SDOType openSequencedType = (SDOType) aHelperContext.getTypeHelper().getType(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType");
            topLinkProject.addDescriptor(openSequencedType.getXmlDescriptor());
            SDOTypeType typeType = (SDOTypeType)aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
            typeType.getXmlDescriptor().setNamespaceResolver(nr);
            if(!typeType.isInitialized()) {
                typeType.initializeMappings();
            }
            topLinkProject.addDescriptor(typeType.getXmlDescriptor());
            SDOPropertyType propertyType = (SDOPropertyType)aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
            if(!propertyType.isInitialized()) {
                propertyType.initializeMappings();
            }
            topLinkProject.addDescriptor(propertyType.getXmlDescriptor());
            
            ((SDOTypeHelper)aHelperContext.getTypeHelper()).addWrappersToProject(topLinkProject);
        }
        return topLinkProject;
    }

    public void setXmlMarshaller(XMLMarshaller xmlMarshaller) {
        this.xmlMarshallerMap.put(Thread.currentThread(), xmlMarshaller);
    }

    public XMLMarshaller getXmlMarshaller() {
    	XMLMarshaller marshaller = xmlMarshallerMap.get(Thread.currentThread());
    	
    	if (marshaller == null) {
            marshaller = getXmlContext().createMarshaller();
            marshaller.setMarshalListener(new SDOMarshalListener(marshaller, (SDOTypeHelper) aHelperContext.getTypeHelper()));
            xmlMarshallerMap.put(Thread.currentThread(), marshaller);
        }
        
    	XMLContext context = getXmlContext();
    	if (marshaller.getXMLContext() != context) { 
    		marshaller.setXMLContext(context);
    	}
        return marshaller;
    }

    public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
    	this.xmlUnmarshallerMap.put(Thread.currentThread(), xmlUnmarshaller);
    }

    public XMLUnmarshaller getXmlUnmarshaller() {
        XMLUnmarshaller unmarshaller = xmlUnmarshallerMap.get(Thread.currentThread());
    	
    	if (unmarshaller == null) {
            unmarshaller = getXmlContext().createUnmarshaller();
        unmarshaller.getProperties().put(SDOConstants.SDO_HELPER_CONTEXT, aHelperContext);
        unmarshaller.setUnmappedContentHandlerClass(SDOUnmappedContentHandler.class);
            unmarshaller.setUnmarshalListener(new SDOUnmarshalListener(aHelperContext));
            unmarshaller.setResultAlwaysXMLRoot(true);
            xmlUnmarshallerMap.put(Thread.currentThread(), unmarshaller);
        }
        
    	XMLContext context = getXmlContext();
    	if (unmarshaller.getXMLContext() != context) { 
    		unmarshaller.setXMLContext(context);
    	}
        return unmarshaller;
    }
    
    public void reset() {
        setTopLinkProject(null);
        setXmlContext(null);
        this.xmlMarshallerMap.clear();
        this.xmlUnmarshallerMap.clear();
        setLoader(new SDOClassLoader(getClass().getClassLoader(), aHelperContext));
    }

    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    private void handleXMLMarshalException(XMLMarshalException xmlException) throws IOException {
        if(xmlException.getErrorCode() == XMLMarshalException.NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT || xmlException.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT){
            throw SDOException.globalPropertyNotFound();
        } else if (xmlException.getCause() instanceof IOException) {
            throw (IOException) xmlException.getCause();
        } else{
            throw xmlException;
        }
    }

    public XMLConversionManager getXmlConversionManager() {
        return (XMLConversionManager)getXmlContext().getSession(0).getDatasourceLogin().getDatasourcePlatform().getConversionManager();
    }
}
