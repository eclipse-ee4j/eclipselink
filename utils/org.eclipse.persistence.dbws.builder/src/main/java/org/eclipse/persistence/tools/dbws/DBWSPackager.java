/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 01 2008, created DBWS tools package

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

//EclipseLink imports
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;

/**
 * This interface defines methods necessary for packaging generated artifacts for
 * deployment to application servers, display in Java IDEs, etc.
 *
 */
public interface DBWSPackager {

    enum ArchiveUse {
        archive, noArchive, ignore;
    }

    // attribute methods
    void setDBWSBuilder(DBWSBuilder builder);
    void setAdditionalArgs(String[] additionalArgs);
    File getStageDir();
    void setStageDir(File stageDir);
    String getSessionsFileName();
    void setSessionsFileName(String sessionsFileName);
    void setHasAttachments(boolean hasAttachments);
    boolean hasAttachments();
    String getPackagerLabel();
    void setArchiveFilename(String archiveFilename);
    String getArchiveFilename();

    void start(); // lifecycle methods
    void end();

    // call-backs for stream management
    OutputStream getSchemaStream() throws FileNotFoundException;
    void closeSchemaStream(OutputStream schemaStream);

    OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException;
    SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder);
    void closeSessionsStream(OutputStream sessionsStream);

    OutputStream getServiceStream() throws FileNotFoundException;
    void closeServiceStream(OutputStream serviceStream);

    OutputStream getOrStream() throws FileNotFoundException;
    String getOrProjectPathPrefix();
    void closeOrStream(OutputStream orStream);

    OutputStream getOxStream() throws FileNotFoundException;
    String getOxProjectPathPrefix();
    void closeOxStream(OutputStream oxStream);

    OutputStream getWSDLStream() throws FileNotFoundException;
    String getWSDLPathPrefix();
    void closeWSDLStream(OutputStream wsdlStream);

    OutputStream getSWARefStream() throws FileNotFoundException;
    void closeSWARefStream(OutputStream swarefStream);

    OutputStream getWebXmlStream() throws FileNotFoundException;
    void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder);
    void closeWebXmlStream(OutputStream webXmlStream);

    OutputStream getProviderSourceStream() throws FileNotFoundException;
    void closeProviderSourceStream(OutputStream sourceProviderStream);
    OutputStream getProviderClassStream() throws FileNotFoundException;
    void closeProviderClassStream(OutputStream classProviderStream);
    OutputStream getProviderListenerClassStream() throws FileNotFoundException;
    void closeProviderListenerClassStream(OutputStream classProviderListenerStream);
    OutputStream getProviderListenerSourceStream() throws FileNotFoundException;
    void closeProviderListenerSourceStream(OutputStream sourceProviderListenerStream);
    void writeProvider(OutputStream sourceProviderStream, OutputStream codeGenProviderStream,
                       OutputStream sourceProviderListenerStream, OutputStream classProviderListenerStream,
                       DBWSBuilder builder);

    /**
     * Write the deployment descriptor contents to the provided OutputStream.
     */
    void writeDeploymentDescriptor(OutputStream descriptorOutputStream);
    /**
     * Return an OutputStream to the deployment descriptor.
     */
    OutputStream getDeploymentDescriptorStream() throws FileNotFoundException;
    /**
     * Closes the provided OutputStream.
     */
    void closeDeploymentDescriptorStream(OutputStream descriptorOutputStream);
    /**
     * Return the name of the deployment descriptor file - this will depend on the
     * target application server.
     */
    String getDeploymentDescriptorFileName();

    void setArchiveUse(ArchiveUse archiveUse);
    String getUsage();

    interface Archiver {
        void setPackager(DBWSPackager packager);
        DBWSPackager getPackager();
        void archive();
        String getFilename();
        void setFilename(String filename);
        String getOrProjectPathPrefix();
        String getOxProjectPathPrefix();
        String getWSDLPathPrefix();
    }

}
