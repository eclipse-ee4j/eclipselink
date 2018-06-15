/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.oxm.Constants.ANY;
import static org.eclipse.persistence.internal.oxm.Constants.ANY_QNAME;
import static org.eclipse.persistence.internal.xr.Util.ALL_QUERYNAME;
import static org.eclipse.persistence.internal.xr.Util.COLON_CHAR;
import static org.eclipse.persistence.internal.xr.Util.DASH_STR;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.PK_QUERYNAME;
import static org.eclipse.persistence.internal.xr.Util.SLASH_CHAR;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.TYPE_STR;
import static org.eclipse.persistence.internal.xr.Util.UNDERSCORE_STR;

//javase imports
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
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
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ServerSession;

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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
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
 * </pre>
 */
public class XRServiceFactory  {
    public XRServiceAdapter xrService;
    public ClassLoader parentClassLoader;
    public InputStream xrSchemaStream;

    static final String XMLNS_STR = "xmlns";
    static final String XML_BINDINGS_STR = "xml-bindings";
    static final String ORM_MAPPINGS_STR = "orm-mappings";
    static final String DOM_PLATFORM_CLASSNAME = "org.eclipse.persistence.oxm.platform.DOMPlatform";
    static final String OXM_PROCESSING_EX = "An exception occurred processing OXM metadata";
    static final String ORM_PROCESSING_EX = "An exception occurred processing ORM metadata";
    static final String OXM_PROCESSING_SCH = "An exception occurred processing schema definitions";
    static final String OX_PRJ_SUFFIX = "-dbws-ox";
    static final String OR_PRJ_SUFFIX = "-dbws-or";
    static final String DEFAULT_PROJECT_NAME = "org.eclipse.persistence.sessions.Project";
    static final String COLLECTION_WRAPPER_STR = UNDERSCORE_STR + org.eclipse.persistence.internal.xr.Util.COLLECTION_WRAPPER_STR;
    static final String SIMPLE_XML_FORMAT_STR = "simple-xml-format";

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
     * Read and unmarshal <code>XRService</code>'s <tt>.xsd</tt> file.
     * @param xrSchemaStream Stream resource for the <code>XRService</code>'s <tt>.xsd</tt> file.
     */
    public void loadXMLSchema(InputStream xrSchemaStream) {
        SchemaModelProject schemaProject = new SchemaModelProject();
        XMLContext xmlContext = new XMLContext(schemaProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        Schema schema;
        try {
            schema = (Schema)unmarshaller.unmarshal(xrSchemaStream);
        } catch (XMLMarshalException e) {
            xmlContext.getSession().getSessionLog().log(
                    SessionLog.WARNING, SessionLog.DBWS, "dbws_xml_schema_read_error", e.getLocalizedMessage());
            throw new DBWSException(OXM_PROCESSING_SCH, e);
        }
        NamespaceResolver nr = schema.getNamespaceResolver();
        String targetNamespace = schema.getTargetNamespace();
        nr.put(TARGET_NAMESPACE_PREFIX, targetNamespace);
        xrService.schema = schema;
        xrService.schemaNamespace = targetNamespace;
    }

    /**
     * <p>INTERNAL:
     * Create a Project using ORM metadata.  The given classloader is expected
     * to successfully load 'META-INF/eclipselink-dbws-or.xml'.
     * @param xrdecl  {@link ClassLoader} used to search for {@code eclipselink-dbws-or.xml}.
     * @param session ORM session.
     */
    protected Project loadORMetadata(final XRDynamicClassLoader xrdecl, final ServerSession session) {
        Project orProject = null;
        String searchPath = null;
        InputStream inStream = null;

        // try "META-INF/" and "/META-INF/"
        for (String prefix : META_INF_PATHS) {
            searchPath = prefix + Util.DBWS_OR_XML;
            inStream = xrdecl.getResourceAsStream(searchPath);
            if (inStream != null) {
                break;
            }
        }

        try {
            if (inStream != null) {
                MetadataProcessor processor = new MetadataProcessor(new XRPersistenceUnitInfo(xrdecl),
                        session, xrdecl, false, true, false, false, false, null, null);
                processor.setMetadataSource(new JPAMetadataSource(xrdecl, new InputStreamReader(inStream)));
                PersistenceUnitProcessor.processORMetadata(processor, true, PersistenceUnitProcessor.Mode.ALL);
                processor.addNamedQueries();
                orProject = processor.getProject().getProject();
                orProject.setName(xrService.getName().concat(OR_PRJ_SUFFIX));
            }
        } catch (Exception pupex) {
            /* could be legacy project, or none set, so just log */
            session.log(SessionLog.FINE, SessionLog.DBWS, "dbws_orm_metadata_read_error", pupex.getLocalizedMessage());
        }
        return orProject;
    }

    /**
     * <p>INTERNAL:
     * Create a Project using OXM metadata.  The given classloader is expected
     * to successfully load 'META-INF/eclipselink-dbws-ox.xml'.
     * @param xrdecl  {@link ClassLoader} used to search for {@code eclipselink-dbws-ox.xml}.
     * @param session OXM session (only for logging).
     */
    protected Project loadOXMetadata(final ClassLoader xrdecl, final Session session) {
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
            Map<String, OXMMetadataSource> metadataMap = null;
            StreamSource xml = new StreamSource(inStream);
            try {
                JAXBContext jc = JAXBContext.newInstance(XmlBindingsModel.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();

                JAXBElement<XmlBindingsModel> jaxbElt = unmarshaller.unmarshal(xml, XmlBindingsModel.class);
                XmlBindingsModel model = jaxbElt.getValue();
                if (model.getBindingsList() != null) {
                    metadataMap = new HashMap<String, OXMMetadataSource>();
                    for (XmlBindings xmlBindings : model.getBindingsList()) {
                        metadataMap.put(xmlBindings.getPackageName(), new OXMMetadataSource(xmlBindings));
                    }
                }
            } catch (JAXBException jaxbex) {
                /* could be legacy project, or none set, so just log */
                session.getSessionLog().log(
                        SessionLog.FINE, SessionLog.DBWS, "dbws_oxm_metadata_read_error", jaxbex.getLocalizedMessage());
                return null;
            }

            if (metadataMap != null) {
                Map<String, Map<String, OXMMetadataSource>> properties = new HashMap<String, Map<String, OXMMetadataSource>>();
                properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataMap);
                try {
                    DynamicJAXBContext jCtx = DynamicJAXBContextFactory.createContextFromOXM(xrdecl, properties);
                    oxProject = jCtx.getXMLContext().getSession(0).getProject();
                    oxProject.setName(xrService.getName().concat(OX_PRJ_SUFFIX));
                } catch (JAXBException e) {
                    throw new DBWSException(OXM_PROCESSING_EX, e);
                }
            }
        }
        return oxProject;
    }

    /**
     * <p>INTERNAL:
     *
     * Perform any post-load descriptor modifications, such as altering attribute classification
     * on a given mapping, or converting class names to classes.  In addition, JAXB and JPA
     * Embeddables will have the descriptor alias set to the class name (w/o package), which
     * will contain an upper case first character, meaning that the OR/OX descriptors will
     * have to be aligned in some cases.
     */
    protected static void prepareDescriptors(Project oxProject, Project orProject, XRDynamicClassLoader xrdcl) {
        // step one:  remove default dummy alias descriptor (i.e. alias = "")
        if (orProject.getAliasDescriptors() != null) {
            orProject.getAliasDescriptors().remove("");
        }

        // step two:  align OR/OX alias names, handle JAXB DataHandler issue, and convert OR class names to classes
        if (oxProject.getAliasDescriptors() != null) {
            for (Object alias : oxProject.getAliasDescriptors().keySet()) {
                if (alias.equals(SIMPLE_XML_FORMAT_STR)) {
                    continue;
                }
                // workaround for JAXB validation:  JAXB expects a DataHandler in the
                // object model for SwaRef, whereas we want to work with a byte[]
                XMLDescriptor xdesc = (XMLDescriptor) oxProject.getAliasDescriptors().get(alias);
                for (DatabaseMapping mapping : xdesc.getMappings()) {
                    if (mapping instanceof XMLBinaryDataMapping) {
                        ((XMLBinaryDataMapping) mapping).setAttributeClassification(APBYTE);
                        ((XMLBinaryDataMapping) mapping).setAttributeClassificationName(APBYTE.getName());
                    }
                }
                // convert class names to classes on the associated OR descriptor
                ClassDescriptor odesc = (ClassDescriptor) orProject.getAliasDescriptors().get(alias);
                if (odesc != null)  {  // shouldn't be null...
                    // step three:  align OR alias and ordered descriptors (alias names and mappings)
                    ClassDescriptor orderedDescriptor = getDescriptorForClassName(orProject, odesc.getJavaClassName());
                    if (orderedDescriptor != null) {
                        orderedDescriptor.setAlias(alias.toString());
                        //orderedDescriptor.setJavaClass(odesc.getJavaClass());
                        orderedDescriptor.setJavaClassName(odesc.getJavaClassName());
                        // need to convert class names to classes such that reference classes, etc are setup
                        orderedDescriptor.convertClassNamesToClasses(xrdcl);
                        // replace the incomplete descriptor in the alias map with this one
                        orProject.addAlias(orderedDescriptor.getAlias(), orderedDescriptor);
                    }
                }
            }
        }
    }

    /**
     * Returns a ClassDescriptor from the given project with the matching javaClassName,
     * or null if not found.
     */
    protected static ClassDescriptor getDescriptorForClassName(Project project, String javaClassName) {
        for (ClassDescriptor cd : project.getOrderedDescriptors()) {
            if (cd != null && cd.getJavaClassName().equals(javaClassName)) {
                return cd;
            }
        }
        return null;
    }

    /**
     * <p>INTERNAL:
     */
    @SuppressWarnings("rawtypes")
    public void buildSessions() {
        XRDynamicClassLoader projectLoader = new XRDynamicClassLoader(parentClassLoader);

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

        // make sure the OR/OX sessions were loaded successfully
        Map sessions = sessionManager.getSessions();
        String orSessionKey = xrService.name + DASH_STR + DBWS_OR_SESSION_NAME_SUFFIX;
        if (!sessions.containsKey(orSessionKey)) {
            throw DBWSException.couldNotLocateORSessionForService(xrService.name);
        }
        String oxSessionKey = xrService.name + DASH_STR + DBWS_OX_SESSION_NAME_SUFFIX;
        if (!sessions.containsKey(oxSessionKey)) {
            throw DBWSException.couldNotLocateOXSessionForService(xrService.name);
        }
        xrService.orSession = (Session) sessions.get(orSessionKey);
        xrService.oxSession = (Session) sessions.get(oxSessionKey);

        // load OX project via xml-bindings
        Project oxProject = null;
        if ((oxProject = loadOXMetadata(projectLoader, xrService.oxSession)) == null) {
            // at this point we may have a legacy deployment XML project, or none set
            oxProject = xrService.oxSession.getProject();
            // check to see if it's a default Project
            if (oxProject.getName().length() == 0) {
                // default to SimpleXMLFormat
                oxProject = new SimpleXMLFormatProject();
            }
        }
        ((XMLLogin) oxProject.getDatasourceLogin()).setPlatformClassName(DOM_PLATFORM_CLASSNAME);
        ((XMLLogin) oxProject.getDatasourceLogin()).setEqualNamespaceResolvers(false);

        // load OR project via entity-mappings
        Project orProject = null;
        if ((orProject = loadORMetadata(projectLoader, (ServerSession) xrService.orSession)) == null) {
            // at this point we may have a legacy deployment XML project, or none set
            orProject = xrService.orSession.getProject();
            updateFindQueryNames(orProject);
        }

        prepareDescriptors(oxProject, orProject, projectLoader);
        ProjectHelper.fixOROXAccessors(orProject, oxProject);

        xrService.xmlContext = new XMLContext(oxProject);
        xrService.oxSession = xrService.xmlContext.getSession(0);
    }

    /**
     * <p>INTERNAL:
     */
    public void loginSessions() {
        ((DatabaseSession)xrService.orSession).login();
        // the oxSession is already logged-in...
    }

    /**
     * <p>INTERNAL:
     */
    @SuppressWarnings("rawtypes")
    public void buildDescriptorIndex() {
        for (Iterator i = xrService.oxSession.getProject().getOrderedDescriptors().iterator();
            i.hasNext();) {
            XMLDescriptor xd = (XMLDescriptor)i.next();
            XMLSchemaReference schemaReference = xd.getSchemaReference();
            if (schemaReference != null && schemaReference.getType() == XMLSchemaReference.COMPLEX_TYPE) {
                String context = schemaReference.getSchemaContext();
                if (context != null && context.lastIndexOf(SLASH_CHAR) == 0) {
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
        @SuppressWarnings("rawtypes")
        Map sessions = manager.getSessions();
        String orSessionName = xrService.name + DASH_STR + DBWS_OR_SESSION_NAME_SUFFIX;
        Session orSession = (Session)sessions.remove(orSessionName);
        if (orSession != null && orSession.isConnected()) {
            ((DatabaseSession)orSession).logout();
        }
        String oxSessionName = xrService.name + DASH_STR + DBWS_OX_SESSION_NAME_SUFFIX;
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
        int index = name.indexOf(COLON_CHAR);
        if (index != -1) {
            String uri = ns.resolveNamespacePrefix(name.substring(0, index));
            return new QName(uri, name.substring(index + 1));
        }
        if (ns.getDefaultNamespaceURI() != null) {
            return new QName(ns.getDefaultNamespaceURI(), name);
        }
        String uri = ns.resolveNamespacePrefix(XMLNS_STR);
        return new QName(uri, name);
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
     * <p>INTERNAL:
     *
     * Legacy projects have 'findAll' and 'findByPrimaryKey' queries, whereas we now
     * expect these to have the descriptor alias appended (preceded with underscore),
     * + 'Type'.  For example, if we have an Employee descriptor, the find queries
     * would be:  'findAll_employeeType' and 'findByPrimaryKey_employeeType'.
     *
     */
    @SuppressWarnings("rawtypes")
    protected static void updateFindQueryNames(Project orProject) {
        for (ClassDescriptor orDesc : orProject.getDescriptors().values()) {
            Vector queries = orDesc.getQueryManager().getAllQueries();
            for (int i=0; i<queries.size(); i++) {
                DatabaseQuery query = (DatabaseQuery) queries.get(i);
                String qName = query.getName();
                String END_PART = UNDERSCORE_STR + query.getDescriptor().getAlias() + TYPE_STR;
                if ((PK_QUERYNAME.equals(qName) || ALL_QUERYNAME.equals(qName)) && !qName.endsWith(END_PART)) {
                    orDesc.getQueryManager().addQuery(qName + END_PART, query);
                }
            }
        }
    }

    // Made static final for performance reasons.
    /**
     * <p>INTERNAL:
     *
     * Implementation of MetadataSource to allow passing XmlBindings
     * to the DynamicJAXBContextFactory
     *
     */
    public static final class OXMMetadataSource implements MetadataSource {
        XmlBindings xmlbindings;

        public OXMMetadataSource(XmlBindings bindings) {
            xmlbindings = bindings;
        }

        @Override
        public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
            return xmlbindings;
        }
    }

    // Made static final for performance reasons.
    /**
     * <p>INTERNAL:
     *
     * Implementation of MetadataSource to allow passing JPA metadata to the
     * MetadataProcessor.
     *
     */
    public static final class JPAMetadataSource implements org.eclipse.persistence.jpa.metadata.MetadataSource {
        XRDynamicClassLoader xrdecl;
        Reader reader;

        public JPAMetadataSource(XRDynamicClassLoader loader, Reader reader) {
            xrdecl = loader;
            this.reader = reader;
        }

        @Override
        public Map<String, Object> getPropertyOverrides(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
            return null;
        }

        @Override
        public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
            return XMLEntityMappingsReader.read(ORM_MAPPINGS_STR, reader, xrdecl, null);
        }
    }

    // Made static final for performance reasons.
    /**
     * <p>INTERNAL:
     *
     * PersistenceUnitInfo implementation to allow creation of a MetadataProcessor
     * instance.  Other than an XRDynamicClassLoader instance, all methods return
     * null, empty lists, etc.
     *
     */
    public static final class XRPersistenceUnitInfo implements PersistenceUnitInfo {
        XRDynamicClassLoader xrdecl;
        public XRPersistenceUnitInfo(XRDynamicClassLoader loader) {
            xrdecl = loader;
        }
        @Override
        public PersistenceUnitTransactionType getTransactionType() { return null; }
        @Override
        public Properties getProperties() { return new Properties(); }
        @Override
        public URL getPersistenceUnitRootUrl() { return null; }
        @Override
        public String getPersistenceUnitName() { return null; }
        @Override
        public String getPersistenceProviderClassName() { return null; }
        @Override
        public DataSource getNonJtaDataSource() { return null; }
        @Override
        public ClassLoader getNewTempClassLoader() { return xrdecl; }
        @Override
        public List<String> getMappingFileNames() { return new ArrayList<String>(); }
        @Override
        public List<String> getManagedClassNames() { return new ArrayList<String>(); }
        @Override
        public DataSource getJtaDataSource() { return null; }
        @Override
        public List<URL> getJarFileUrls() { return new ArrayList<URL>(); }
        @Override
        public ClassLoader getClassLoader() { return xrdecl; }
        @Override
        public boolean excludeUnlistedClasses() { return false; }
        @Override
        public void addTransformer(ClassTransformer arg0) { }
        @Override
        public SharedCacheMode getSharedCacheMode() { return null; }
        @Override
        public ValidationMode getValidationMode() { return null; }
        @Override
        public String getPersistenceXMLSchemaVersion() { return null; }
    }
}
