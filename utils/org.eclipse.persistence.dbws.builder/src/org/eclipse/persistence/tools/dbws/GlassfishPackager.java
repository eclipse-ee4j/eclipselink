/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - July 13 2010
 *       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=318207
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

public class GlassfishPackager extends WarPackager {

    public GlassfishPackager() {
        this(new WarArchiver(),"glassfish", archive);
    }
    protected GlassfishPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
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
                "org.eclipse.persistence.platform.server.sunas.SunAS9ServerPlatform");
            customServerPlatformConfig.setExternalTransactionControllerClass(
                "org.eclipse.persistence.transaction.sunas.SunAS9TransactionController");
            orSessionConfig.setServerPlatformConfig(customServerPlatformConfig);
            DatabaseLoginConfig dlc = new DatabaseLoginConfig();
            dlc.setPlatformClass(builder.getPlatformClassname());
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(dataSource);
            dlc.setBindAllParameters(true);
            dlc.setStreamsForBinding(true);
            orSessionConfig.setLoginConfig(dlc);
            ts.getSessionConfigs().set(0, orSessionConfig);
        }
        return ts;
    }
}
