/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - Aug 2008, created DBWS JDev(Boxer) packager
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

// EclipseLink imports
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSPackager;
import org.eclipse.persistence.tools.dbws.WebFilesPackager;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;

/**
 * <p>
 * <b>INTERNAL:</b> JDevPackager implements the {@link DBWSPackager} interface.
 * The output files from the {@link DBWSBuilder} are written to the <tt>stageDir</tt>.
 * <pre>
 * \--- JDev <b>Projectnnn</b> root directory
 *    |   application.xml
 *    |   build.properties
 *    |   build.xml
 *    |   data-sources.xml
 *    |   dbws-builder.xml
 *    |   Projectnnn.jpr
 *    |
 *    +---<b>public_html</b>
 *    |   \---WEB-INF
 *    |       |   web.xml
 *    |       |
 *    |       \---wsdl
 *    |               swaref.xsd                  -- optional if attachements are enabled
 *    |               eclipselink-dbws-schema.xsd
 *    |               eclipselink-dbws.wsdl
 *    |
 *    \---<b>src</b>
 *        |   eclipselink-dbws-or.xml
 *        |   eclipselink-dbws-ox.xml
 *        |   eclipselink-dbws-sessions.xml
 *        |   eclipselink-dbws.xml
 *        |
 *        \---_dbws
 *                DBWSProvider.java
 * </pre>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class JDevPackager extends WebFilesPackager implements DBWSPackager {

    public static final String WEB_XML_PREAMBLE =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.5\">\n" +
    "  <servlet>\n" +
    "    <servlet-name>DBWSProvider</servlet-name>\n" +
    "    <servlet-class>_dbws.DBWSProvider</servlet-class>\n" +
    "    <load-on-startup>1</load-on-startup>\n" +
    "  </servlet>\n" +
    "  <servlet-mapping>\n" +
    "    <servlet-name>DBWSProvider</servlet-name>\n" +
    "    <url-pattern>";
        // contextRoot ^^ here
    public static final String WEB_XML_POSTSCRIPT =
            "</url-pattern>\n" +
        "  </servlet-mapping>\n" +
        "</web-app>\n";

    public static final String SRC_DIR = "src";
    public static final String PUBLIC_HTML_DIR = "public_html";

    protected File srcDir;
    protected File publicHTMLDir;
    protected File webInfDir;
    protected File wsdlDir;
    protected File underDBWSDir;

	public JDevPackager() {
        super();
    }
    public JDevPackager(boolean useArchiver) {
        super(useArchiver);
    }
    public JDevPackager(boolean useArchiver, String warName) {
        super(useArchiver, warName);
    }

    // create streams according to JDev project layout (see above)

    @Override
    public OutputStream getSchemaStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
    }

    @Override
    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, sessionsFileName));
    }

    @Override
    public OutputStream getServiceStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_SERVICE_XML));
    }

    @Override
    public OutputStream getOrStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OR_XML));
    }

    @Override
    public OutputStream getOxStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OX_XML));
    }

    @Override
    public OutputStream getWSDLStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_WSDL));
    }

    @Override
    public OutputStream getSWARefStream() throws FileNotFoundException {
        if (hasAttachments) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, SWAREF_FILENAME));
        }
        else {
            return __nullStream;
        }
    }

    @Override
    public OutputStream getWebXmlStream() throws FileNotFoundException {
        buildWebInfDir();
        return new FileOutputStream(new File(webInfDir, WEB_XML_FILENAME));
    }

    @Override
    public OutputStream getSourceProviderStream() throws FileNotFoundException {
        buildUnderDBWS();
        return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_SOURCE_FILE));
    }

    // streams not used
    @Override
    public OutputStream getWebservicesXmlStream() throws FileNotFoundException {
        // TBD - figure out WLS 10.n webservice.xml format
        return __nullStream;
    }
    @Override
    public void writeWebservicesXML(OutputStream webservicesXmlStream, DBWSBuilder builder) {
        // do nothing (for now - see note above for getWebservicesXmlStream)
    }

    @Override
    public OutputStream getCodeGenProviderStream() throws FileNotFoundException {
        return __nullStream;
    }
    @Override
    public void writeDBWSProviderClass(OutputStream codeGenProviderStream, DBWSBuilder builder) {
        // do nothing
    }

    @Override
    public void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(WEB_XML_PREAMBLE);
        sb.append(dbwsBuilder.getContextRoot());
        sb.append(WEB_XML_POSTSCRIPT);
        OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(webXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
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
        webInfDir = new File(publicHTMLDir, WEB_INF_DIR);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_DIR + " under " + PUBLIC_HTML_DIR);
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
                    WSDL_DIR + " under " + WEB_INF_DIR);
            }
        }
    }
}