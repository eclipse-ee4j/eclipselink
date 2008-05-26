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
import java.io.IOException;
import java.io.OutputStream;

// EclipseLink imports
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.tools.dbws.DBWSPackager.Archiver;

public class DBWSBasePackager {

	// some packagers don't need to write out some files -
	// this the do-nothing/go-nowhere stream handles that case 
    protected static class NullOutputStream extends OutputStream {
            public void close(){}
            public void flush(){}
            public void write(byte[]b){}
            public void write(byte[]b,int i,int l){}
            public void write(int b){}
        }
    public static NullOutputStream __nullStream = new NullOutputStream();

    protected File stageDir;
    protected String sessionsFileName;
    protected boolean hasAttachments;
    protected DBWSPackager.Archiver archiver;

    public DBWSBasePackager() {
        super();
    }

    public File getStageDir() {
        return this.stageDir;
    }
    public void setStageDir(File stageDir) {
        this.stageDir = stageDir;
    }

    public String getSessionsFileName() {
        return sessionsFileName;
    }
    public void setSessionsFileName(String sessionsFileName) {
        this.sessionsFileName = sessionsFileName;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Archiver getArchiver() {
        return archiver;
    }
    public void setArchiver(Archiver archiver) {
        this.archiver = archiver;
    }

    // lifecycle methods
    public void start() {
        if (stageDir == null) {
            throw new DBWSException(this.getClass().getSimpleName() + " stageDir cannot be null");
        }
    }
    public void end() {
        if (archiver != null) {
            archiver.archive();
        }
    }
    
    public void closeSchemaStream(OutputStream schemaStream) {
    	closeStream(schemaStream);
    }

    public void closeSessionsStream(OutputStream sessionsStream) {
    	closeStream(sessionsStream);
    }

    public void closeServiceStream(OutputStream serviceStream) {
    	closeStream(serviceStream);
    }

    public void closeOrStream(OutputStream orStream) {
    	closeStream(orStream);
    }

    public void closeOxStream(OutputStream oxStream) {
    	closeStream(oxStream);
    }

    public void closeWSDLStream(OutputStream wsdlStream) {
    	closeStream(wsdlStream);
    }

    public void closeSWARefStream(OutputStream swarefStream) {
    	closeStream(swarefStream);
    }

    @SuppressWarnings("unused")
    public void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder) {
    }
    
    public void closeWebXmlStream(OutputStream webXmlStream) {
    	closeStream(webXmlStream);
    }
    
    @SuppressWarnings("unused")
	public void writeWebservicesXML(OutputStream webservicesXmlStream, DBWSBuilder builder) {
	}
	
    public void closeWebservicesXmlStream(OutputStream webservicesXmlStream) {
    	closeStream(webservicesXmlStream);
    }

    public void closePlatformWebservicesXmlStream(OutputStream platformWebservicesXmlStream) {
    	closeStream(platformWebservicesXmlStream);
    }

    @SuppressWarnings("unused")
	public void writeDBWSProviderClass(OutputStream codeGenProviderStream, DBWSBuilder builder) {
	}
	
    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream) {
    	closeStream(codeGenProviderStream);
    }

    @SuppressWarnings("unused")
    public void writeDBWSProviderSource(OutputStream sourceProviderStream, DBWSBuilder builder) {	
    }
    
    public void closeSourceProviderStream(OutputStream sourceProviderStream) {
        closeStream(sourceProviderStream);
    }

    protected void closeStream(OutputStream outputStream) {
        if (outputStream != null && outputStream != __nullStream) {
            try {
            	outputStream.flush();
            	outputStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }
}