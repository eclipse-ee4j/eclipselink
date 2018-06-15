/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - July 13 2010
//       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=318207
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

//EclipseLink imports
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.sessions.JNDIConnector;

import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;

/**
 * Packages generated artifacts for deployment to JBoss AS.
 *
 */
public class JBossPackager extends WarPackager {
    // For JBoss AS 7
    static final String deploymentDescriptorFileName = "jboss-deployment-structure.xml";

    // JBoss AS 7 utilizes module dependencies - we need the EclipseLink module
    static final String deploymentDescriptorXml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<jboss-deployment-structure>\n" +
        "   <deployment>\n" +
        "      <dependencies>\n" +
        "         <module name=\"org.eclipse.persistence\" />\n" +
        "      </dependencies>\n" +
        "   </deployment>\n" +
        "</jboss-deployment-structure>";

    /**
     * The default constructor configures for deployment as a WAR archive to JBoss AS.
     */
    public JBossPackager() {
        this(new WarArchiver(),"jboss", archive);
    }
    protected JBossPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    /**
     * Write the deployment descriptor contents to the provided OutputStream.
     */
    @Override
    public void writeDeploymentDescriptor(OutputStream descriptorOutputStream) {
        OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(descriptorOutputStream));
        try {
            osw.write(new StringBuilder(deploymentDescriptorXml).toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }
    /**
     * Return an OutputStream to the deployment descriptor.
     */
    @Override
    public OutputStream getDeploymentDescriptorStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, deploymentDescriptorFileName));
    }
    /**
     * Closes the given OutputStream.
     */
    @Override
    public void closeDeploymentDescriptorStream(OutputStream descriptorOutputStream) {
        closeStream(descriptorOutputStream);
    }
    /**
     * Return the name of the deployment descriptor file.
     */
    @Override
    public String getDeploymentDescriptorFileName() {
        return deploymentDescriptorFileName;
    }

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
                "org.eclipse.persistence.platform.server.jboss.JBossPlatform");
            customServerPlatformConfig.setExternalTransactionControllerClass(
                "org.eclipse.persistence.transaction.jboss.JBossTransactionController");
            orSessionConfig.setServerPlatformConfig(customServerPlatformConfig);
            DatabaseLoginConfig dlc = new DatabaseLoginConfig();
            dlc.setPlatformClass(builder.getPlatformClassname());
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(dataSource);
            dlc.setLookupType(JNDIConnector.STRING_LOOKUP);
            dlc.setBindAllParameters(true);
            dlc.setStreamsForBinding(true);
            orSessionConfig.setLoginConfig(dlc);
            ts.getSessionConfigs().set(0, orSessionConfig);
        }
        return ts;
    }
}
