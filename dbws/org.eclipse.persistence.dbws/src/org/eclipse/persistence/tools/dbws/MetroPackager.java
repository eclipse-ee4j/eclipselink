package org.eclipse.persistence.tools.dbws;

// javase imports
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

// EclipseLink imports
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_PACKAGE;

public class MetroPackager extends WebFilesPackager {

    public static final String SUN_JAXWS_FILENAME =
        "sun-jaxws.xml";
    
    public static final String WEB_XML_PREAMBLE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<web-app\n" +
        "  xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee" +
        "  http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n" +
        "  version=\"2.5\"\n" +
        "  >\n" +
        "  <listener>\n" +
        "    <listener-class>com.sun.xml.ws.transport.http.servlet.WSServletContextListener</listener-class>\n" +
        "  </listener>\n" +
        "  <servlet>\n" +
        "    <servlet-name>";
    public static final String WEB_XML_SERVLET_NAME = 
                           "</servlet-name>\n" +
        "    <servlet-class>com.sun.xml.ws.transport.http.servlet.WSServlet</servlet-class>\n" +
        "    <load-on-startup>1</load-on-startup>\n" +
        "  </servlet>\n" +
        "  <servlet-mapping>\n" +
        "    <servlet-name>";
    public static final String WEB_XML_SERVLET_MAPPING =
                           "</servlet-name>\n" +
        "    <url-pattern>";
    public static final String WEB_XML_URL_PATTERN =
                          "</url-pattern>\n" +
        "  </servlet-mapping>\n" +
        "  <session-config>\n" +
        "    <session-timeout>60</session-timeout>\n" +
        "  </session-config>\n" +
        "</web-app>";
    
    public static final String SUN_JAXWS_PREABLE = 
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<endpoints" +
        "  xmlns=\"http://java.sun.com/xml/ns/jax-ws/ri/runtime\"\n" +
        "  version=\"2.0\"\n>" +
        "  <endpoint\n" +
        "    name=\"";
    public static final String SUN_JAXWS_IMPLEMENTATION = 
                  "\"\n" +
        "    implementation=\"" + DBWS_PROVIDER_PACKAGE + "." + DBWS_PROVIDER_NAME + "\"\n" +
        "    url-pattern=\"";
    public static final String SUN_JAXWS_POSTSCRIPT =
                         "\"/>\n" +
        "</endpoints>"; 
    
    public MetroPackager() {
        super();
    }
    public MetroPackager(boolean useArchiver) {
        super(useArchiver);
    }
    public MetroPackager(boolean useArchiver, String warName) {
        super(useArchiver, warName);
    }
    
    public void writeWebXml(OutputStream webXmlStream, DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(WEB_XML_PREAMBLE);
        String serviceName = dbwsBuilder.getWSDLGenerator().getServiceName();
        sb.append(serviceName);
        sb.append(WEB_XML_SERVLET_NAME);
        sb.append(serviceName);
        sb.append(WEB_XML_SERVLET_MAPPING);
        sb.append(dbwsBuilder.getContextRoot());
        sb.append(WEB_XML_URL_PATTERN);
        OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(webXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }

    @Override
    public String getPlatformWebservicesFilename() {
        return SUN_JAXWS_FILENAME;
    }
    @Override
    public void writePlatformWebservicesXML(OutputStream platformWebservicesXmlStream,
        DBWSBuilder dbwsBuilder) {
        StringBuilder sb = new StringBuilder(SUN_JAXWS_PREABLE);
        String serviceName = dbwsBuilder.getWSDLGenerator().getServiceName();
        sb.append(serviceName);
        sb.append(SUN_JAXWS_IMPLEMENTATION);
        sb.append(dbwsBuilder.getContextRoot());
        sb.append(SUN_JAXWS_POSTSCRIPT);
        OutputStreamWriter osw = new OutputStreamWriter(
            new BufferedOutputStream(platformWebservicesXmlStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }
    
}