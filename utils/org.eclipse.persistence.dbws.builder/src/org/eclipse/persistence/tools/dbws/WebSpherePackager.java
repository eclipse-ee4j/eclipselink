/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - July 13 2010
//       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=318207
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

public class WebSpherePackager extends WarPackager {

    public WebSpherePackager() {
        this(new WarArchiver(), "was", archive);
    }
    protected WebSpherePackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
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
                "org.eclipse.persistence.platform.server.was.WebSphere_7_Platform");
            customServerPlatformConfig.setExternalTransactionControllerClass(
                "org.eclipse.persistence.transaction.was.WebSphereTransactionController");
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
