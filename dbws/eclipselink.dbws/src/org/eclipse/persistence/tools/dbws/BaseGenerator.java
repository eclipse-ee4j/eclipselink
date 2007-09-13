/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.sql.Connection;

// Java extension imports
import javax.wsdl.WSDLException;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.Version;
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.oc4j.Oc4j_11_1_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;

// Ant imports
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.taskdefs.JDBCTask;

public class BaseGenerator extends JDBCTask implements OROXProjectGenerator, TaskContainer {

    // JDBCTask doesn't have a way to get the driver value; therefore, create a
    // 'shadow' variable to capture value in @Override public void setDriver
    protected String savedDriverName;
    protected Connection savedConnection;

    // passed-in from owning BuildDBWSWar task
    protected String projectName;
    protected String logLevel;
    protected String dataSource;
    protected String contextRoot;
    protected String sessionsFileName;
    protected String sessionCustomizerClassName;
    protected String wsdlLocationURI;
    protected String targetNamespace;
    protected String platformClassName;
    // to be built by concrete generator impls
    protected org.eclipse.persistence.sessions.Project orProject;
    protected org.eclipse.persistence.sessions.Project oxProject;
    protected Schema schema;
    protected XRServiceModel xrServiceModel;


    public BaseGenerator() {
        super();
        schema = new Schema();
        NamespaceResolver ns = schema.getNamespaceResolver();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);
    }

    public void addTask(Task task) {
    } // ignore

    public void execute() throws BuildException {
        buildOROXProjects();
    }

    public void buildOROXProjects() {
    }

    public boolean hasAttachments() {
        for (Operation op : xrServiceModel.getOperationsList()) {
            if (op instanceof QueryOperation) {
                if (((QueryOperation)op).isAttachment()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addSessionsXMLToStream(FilterOutputStream fos)
        throws IOException {

        SessionConfigs ts = new SessionConfigs();
        ts.setVersion(Version.getVersion());
        DatabaseSessionConfig orSessionConfig = null;
        if (dataSource != null) {
            orSessionConfig = new ServerSessionConfig();
        }
        else {
            orSessionConfig = new DatabaseSessionConfig();
        }
        orSessionConfig.setName(projectName + "-" + DBWS_OR_SESSION_NAME_SUFFIX);

        ProjectConfig orProjectConfig = buildORProjectConfig();
        orSessionConfig.setPrimaryProject(orProjectConfig);
        if (sessionCustomizerClassName != null && !"".equals(sessionCustomizerClassName)) {
            orSessionConfig.setSessionCustomizerClass(sessionCustomizerClassName);
        }

        DatabaseLoginConfig dlc = new DatabaseLoginConfig();
        dlc.setBindAllParameters(true);
        dlc.setJdbcBatchWriting(true);
        if (dataSource != null) {
            ServerPlatformConfig spc = new Oc4j_11_1_1_PlatformConfig();
            spc.setEnableJTA(true);
            spc.setEnableRuntimeServices(true);
            orSessionConfig.setServerPlatformConfig(spc);
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(dataSource);
        }
        else {
            dlc.setConnectionURL(getUrl());
            dlc.setDriverClass(savedDriverName);
            dlc.setUsername(getUserId());
            dlc.setEncryptedPassword(getPassword());
        }
        dlc.setPlatformClass(platformClassName);
        orSessionConfig.setLoginConfig(dlc);
        DefaultSessionLogConfig orLogConfig = new DefaultSessionLogConfig();
        orLogConfig.setLogLevel(logLevel);
        orSessionConfig.setLogConfig(orLogConfig);
        ts.addSessionConfig(orSessionConfig);

        DatabaseSessionConfig oxSessionConfig = new DatabaseSessionConfig();
        oxSessionConfig.setName(projectName + "-" + DBWS_OX_SESSION_NAME_SUFFIX);
        ProjectConfig oxProjectConfig = buildOXProjectConfig();
        oxSessionConfig.setPrimaryProject(oxProjectConfig);
        DefaultSessionLogConfig oxLogConfig = new DefaultSessionLogConfig();
        oxLogConfig.setLogLevel("off");
        oxSessionConfig.setLogConfig(oxLogConfig);
        ts.addSessionConfig(oxSessionConfig);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLSessionConfigWriter.write(ts, new OutputStreamWriter(baos));
        fos.write(baos.toByteArray(), 0, baos.size());

    }

    public ProjectConfig buildORProjectConfig() {
        ProjectClassConfig orProjectClassConfig = new ProjectClassConfig();
        orProjectClassConfig.setProjectString(Project.class.getName());
        return orProjectClassConfig;
    }

    public ProjectConfig buildOXProjectConfig() {
        ProjectClassConfig oxProjectClassConfig = new ProjectClassConfig();
        oxProjectClassConfig.setProjectString(SimpleXMLFormatProject.class.getName());
        return oxProjectClassConfig;
    }

    public void addWSDLToStream(FilterOutputStream fos) throws IOException,
        WSDLException {
        WSDLGenerator wsdlGenerator = new WSDLGenerator(xrServiceModel, wsdlLocationURI,
          hasAttachments(), fos);
        wsdlGenerator.generateWSDL();
    }

    public void addSchemaToStream(FilterOutputStream fos) throws IOException {
        schema.setName(projectName);
    }

    public void addDBWSToStream(FilterOutputStream fos) throws IOException {

    }

    protected void initXRServiceModel() {
        xrServiceModel = new DBWSModel();
        xrServiceModel.setName(projectName);
        if (sessionsFileName != null && sessionsFileName.length() > 0) {
            xrServiceModel.setSessionsFile(sessionsFileName);
        }
    }

    @Override
    public void setDriver(String driver) {
        this.savedDriverName = driver;
        super.setDriver(driver);
    }

    @Override
    public Connection getConnection() {
        if (savedConnection == null) {
            savedConnection = super.getConnection();
        }
        return savedConnection;
    }

    public void set_ProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void set_LogLevel(String logLevel) {
      this.logLevel = logLevel;
    }

    public void set_DataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void set_ContextRoot(String contextRoot) {
      this.contextRoot = contextRoot;
    }

    public void set_SessionCustomizerClassName(String sessionCustomizerClassName) {
      this.sessionCustomizerClassName = sessionCustomizerClassName;
    }

    public void set_SessionsFileName(String sessionsFileName) {
        this.sessionsFileName = sessionsFileName;
    }

    public void set_PlatformClassName(String platformClassName) {
      this.platformClassName = platformClassName;
    }

    @SuppressWarnings("unchecked")
    public DatabasePlatform getDatabasePlatform() {
        DatabasePlatform databasePlatform = null;
        try {
            Class<?> platformClass = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                platformClass = (Class<?>)AccessController.doPrivileged(
                    new PrivilegedClassForName(platformClassName));
            }
            else{
                platformClass = (Class<?>)PrivilegedAccessHelper.getClassForName(platformClassName);
            }
            databasePlatform = (DatabasePlatform)Helper.getInstanceFromClass(platformClass);
        }
        catch (Exception e) {
            databasePlatform = new OraclePlatform();
        }
        return databasePlatform;
    }

    public void set_TargetNamespace(String targetNamespace) {
      this.targetNamespace = targetNamespace;
    }

    public void set_WSDLLocationURI(String wsdlLocationURI) {
      this.wsdlLocationURI = wsdlLocationURI;
    }

    public org.eclipse.persistence.sessions.Project getORProject() {
        return orProject;
    }

    public void setOrProject(org.eclipse.persistence.sessions.Project orProject) {
        this.orProject = orProject;
    }

    public org.eclipse.persistence.sessions.Project getOXProject() {
        return oxProject;
    }

    public void setOxProject(org.eclipse.persistence.sessions.Project oxProject) {
        this.oxProject = oxProject;
    }
}
