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
import java.io.FileOutputStream;
import java.io.OutputStream;

// EclipseLink imports
import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;

/**
 * <p>
 * <b>INTERNAL:</b> SimplePackager implements the {@link DBWSPackager} interface.
 * The output files from the {@link DBWSBuilder} are written to the <tt>stageDir</tt>
 * 'flat' with no directory structure.
 * <p>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle EclipseLink 1.x
 * <pre>
 * \--- <b>stageDir</b> root directory
 *    |   <b>eclipselink-dbws-schema.xsd</b>
 *    |   swaref.xsd                  -- optional if attachements are enabled
 *    |   <b>eclipselink-dbws-or.xml</b>
 *    |   <b>eclipselink-dbws-ox.xml</b>
 *    |   <b>eclipselink-dbws-sessions.xml</b>
 *    |   <b>eclipselink-dbws.xml</b>
 *
 * </pre>
 */
public class SimplePackager extends DBWSBasePackager implements DBWSPackager {

    // default constructor
    public SimplePackager() {
        super();
    }

    public OutputStream getSchemaStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_SCHEMA_XML));
    }

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, sessionsFileName));
    }
    public SessionConfigs buildSessionsXML(OutputStream dbwsSessionsStream, DBWSBuilder builder) {

        SessionConfigs ts =	new SessionConfigs();
        ts.setVersion(Version.getVersion());

        DatabaseSessionConfig orSessionConfig = new DatabaseSessionConfig();
        String projectName = builder.getProjectName();
        orSessionConfig.setName(projectName + "-" + DBWS_OR_SESSION_NAME_SUFFIX);
        ProjectConfig orProjectConfig = builder.buildORProjectConfig();
        orSessionConfig.setPrimaryProject(orProjectConfig);
            String orSessionCustomizerClassName = builder.getOrSessionCustomizerClassName();
            if (orSessionCustomizerClassName != null && !"".equals(orSessionCustomizerClassName)) {
                orSessionConfig.setSessionCustomizerClass(orSessionCustomizerClassName);
        }
        DatabaseLoginConfig dlc = new DatabaseLoginConfig();
        dlc.setBindAllParameters(true);
        dlc.setJdbcBatchWriting(true);
        dlc.setConnectionURL(builder.getUrl());
        dlc.setDriverClass(builder.getDriver());
        dlc.setUsername(builder.getUsername());
        dlc.setEncryptedPassword(builder.getPassword());
        dlc.setPlatformClass(builder.getPlatformClassname());
        orSessionConfig.setLoginConfig(dlc);
        DefaultSessionLogConfig orLogConfig = new DefaultSessionLogConfig();
        orLogConfig.setLogLevel(builder.getLogLevel());
        orSessionConfig.setLogConfig(orLogConfig);
        ts.addSessionConfig(orSessionConfig);

        DatabaseSessionConfig oxSessionConfig = new DatabaseSessionConfig();
        oxSessionConfig.setName(projectName + "-" + DBWS_OX_SESSION_NAME_SUFFIX);
        ProjectConfig oxProjectConfig = builder.buildOXProjectConfig();
        oxSessionConfig.setPrimaryProject(oxProjectConfig);
        DefaultSessionLogConfig oxLogConfig = new DefaultSessionLogConfig();
        oxLogConfig.setLogLevel("off");
        oxSessionConfig.setLogConfig(oxLogConfig);
        String oxSessionCustomizerClassName = builder.getOxSessionCustomizerClassName();
        if (oxSessionCustomizerClassName != null && !"".equals(oxSessionCustomizerClassName)) {
            oxSessionConfig.setSessionCustomizerClass(oxSessionCustomizerClassName);
        }
        ts.addSessionConfig(oxSessionConfig);
        return ts;
	}

    public OutputStream getServiceStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_SERVICE_XML));
    }

    public OutputStream getOrStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_OR_XML));
    }

    public OutputStream getOxStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_OX_XML));
    }

    public OutputStream getWSDLStream() throws FileNotFoundException {
        return __nullStream;
    }

    public OutputStream getSWARefStream() throws FileNotFoundException {
    	if (!hasAttachments) {
    		return __nullStream;
    	}
    	else {
            return new FileOutputStream(new File(stageDir, SWAREF_FILENAME));
    	}
    }

    public OutputStream getWebXmlStream() throws FileNotFoundException {
        return __nullStream;
    }

    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        return __nullStream;
    }

	public String getPlatformWebservicesFilename() {
		return "";
	}
    public OutputStream getPlatformWebservicesXmlStream() throws FileNotFoundException {
        return __nullStream;
    }
    public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
    	DBWSBuilder dbwsBuilder) {
	}

    public OutputStream getCodeGenProviderStream() throws FileNotFoundException {
        return __nullStream;
    }

    public OutputStream getSourceProviderStream() throws FileNotFoundException {
        return __nullStream;
    }

	public String getOrProjectPathPrefix() {
		return null;
	}
	public String getOxProjectPathPrefix() {
		return null;
	}
}