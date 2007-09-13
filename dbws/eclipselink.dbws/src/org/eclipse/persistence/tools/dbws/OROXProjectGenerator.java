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
import java.io.FilterOutputStream;
import java.io.IOException;

// Java extension imports
import javax.wsdl.WSDLException;

// EclipseLink imports
import org.eclipse.persistence.sessions.Project;

// Ant imports
import org.apache.tools.ant.BuildException;

public interface OROXProjectGenerator {

    public abstract void buildOROXProjects() throws BuildException;

    // config variables passed from owning BuildDatabaseWebService task to generator
    public abstract void set_ProjectName(String projectName);

    public abstract void set_LogLevel(String logLevel);

    public abstract void set_DataSource(String dataSource);

    public abstract void set_ContextRoot(String contextRoot);

    public abstract void set_SessionsFileName(String sessionsFileName);

    public abstract void set_SessionCustomizerClassName(String sessionCustomizerClassName);

    public abstract void set_WSDLLocationURI(String wsdlLocationURI);

    public abstract void set_TargetNamespace(String targetNamespace);

    public abstract void set_PlatformClassName(String platformClassName);

    public abstract Project getORProject();

    public abstract Project getOXProject();

    public abstract void addSessionsXMLToStream(FilterOutputStream fos) throws IOException;

    public abstract void addWSDLToStream(FilterOutputStream fos)
        throws IOException, WSDLException;

    public abstract void addSchemaToStream(FilterOutputStream fos) throws IOException;

    public abstract void addDBWSToStream(FilterOutputStream fos) throws IOException;

    public boolean hasAttachments();

}
