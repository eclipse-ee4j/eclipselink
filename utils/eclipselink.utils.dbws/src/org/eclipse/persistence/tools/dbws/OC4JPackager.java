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

/**
 * <p>
 * <b>INTERNAL:</b> OC4JPackager implements the {@link DBWSPackager} interface.
 * The output files from the {@link DBWSBuilder} are written to a <tt>.war</tt> file in
 * the <tt>stageDir</tt>.
 * <pre>
 * \--- expanded .war file
 *      |
 *      \---web-inf
 *          |   oracle-webservices.xml
 *          |   web.xml
 *          |
 *          +---classes
 *          |   +---META-INF
 *          |   |    eclipselink-dbws.xml
 *          |   |    eclipselink-dbws-sessions.xml
 *          |   |    eclipselink-dbws-or.xml
 *          |   |    eclipselink-dbws-ox.xml
 *          |   |
 *          |   \---_dbws
 *          |        DBWSProvider.class        -- code-generated javax.xml.ws.Provider
 *          |        DBWSProvider.java
 *          |
 *          \---wsdl
 *                 swaref.xsd                  -- optional if attachements are enabled
 *                 eclipselink-dbws.wsdl
 *                 eclipselink-dbws-schema.xsd
 * </pre>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class OC4JPackager extends WebFilesPackager implements DBWSPackager {

    public static final String WEB_XML_PREAMBLE =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<web-app\n" +
    "  xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee" +
    "  http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n" +
    "  version=\"2.5\"\n" +
    "  >\n" +
    "  <servlet>\n" +
    "    <servlet-name>DBWSProvider</servlet-name>\n" +
    "    <display-name>DBWSProvider</display-name>\n" +
    "    <description>JAX-WS endpoint Provider</description>\n" +
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
	public static final String ORACLE_WEBSERVICES_FILENAME =
		"oracle-webservices.xml";
    public static final String ORACLE_WEBSERVICES_PREAMBLE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<oracle-webservices \n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
        "  xsi:noNamespaceSchemaLocation=\"http://xmlns.oracle.com/oracleas/schema/oracle-webservices-11_1.xsd\" \n" +
        "  > \n" +
        "  <webservice-description name=\"";
                           // serviceName ^^ here
    public static final String ORACLE_WEBSERVICES_PORT_COMPONENT_NAME =
                                             "\"> \n" +
        "    <port-component name=\"";
              // dotted-format serviceName.portName ^^ here
    public static final String ORACLE_WEBSERVICES_SUFFIX =
                                        "\"> \n" +
        "      <policy-references>\n" +
        "        <!-- add policies here -->\n" +
        "      </policy-references>\n" +
        "    </port-component>\n" +
        "  </webservice-description>\n" +
        "</oracle-webservices>";

	public OC4JPackager() {
        super();
    }
    public OC4JPackager(boolean useArchiver) {
        super(useArchiver);
    }
    public OC4JPackager(boolean useArchiver, String warName) {
        super(useArchiver, warName);
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

	@Override
	public OutputStream getPlatformWebservicesXmlStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, ORACLE_WEBSERVICES_FILENAME));
	}
    @Override
    public String getPlatformWebservicesFilename() {
        return ORACLE_WEBSERVICES_FILENAME;
    }
    @Override
    public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
        DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(ORACLE_WEBSERVICES_PREAMBLE);
        String serviceName = dbwsBuilder.getWSDLGenerator().getServiceName();
        sb.append(serviceName);
        sb.append(ORACLE_WEBSERVICES_PORT_COMPONENT_NAME);
        sb.append(serviceName + "." + serviceName);
        sb.append(ORACLE_WEBSERVICES_SUFFIX);
        OutputStreamWriter osw = new OutputStreamWriter(
            new BufferedOutputStream(platformWebservicesXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }

}