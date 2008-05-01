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

// TopLink imports
import org.eclipse.persistence.exceptions.DBWSException;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_PATHS;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.ORACLE_WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSDL_DIR;

/**
 * <p>
 * <b>INTERNAL:</b> DBWSBuilderExpandedWARPackager implements the {@link DBWSBuilderPackager} interface
 * so that the output from the {@link DBWSBuilder} is written to the <tt>stageDir</t>> as an
 * expanded <tt>.war</tt> file
 * <p>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 * <pre>
 * \--- stageDir
 *      |
 *      \---web-inf
 *          |   oracle-webservices.xml
 *          |   web.xml
 *          |
 *          +---classes
 *          |   +---META-INF
 *          |   |    toplink-dbws.xml
 *          |   |    toplink-dbws-sessions.xml
 *          |   |    toplink-dbws-or.xml
 *          |   |    toplink-dbws-ox.xml
 *          |   |
 *          |   \---_dbws
 *          |        DBWSProvider.class        -- code-generated javax.xml.ws.Provider
 *          |        DBWSProvider.java
 *          |
 *          \---wsdl
 *                 swaref.xsd                  -- optional if attachements are enabled
 *                 toplink-dbws.wsdl
 *                 toplink-dbws-schema.xsd
 * </pre>
 */
public class DBWSBuilderExpandedWARPackager extends DBWSBaseBuilderPackager implements DBWSBuilderPackager {

    protected File webInfDir;
    protected File wsdlDir;
    protected File classesDir;
    protected File metaDir;
    protected File underDBWSDir;

    public OutputStream getSchemaStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        if (stageDir != null) {
            buildMetaInfDir();
            return new FileOutputStream(new File(metaDir, sessionsFileName));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getServiceStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildMetaInfDir();
            return new FileOutputStream(new File(metaDir, DBWS_SERVICE_XML));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getOrStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildMetaInfDir();
            return new FileOutputStream(new File(metaDir, DBWS_OR_XML));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }
    public OutputStream getOxStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildMetaInfDir();
            return new FileOutputStream(new File(metaDir, DBWS_OX_XML));
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }
    public OutputStream getWSDLStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildWSDLDir();
                return new FileOutputStream(new File(wsdlDir, DBWS_WSDL));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getSWARefStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode && hasAttachments) {
                buildWSDLDir();
                return new FileOutputStream(new File(wsdlDir, SWAREF_FILENAME));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getWebXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildWebInfDir();
                return new FileOutputStream(new File(webInfDir, WEB_XML_FILENAME));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildWebInfDir();
                return new FileOutputStream(new File(webInfDir, WEBSERVICES_FILENAME));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getOracleWebservicesXmlStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildWebInfDir();
                return new FileOutputStream(new File(webInfDir, ORACLE_WEBSERVICES_FILENAME));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getCodeGenProviderStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildUnderDBWS();
                return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_CLASS_FILE));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    public OutputStream getSourceProviderStream() throws FileNotFoundException {
        if (stageDir != null) {
            if (!javaseMode) {
                buildUnderDBWS();
                return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_SOURCE_FILE));
            }
            else {
                return __nullStream;
            }
        }
        else {
            throw new DBWSException("DBWSBuilder packager - stage directory cannot be null");
        }
    }

    protected void buildWebInfDir() throws FileNotFoundException {
        webInfDir = new File(stageDir, WEB_INF_PATHS[1]);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[1] + " under " + stageDir);
            }
        }
    }

    protected void buildWSDLDir() throws FileNotFoundException {
        buildWebInfDir();
        wsdlDir = new File(webInfDir, WSDL_DIR);
        if (!wsdlDir.exists()) {
            boolean worked = wsdlDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WSDL_DIR + " under " + WEB_INF_PATHS[1] + stageDir);
            }
        }
    }

    protected void buildClassesDir() throws FileNotFoundException {
        buildWebInfDir();
        classesDir = new File(webInfDir, CLASSES_DIR);
        if (!classesDir.exists()) {
            boolean worked = classesDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[1] + "/" + CLASSES_DIR + " dir under " + stageDir);
            }
        }
    }

    protected void buildUnderDBWS() throws FileNotFoundException {
        buildClassesDir();
        underDBWSDir = new File(classesDir, UNDER_DBWS);
        if (!underDBWSDir.exists()) {
            boolean worked = underDBWSDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[1] + "/" + CLASSES_DIR + "/" + UNDER_DBWS + " dir under " + stageDir);
            }
        }
    }

    protected void buildMetaInfDir() throws FileNotFoundException {
        buildClassesDir();
        metaDir = new File(classesDir, META_INF_PATHS[0]);
        if (!metaDir.exists()) {
            boolean worked = metaDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[0] + "/" + CLASSES_DIR + "/" +
                    META_INF_PATHS[0] + " dir under " + stageDir);
            }
        }
    }
}
