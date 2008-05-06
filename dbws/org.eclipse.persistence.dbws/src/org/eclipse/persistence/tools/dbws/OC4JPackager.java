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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

// EclipseLink imports
import org.eclipse.persistence.Version;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.oc4j.Oc4j_11_1_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;

import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_PATHS;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_INF_DIR;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSDL_DIR;

/**
 * <p>
 * <b>INTERNAL:</b> OC4JPackager implements the {@link DBWSPackager} interface
 * so that the output from the {@link DBWSBuilder} is written to the <tt>src</tt> and
 * <tt>public_html</tt> directories as JDev expects.
 * <p>
 *
 * For this packager, the <tt>stageDir</tt> is the Project's root directory
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 * <pre>
 * \--- JDev <b>Projectnnn</b> root directory
 *    |   application.xml
 *    |   build.properties
 *    |   build.xml
 *    |   data-sources.xml
 *    |   dbws-builder.xml
 *    |   orion-application.xml
 *    |   Projectnnn.jpr
 *    |
 *    +---<b>public_html</b>
 *    |   \---WEB-INF
 *    |       |   oracle-webservices.xml
 *    |       |   web.xml
 *    |       |
 *    |       \---wsdl
 *    |               swaref.xsd                  -- optional if attachements are enabled
 *    |               toplink-dbws-schema.xsd
 *    |               toplink-dbws.wsdl
 *    |
 *    \---<b>src</b>
 *        |   toplink-dbws-or.xml
 *        |   toplink-dbws-ox.xml
 *        |   toplink-dbws-sessions.xml
 *        |   toplink-dbws.xml
 *        |
 *        \---_dbws
 *                DBWSProvider.java
 * </pre>
 */
public class OC4JPackager extends DBWSBasePackager implements DBWSPackager {

    public static final String ORACLE_WEBSERVICES_FILENAME =
        "oracle-webservices.xml";
    public static final String ORACLE_WEBSERVICES_FILE = WEB_INF_DIR + ORACLE_WEBSERVICES_FILENAME;
    public static final String ORACLE_WEBSERVICES_PREAMBLE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<oracle-webservices \n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
        "  xsi:noNamespaceSchemaLocation=\"http://xmlns.oracle.com/oracleas/schema/oracle-webservices-11_1.xsd\" \n" +
        "  > \n" +
        "  <webservice-description name=\"";
                           // serviceName ^^ here
    public static final String ORACLE_WEBSERVICES_PORT_COMPONENT_NAME =
                                             "\"> \n" +
        "    <port-component name=\"";
              // dotted-format serviceName.portName ^^ here
    public static final String ORACLE_WEBSERVICES_SUFFIX =
                                        "\"> \n" +
        "    <!-- add WS policies here \n" +
        "      <policy-references>\n" +
        "      </policy-references>\n" +
        "    -->\n" +
        "    </port-component>\n" +
        "  </webservice-description>\n" +
        "</oracle-webservices>";

    public static final String SRC_DIR = "src";
    public static final String PUBLIC_HTML_DIR = "public_html";

    protected File srcDir;
    protected File publicHTMLDir;
    protected File webInfDir;
    protected File wsdlDir;
    protected File underDBWSDir;

    // default constructor
    public OC4JPackager() {
        super();
    }

    public OutputStream getSchemaStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, sessionsFileName));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder) {

        SessionConfigs ts =	new SessionConfigs();
        ts.setVersion(Version.getVersion());

        DatabaseSessionConfig orSessionConfig = null;
        String dataSource = builder.getDataSource();
        if (dataSource != null) {
            orSessionConfig = new ServerSessionConfig();
        }
        else {
            orSessionConfig = new DatabaseSessionConfig();
        }
        String projectName = builder.getProjectName();
        orSessionConfig.setName(projectName + "-" + DBWS_OR_SESSION_NAME_SUFFIX);
        ProjectConfig orProjectConfig = builder.buildORProjectConfig();
        orSessionConfig.setPrimaryProject(orProjectConfig);
            String orSessionCustomizerClassName = builder.getOrSessionCustomizerClassName();
            if (orSessionCustomizerClassName != null && !"".equals(orSessionCustomizerClassName)) {
                orSessionConfig.setSessionCustomizerClass(orSessionCustomizerClassName);
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
            dlc.setConnectionURL(builder.getUrl());
            dlc.setDriverClass(builder.getDriver());
            dlc.setUsername(builder.getUsername());
            dlc.setEncryptedPassword(builder.getPassword());
        }
        dlc.setPlatformClass(builder.getPlatformClassname());
        orSessionConfig.setLoginConfig(dlc);
        DefaultSessionLogConfig orLogConfig = new DefaultSessionLogConfig();
        orLogConfig.setLogLevel(builder.getLogLevel());
        orSessionConfig.setLogConfig(orLogConfig);
        ts.addSessionConfig(orSessionConfig);
        DatabaseSessionConfig oxSessionConfig = new DatabaseSessionConfig();
        oxSessionConfig.setName(projectName + "-" + DBWS_OX_SESSION_NAME_SUFFIX);
        ProjectConfig oxProjectConfig = builder.buildOXProjectConfig();
        oxSessionConfig.setPrimaryProject(oxProjectConfig);
        DefaultSessionLogConfig oxLogConfig = new DefaultSessionLogConfig();
        oxLogConfig.setLogLevel("off");
        oxSessionConfig.setLogConfig(oxLogConfig);
        String oxSessionCustomizerClassName = builder.getOxSessionCustomizerClassName();
        if (oxSessionCustomizerClassName != null && !"".equals(oxSessionCustomizerClassName)) {
            oxSessionConfig.setSessionCustomizerClass(oxSessionCustomizerClassName);
        }
        ts.addSessionConfig(oxSessionConfig);
        return ts;
	}

    public OutputStream getServiceStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_SERVICE_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getOrStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_OR_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getOxStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_OX_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getWSDLStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, DBWS_WSDL));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getSWARefStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, SWAREF_FILENAME));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getWebXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWebInfDir();
            return new FileOutputStream(new File(webInfDir, WEB_XML_FILENAME));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWebInfDir();
            return new FileOutputStream(new File(webInfDir, WEBSERVICES_FILENAME));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getPlatformWebservicesXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWebInfDir();
            return new FileOutputStream(new File(webInfDir, ORACLE_WEBSERVICES_FILENAME));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }
	public String getPlatformWebservicesFilename() {
		return ORACLE_WEBSERVICES_FILENAME;
	}

    public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
    	DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(ORACLE_WEBSERVICES_PREAMBLE);
        sb.append(dbwsBuilder.wsdlGenerator.serviceName);
        sb.append(ORACLE_WEBSERVICES_PORT_COMPONENT_NAME);
        sb.append(dbwsBuilder.wsdlGenerator.serviceName + "." + dbwsBuilder.wsdlGenerator.serviceName);
        sb.append(ORACLE_WEBSERVICES_SUFFIX);
        OutputStreamWriter osw =
        	new OutputStreamWriter(new BufferedOutputStream(platformWebservicesXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
	}

    public OutputStream getCodeGenProviderStream() throws FileNotFoundException {
        return __nullStream;
    }
    @Override
    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream) {
        // do nothing
    }

    public OutputStream getSourceProviderStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildUnderDBWS();
            return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_SOURCE_FILE));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

	public String getOrProjectPathPrefix() {
		return META_INF_PATHS[0]; // META_INF_PATHS[0] is the upper-case version
	}
	public String getOxProjectPathPrefix() {
		return META_INF_PATHS[0]; // META_INF_PATHS[0] is the upper-case version
	}

    protected void buildSrcDir() throws FileNotFoundException {
        srcDir = new File(stageDir, SRC_DIR);
        if (!srcDir.exists()) {
            boolean worked = srcDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    SRC_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildUnderDBWS() throws FileNotFoundException {
        buildSrcDir();
        underDBWSDir = new File(srcDir, UNDER_DBWS);
        if (!underDBWSDir.exists()) {
            boolean worked = underDBWSDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + SRC_DIR + "/" + UNDER_DBWS +
                    " dir under " + stageDir);
            }
        }
    }

    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, PUBLIC_HTML_DIR);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    PUBLIC_HTML_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildWebInfDir() throws FileNotFoundException {
        buildPublicHTMLDir();
        webInfDir = new File(publicHTMLDir, WEB_INF_PATHS[1]);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[1] + " under " + PUBLIC_HTML_DIR);
            }
        }
    }

    protected void buildWSDLDir() throws FileNotFoundException {
        buildWebInfDir();
        wsdlDir = new File(webInfDir, WSDL_DIR);
        if (!wsdlDir.exists()) {
            boolean worked = wsdlDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WSDL_DIR + " under " + WEB_INF_PATHS[1]);
            }
        }
    }
}
