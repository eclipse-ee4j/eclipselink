/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.xr;

//javase imports
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//java eXtension imports
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;

import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.ANY;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_QNAME;

/**
 * <p><b>INTERNAL</b>: helper class that knows how to build a {@link XRServiceAdapter} (a.k.a DBWS). An
 * <code>XRService</code> requires the following resources:
 * <ul>
 * <li>metadata in the form of a descriptor file called <tt><b>eclipselink-dbws.xml</b></tt><br>
 * </li>
 * <li>an XML Schema Definition (<tt>.xsd</tt>) file called <tt><b>eclipselink-dbws-schema.xsd</b></tt>
 * </li>
 * <li>a TopLink <tt>sessions.xml</tt> file called <tt><b>eclipselink-dbws-sessions.xml</b></tt><br>
 * &nbsp; the naming convention for the <tt>sessions.xml</tt> files can be overriden by the
 * <b>optional</b> <tt>&lt;sessions-file&gt;</tt> entry in the <code>eclipselink-dbws.xml</code>
 * descriptor file.
 * </li>
 * <li>EclipseLink metadata in the form of a EclipseLink {@link Project} (either deployment XML or Java classes).
 * <p>A typical <code>XRService</code> requires two projects: one to represent the O-R side, the other to
 * represent the O-X side.<br>
 * The O-R and O-X <code>Projects</code> metadata must have:<br>
 * i) identical case-sensitive <code>Project</code> names:<pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;eclipselink:object-persistence version="Eclipse Persistence Services ..."
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   xmlns:eclipselink="http://xmlns.oracle.com/ias/xsds/eclipselink"
 *   &gt;
 *   &lt;eclipselink:name&gt;example&lt;/eclipselink:name&gt;
 * or
 * ...
 * import org.eclipse.persistence.sessions.Project;
 * public class SomeORProject extends Project {
 *   public SomeORProject () {
 *     setName("Example");
 *     ...
 * }
 * public class SomeOXProject extends Project {
 *   public SomeOXProject () {
 *     setName("Example");
 *     ...
 * }
 * </pre>
 * ii) identical case-sensitive aliases for {@link ClassDescriptor Descriptors} that are common
 * between the projects:
 * <pre>
 * &lt;eclipselink:class-mapping-descriptor xsi:type="eclipselink:relational-class-mapping-descriptor"&gt;
 *   &lt;eclipselink:class&gt;some.package.SomeClass&lt;/eclipselink:class&gt;
 *   &lt;eclipselink:alias&gt;SomeAlias&lt;/eclipselink:alias&gt;
 * ...
 * &lt;eclipselink:class-mapping-descriptor xsi:type="eclipselink:xml-class-mapping-descriptor"&gt;
 *   &lt;eclipselink:class&gt;some.package.SomeClass&lt;/eclipselink:class&gt;
 *   &lt;eclipselink:alias&gt;SomeAlias&lt;/eclipselink:alias&gt;
 * </pre>
 * </li>
 * </ul>
 * An example <tt><b>eclipselink-dbws.xml</b></tt> descriptor file:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;dbws
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   &gt;
 *   &lt;name&gt;example&lt;/name&gt;
 *   &lt;sessions-file&gt;example-dbws-sessions.xml&lt;/sessions-file&gt;
 *   &lt;query&gt;
 *     &lt;name&gt;countEmployees&lt;/name&gt;
 *     &lt;result&gt;
 *       &lt;type&gt;xsd:int&lt;/type&gt;
 *       &lt;simple-xml-format&gt;
 *         &lt;simple-xml-format-tag&gt;employee-info&lt;/simple-xml-format-tag&gt;
 *         &lt;simple-xml-tag&gt;aggregate-info&lt;/simple-xml-tag&gt;
 *       &lt;/simple-xml-format&gt;
 *     &lt;/result&gt;
 *     &lt;sql&gt;&lt;![CDATA[select count(*) from EMP]]&gt;&lt;/sql&gt;
 *   &lt;/query&gt;
 *   &lt;query&gt;
 *     &lt;name&gt;findAllEmployees&lt;/name&gt;
 *     &lt;result isCollection="true"&gt;
 *       &lt;type&gt;empType&lt;/type&gt;
 *     &lt;/result&gt;
 *     &lt;sql&gt;&lt;![CDATA[select * from EMP]]&gt;&lt;/sql&gt;
 *   &lt;/query&gt;
 * &lt;/dbws&gt;
 */
@SuppressWarnings({"unchecked"/*, "rawtypes"*/})
public class XRServiceFactory  {
    public XRServiceAdapter xrService;
    public ClassLoader parentClassLoader;
    public InputStream xrSchemaStream;

    static final String TYPE_STR = "Type";
    static final String XML_BINDINGS_STR = "xml-bindings";
    static final String DOM_PLATFORM_CLASSNAME = "org.eclipse.persistence.oxm.platform.DOMPlatform";
    static final String OXM_PROCESSING_EX = "An exception occurred processing OXM metadata";
    
    public XRServiceFactory() {
        super();
    }

    public XRServiceAdapter buildService() {
        initializeService(parentClassLoader, xrSchemaStream);
        return xrService;
    }

    public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {
        xrService = new XRServiceAdapter();
        xrService.setName(xrServiceModel.getName());
        xrService.setSessionsFile(xrServiceModel.getSessionsFile());
        xrService.setOperations(xrServiceModel.getOperations());
        initializeService(parentClassLoader, xrSchemaStream);
        return xrService;
    }

    /**
     * <p><b>INTERNAL</b>: Initialize the various components (of the <code>XRService</code>}
     * (O-R Project, O-X Project, Schema definitions, auto-generated classes, etc.)
     * @param parentClassLoader  the parent <code>ClassLoader</code> for the auto-generated classes.
     * @param xrSchemaStream   stream resource for the <code>XRService</code>'s <tt>.xsd</tt> file.
     */
    public void initializeService(ClassLoader parentClassLoader, InputStream xrSchemaStream) {
        this.parentClassLoader = parentClassLoader;
        this.xrSchemaStream = xrSchemaStream;
        loadXMLSchema(xrSchemaStream);
        logoutSessions();
        buildSessions();
        customizeSession(xrService.orSession, xrService.oxSession);
        buildDescriptorIndex();
        validateOperations();
        initializeOperations();
        loginSessions();
    }

    @SuppressWarnings("unused")
    public void customizeSession(Session orSession, Session oxSession) {
    }

    /**
     * <p>INTERNAL:
     *
     */
    public void loadXMLSchema(InputStream xrSchemaStream) {
        SchemaModelProject schemaProject = new SchemaModelProject();
        XMLContext xmlContext = new XMLContext(schemaProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        Schema schema = (Schema)unmarshaller.unmarshal(xrSchemaStream);
        NamespaceResolver nr = schema.getNamespaceResolver();
        String targetNamespace = schema.getTargetNamespace();
        nr.put(TARGET_NAMESPACE_PREFIX, targetNamespace);
        xrService.schema = schema;
        xrService.schemaNamespace = targetNamespace;
    }
    
    /**
     * <p>INTERNAL:
     * Create a Project using OXM metadata.  The given classloader is expected 
     * to successfully load 'META-INF/eclipselink-dbws-ox.xml'.
     */
    protected Project loadOXMetadata(ClassLoader xrdecl) {
        Project oxProject = null;
        InputStream inStream = null;
        String searchPath = null;
        
        // try "META-INF/" and "/META-INF/"
        for (String prefix : META_INF_PATHS) {
            searchPath = prefix + Util.DBWS_OX_XML;
            inStream = xrdecl.getResourceAsStream(searchPath);
            if (inStream != null) {
                break;
            }
        }
        if (inStream != null) {
            Map<String, DBWSMetadataSource> metadataMap = null;
            StreamSource xml = new StreamSource(inStream);
            try {
                JAXBContext jc = JAXBContext.newInstance(XmlBindingsModel.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                
                JAXBElement<XmlBindingsModel> jaxbElt = unmarshaller.unmarshal(xml, XmlBindingsModel.class);
                XmlBindingsModel model = jaxbElt.getValue();
                if (model.getBindingsList() != null) {
                    metadataMap = new HashMap<String, DBWSMetadataSource>();
                    for (XmlBindings xmlBindings : model.getBindingsList()) {
                        metadataMap.put(xmlBindings.getPackageName(), new DBWSMetadataSource(xmlBindings));
                    }
                }
            } catch (JAXBException jaxbex) {
                throw new DBWSException(OXM_PROCESSING_EX, jaxbex);
            }
            
            if (metadataMap != null) {
                Map<String, Map<String, DBWSMetadataSource>> properties = new HashMap<String, Map<String, DBWSMetadataSource>>();
                properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataMap);
                try {
                    DynamicJAXBContext jCtx = DynamicJAXBContextFactory.createContextFromOXM(xrdecl, properties);
                    oxProject = jCtx.getXMLContext().getSession(0).getProject();
        
                    // may need to alter descriptor alias
                    if (oxProject.getAliasDescriptors() != null) {
                        Map<String, ClassDescriptor> aliasDescriptors = new HashMap<String, ClassDescriptor>();
                        for (Object key : oxProject.getAliasDescriptors().keySet()) {
                            XMLDescriptor xdesc = (XMLDescriptor) oxProject.getAliasDescriptors().get(key.toString());
                            
                            String defaultRootElement = xdesc.getDefaultRootElement();
                            String proposedAlias = defaultRootElement;
                            if (proposedAlias.endsWith(TYPE_STR)) {
                                proposedAlias = proposedAlias.substring(0, proposedAlias.lastIndexOf(TYPE_STR));
                            }
                            xdesc.setAlias(proposedAlias);
                            aliasDescriptors.put(proposedAlias, xdesc);
                            
                            // workaround for JAXB validation:  JAXB expects a DataHandler in the 
                            // object model for SwaRef, whereas we want to work with a byte[]
                            for (DatabaseMapping mapping : xdesc.getMappings()) {
                                if (mapping instanceof XMLBinaryDataMapping) {
                                    ((XMLBinaryDataMapping) mapping).setAttributeClassification(APBYTE);
                                    ((XMLBinaryDataMapping) mapping).setAttributeClassificationName(APBYTE.getName());
                                }
                            }
                        }
                        oxProject.setAliasDescriptors(aliasDescriptors);
                    }
                } catch (JAXBException e) {
                    throw new DBWSException(OXM_PROCESSING_EX, e);
                }
            }
        }
        return oxProject; 
    }
    
    /**
     * <p>INTERNAL:
     */
    public void buildSessions() {
        ClassLoader projectLoader = new XRDynamicClassLoader(parentClassLoader);

        SessionManager sessionManager = SessionManager.getManager();
        boolean found = false;
        String sessionsFile = xrService.sessionsFile == null ? DBWS_SESSIONS_XML : xrService.sessionsFile;
        for (String prefix : META_INF_PATHS) {
            String searchPath = prefix + sessionsFile;
            XRSessionConfigLoader loader = new XRSessionConfigLoader(searchPath);
            loader.setShouldLogin(false);
            try {
                found = loader.load(sessionManager, projectLoader);
            } catch (RuntimeException e) { /* ignore */
            }
            if (found) {
                break;
            }
        }
        if (!found) {
            throw DBWSException.couldNotLocateFile(DBWS_SESSIONS_XML);
        }
        Map sessions = sessionManager.getSessions();
        String orSessionKey = xrService.name + "-" + DBWS_OR_SESSION_NAME_SUFFIX;
        if (sessions.containsKey(orSessionKey)) {
            xrService.orSession = (Session)sessions.get(orSessionKey);
        } else {
            throw DBWSException.couldNotLocateORSessionForService(xrService.name);
        }

        Project oxProject = null;
        // load OX project via xml-bindings
        if ((oxProject = loadOXMetadata(projectLoader)) == null) {
            // at this point we may have a legacy deployment XML project
            String oxSessionKey = xrService.name + "-" + DBWS_OX_SESSION_NAME_SUFFIX;
            if (sessions.containsKey(oxSessionKey)) {
                xrService.oxSession = (Session)sessions.get(oxSessionKey);
                ((XMLLogin)xrService.oxSession.getDatasourceLogin()).setEqualNamespaceResolvers(false);
                oxProject = xrService.oxSession.getProject(); 
            }
            if (oxProject == null) {
                oxProject = new SimpleXMLFormatProject();
            }
        }
        ProjectHelper.fixOROXAccessors(xrService.orSession.getProject(), oxProject);
        ((XMLLogin)oxProject.getDatasourceLogin()).setPlatformClassName(DOM_PLATFORM_CLASSNAME);
        ((XMLLogin)oxProject.getDatasourceLogin()).setEqualNamespaceResolvers(false);
        xrService.xmlContext = new XMLContext(oxProject);
        xrService.oxSession = xrService.xmlContext.getSession(0);
    }

    /**
     * <p>INTERNAL:
     */
    public void loginSessions() {
        ((DatabaseSession)xrService.orSession).login();
        // the 'weird' stuff above with XMLContext results in the oxSession begin already logged-in
        //((DatabaseSession)xrService.oxSession).login();
    }

    /**
     * <p>INTERNAL:
     */
    public void buildDescriptorIndex() {
        for (Iterator i = xrService.oxSession.getProject().getOrderedDescriptors().iterator();
            i.hasNext();) {
            XMLDescriptor xd = (XMLDescriptor)i.next();
            XMLSchemaReference schemaReference = xd.getSchemaReference();
            if (schemaReference != null && schemaReference.getType() == XMLSchemaReference.COMPLEX_TYPE) {
                String context = schemaReference.getSchemaContext();
                if (context != null && context.lastIndexOf('/') == 0) {
                    String elementNameNS = context.substring(1);
                    QName elementName = resolveName(elementNameNS, xd.getNamespaceResolver());
                    if (elementName == null) {
                        continue;
                    }
                    xrService.descriptorsByQName.put(elementName, xd);
                }
            }
        }
    }

    /**
     * <p>INTERNAL:
     */
    public void validateOperations() {
        for (Operation operation : xrService.getOperationsList()) {
            operation.validate(xrService);
        }
    }

    /**
     * <p>INTERNAL:
     */
    public void initializeOperations() {
        for (Operation operation : xrService.getOperationsList()) {
            operation.initialize(xrService);
        }
    }

    /**
     * <p>INTERNAL:
     */
    protected void logoutSessions() {
        SessionManager manager = SessionManager.getManager();
        Map sessions = manager.getSessions();
        String orSessionName = xrService.name + "-" + DBWS_OR_SESSION_NAME_SUFFIX;
        Session orSession = (Session)sessions.remove(orSessionName);
        if (orSession != null && orSession.isConnected()) {
            ((DatabaseSession)orSession).logout();
        }
        String oxSessionName = xrService.name + "-" + DBWS_OX_SESSION_NAME_SUFFIX;
        sessions.remove(oxSessionName);
        xrService.orSession = null;
        xrService.oxSession = null;
    }

    /**
     * <p>INTERNAL:
     *
     */
    protected QName resolveName(String name, NamespaceResolver ns) {
        if (ns == null) {
            return null;
        }
        if (ANY.equals(name)) {
            return ANY_QNAME;
        }
        int index = name.indexOf(':');
        if (index != -1) {
            String uri = ns.resolveNamespacePrefix(name.substring(0, index));
            return new QName(uri, name.substring(index + 1));
        }
        else if (ns.getDefaultNamespaceURI() != null) {
                return new QName(ns.getDefaultNamespaceURI(), name);
            }
        else {
            String uri = ns.resolveNamespacePrefix("xmlns");
            return new QName(uri, name);
        }
    }

    public static DocumentBuilder getDocumentBuilder() {
        DocumentBuilder db = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            /* extremely rare, safe to ignore */
        }
        return db;
    }

    public static Transformer getTransformer() {
        Transformer transformer = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            transformer = tf.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            /* extremely rare, safe to ignore */
        }
        return transformer;
    }

    /**
     * Implementation of MetadataSource to allow passing XmlBindings
     * to the DynamicJAXBContextFactory
     *
     */
    public class DBWSMetadataSource implements MetadataSource {
        XmlBindings xmlbindings;
        
        public DBWSMetadataSource(XmlBindings bindings) {
            xmlbindings = bindings;
        }
        
        @Override
        public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
            return xmlbindings;
        }
    }
}
