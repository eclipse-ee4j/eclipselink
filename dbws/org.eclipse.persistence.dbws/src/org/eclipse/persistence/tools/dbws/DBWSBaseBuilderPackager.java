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

// TopLink imports

public class DBWSBaseBuilderPackager {

    protected static class NullOutputStream extends OutputStream {
            public void close(){}
            public void flush(){}
            public void write(byte[]b){}
            public void write(byte[]b,int i,int l){}
            public void write(int b){}
        }
    protected static NullOutputStream __nullStream = new NullOutputStream();

    public static final String CLASSES_DIR = "classes";
    protected File stageDir;
    protected boolean javaseMode;
    protected boolean hasAttachments;

    public DBWSBaseBuilderPackager() {
        super();
    }

    public File getStageDir() {
        return this.stageDir;
    }
    public void setStageDir(File stageDir) {
        this.stageDir = stageDir;
    }

    public boolean getMode() {
        return javaseMode;
    }
    public void setMode(boolean javaseMode) {
        this.javaseMode = javaseMode;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public void closeSchemaStream(OutputStream schemaStream) {
        if (nonNullStream(schemaStream)) {
            try {
                schemaStream.flush();
                schemaStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeSessionsStream(OutputStream sessionsStream) {
        if (nonNullStream(sessionsStream)) {
            try {
                sessionsStream.flush();
                sessionsStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeServiceStream(OutputStream serviceStream) {
        if (nonNullStream(serviceStream)) {
            try {
                serviceStream.flush();
                serviceStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeOrStream(OutputStream orStream) {
        if (nonNullStream(orStream)) {
            try {
                orStream.flush();
                orStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeOxStream(OutputStream oxStream) {
        if (nonNullStream(oxStream)) {
            try {
                oxStream.flush();
                oxStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeWSDLStream(OutputStream wsdlStream) {
        if (nonNullStream(wsdlStream)) {
            try {
                wsdlStream.flush();
                wsdlStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeSWARefStream(OutputStream swarefStream) {
        if (nonNullStream(swarefStream)) {
            try {
                swarefStream.flush();
                swarefStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeWebXmlStream(OutputStream webXmlStream) {
        if (nonNullStream(webXmlStream)) {
            try {
                webXmlStream.flush();
                webXmlStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeWebservicesXmlStream(OutputStream webservicesXmlStream) {
        if (nonNullStream(webservicesXmlStream)) {
            try {
                webservicesXmlStream.flush();
                webservicesXmlStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeOracleWebservicesXmlStream(OutputStream oracleWebservicesXmlStream) {
        if (nonNullStream(oracleWebservicesXmlStream)) {
            try {
                oracleWebservicesXmlStream.flush();
                oracleWebservicesXmlStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream) {
        if (nonNullStream(codeGenProviderStream)) {
            try {
                codeGenProviderStream.flush();
                codeGenProviderStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    public void closeSourceProviderStream(OutputStream sourceProviderStream) {
        if (nonNullStream(sourceProviderStream)) {
            try {
                sourceProviderStream.flush();
                sourceProviderStream.close();
            }
            catch (IOException e) {/* ignore */}
        }
    }

    protected boolean nonNullStream(OutputStream outputStream) {
        return outputStream != null && outputStream != __nullStream;
    }
}
