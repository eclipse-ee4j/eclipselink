/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

    public enum ArchiveUse {
        archive, noArchive, ignore;
    }

    // attribute methods
    public void setDBWSBuilder(DBWSBuilder builder);
    public void setAdditionalArgs(String[] additionalArgs);
    public File getStageDir();
    public void setStageDir(File stageDir);
    public String getSessionsFileName();
    public void setSessionsFileName(String sessionsFileName);
    public void setHasAttachments(boolean hasAttachments);
    public boolean hasAttachments();
    public String getPackagerLabel();
    public void setArchiveFilename(String archiveFilename);
    public String getArchiveFilename();

    public void start(); // lifecycle methods
    public void end();

    // call-backs for stream management
    public OutputStream getSchemaStream() throws FileNotFoundException;
    public void closeSchemaStream(OutputStream schemaStream);

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException;
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder);
    public void closeSessionsStream(OutputStream sessionsStream);

    public OutputStream getServiceStream() throws FileNotFoundException;
    public void closeServiceStream(OutputStream serviceStream);

    public OutputStream getOrStream() throws FileNotFoundException;
    public String getOrProjectPathPrefix();
    public void closeOrStream(OutputStream orStream);

    public OutputStream getOxStream() throws FileNotFoundException;
    public String getOxProjectPathPrefix();
    public void closeOxStream(OutputStream oxStream);

    public OutputStream getWSDLStream() throws FileNotFoundException;
    public String getWSDLPathPrefix();
    public void closeWSDLStream(OutputStream wsdlStream);

    public OutputStream getSWARefStream() throws FileNotFoundException;
    public void closeSWARefStream(OutputStream swarefStream);

    public OutputStream getWebXmlStream() throws FileNotFoundException;
    public void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder);
    public void closeWebXmlStream(OutputStream webXmlStream);

    public OutputStream getProviderSourceStream() throws FileNotFoundException;
    public void closeProviderSourceStream(OutputStream sourceProviderStream);
    public OutputStream getProviderClassStream() throws FileNotFoundException;
    public void closeProviderClassStream(OutputStream classProviderStream);
    public OutputStream getProviderListenerClassStream() throws FileNotFoundException;
    public void closeProviderListenerClassStream(OutputStream classProviderListenerStream);
    public OutputStream getProviderListenerSourceStream() throws FileNotFoundException;
    public void closeProviderListenerSourceStream(OutputStream sourceProviderListenerStream);
    public void writeProvider(OutputStream sourceProviderStream, OutputStream codeGenProviderStream,
        OutputStream sourceProviderListenerStream, OutputStream classProviderListenerStream,
        DBWSBuilder builder);

    /**
     * Write the deployment descriptor contents to the provided OutputStream.
     */
    public void writeDeploymentDescriptor(OutputStream descriptorOutputStream);
    /**
     * Return an OutputStream to the deployment descriptor.
     */
    public OutputStream getDeploymentDescriptorStream() throws FileNotFoundException;
    /**
     * Closes the provided OutputStream.
     */
    public void closeDeploymentDescriptorStream(OutputStream descriptorOutputStream);
    /**
     * Return the name of the deployment descriptor file - this will depend on the
     * target application server.
     */
    public String getDeploymentDescriptorFileName();

    public void setArchiveUse(ArchiveUse archiveUse);
    public String getUsage();

    public static interface Archiver {
        public void setPackager(DBWSPackager packager);
        public DBWSPackager getPackager();
        public void archive();
        public String getFilename();
        public void setFilename(String filename);
        public String getOrProjectPathPrefix();
        public String getOxProjectPathPrefix();
        public String getWSDLPathPrefix();
    }

}
