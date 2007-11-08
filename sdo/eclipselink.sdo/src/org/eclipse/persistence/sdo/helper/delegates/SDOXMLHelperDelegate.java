/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper.delegates;

import commonj.sdo.DataObject;
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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.SDOXMLDocument;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOMarshalListener;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOUnmarshalListener;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
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
    private boolean isDirty;
    private TimeZone timeZone;
    private boolean timeZoneQualified;
    
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOXMLHelperDelegate(HelperContext aContext) {
        aHelperContext = aContext;
        // This ClassLoader is internal to SDO so no inter servlet-ejb container context issues should arise
        loader = new SDOClassLoader(Thread.currentThread().getContextClassLoader(), aContext);
        xmlMarshallerMap = new WeakHashMap<Thread, XMLMarshaller>();
        xmlUnmarshallerMap = new WeakHashMap<Thread, XMLUnmarshaller>();        
    }

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */   
    public void setTimeZone(TimeZone timeZone) {
    	if(this.timeZone == timeZone) {
    		return;
    	}
    	if(null == timeZone) {
    		timeZone = TimeZone.getDefault();
    	}
    	if(!timeZone.equals(this.timeZone)) {
        	this.timeZone = timeZone;
        	isDirty = true;    		
    	}
    }
    
    /**
     * By setting this flag to true the marshalled date objects marshalled to 
     * the XML schema types time and dateTime will be qualified by a time zone.  
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified) {
    	if(this.timeZoneQualified != timeZoneQualified) {
        	this.timeZoneQualified = timeZoneQualified;
        	isDirty = true;    		
    	}
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
            unmarshalledObject = anXMLUnmarshaller.unmarshal(inputSource);
        } else {
            try {
                Map optionsMap = (Map)options;
                try {
                    SDOType theType = (SDOType)optionsMap.get(SDOConstants.TYPE_LOAD_OPTION);
                    if (theType != null) {
                        unmarshalledObject = anXMLUnmarshaller.unmarshal(inputSource, theType.getImplClass());
                    }
                } catch (ClassCastException ccException) {                  
                  throw SDOException.typeOptionMustBeAnSDOType(ccException);
                }
            } catch (ClassCastException ccException) {
               throw SDOException.optionsMustBeAMap(ccException);            
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
            unmarshalledObject = anXMLUnmarshaller.unmarshal(source);
        } else {                     
            try {
                Map optionsMap = (Map)options;
                try {
                    SDOType theType = (SDOType)optionsMap.get(SDOConstants.TYPE_LOAD_OPTION);
                    if (theType != null) {
                        unmarshalledObject = anXMLUnmarshaller.unmarshal(source, theType.getImplClass());
                    }
                } catch (ClassCastException ccException) {                  
                  throw SDOException.typeOptionMustBeAnSDOType(ccException);
                }
            } catch (ClassCastException ccException) {
               throw SDOException.optionsMustBeAMap(ccException);            
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
            save(dataObject, rootElementURI, rootElementName, writer);
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
    public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws XMLMarshalException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        save(dataObject, rootElementURI, rootElementName, writer);
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
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
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
        // get XMLMarshaller once - as we may create a new instance if this helper isDirty=true
        XMLMarshaller anXMLMarshaller = getXmlMarshaller();

        anXMLMarshaller.setEncoding(xmlDocument.getEncoding());
        anXMLMarshaller.setSchemaLocation(xmlDocument.getSchemaLocation());
        anXMLMarshaller.setNoNamespaceSchemaLocation(xmlDocument.getNoNamespaceSchemaLocation());
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(xmlDocument.getRootObject());
        anXMLMarshaller.marshal(xmlDocument, outputWriter);
    }

    public void save(XMLDocument xmlDocument, Result result, Object options) throws IOException {
        // get XMLMarshaller once - as we may create a new instance if this helper isDirty=true
        XMLMarshaller anXMLMarshaller = getXmlMarshaller();

        anXMLMarshaller.setEncoding(xmlDocument.getEncoding());
        anXMLMarshaller.setSchemaLocation(xmlDocument.getSchemaLocation());
        anXMLMarshaller.setNoNamespaceSchemaLocation(xmlDocument.getNoNamespaceSchemaLocation());
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(xmlDocument.getRootObject());
        anXMLMarshaller.marshal(xmlDocument, result);

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
    private void save(DataObject rootObject, String rootElementURI, String rootElementName, Writer writer) throws XMLMarshalException {
    	// TODO: Change IOException here and in the public String save() caller to propagate XMLMarshalException instead of IOException
        SDOXMLDocument xmlDocument = (SDOXMLDocument)createDocument(rootObject, rootElementURI, rootElementName);

        // get XMLMarshaller once - as we may create a new instance if this helper isDirty=true
        XMLMarshaller anXMLMarshaller = getXmlMarshaller();
        ((SDOMarshalListener)anXMLMarshaller.getMarshalListener()).setMarshalledObject(rootObject);
        anXMLMarshaller.marshal(xmlDocument, writer);
    }

    public void setLoader(SDOClassLoader loader) {
        this.loader = loader;
    }

    public SDOClassLoader getLoader() {
        return loader;
    }

    public void setXmlContext(XMLContext xmlContext) {
        this.xmlContext = xmlContext;
    }

    public synchronized XMLContext getXmlContext() {
        if (xmlContext == null || isDirty) {
            xmlContext = new XMLContext(getTopLinkProject());
            isDirty = false;
            List sessions = xmlContext.getSessions();
            int sessionsSize = sessions.size();
            for(int x=0; x<sessionsSize; x++) {
            	Session session = (Session) sessions.get(x);
            	XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourceLogin().getDatasourcePlatform().getConversionManager();
            	xmlConversionManager.setLoader(this.loader);
            	if(null != this.timeZone) {
            		xmlConversionManager.setTimeZone(timeZone);
            	}
            	xmlConversionManager.setTimeZoneQualified(timeZoneQualified);
            }
        }
        return xmlContext;
    }

    public void addDescriptor(XMLDescriptor descriptor) {
        getTopLinkProject().addDescriptor(descriptor);
        isDirty = true;
    }

    public void addDescriptors(List descriptors) {
        for (int i = 0; i < descriptors.size(); i++) {
            XMLDescriptor nextDescriptor = (XMLDescriptor)descriptors.get(i);
            getTopLinkProject().addDescriptor(nextDescriptor);
        }
        isDirty = true;
    }

    public void setTopLinkProject(Project toplinkProject) {
        this.topLinkProject = toplinkProject;
        //TODO: temporarily nulling things out but should eventually have sessionbroker or list of sessions        
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
            String sdoPrefix = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getPrefix(SDOConstants.SDO_URL);
            nr.put(sdoPrefix, SDOConstants.SDO_URL);
            SDOConstants.SDO_CHANGESUMMARY.getXmlDescriptor().setNamespaceResolver(nr);
            topLinkProject.addDescriptor(SDOConstants.SDO_CHANGESUMMARY.getXmlDescriptor());
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
            marshaller.setMarshalListener(new SDOMarshalListener(marshaller, aHelperContext.getTypeHelper()));
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

    public void setDirty(boolean value) {
    	this.isDirty = value;    	
    }
    
    
    public void reset() {
        setXmlContext(null);
        this.xmlMarshallerMap.clear();
        this.xmlUnmarshallerMap.clear();
        setLoader(new SDOClassLoader(getClass().getClassLoader(), (HelperContext)this));
        setTopLinkProject(null);
    }

    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}