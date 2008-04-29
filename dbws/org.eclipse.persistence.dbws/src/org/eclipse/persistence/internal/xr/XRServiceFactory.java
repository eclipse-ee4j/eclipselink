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
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.sessions.factories.SessionManager;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.SEARCH_PATHS;

/**
 * <p><b>INTERNAL</b>: helper class that knows how to build a {@link XRServiceAdapter} (a.k.a DBWS). An
 * <code>XRService</code> requires the following resources:
 * <ul>
 * <li>metadata in the form of a descriptor file called <tt><b>toplink-dbws.xml</b></tt><br>
 * </li>
 * <li>an XML Schema Definition (<tt>.xsd</tt>) file called <tt><b>toplink-dbws-schema.xsd</b></tt>
 * </li>
 * <li>a TopLink <tt>sessions.xml</tt> file called <tt><b>toplink-dbws-sessions.xml</b></tt><br>
 * &nbsp; the naming convention for the <tt>sessions.xml</tt> files can be overridden by the
 * <b>optional</b> <tt>&lt;sessions-file&gt;</tt> entry in the <code>toplink-dbws.xml</code>
 * descriptor file.
 * </li>
 * <li>TopLink metadata in the form of a TopLink {@link Project} (either deployment XML or Java classes).
 * <p>A typical <code>XRService</code> requires two projects: one to represent the O-R side, the other to
 * represent the O-X side.<br>
 * The O-R and O-X <code>Projects</code> metadata must have:<br>
 * i) identical case-sensitive <code>Project</code> names:<pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;toplink:object-persistence version="Oracle TopLink - 11g Release 1 (11.1.1.0.0) (Build YYMMDD)"
 *   xmlns:opm="http://xmlns.oracle.com/ias/xsds/opm"
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:toplink="http://xmlns.oracle.com/ias/xsds/toplink"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   &gt;
 *   &lt;opm:name&gt;example&lt;/opm:name&gt;
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
 * &lt;opm:class-mapping-descriptor xsi:type="toplink:relational-class-mapping-descriptor"&gt;
 *   &lt;opm:class&gt;some.package.SomeClass&lt;/opm:class&gt;
 *   &lt;opm:alias&gt;SomeAlias&lt;/opm:alias&gt;
 * ...
 * &lt;opm:class-mapping-descriptor xsi:type="toplink:xml-class-mapping-descriptor"&gt;
 *   &lt;opm:class&gt;some.package.SomeClass&lt;/opm:class&gt;
 *   &lt;opm:alias&gt;SomeAlias&lt;/opm:alias&gt;
 * </pre>
 * </li>
 * </ul>
 * An example <tt><b>toplink-dbws.xml</b></tt> descriptor file:
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

        // sub-classes override with specific behavior

        initializeService(parentClassLoader, xrSchemaStream);
        return xrService;
    }

    public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {

        // sub-classes override with specific behavior

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
        xrService.schema = schema;
        xrService.schemaNamespace = schema.getTargetNamespace();
        for (Iterator i = schema.getTopLevelComplexTypes().keySet().iterator(); i.hasNext();) {
            String typeLocal = (String) i.next();
            QName typeName = new QName(schema.getTargetNamespace(), typeLocal);
            xrService.schemaTypes.add(typeName);
        }
        for (Iterator i = schema.getTopLevelElements().keySet().iterator(); i.hasNext();) {
            String elementNameLocal = (String) i.next();
            QName elementName = new QName(schema.getTargetNamespace(), elementNameLocal);
            Element element = (Element)schema.getTopLevelElements().get(elementNameLocal);
            // ignore elements with inline complex types
            if (element.getComplexType() != null) {
                continue;
            }
            String typeNameNS = element.getType();
            QName typeName = resolveName(typeNameNS, schema.getNamespaceResolver());
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
        for (String searchPath : SEARCH_PATHS) {
            String path = searchPath + sessionsFile;
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader(path);
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
                QName rootName = resolveName(xd.getDefaultRootElement(), xd.getNamespaceResolver());
                QName typeName = xrService.elementTypes.get(rootName);
                if (typeName == null) {
                    continue;
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
        if (sessions.containsKey(orSessionName)) {
            Session session = (Session)manager.getSessions().get(orSessionName);
            if (session != null) {
                if (session.isDatabaseSession() && session.isConnected()) {
                    ((DatabaseSession)session).logout();
                }
            }
            xrService.orSession = null;
        }
        String oxSessionName = xrService.name + "-" + DBWS_OX_SESSION_NAME_SUFFIX;
        if (sessions.containsKey(oxSessionName)) {
            Session session = (Session)manager.getSessions().get(oxSessionName);
            if (session != null) {
                if (session.isDatabaseSession() && session.isConnected()) {
                    ((DatabaseSession)session).logout();
                }
            }
            xrService.oxSession = null;
        }
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
