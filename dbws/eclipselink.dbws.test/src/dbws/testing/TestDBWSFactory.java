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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package dbws.testing;

// Javase imports
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import org.w3c.dom.Document;

//Java extension imports
import javax.wsdl.WSDLException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.MySQL4Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.SimpleJarPackager;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.exceptions.DBWSException;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;

public class TestDBWSFactory extends XRServiceFactory {

    public final static String DATABASE_USERNAME_KEY = "db.user";
    public final static String DATABASE_PASSWORD_KEY = "db.pwd";
    public final static String DATABASE_URL_KEY = "db.url";
    public final static String DATABASE_DRIVER_KEY = "db.driver";
    public final static String DATABASE_PLATFORM_KEY = "db.platform";
    public final static String DEFAULT_DATABASE_USERNAME = "MNORMAN";
    public final static String DEFAULT_DATABASE_PASSWORD = "password";
    public final static String DEFAULT_DATABASE_URL = "jdbc:mysql://tlsvrdb4.ca.oracle.com/" +
        DEFAULT_DATABASE_USERNAME;
    public final static String DEFAULT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public final static String DEFAULT_DATABASE_PLATFORM =
        MySQL4Platform.class.getName();
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();

    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }

    public static void buildJar(String builderString, String jarName) throws WSDLException {
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
        	(DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
    	DBWSBuilder dbwsBuilder = new DBWSBuilder();
    	dbwsBuilder.quiet = true;
    	dbwsBuilder.properties = builderModel.properties;
    	dbwsBuilder.operations = builderModel.operations;
    	SimpleJarPackager simpleDBWSJarPackager = new SimpleJarPackager("dbws" + jarName + ".jar");
    	simpleDBWSJarPackager.setStageDir(new File("."));
    	dbwsBuilder.setPackager(simpleDBWSJarPackager);
    	dbwsBuilder.start();
    }

    public TestDBWSFactory() {
        super();
        parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = ClassLoader.getSystemClassLoader();
        }
    }

    @Override
    public XRServiceAdapter buildService() {

        InputStream dbwsServiceStream = null;
        for (String searchPath : META_INF_PATHS) {
            String path = searchPath + DBWS_SERVICE_XML;
            dbwsServiceStream = parentClassLoader.getResourceAsStream(path);
            if (dbwsServiceStream != null) {
                break;
            }
        }
        if (dbwsServiceStream == null) {
            throw DBWSException.couldNotLocateFile(DBWS_SERVICE_XML);
        }
        DBWSModelProject xrServiceModelProject = new DBWSModelProject();
        XMLContext xmlContext = new XMLContext(xrServiceModelProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        XRServiceModel dbwsModel = (XRServiceModel)unmarshaller.unmarshal(dbwsServiceStream);
        xrSchemaStream = parentClassLoader.getResourceAsStream(DBWS_SCHEMA_XML);
        if (xrSchemaStream == null) {
            xrSchemaStream = parentClassLoader.getResourceAsStream("/" + DBWS_SCHEMA_XML);
            if (xrSchemaStream == null) {
                xrSchemaStream = parentClassLoader.getResourceAsStream("\\" + DBWS_SCHEMA_XML);
                if (xrSchemaStream == null) {
                	throw DBWSException.couldNotLocateFile(DBWS_SCHEMA_XML);
            	}
            }
        }
        return buildService(dbwsModel);
    }
}