/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

// Javase imports
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

// Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.NULL_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.SIMPLE_XML_FORMAT_TYPE;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;

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
public class XRServiceFactory  {

    public XRServiceAdapter xrService;
    public ClassLoader parentClassLoader;
    public InputStream xrSchemaStream;

    public XRServiceFactory() {
        super();
    }

    public XRServiceAdapter buildService() {

        // sub-classes override with specific behaviour

        initializeService(parentClassLoader, xrSchemaStream);
        return xrService;
    }

    public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {

        // sub-classes override with specific behaviour

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
        loginSessions();
        buildDescriptorIndex();
        validateOperations();
        initializeOperations();
    }
    
    @SuppressWarnings("unused") 
    public void customizeSession(Session orSession, Session oxSession) {
    }

    /**
     * <p>INTERNAL:
     *
     */
    @SuppressWarnings("unchecked")
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
        for (Iterator i = schema.getTopLevelComplexTypes().keySet().iterator(); i.hasNext();) {
            String typeLocal = (String)i.next();
            QName typeName = null;
            // special case simple-xml-format
            if (typeLocal.equals(SIMPLE_XML_FORMAT_TYPE)) {
                typeName = new QName(typeLocal);
            }
            else {
                typeName = new QName(targetNamespace, typeLocal);
            }
            xrService.schemaTypes.add(typeName);
        }
        for (Iterator i = schema.getTopLevelElements().keySet().iterator(); i.hasNext();) {
            String elementNameLocal = (String)i.next();
            QName elementName = null;
            // special case simple-xml-format
            if (elementNameLocal.equals(DEFAULT_SIMPLE_XML_FORMAT_TAG)) {
                elementName = new QName(elementNameLocal);
            }
            else {
                elementName = new QName(targetNamespace, elementNameLocal);
            }
            Element element = (Element)schema.getTopLevelElements().get(elementNameLocal);
            // ignore elements with inline complex types
            if (element.getComplexType() != null) {
                continue;
            }
            String typeNameNS = element.getType();
            QName typeName = resolveName(typeNameNS, nr);
            if (elementName.getNamespaceURI() != null && elementName.getNamespaceURI() != NULL_NS_URI) {
                typeName = new QName(elementName.getNamespaceURI(), typeName.getLocalPart());
            }
            // ignore elements that reference types outside of this schema
            if (!xrService.schemaTypes.contains(typeName)) {
                continue;
            }
            xrService.elementTypes.put(elementName, typeName);
        }
    }

    /**
     * <p>INTERNAL:
     */
    @SuppressWarnings("unchecked")
    public void buildSessions() {

        ClassLoader projectLoader = new BaseEntityClassLoader(parentClassLoader);
        SessionManager sessionManager = SessionManager.getManager();
        boolean found = false;
        String sessionsFile =
            xrService.sessionsFile == null ? DBWS_SESSIONS_XML : xrService.sessionsFile;
        for (String prefix : META_INF_PATHS) {
            String searchPath = prefix + sessionsFile;
            XRSessionConfigLoader loader = new XRSessionConfigLoader(searchPath);
            loader.setShouldLogin(false);
            try {
                found = loader.load(sessionManager, projectLoader);
            }
            catch (RuntimeException e) { /* ignore */
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
        }
        else {
            throw DBWSException.couldNotLocateORSessionForService(xrService.name);
        }
        String oxSessionKey = xrService.name + "-" + DBWS_OX_SESSION_NAME_SUFFIX;
        if (sessions.containsKey(oxSessionKey)) {
            xrService.oxSession = (Session)sessions.get(oxSessionKey);
        }
        else {
            throw DBWSException.couldNotLocateOXSessionForService(xrService.name);
        }
        ProjectHelper.fixOROXAccessors(xrService.orSession.getProject(),
            xrService.oxSession.getProject());
        xrService.xmlContext = new XMLContext(xrService.oxSession.getProject());
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
    @SuppressWarnings("unchecked")
    public void buildDescriptorIndex() {
        for (Iterator i = xrService.oxSession.getProject().getOrderedDescriptors().iterator();
            i.hasNext();) {
            XMLDescriptor xd = (XMLDescriptor)i.next();
            XMLSchemaReference reference = xd.getSchemaReference();
            if (reference == null) {
                QName rootName = resolveName(xd.getDefaultRootElementField().getQualifiedName(),
                    xd.getNamespaceResolver());
                QName typeName = xrService.elementTypes.get(rootName);
                if (typeName == null) {
                    for (Iterator<QName> i2 = xrService.schemaTypes.iterator(); i2.hasNext();) {
                        QName qName = i2.next();
                        if (qName.equals(rootName)) {
                            typeName = qName;
                            break;
                        }
                    }
                    if (typeName == null) {
                        continue;
                    }
                }
                xrService.descriptorsByElement.put(rootName, xd);
                xrService.descriptorsByType.put(typeName, xd);
            }
            else if (reference.getType() == XMLSchemaReference.ELEMENT) {
                String context = reference.getSchemaContext();
                if (context != null && context.lastIndexOf('/') == 0) {
                    String elementNameNS = context.substring(1);
                    QName elementName = resolveName(elementNameNS, xd.getNamespaceResolver());
                    QName typeName = xrService.elementTypes.get(elementName);
                    if (typeName == null) {
                        continue;
                    }
                    xrService.descriptorsByElement.put(elementName, xd);
                    xrService.descriptorsByType.put(typeName, xd);
                }
            }
            else if (reference.getType() == XMLSchemaReference.COMPLEX_TYPE) {
                String context = reference.getSchemaContext();
                if (context != null && context.lastIndexOf('/') == 0) {
                    String typeNameNS = context.substring(1);
                    QName typeName = resolveName(typeNameNS, xd.getNamespaceResolver());
                    if (!xrService.schemaTypes.contains(typeName)) {
                        continue;
                    }
                    xrService.descriptorsByType.put(typeName, xd);
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
    @SuppressWarnings("unchecked")
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
        int index = name.indexOf(':');
        if (index != -1) {
            String uri = ns.resolveNamespacePrefix(name.substring(0, index));
            return new QName(uri, name.substring(index + 1));
        }
        else {
            String uri = ns.resolveNamespacePrefix("xmlns");
            return new QName(uri, name);
        }
    }
}
