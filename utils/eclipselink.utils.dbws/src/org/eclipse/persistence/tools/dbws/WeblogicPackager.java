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
 *     Mike Norman - July 17 2008, creating packager for WLS 10.3 version of JAX-WS RI
 ******************************************************************************/

 package org.eclipse.persistence.tools.dbws;

//javase imports
 import java.io.OutputStream;

//EclipseLink imports
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;

/**
 * <p>
 * <b>PUBLIC:</b> WeblogicPackager extends {@link JSR109WebServicePackager}. It is responsible for generating <br>
 * the WebLogic-specific deployment information - specifically, the settings in the sessions.xml file <br>
 * that require WebLogic-specific platform information
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class WeblogicPackager extends WarPackager {

    public WeblogicPackager() {
        this(new WarArchiver(), "wls", archive);
    }
    protected WeblogicPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    @Override
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder) {
        SessionConfigs ts = super.buildSessionsXML(dbwsSessionsStream, builder);
        String dataSource = builder.getDataSource();
        if (dataSource != null) {
            DatabaseSessionConfig tmpConfig =
                (DatabaseSessionConfig)ts.getSessionConfigs().firstElement();
            buildDatabaseSessionConfig(ts, tmpConfig, builder);
        }
        return ts;
    }

    // WebLogic_10_Platform
    @SuppressWarnings("unchecked")
    public static void buildDatabaseSessionConfig(SessionConfigs ts, DatabaseSessionConfig tmpConfig,
        DBWSBuilder builder) {
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
        dlc.setDatasource(builder.getDataSource());
        dlc.setBindAllParameters(true);
        dlc.setStreamsForBinding(true);
        orSessionConfig.setLoginConfig(dlc);
        ts.getSessionConfigs().set(0, orSessionConfig);
    }
}