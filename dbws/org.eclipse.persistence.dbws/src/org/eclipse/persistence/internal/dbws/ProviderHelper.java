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

package org.eclipse.persistence.internal.dbws;

//javase imports
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Java extension imports
import javax.activation.DataHandler;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import static javax.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL;
import static javax.xml.soap.SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
import static javax.xml.soap.SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
import static javax.xml.ws.handler.MessageContext.INBOUND_MESSAGE_ATTACHMENTS;

// EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.dbws.DBWSAdapter;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ValueObject;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.SCHEMA_2_CLASS;
import static org.eclipse.persistence.internal.xr.Util.SERVICE_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT;

/**
 * <p>
 * <b>INTERNAL:</b> ProviderHelper bridges between {@link DBWSAdapter}'s and JAX-WS {@link Provider}'s
 * <p>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.1
 * <pre>
 * packaging required for deployment as a Web Service
 * \--- root of war file
 *      |
 *      \---web-inf
 *          |   web.xml
 *          |
 *          +---classes
 *          |   +---META-INF
 *          |   |    eclipselink-dbws.xml
 *          |   |    eclipselink-dbws-sessions.xml -- name can be overriden by <sessions-file> entry in eclipselink-dbws.xml
 *          |   |    eclipselink-dbws-or.xml
 *          |   |    eclipselink-dbws-ox.xml
 *          |   |
 *          |   +---_dbws
 *          |   |    DBWSProvider.java         -- (source provided as a convenience for IDE integration)
 *          |   |    DBWSProvider.class        -- ASM-generated javax.xml.ws.Provider
 *          |   |
 *          |   \---foo                        -- optional domain classes
 *          |       \---bar
 *          |             Address.class
 *          |             Employee.class
 *          |             PhoneNumber.class
 *          \---wsdl
 *                 swaref.xsd                  -- optional to handle attachements
 *                 eclipselink-dbws.wsdl
 *                 eclipselink-dbws-schema.xsd
 * </pre>
 */
public class ProviderHelper extends XRServiceFactory {

    protected static final String XSL_PREAMBLE =
      "<?xml version=\"1.0\"?> " +
      "<xsl:stylesheet " +
        "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\" " +
        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
        "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" " +
        "> " +
      "<xsl:output method=\"xml\" encoding=\"UTF-8\"/> ";
    protected static final String XSL_POSTSCRIPT = "</xsl:stylesheet>";
    public static final String MATCH_SCHEMA =
      XSL_PREAMBLE +
        "<xsl:template match=\"/\">" +
             "<xsl:apply-templates/>" +
        "</xsl:template>" +
        "<xsl:template match=\"//xsd:schema\">" +
          "<xsl:copy-of select=\".\"/>" +
        "</xsl:template>" +
      XSL_POSTSCRIPT;
    public SOAPResponseWriter responseWriter = null;
    protected boolean mtomEnabled;
    protected MessageContext mc;

    // Default constructor required by servlet/jax-ws spec
    public ProviderHelper() {
        super();
    }

    protected void setMessageContext(MessageContext mc) {
        this.mc = mc;
    }

    protected InputStream initXRServiceStream(ClassLoader parentClassLoader,
        @SuppressWarnings("unused") ServletContext sc) {
        InputStream xrServiceStream = null;
        for (String searchPath : META_INF_PATHS) {
            String path = searchPath + DBWS_SERVICE_XML;
            xrServiceStream = parentClassLoader.getResourceAsStream(path);
            if (xrServiceStream != null) {
                break;
            }
        }
        if (xrServiceStream == null) {
            throw new WebServiceException(DBWSException.couldNotLocateFile(DBWS_SERVICE_XML));
        }
        return xrServiceStream;
    }
    
    protected InputStream initXRSchemaStream(ClassLoader parentClassLoader, ServletContext sc) {
        InputStream xrSchemaStream = null;
        String path = WSDL_DIR + DBWS_SCHEMA_XML;
        if (sc != null) {
            path = "/" + WEB_INF_DIR + path; 
            xrSchemaStream = sc.getResourceAsStream(path);
        }
        else {
            // if ServletContext is null, then we are running in JavaSE6 'container-less' mode
            xrSchemaStream = parentClassLoader.getResourceAsStream(path); 
        }
        if (xrSchemaStream == null) {
            throw new WebServiceException(DBWSException.couldNotLocateFile(DBWS_SCHEMA_XML));
        }
        return xrSchemaStream;
    }
    
    protected InputStream initWSDLInputStream(ClassLoader parentClassLoader, ServletContext sc) {
        InputStream wsdlInputStream = null;
        String path = WSDL_DIR + DBWS_WSDL;
        if (sc != null) {
            path = "/" + WEB_INF_DIR + path;
            wsdlInputStream = sc.getResourceAsStream(path);
        }
        else {
            // if ServletContext is null, then we are running in JavaSE6 'container-less' mode
            wsdlInputStream = parentClassLoader.getResourceAsStream(path);
        } 
        if (wsdlInputStream == null) {
            throw new WebServiceException(DBWSException.couldNotLocateFile(DBWS_WSDL));
        }
        return wsdlInputStream;
    }
    
    @SuppressWarnings("unchecked")
    public void init(ClassLoader parentClassLoader, ServletContext sc, boolean mtomEnabled) {
        this.parentClassLoader = parentClassLoader;
        this.mtomEnabled = mtomEnabled;

        InputStream xrServiceStream = initXRServiceStream(parentClassLoader, sc);
        DBWSModelProject xrServiceModelProject = new DBWSModelProject();
        XMLContext xmlContext = new XMLContext(xrServiceModelProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        XRServiceModel xrServiceModel;
        try {
            xrServiceModel = (XRServiceModel)unmarshaller.unmarshal(xrServiceStream);
        }
        catch (XMLMarshalException e) {
            // something went wrong parsing the eclipselink-dbws.xml - can't recover from that
            throw new WebServiceException(DBWSException.couldNotParseDBWSFile());
        }
        finally {
            try {
                xrServiceStream.close();
            }
            catch (IOException e) {
                // ignore
            }
        }

        xrSchemaStream = initXRSchemaStream(parentClassLoader, sc);
        try {
            buildService(xrServiceModel); // inherit xrService processing from XRServiceFactory
        }
        catch (Exception e) {
            // something went wrong building the service
            throw new WebServiceException(e);
        }

        // the xrService built by 'buildService' above is overridden to produce an
        // instance of DBWSAdapter (a sub-class of XRService)
        DBWSAdapter dbwsAdapter = (DBWSAdapter)xrService;

        InputStream wsdlInputStream = initWSDLInputStream(parentClassLoader, sc);
        // get inline schema from WSDL - has additional types for the operations
        try {
            StringWriter sw = new StringWriter();
            StreamSource wsdlStreamSource = new StreamSource(wsdlInputStream);
        	Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(
        	    new StringReader(MATCH_SCHEMA)));
        	StreamResult streamResult = new StreamResult(sw);
        	t.transform(wsdlStreamSource, streamResult);
        	sw.toString();
        	wsdlInputStream.close();
            SchemaModelProject schemaProject = new SchemaModelProject();
            XMLContext xmlContext2 = new XMLContext(schemaProject);
            unmarshaller = xmlContext2.createUnmarshaller();
            Schema extendedSchema = (Schema)unmarshaller.unmarshal(new StringReader(sw.toString()));
            dbwsAdapter.setExtendedSchema(extendedSchema);
        }
        catch (Exception e) {
        	// that's Ok, WSDL may not contain inline schema
        }
        finally {
            try {
                wsdlInputStream.close();
            }
            catch (IOException e) {
                // ignore
            }
        }
        
        // an Invocation needs a mapping for its parameters - use XMLAnyCollectionMapping +
        // custom AttributeAccessor
        // NB - this code is NOt in it own initNNN method, cannot be overridden 
        String tns = dbwsAdapter.getExtendedSchema().getTargetNamespace();
        Project oxProject = dbwsAdapter.getOXSession().getProject();
        XMLDescriptor invocationDescriptor = new XMLDescriptor();
        invocationDescriptor.setJavaClass(Invocation.class);
        NamespaceResolver nr = new NamespaceResolver();
        invocationDescriptor.setNamespaceResolver(nr);
        nr.put(SERVICE_NAMESPACE_PREFIX, tns);
        nr.setDefaultNamespaceURI(tns);
        XMLAnyCollectionMapping parametersMapping = new XMLAnyCollectionMapping();
        parametersMapping.setAttributeName("parameters");
        parametersMapping.setAttributeAccessor(new AttributeAccessor() {
            Project oxProject;
            DBWSAdapter dbwsAdapter;
            @Override
            public Object getAttributeValueFromObject(Object object) {
              return ((Invocation)object).getParameters();
            }
            @Override
            public void setAttributeValueInObject(Object object, Object value) {
                Invocation invocation = (Invocation)object;
                Vector values = (Vector)value;
                for (Iterator i = values.iterator(); i.hasNext();) {
                  /* scan through values:
                   *  if XML conforms to something mapped, it an object; else it is a DOM Element
                   *  (probably a scalar). Walk through operations for the types, converting
                   *   as required. The 'key' is the local name of the element - for mapped objects,
                   *   have to get the element name from the schema context for the object
                   */
                  Object o = i.next();
                  if (o instanceof Element) {
                    Element e = (Element)o;
                    String key = e.getLocalName();
                    if ("theInstance".equals(key)) {
                        NodeList nl = e.getChildNodes();
                        for (int j = 0; j < nl.getLength(); j++) {
                            Node n = nl.item(j);
                            if (n.getNodeType() == Node.ELEMENT_NODE) {
                                try {
                                    Object theInstance = 
                                        dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(n);
                                    if (theInstance instanceof XMLRoot) {
                                        theInstance = ((XMLRoot)theInstance).getObject();
                                    }
                                    invocation.setParameter(key, theInstance);
                                    break;
                                }
                                catch (XMLMarshalException xmlMarshallException) {
                                   throw new WebServiceException(xmlMarshallException);
                                }
                            }
                        }
                    }
                    else {
                        ClassDescriptor desc = null;
                        for (XMLDescriptor xdesc : (Vector<XMLDescriptor>)oxProject.getOrderedDescriptors()) {
                            XMLSchemaReference schemaReference = xdesc.getSchemaReference();
                            if (schemaReference != null && 
                                schemaReference.getSchemaContext().equalsIgnoreCase(key)) {
                                desc = xdesc;
                                break;
                            }
                        }
                        if (desc != null) {
                            try {
                                Object theObject = 
                                    dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(e,
                                        desc.getJavaClass());
                                if (theObject instanceof XMLRoot) {
                                    theObject = ((XMLRoot)theObject).getObject();
                                }
                                invocation.setParameter(key, theObject);
                            }
                            catch (XMLMarshalException xmlMarshallException) {
                               throw new WebServiceException(xmlMarshallException);
                            }
                        }
                        else {
                            String serviceName = e.getParentNode().getLocalName();
                            boolean found = false;
                            for (Operation op : dbwsAdapter.getOperationsList()) {
                                if (op.getName().equals(serviceName)) {
                                    for (Parameter p : op.getParameters()) {
                                        if (p.getName().equals(key)) {
                                            desc = dbwsAdapter.getDescriptorsByQName().get(p.getType());
                                            if (desc != null) {
                                                found = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (found) {
                                    break;
                                }
                            }
                            if (found) {
                                Object theObject = 
                                    dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(e,
                                        desc.getJavaClass());
                                if (theObject instanceof XMLRoot) {
                                    theObject = ((XMLRoot)theObject).getObject();
                                }
                                invocation.setParameter(key, theObject);
                            }
                            else {
                                // cant use e.getTextContent() - some DOM impls dont support it :-(
                                //String val = e.getTextContent();
                                StringBuffer sb = new StringBuffer();
                                NodeList childNodes = e.getChildNodes();
                                for(int idx=0; idx < childNodes.getLength(); idx++ ) {
                                    if (childNodes.item(idx).getNodeType() == Node.TEXT_NODE ) {
                                        sb.append(childNodes.item(idx).getNodeValue());
                                    }
                                }
                                invocation.setParameter(key, sb.toString());
                            }
                        }
                    }
                  }
                  else {
                    XMLDescriptor descriptor = (XMLDescriptor)oxProject.getDescriptor(o.getClass());
                    String key = descriptor.getDefaultRootElement();
                    int idx = key.indexOf(':');
                    if (idx != -1) {
                      key = key.substring(idx+1);
                    }
                    invocation.setParameter(key, o);
                  }
                }
            }
            public AttributeAccessor setProjectAndAdapter(Project oxProject, DBWSAdapter dbwsAdapter) {
              this.oxProject = oxProject;
              this.dbwsAdapter = dbwsAdapter;
              return this;
            }
        }.setProjectAndAdapter(oxProject, dbwsAdapter));
        parametersMapping.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
        invocationDescriptor.addMapping(parametersMapping);
        oxProject.addDescriptor(invocationDescriptor);
        ((DatabaseSessionImpl)dbwsAdapter.getOXSession()).initializeDescriptorIfSessionAlive(invocationDescriptor);
        dbwsAdapter.getXMLContext().storeXMLDescriptorByQName(invocationDescriptor);

        // create SOAP message response handler of appropriate version
        responseWriter = new SOAPResponseWriter(dbwsAdapter);
        responseWriter.initialize();
    }

    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    public SOAPMessage invoke(SOAPMessage request) {
        Map<String,DataHandler> attachments = null;
        if (mtomEnabled) {
            attachments = (Map<String, DataHandler>)mc.get(INBOUND_MESSAGE_ATTACHMENTS);
        }
        SOAPMessage response = null;
        boolean usesSOAP12 = false;
        DBWSAdapter dbwsAdapter = (DBWSAdapter)xrService;

        SOAPEnvelope envelope = null;
        try {
            envelope = request.getSOAPPart().getEnvelope();
        }
        catch (SOAPException se) {
            throw new WebServiceException(se.getMessage(), se);
        }
        // check soap 1.2 Namespace in envelope
        String namespaceURI = envelope.getNamespaceURI();
        usesSOAP12 = namespaceURI.equals(URI_NS_SOAP_1_2_ENVELOPE);

        SOAPElement body;
        try {
            body = getSOAPBodyElement(envelope);
        }
        catch (SOAPException se) {
            throw new WebServiceException(se.getMessage(), se);
        }

        if (body == null) {
            SOAPFault soapFault = null;
            try {
                SOAPFactory soapFactory = null;
                if (usesSOAP12) {
                    soapFactory = SOAPFactory.newInstance(SOAP_1_2_PROTOCOL);
                }
                else {
                    soapFactory = SOAPFactory.newInstance();
                }
                QName clientQName = null;
                if (usesSOAP12) {
                    clientQName = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Client");
                }
                else {
                    clientQName = new QName(URI_NS_SOAP_1_1_ENVELOPE, "Client");
                }
                soapFault =
                    soapFactory.createFault("SOAPMessage request format error - missing body element",
                        clientQName);
            }
            catch (SOAPException se) {
                /* safe to ignore */
            }
            throw new SOAPFaultException(soapFault);
        }

        XMLRoot xmlRoot = null;
        try {
            XMLContext xmlContext = dbwsAdapter.getXMLContext();
            XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
            if (attachments != null && attachments.size() > 0) {
                unmarshaller.setAttachmentUnmarshaller(new XMLAttachmentUnmarshaller() {
                    Map<String,DataHandler> attachments;
                    public XMLAttachmentUnmarshaller setAttachments(Map<String, DataHandler> attachments) {
                        this.attachments = attachments;
                        return this;
                    }
                    public boolean isXOPPackage() {
                        return true;
                    }
                    public DataHandler getAttachmentAsDataHandler(String id) {
                        // strip off 'cid:' (Is this needed?)
                        String attachmentRefId = id;
                        if (attachmentRefId.startsWith("cid:")) {
                            attachmentRefId = attachmentRefId.substring(4);
                        }
                        return attachments.get(attachmentRefId);
                    }
                    public byte[] getAttachmentAsByteArray(String id) {
                        ByteArrayOutputStream out = null;
                        try {
                            DataHandler dh = attachments.get(id);
                            if (dh == null) {
                                return null;
                            }
                            InputStream in = dh.getInputStream();
                            out = new ByteArrayOutputStream(1024);
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                              out.write(buf, 0, len);
                            }
                        }
                        catch (IOException e) {
                            // e.printStackTrace();
                        }
                        if (out != null) {
                            return out.toByteArray();
                        }
                        return null;
                    }
                }.setAttachments(attachments));
                dbwsAdapter.setCurrentAttachmentUnmarshaller(unmarshaller.getAttachmentUnmarshaller());
            }
            xmlRoot = (XMLRoot)unmarshaller.unmarshal(body, Invocation.class);
        }
        catch (Exception e) {
            SOAPFault soapFault = null;
            try {
                SOAPFactory soapFactory = null;
                if (usesSOAP12) {
                    soapFactory = SOAPFactory.newInstance(SOAP_1_2_PROTOCOL);
                }
                else {
                    soapFactory = SOAPFactory.newInstance();
                }
                QName clientQName = null;
                if (usesSOAP12) {
                    clientQName = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Client");
                }
                else {
                    clientQName = new QName(URI_NS_SOAP_1_1_ENVELOPE, "Client");
                }
                Throwable e1 = e;
                if (e.getCause() != null) {
                    e1 = e.getCause();
                }
                soapFault = soapFactory.createFault("SOAPMessage request format error - " +
                    e1, clientQName);
            }
            catch (SOAPException se) {
                // ignore
            }
            throw new SOAPFaultException(soapFault);
        }

        Invocation invocation = (Invocation)xmlRoot.getObject();
        invocation.setName(xmlRoot.getLocalName());
        Operation op = dbwsAdapter.getOperation(invocation.getName());
        /*
         * Fix up types for arguments - scan the extended schema for the operation's Request type.
         *
         * For most parameters, the textual node content is fine, but for date/time and
         * binary objects, we must convert
         */
        org.eclipse.persistence.internal.oxm.schema.model.Element invocationElement =
          (org.eclipse.persistence.internal.oxm.schema.model.Element)
           dbwsAdapter.getExtendedSchema().getTopLevelElements().get(invocation.getName());
        String typeName = invocationElement.getType();
        int idx = typeName.indexOf(':');
        if (idx != -1) {
          // strip-off any namespace prefix
          typeName = typeName.substring(idx+1);
        }
        ComplexType complexType =
          (ComplexType)dbwsAdapter.getExtendedSchema().getTopLevelComplexTypes().get(typeName);
        if (complexType.getSequence() != null) {
            // for each operation, there is a corresponding top-level Request type
            // which has the arguments to the operation
            for (Iterator i = complexType.getSequence().getOrderedElements().iterator(); i .hasNext();) {
                org.eclipse.persistence.internal.oxm.schema.model.Element e =
                (org.eclipse.persistence.internal.oxm.schema.model.Element)i.next();
              String argName = e.getName();
              Object argValue = invocation.getParameter(argName);
              String argType = e.getType();
              if (argType != null) {
                 String argTypePrefix = null;
                 String nameSpaceURI = null;
                 idx = argType.indexOf(':');
                 if (idx != -1) {
                   argTypePrefix = argType.substring(0,idx);
                   argType = argType.substring(idx+1);
                   nameSpaceURI =
                     dbwsAdapter.getSchema().getNamespaceResolver().resolveNamespacePrefix(argTypePrefix);
                 }
                 QName argQName = argTypePrefix == null ? new QName(nameSpaceURI, argType) :
                     new QName(nameSpaceURI, argType, argTypePrefix);
                 Class<?> clz = SCHEMA_2_CLASS.get(argQName);
                 if (clz != null) {
                   argValue = ((XMLConversionManager)dbwsAdapter.getOXSession().getDatasourcePlatform().
                     getConversionManager()).convertObject(argValue, clz, argQName);
                   invocation.setParameter(argName, argValue);
                 }
              }
              // incoming attachments ?
            }
        }
        Object result = null;
        try {
            result = op.invoke(dbwsAdapter, invocation);
            if (result instanceof ValueObject) {
                result = ((ValueObject)result).value;
            }
            response = responseWriter.generateResponse(op, usesSOAP12, result);
        }
        catch (SOAPException se) {
            throw new WebServiceException(se.getMessage(), se);
        }
        catch (Exception e) {
            try {
                response = responseWriter.generateResponse(op, usesSOAP12, e);
            }
            catch (SOAPException soape1) {
                SOAPFault soapFault = null;
                try {
                    SOAPFactory soapFactory = null;
                    if (usesSOAP12) {
                        soapFactory = SOAPFactory.newInstance(SOAP_1_2_PROTOCOL);
                    }
                    else {
                        soapFactory = SOAPFactory.newInstance();
                    }
                    QName serverQName = null;
                    if (usesSOAP12) {
                        serverQName = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Server");
                    }
                    else {
                        serverQName = new QName(URI_NS_SOAP_1_1_ENVELOPE, "Server");
                    }
                    soapFault = soapFactory.createFault("SOAPMessage response error - " + 
                        e.getMessage(), serverQName);
                }
                catch (SOAPException soape2) {
                    // ignore
                }
                throw new SOAPFaultException(soapFault);
            }
        }
        return response;
    }

    public void destroy() {
        logoutSessions();
        responseWriter = null;
        try {
            xrSchemaStream.close();
        }
        catch (IOException ioe) {
            /* safe to ignore */
        }
        xrSchemaStream = null;
        parentClassLoader = null;
        xrService.setXMLContext(null);
        xrService = null;
    }

    @Override
    public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {
        xrService = new DBWSAdapter(); // use subclass to hold extended WSDL schema
        DBWSAdapter dbws = (DBWSAdapter)xrService;
        dbws.setName(xrServiceModel.getName());
        dbws.setSessionsFile(xrServiceModel.getSessionsFile());
        dbws.setOperations(xrServiceModel.getOperations());
        initializeService(parentClassLoader, xrSchemaStream);
        return dbws;
    }

    public static SOAPElement getSOAPBodyElement(SOAPEnvelope envelope) throws SOAPException {
        NodeList nodes = envelope.getBody().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof SOAPBodyElement) {
                return (SOAPElement)node;
            }
        }
        return null;
    }

}