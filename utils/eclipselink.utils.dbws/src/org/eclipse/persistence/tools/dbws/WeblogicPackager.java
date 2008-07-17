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
 *     Mike Norman - July 17 2008, creating packager for WLS 10.3 version of JAX-WS RI
 ******************************************************************************/

 package org.eclipse.persistence.tools.dbws;

// javase imports
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;

public class WeblogicPackager extends WebFilesPackager {

    public static final String WEB_XML_PREAMBLE =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.5\">\n" +
        "  <display-name>";
    public static final String WEB_XML_SERVICE_NAME =
                                          "</display-name>\n" +
        "  <servlet>\n" +
        "    <servlet-name>DBWSProvider</servlet-name>\n" +
        "    <servlet-class>_dbws.DBWSProvider</servlet-class>\n" +
        "    <load-on-startup>0</load-on-startup>\n" +
        "  </servlet>\n" +
        "  <servlet-mapping>\n" +
        "    <servlet-name>DBWSProvider</servlet-name>\n" +
        "    <url-pattern>";
    public static final String WEB_XML_URL_PATTERN =
                               "</url-pattern>\n" +
        "  </servlet-mapping>\n" +
        "</web-app>";

    public WeblogicPackager() {
        super();
    }
    public WeblogicPackager(boolean useArchiver) {
        super(useArchiver);
    }
    public WeblogicPackager(boolean useArchiver, String warName) {
        super(useArchiver, warName);
    }

    @Override
    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        return __nullStream;
    }

    @Override
    public void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(WEB_XML_PREAMBLE);
        String serviceName = dbwsBuilder.getWSDLGenerator().getServiceName();
        sb.append(serviceName);
        sb.append(WEB_XML_SERVICE_NAME);
        sb.append(dbwsBuilder.getContextRoot());
        sb.append(WEB_XML_URL_PATTERN);
        OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(webXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }

    @SuppressWarnings("unchecked")
    @Override
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder) {
        SessionConfigs ts = super.buildSessionsXML(dbwsSessionsStream, builder);
        String dataSource = builder.getDataSource();
        if (dataSource != null) {
            DatabaseSessionConfig tmpConfig =
                (DatabaseSessionConfig)ts.getSessionConfigs().firstElement();
            ProjectConfig orProject = tmpConfig.getPrimaryProject();
            LogConfig logConfig = tmpConfig.getLogConfig();
            String sessionName = tmpConfig.getName();
            DatabaseSessionConfig orSessionConfig = new ServerSessionConfig();
            orSessionConfig.setPrimaryProject(orProject);
            orSessionConfig.setName(sessionName);
            orSessionConfig.setLogConfig(logConfig);
            CustomServerPlatformConfig customServerPlatformConfig = new CustomServerPlatformConfig();
            customServerPlatformConfig.setEnableJTA(true);
            customServerPlatformConfig.setEnableRuntimeServices(true);
            customServerPlatformConfig.setServerClassName(
                "org.eclipse.persistence.platform.server.wls.WebLogic_10_Platform");
            customServerPlatformConfig.setExternalTransactionControllerClass(
                "org.eclipse.persistence.transaction.wls.WebLogicTransactionController");
            orSessionConfig.setServerPlatformConfig(customServerPlatformConfig);
            DatabaseLoginConfig dlc = new DatabaseLoginConfig();
            dlc.setPlatformClass(builder.getPlatformClassname());
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(dataSource);
            orSessionConfig.setLoginConfig(dlc);
            ts.getSessionConfigs().set(0, orSessionConfig);
        }
        return ts;
    }
}