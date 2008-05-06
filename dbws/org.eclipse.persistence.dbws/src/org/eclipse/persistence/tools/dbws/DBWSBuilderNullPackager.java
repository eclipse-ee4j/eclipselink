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

public class DBWSBuilderNullPackager extends DBWSBasePackager implements DBWSPackager {

    boolean mode;
    boolean hasAtttachments;

    public DBWSBuilderNullPackager() {
        super();
    }

    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream) {
    }
    public void closeWebservicesXmlStream(OutputStream webservicesXmlStream) {
    }
    public void closePlatformWebservicesXmlStream(OutputStream platformWebservicesXmlStream) {
    }
    public void closeOrStream(OutputStream orStream) {
    }
    public void closeOxStream(OutputStream oxStream) {
    }
    public void closeSchemaStream(OutputStream schemaStream) {
    }
    public void closeServiceStream(OutputStream serviceStream) {
    }
    public void closeSessionsStream(OutputStream sessionsStream) {
    }
    public void closeSourceProviderStream(OutputStream sourceProviderStream) {
    }
    public void closeSWARefStream(OutputStream swarefStream) {
    }
    public void closeWebXmlStream(OutputStream webXmlStream) {
    }
    public void closeWSDLStream(OutputStream wsdlStream) {
    }
    public OutputStream getCodeGenProviderStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getPlatformWebservicesXmlStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getOrStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getOxStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getSchemaStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getServiceStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        return null;
    }
	public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream,DBWSBuilder builder) {
		return null;
	}
    public OutputStream getSourceProviderStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getSWARefStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getWebXmlStream() throws FileNotFoundException {
        return null;
    }
    public OutputStream getWSDLStream() throws FileNotFoundException {
        return null;
    }
    public boolean getMode() {
        return mode;
    }
    public void setMode(boolean mode) {
        this.mode = mode;
    }
    public boolean hasAttachments() {
        return hasAtttachments;
    }
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAtttachments = hasAttachments;
    }
    public void setStageDir(File stageDir) {
    }
	public String getPlatformWebservicesFilename() {
		return "";
	}
	public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
		DBWSBuilder builder) {
	}
	public String getOrProjectPathPrefix() {
		return null;
	}
	public String getOxProjectPathPrefix() {
		return null;
	}
}
