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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;

public interface DBWSPackager {

	public void start(); // lifecycle methods
	public void end();

    public void setStageDir(File stageDir);
    public void setHasAttachments(boolean hasAttachments);
    public boolean hasAttachments();

    public OutputStream getSchemaStream() throws FileNotFoundException;
    public void closeSchemaStream(OutputStream schemaStream);

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException;
	public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream,
			DBWSBuilder builder);
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
    public void closeWSDLStream(OutputStream wsdlStream);

    public OutputStream getSWARefStream() throws FileNotFoundException;
    public void closeSWARefStream(OutputStream swarefStream);

    public OutputStream getWebXmlStream() throws FileNotFoundException;
    public void closeWebXmlStream(OutputStream webXmlStream);

    public OutputStream getWebservicesXmlStream() throws FileNotFoundException;
    public void closeWebservicesXmlStream(OutputStream webservicesXmlStream);

    public String getPlatformWebservicesFilename();
    public OutputStream getPlatformWebservicesXmlStream() throws FileNotFoundException;
	public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
			DBWSBuilder builder);
    public void closePlatformWebservicesXmlStream(OutputStream platformWebservicesXmlStream);

    public OutputStream getCodeGenProviderStream() throws FileNotFoundException;
    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream);

    public OutputStream getSourceProviderStream() throws FileNotFoundException;
    public void closeSourceProviderStream(OutputStream sourceProviderStream);

}