/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.simpletablewithnestedsql;

//javase imports
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//JUnit4 imports
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import static org.eclipse.persistence.internal.dbws.ProviderHelper.MATCH_SCHEMA;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//domain-specific (testing) imports
import dbws.testing.DBWSTestSuite;

public class SimpleTableWithNestedSQLTestSuite extends DBWSTestSuite  {

	public final static String FINDBYNAME_RESPONSETYPE = "findByNameResponseType";
	public final static String TABLE_ALIAS ="ns1:simpletable2Type";
	
    @Test
    public void checkWSDL() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">simpletable2</property>" +
                "<property name=\"logLevel\">off</property>" +
                "<property name=\"username\">";
        DBWS_BUILDER_XML_PASSWORD =
                "</property><property name=\"password\">";
        DBWS_BUILDER_XML_URL =
                "</property><property name=\"url\">";
        DBWS_BUILDER_XML_DRIVER =
                "</property><property name=\"driver\">";
        DBWS_BUILDER_XML_PLATFORM =
                "</property><property name=\"platformClassname\">";
        DBWS_BUILDER_XML_MAIN =
              "</property>" +
          "</properties>" +
          "<table " +
            "schemaPattern=\"%\" " +
            "tableNamePattern=\"simpletable2\" " +
            ">" +
            "<sql " +
              "name=\"findByName\" " +
              "isCollection=\"true\" " +
              "returnType=\"simpletable2Type\" " +
              ">" +
              "<text><![CDATA[select * from SIMPLETABLE2 where NAME like ?]]></text>" +
              "<binding name=\"NAME\" type=\"xsd:string\"/>" +
            "</sql>" +
          "</table>" +
        "</dbws-builder>";
    	String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
        password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
        DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
            (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
        DBWSBuilder builder = new DBWSBuilder(); 
        builder.quiet = true;
        builder.setPlatformClassname(platform);
        builder.properties = builderModel.properties;
        builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
        builder.operations = builderModel.operations;
        builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
            	// do nothing - don't have to verify existence of 'stageDir' when
            	// all the streams are in-memory
            }
        });
        ByteArrayOutputStream dbwsServiceStream = new ByteArrayOutputStream();
        ByteArrayOutputStream wsdlStream = new ByteArrayOutputStream();
        builder.build(__nullStream, __nullStream, dbwsServiceStream, __nullStream, __nullStream,
        	__nullStream, __nullStream, wsdlStream, __nullStream, __nullStream, __nullStream,
        	__nullStream, null);
        // verify that the generated WSDL has the correct response type
        // for the nested sql operation 'findByName'
        try {
            StringWriter sw = new StringWriter();
            StreamSource wsdlStreamSource = new StreamSource(new StringReader(wsdlStream.toString()));
        	Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(
        	    new StringReader(MATCH_SCHEMA)));
        	StreamResult streamResult = new StreamResult(sw);
        	t.transform(wsdlStreamSource, streamResult);
        	sw.toString();
            SchemaModelProject schemaProject = new SchemaModelProject();
            XMLContext xmlContext2 = new XMLContext(schemaProject);
            unmarshaller = xmlContext2.createUnmarshaller();
            Schema schema = (Schema)unmarshaller.unmarshal(new StringReader(sw.toString()));
            ComplexType findByNameResponseType = 
            	(ComplexType) schema.getTopLevelComplexTypes().get(FINDBYNAME_RESPONSETYPE);
            Element result = (Element)findByNameResponseType.getSequence().getElements().get(0);
            Element unnamed = (Element)result.getComplexType().getSequence().getElements().get(0);
            assertTrue("wrong refType for " + FINDBYNAME_RESPONSETYPE,
            	TABLE_ALIAS.equals(unnamed.getRef()));
        }
        catch (Exception e) {
        	fail(e.getMessage());
        }
    }
}