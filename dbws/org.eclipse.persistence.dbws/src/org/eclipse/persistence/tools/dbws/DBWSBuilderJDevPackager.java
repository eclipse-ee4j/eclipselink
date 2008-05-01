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
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_PATHS;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.ORACLE_WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSDL_DIR;

/**
 * <p>
 * <b>INTERNAL:</b> DBWSBuilderJDevPackager implements the {@link DBWSBuilderPackager} interface
 * so that the output from the {@link DBWSBuilder} is written to the <tt>src</tt> and
 * <tt>public_html</tt> directories as JDev expects.
 * <p>
 *
 * For this packager, the <tt>stageDir</tt> is the Project's root directory
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 * <pre>
 * \--- JDev <b>Projectnnn</b> root directory
 *    |   application.xml
 *    |   build.properties
 *    |   build.xml
 *    |   data-sources.xml
 *    |   dbws-builder.xml
 *    |   orion-application.xml
 *    |   Projectnnn.jpr
 *    |
 *    +---<b>public_html</b>
 *    |   \---WEB-INF
 *    |       |   oracle-webservices.xml
 *    |       |   web.xml
 *    |       |
 *    |       \---wsdl
 *    |               swaref.xsd                  -- optional if attachements are enabled
 *    |               toplink-dbws-schema.xsd
 *    |               toplink-dbws.wsdl
 *    |
 *    \---<b>src</b>
 *        |   toplink-dbws-or.xml
 *        |   toplink-dbws-ox.xml
 *        |   toplink-dbws-sessions.xml
 *        |   toplink-dbws.xml
 *        |
 *        \---_dbws
 *                DBWSProvider.java
 * </pre>
 */
public class DBWSBuilderJDevPackager extends DBWSBaseBuilderPackager implements DBWSBuilderPackager {

    public static final String SRC_DIR = "src";
    public static final String PUBLIC_HTML_DIR = "public_html";

    protected File srcDir;
    protected File publicHTMLDir;
    protected File webInfDir;
    protected File wsdlDir;
    protected File underDBWSDir;

    // default constructor
    public DBWSBuilderJDevPackager() {
        super();
    }

    public OutputStream getSchemaStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, sessionsFileName));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getServiceStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_SERVICE_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getOrStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_OR_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
        }
    }

    public OutputStream getOxStream() throws FileNotFoundException {
        if (stageDir != null) {
            buildSrcDir();
            return new FileOutputStream(new File(srcDir, DBWS_OX_XML));
        }
        else {
            throw new DBWSException("DBWSBuilderJDevPackager - Project root directory cannot be null");
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
        return __nullStream;
    }
    @Override
    public void closeCodeGenProviderStream(OutputStream codeGenProviderStream) {
        // do nothing
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

    protected void buildSrcDir() throws FileNotFoundException {
        srcDir = new File(stageDir, SRC_DIR);
        if (!srcDir.exists()) {
            boolean worked = srcDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    SRC_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildUnderDBWS() throws FileNotFoundException {
        buildSrcDir();
        underDBWSDir = new File(srcDir, UNDER_DBWS);
        if (!underDBWSDir.exists()) {
            boolean worked = underDBWSDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + SRC_DIR + "/" + UNDER_DBWS +
                    " dir under " + stageDir);
            }
        }
    }

    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, PUBLIC_HTML_DIR);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    PUBLIC_HTML_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildWebInfDir() throws FileNotFoundException {
        buildPublicHTMLDir();
        webInfDir = new File(publicHTMLDir, WEB_INF_PATHS[1]);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_PATHS[1] + " under " + PUBLIC_HTML_DIR);
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
                    WSDL_DIR + " under " + WEB_INF_PATHS[1]);
            }
        }
    }
}
