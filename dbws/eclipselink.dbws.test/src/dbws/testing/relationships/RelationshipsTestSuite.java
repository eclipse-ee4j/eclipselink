/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package dbws.testing.relationships;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicEntity_CollectionWrapper;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;

//testing imports
import static dbws.testing.DBWSTestHelper.DATABASE_DRIVER_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_USERNAME_KEY;

public class RelationshipsTestSuite {

    static final String RELATIONSHIPS_SCHEMA =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<xsd:schema\n" +
          "targetNamespace=\"urn:relationships\" xmlns=\"urn:relationships\" elementFormDefault=\"qualified\"\n" +
          "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
          "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
          ">\n" +
          "<xsd:complexType name=\"phone\">\n" +
            "<xsd:sequence>\n" +
              "<xsd:element name=\"area-code\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"phonenumber\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"type\" type=\"xsd:string\" />\n" +
            "</xsd:sequence>\n" +
          "</xsd:complexType>\n" +
          "<xsd:element name=\"phone\" type=\"phone\"/>\n" +
          "<xsd:complexType name=\"address\">\n" +
            "<xsd:sequence>\n" +
              "<xsd:element name=\"id\" type=\"xsd:int\" />\n" +
              "<xsd:element name=\"street\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"city\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"province\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"postal-code\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"country\" type=\"xsd:string\" />\n" +
            "</xsd:sequence>\n" +
          "</xsd:complexType>\n" +
          "<xsd:element name=\"address\" type=\"address\"/>\n" +
          "<xsd:complexType name=\"employee\">\n" +
            "<xsd:sequence>\n" +
              "<xsd:element name=\"id\" type=\"xsd:int\" />\n" +
              "<xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
             "<xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"address\" type=\"address\" minOccurs=\"0\" />\n" +
              "<xsd:sequence>\n" +
                "<xsd:element name=\"phones\" type=\"phone\" minOccurs=\"0\" />\n" +
              "</xsd:sequence>\n" +
              "<xsd:sequence>\n" +
                "<xsd:element name=\"responsibilities\" type=\"xsd:string\" minOccurs=\"0\" />\n" +
              "</xsd:sequence>\n" +
              "<xsd:element name=\"start-time\" type=\"xsd:time\" />\n" +
              "<xsd:element name=\"end-time\" type=\"xsd:time\" />\n" +
              "<xsd:element name=\"start-date\" type=\"xsd:date\" />\n" +
              "<xsd:element name=\"end-date\" type=\"xsd:date\" />\n" +
              "<xsd:element name=\"gender\" type=\"xsd:string\" />\n" +
              "<xsd:element name=\"salary\" type=\"xsd:decimal\" />\n" +
              "<xsd:element name=\"version\" type=\"xsd:int\"/>\n" +
            "</xsd:sequence>\n" +
          "</xsd:complexType>\n" +
          "<xsd:element name=\"employee\" type=\"employee\"/>\n" +
        "</xsd:schema>";
    static final String RELATIONSHIPS_DBWS =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<dbws\n" +
         "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
         "xmlns:ns1=\"urn:relationships\"\n" +
         ">\n" +
         "<name>relationships</name>\n" +
          "<query>\n" +
           "<name>getAllEmployees</name>\n" +
           "<result isCollection=\"true\">\n" +
             "<type>ns1:employee</type>\n" +
           "</result>\n" +
           "<named-query>\n" +
             "<name>getAllEmployees</name>\n" +
             "<descriptor>employee</descriptor>\n" +
           "</named-query>\n" +
          "</query>\n" +
        "</dbws>";
    public static final String RELATIONSHIPS_OR_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
          "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
          "xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\"\n" +
          ">\n" +
           "<name>relationships</name>" +
           "<class-mapping-descriptors>" +
              "<class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
                 "<class>dbws.testing.relationships.RelationshipsAddress</class>" +
                 "<alias>address</alias>" +
                 "<primary-key>" +
                    "<field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
                 "</primary-key>" +
                 "<events xsi:type=\"event-policy\"/>" +
                 "<querying xsi:type=\"query-policy\"/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>addressId</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>city</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"CITY\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>country</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"COUNTRY\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>postalCode</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"P_CODE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>province</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"PROVINCE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>street</attribute-name>" +
                       "<field table=\"XR_ADDRESS\" name=\"STREET\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>independent</descriptor-type>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
                 "<tables>" +
                    "<table name=\"XR_ADDRESS\"/>" +
                 "</tables>" +
              "</class-mapping-descriptor>" +
              "<class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
                 "<class>dbws.testing.relationships.RelationshipsEmployee</class>" +
                 "<alias>employee</alias>" +
                 "<primary-key>" +
                    "<field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                 "</primary-key>" +
                 "<events xsi:type=\"event-policy\"/>" +
                 "<querying xsi:type=\"query-policy\">" +
                    "<queries>" +
                       "<query name=\"getAllEmployees\" xsi:type=\"read-all-query\">" +
                          "<reference-class>dbws.testing.relationships.RelationshipsEmployee</reference-class>" +
                          "<outer-join-subclasses>false</outer-join-subclasses>" +
                          "<container xsi:type=\"list-container-policy\">" +
                             "<collection-type>java.util.Vector</collection-type>" +
                          "</container>" +
                          "<order-by-expressions>" +
                             "<expression function=\"ascending\" xsi:type=\"function-expression\">" +
                                "<arguments>" +
                                   "<argument name=\"empId\" xsi:type=\"query-key-expression\">" +
                                      "<base xsi:type=\"base-expression\"/>" +
                                   "</argument>" +
                                "</arguments>" +
                             "</expression>" +
                          "</order-by-expressions>" +
                       "</query>" +
                    "</queries>" +
                 "</querying>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"one-to-one-mapping\">" +
                       "<attribute-name>address</attribute-name>" +
                       "<reference-class>dbws.testing.relationships.RelationshipsAddress</reference-class>" +
                       "<private-owned>true</private-owned>" +
                       "<foreign-key>" +
                          "<field-reference>" +
                             "<source-field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"column\"/>" +
                             "<target-field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
                          "</field-reference>" +
                       "</foreign-key>" +
                       "<foreign-key-fields>" +
                          "<field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"column\"/>" +
                       "</foreign-key-fields>" +
                       "<selection-query xsi:type=\"read-object-query\"/>" +
                       "<join-fetch>inner-join</join-fetch>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>empId</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>endDate</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"END_DATE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>endTime</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"END_TIME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>firstName</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"F_NAME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>gender</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"GENDER\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>lastName</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"L_NAME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"one-to-many-mapping\">" +
                       "<attribute-name>phones</attribute-name>" +
                       "<reference-class>dbws.testing.relationships.RelationshipsPhone</reference-class>" +
                       "<private-owned>true</private-owned>" +
                       "<target-foreign-key>" +
                          "<field-reference>" +
                             "<source-field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                             "<target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                          "</field-reference>" +
                       "</target-foreign-key>" +
                       "<batch-reading>true</batch-reading>" +
                       "<container xsi:type=\"container-policy\">" +
                          "<collection-type>java.util.ArrayList</collection-type>" +
                       "</container>" +
                       "<selection-query xsi:type=\"read-all-query\">" +
                          "<container xsi:type=\"container-policy\">" +
                             "<collection-type>java.util.ArrayList</collection-type>" +
                          "</container>" +
                          "<order-by-expressions>" +
                             "<expression function=\"ascending\" xsi:type=\"function-expression\">" +
                                "<arguments>" +
                                   "<argument name=\"type\" xsi:type=\"query-key-expression\">" +
                                      "<base xsi:type=\"base-expression\"/>" +
                                   "</argument>" +
                                "</arguments>" +
                             "</expression>" +
                          "</order-by-expressions>" +
                       "</selection-query>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-collection-mapping\">" +
                       "<attribute-name>responsibilities</attribute-name>" +
                       "<batch-reading>true</batch-reading>" +
                       "<container xsi:type=\"container-policy\">" +
                          "<collection-type>java.util.ArrayList</collection-type>" +
                       "</container>" +
                       "<selection-query xsi:type=\"direct-read-query\">" +
                          "<maintain-cache>false</maintain-cache>" +
                          "<container xsi:type=\"container-policy\">" +
                             "<collection-type>java.util.ArrayList</collection-type>" +
                          "</container>" +
                       "</selection-query>" +
                       "<reference-table>XR_RESPONS</reference-table>" +
                       "<direct-field table=\"XR_RESPONS\" name=\"DESCRIP\" xsi:type=\"column\"/>" +
                       "<reference-foreign-key>" +
                          "<field-reference>" +
                             "<source-field table=\"XR_RESPONS\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                             "<target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                          "</field-reference>" +
                       "</reference-foreign-key>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>salary</attribute-name>" +
                       "<field table=\"XR_SALARY\" name=\"SALARY\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>startDate</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"START_DATE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>startTime</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"START_TIME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>version</attribute-name>" +
                       "<field table=\"XR_EMPLOYEE\" name=\"VERSION\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>independent</descriptor-type>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
                 "<tables>" +
                    "<table name=\"XR_EMPLOYEE\"/>" +
                    "<table name=\"XR_SALARY\"/>" +
                 "</tables>" +
              "</class-mapping-descriptor>" +
              "<class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
                 "<class>dbws.testing.relationships.RelationshipsPhone</class>" +
                 "<alias>phone</alias>" +
                 "<primary-key>" +
                    "<field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                    "<field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"column\"/>" +
                 "</primary-key>" +
                 "<events xsi:type=\"event-policy\"/>" +
                 "<querying xsi:type=\"query-policy\"/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>areaCode</attribute-name>" +
                       "<field table=\"XR_PHONE\" name=\"AREA_CODE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>empId</attribute-name>" +
                       "<field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>phonenumber</attribute-name>" +
                       "<field table=\"XR_PHONE\" name=\"P_NUMBER\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>type</attribute-name>" +
                       "<field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>independent</descriptor-type>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
                 "<tables>" +
                    "<table name=\"XR_PHONE\"/>" +
                 "</tables>" +
              "</class-mapping-descriptor>" +
              "<class-mapping-descriptor xsi:type=\"class-mapping-descriptor\">" +
                 "<class>org.eclipse.persistence.internal.xr.XRDynamicEntity_CollectionWrapper</class>" +
                 "<alias>XRDynamicEntity_CollectionWrapper</alias>" +
                 "<events xsi:type=\"event-policy\"/>" +
                 "<querying xsi:type=\"query-policy\"/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"aggregate-collection-mapping\">" +
                       "<attribute-name>items</attribute-name>" +
                       "<private-owned>true</private-owned>" +
                       "<cascade-persist>true</cascade-persist>" +
                       "<cascade-merge>true</cascade-merge>" +
                       "<cascade-refresh>true</cascade-refresh>" +
                       "<cascade-remove>true</cascade-remove>" +
                       "<container xsi:type=\"list-container-policy\">" +
                          "<collection-type>java.util.ArrayList</collection-type>" +
                       "</container>" +
                       "<selection-query xsi:type=\"read-all-query\">" +
                          "<container xsi:type=\"list-container-policy\">" +
                             "<collection-type>java.util.ArrayList</collection-type>" +
                          "</container>" +
                       "</selection-query>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>aggregate</descriptor-type>" +
                 "<caching>" +
                    "<cache-size>-1</cache-size>" +
                 "</caching>" +
                 "<remote-caching>" +
                    "<cache-size>-1</cache-size>" +
                 "</remote-caching>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
              "</class-mapping-descriptor>" +
           "</class-mapping-descriptors>" +
           "<login xsi:type=\"database-login\">\n" +
              "<bind-all-parameters>true</bind-all-parameters>\n" +
           "</login>\n" +
        "</object-persistence>";

    static final String RELATIONSHIPS_OX_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
          "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
          "xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\"\n" +
          ">\n" +
          "<name>relationships</name>\n" +
          "<class-mapping-descriptors>\n" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>dbws.testing.relationships.RelationshipsAddress</class>\n" +
                 "<alias>address</alias>\n" +
                 "<events xsi:type=\"event-policy\"/>\n" +
                 "<querying xsi:type=\"query-policy\"/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>addressId</attribute-name>\n" +
                       "<field name=\"@address-id/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>street</attribute-name>\n" +
                       "<field name=\"street/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>city</attribute-name>\n" +
                       "<field name=\"city/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>province</attribute-name>\n" +
                       "<field name=\"province/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>postalCode</attribute-name>\n" +
                       "<field name=\"postal-code/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>country</attribute-name>\n" +
                       "<field name=\"country/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<default-root-element>address</default-root-element>\n" +
                 "<namespace-resolver>" +
                   "<default-namespace-uri>urn:relationships</default-namespace-uri>" +
                 "</namespace-resolver>" +
                 "<schema xsi:type=\"schema-url-reference\">" +
                   "<resource></resource>" +
                   "<schema-context>/address</schema-context>" +
                   "<node-type>complex-type</node-type>" +
                 "</schema>" +
              "</class-mapping-descriptor>\n" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>dbws.testing.relationships.RelationshipsEmployee</class>\n" +
                 "<alias>employee</alias>\n" +
                 "<events xsi:type=\"event-policy\"/>\n" +
                 "<querying xsi:type=\"query-policy\"/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>empId</attribute-name>\n" +
                       "<field name=\"@emp-id/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>firstName</attribute-name>\n" +
                       "<field name=\"first-name/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>lastName</attribute-name>\n" +
                       "<field name=\"last-name/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-composite-object-mapping\">\n" +
                       "<attribute-name>address</attribute-name>\n" +
                       "<reference-class>dbws.testing.relationships.RelationshipsAddress</reference-class>\n" +
                       "<field name=\"address\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-composite-collection-mapping\">\n" +
                       "<attribute-name>phones</attribute-name>\n" +
                       "<reference-class>dbws.testing.relationships.RelationshipsPhone</reference-class>\n" +
                       "<field name=\"phones/phone\" xsi:type=\"node\"/>\n" +
                       "<container xsi:type=\"container-policy\">\n" +
                          "<collection-type>java.util.Vector</collection-type>\n" +
                       "</container>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-composite-direct-collection-mapping\">\n" +
                       "<attribute-name>responsibilities</attribute-name>\n" +
                       "<field name=\"responsibilities/responsibility/text()\" xsi:type=\"node\"/>\n" +
                       "<container xsi:type=\"container-policy\">\n" +
                          "<collection-type>java.util.Vector</collection-type>\n" +
                       "</container>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>startTime</attribute-name>\n" +
                       "<field name=\"start-time/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}time</schema-type>\n" +
                       "</field>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>startDate</attribute-name>\n" +
                       "<field name=\"start-date/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}date</schema-type>\n" +
                       "</field>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>endDate</attribute-name>\n" +
                       "<field name=\"end-date/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}date</schema-type>\n" +
                       "</field>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>endTime</attribute-name>\n" +
                       "<field name=\"end-time/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}time</schema-type>\n" +
                       "</field>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>gender</attribute-name>\n" +
                       "<field name=\"gender/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>salary</attribute-name>\n" +
                       "<field name=\"salary/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>version</attribute-name>\n" +
                       "<field name=\"version/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<default-root-element>employee</default-root-element>\n" +
                 "<namespace-resolver>" +
                   "<default-namespace-uri>urn:relationships</default-namespace-uri>" +
                 "</namespace-resolver>" +
                 "<schema xsi:type=\"schema-url-reference\">" +
                   "<resource></resource>" +
                   "<schema-context>/employee</schema-context>" +
                   "<node-type>complex-type</node-type>" +
                 "</schema>" +
              "</class-mapping-descriptor>\n" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>dbws.testing.relationships.RelationshipsPhone</class>\n" +
                 "<alias>phone</alias>\n" +
                 "<events xsi:type=\"event-policy\"/>\n" +
                 "<querying xsi:type=\"query-policy\"/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>areaCode</attribute-name>\n" +
                       "<field name=\"area-code/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>phonenumber</attribute-name>\n" +
                       "<field name=\"phonenumber/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>type</attribute-name>\n" +
                       "<field name=\"type/text()\" xsi:type=\"node\"/>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<default-root-element>phone</default-root-element>\n" +
                 "<namespace-resolver>" +
                   "<default-namespace-uri>urn:relationships</default-namespace-uri>" +
                 "</namespace-resolver>" +
                 "<schema xsi:type=\"schema-url-reference\">" +
                   "<resource></resource>" +
                   "<schema-context>/phone</schema-context>" +
                   "<node-type>complex-type</node-type>" +
                 "</schema>" +
              "</class-mapping-descriptor>\n" +
                 "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>org.eclipse.persistence.internal.xr.XRDynamicEntity_CollectionWrapper</class>\n" +
                 "<alias>XRDynamicEntity_CollectionWrapper</alias>\n" +
                 "<events xsi:type=\"event-policy\"/>\n" +
                 "<querying xsi:type=\"query-policy\"/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-any-collection-mapping\">\n" +
                       "<attribute-name>items</attribute-name>\n" +
                       "<container xsi:type=\"list-container-policy\">\n" +
                          "<collection-type>java.util.ArrayList</collection-type>\n" +
                       "</container>\n" +
                       "<keep-as-element-policy>KEEP_NONE_AS_ELEMENT</keep-as-element-policy>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<instantiation/>\n" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>\n" +
                 "<change-policy xsi:type=\"deferred-detection-change-policy\"/>\n" +
                 "<default-root-element>employee-collection</default-root-element>\n" +
                 "<namespace-resolver>" +
                    "<namespaces>\n" +
                       "<namespace>\n" +
                          "<prefix>xsd</prefix>\n" +
                          "<namespace-uri>http://www.w3.org/2001/XMLSchema</namespace-uri>\n" +
                       "</namespace>\n" +
                       "<namespace>\n" +
                          "<prefix>xsi</prefix>\n" +
                          "<namespace-uri>http://www.w3.org/2001/XMLSchema-instance</namespace-uri>\n" +
                       "</namespace>\n" +
                    "</namespaces>\n" +
                    "<default-namespace-uri>urn:relationships</default-namespace-uri>" +
                 "</namespace-resolver>" +
              "</class-mapping-descriptor>\n" +
           "</class-mapping-descriptors>\n" +
           "<login xsi:type=\"xml-login\">\n" +
              "<platform-class>org.eclipse.persistence.oxm.platform.DOMPlatform</platform-class>\n" +
           "</login>\n" +
        "</object-persistence>";

    // test fixtures
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUp() {
        final String username = System.getProperty(DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        final String password = System.getProperty(DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        final String url = System.getProperty(DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        final String driver = System.getProperty(DATABASE_DRIVER_KEY);
        if (driver == null) {
            fail("error retrieving database driver");
        }
        XRServiceFactory factory = new XRServiceFactory() {
            @Override
            public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {
                parentClassLoader = this.getClass().getClassLoader();
                xrSchemaStream = new ByteArrayInputStream(RELATIONSHIPS_SCHEMA.getBytes());
                return super.buildService(xrServiceModel);
            }
            @Override
            public void buildSessions() {
                XMLContext context = new XMLContext(
                    new EclipseLinkObjectPersistenceRuntimeXMLProject());
                XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                Project orProject = (Project)unmarshaller.unmarshal(
                        new StringReader(RELATIONSHIPS_OR_PROJECT));
                DatasourceLogin login = new DatabaseLogin();
                login.setUserName(username);
                login.setPassword(password);
                ((DatabaseLogin)login).setConnectionString(url);
                ((DatabaseLogin)login).setDriverClassName(driver);
                Platform platform = new MySQLPlatform();
                login.setDatasourcePlatform(platform);
                ((DatabaseLogin)login).bindAllParameters();
                orProject.setDatasourceLogin(login);
                Project oxProject = (Project)unmarshaller.unmarshal(
                    new StringReader(RELATIONSHIPS_OX_PROJECT));
                ProjectHelper.fixOROXAccessors(orProject, oxProject);
                xrService.setORSession(orProject.createDatabaseSession());
                xrService.getORSession().dontLogMessages();
                xrService.setXMLContext(new XMLContext(oxProject));
                xrService.setOXSession(xrService.getXMLContext().getSession(0));
            }
        };
        XMLContext context = new XMLContext(new DBWSModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSModel model = (DBWSModel)unmarshaller.unmarshal(new StringReader(RELATIONSHIPS_DBWS));
        xrService = factory.buildService(model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAllEmployees() {
      Invocation invocation = new Invocation("getAllEmployees");
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Vector<RelationshipsEmployee> resultVector = (Vector<RelationshipsEmployee>)result;
      XRDynamicEntity_CollectionWrapper xrDynamicEntityCol = new XRDynamicEntity_CollectionWrapper();
      for (RelationshipsEmployee employee : resultVector) {
          xrDynamicEntityCol.add(employee);
      }
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(xrDynamicEntityCol, doc);
      Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEE_COLLECTION_XML));
      assertTrue("control document not same as XRService instance document",
          comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String EMPLOYEE_COLLECTION_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<employee-collection xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
         "<employee emp-id=\"52\">" +
            "<first-name>Betty</first-name>" +
            "<last-name>Jones</last-name>" +
            "<address address-id=\"56\">" +
               "<street>1 Chocolate Drive</street>" +
               "<city>Smith Falls</city>" +
               "<province>ONT</province>" +
               "<postal-code>C6C6C6</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-01</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>F</gender>" +
            "<salary>500001</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"53\">" +
            "<first-name>Sarah</first-name>" +
            "<last-name>Way</last-name>" +
            "<address address-id=\"52\">" +
               "<street>12 Merival Rd., suite 5</street>" +
               "<city>Ottawa</city>" +
               "<province>ONT</province>" +
               "<postal-code>K5J2B5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>5551234</phonenumber>" +
                  "<type>Home</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Write code documentation.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-05-01</start-date>" +
            "<end-date>2001-07-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>F</gender>" +
            "<salary>87000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"54\">" +
            "<first-name>Emanual</first-name>" +
            "<last-name>Smith</last-name>" +
            "<address address-id=\"53\">" +
               "<street>1111 Mountain Blvd. Floor 53, suite 6</street>" +
               "<city>Vancouver</city>" +
               "<province>BC</province>" +
               "<postal-code>N5J2N5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>416</area-code>" +
                  "<phonenumber>5551111</phonenumber>" +
                  "<type>Cellular</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>976</area-code>" +
                  "<phonenumber>5556666</phonenumber>" +
                  "<type>Pager</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2255943</phonenumber>" +
                  "<type>Work Fax</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Have to fix the Database problem.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-01</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>49631</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"55\">" +
            "<first-name>Marcus</first-name>" +
            "<last-name>Saunders</last-name>" +
            "<address address-id=\"58\">" +
               "<street>234 I'm Lost Lane</street>" +
               "<city>Perth</city>" +
               "<province>ONT</province>" +
               "<postal-code>Y3Q2N9</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Write user specifications.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-12</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>54300</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"56\">" +
            "<first-name>Sarah-loo</first-name>" +
            "<last-name>Smitty</last-name>" +
            "<address address-id=\"55\">" +
               "<street>1 Nowhere Drive</street>" +
               "<city>Arnprior</city>" +
               "<province>ONT</province>" +
               "<postal-code>W1A2B5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>416</area-code>" +
                  "<phonenumber>5551111</phonenumber>" +
                  "<type>Cellular</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>5551234</phonenumber>" +
                  "<type>Home</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2255943</phonenumber>" +
                  "<type>Work Fax</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1993-01-01</start-date>" +
            "<end-date>1996-01-01</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>F</gender>" +
            "<salary>75000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"57\">" +
            "<first-name>Nancy</first-name>" +
            "<last-name>White</last-name>" +
            "<address address-id=\"62\">" +
               "<street>2 Anderson Rd.</street>" +
               "<city>Metcalfe</city>" +
               "<province>ONT</province>" +
               "<postal-code>Y4F7V6</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>5551234</phonenumber>" +
                  "<type>Home</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1993-01-01</start-date>" +
            "<end-date>1996-01-01</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>F</gender>" +
            "<salary>31000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"58\">" +
            "<first-name>Jill</first-name>" +
            "<last-name>May</last-name>" +
            "<address address-id=\"61\">" +
               "<street>1111 Moose Rd.</street>" +
               "<city>Calgary</city>" +
               "<province>ALB</province>" +
               "<postal-code>J5J2B5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2255943</phonenumber>" +
                  "<type>Work Fax</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1991-11-11</start-date>" +
            "<end-date>2006-08-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>F</gender>" +
            "<salary>56232</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"59\">" +
            "<first-name>Charles</first-name>" +
            "<last-name>Chanley</last-name>" +
            "<address address-id=\"59\">" +
               "<street>1 Habs Place</street>" +
               "<city>Montreal</city>" +
               "<province>QUE</province>" +
               "<postal-code>Q2S5Z5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>976</area-code>" +
                  "<phonenumber>5556666</phonenumber>" +
                  "<type>Pager</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Write lots of Java code.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-01</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>43000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"60\">" +
            "<first-name>Fred</first-name>" +
            "<last-name>Jones</last-name>" +
            "<address address-id=\"63\">" +
               "<street>382 Hyde Park</street>" +
               "<city>Victoria</city>" +
               "<province>BC</province>" +
               "<postal-code>Z5J2N5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>416</area-code>" +
                  "<phonenumber>5551111</phonenumber>" +
                  "<type>Cellular</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-01</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>500000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"61\">" +
            "<first-name>Bob</first-name>" +
            "<last-name>Smith</last-name>" +
            "<address address-id=\"57\">" +
               "<street>1450 Acme Cr., suite 4</street>" +
               "<city>Toronto</city>" +
               "<province>ONT</province>" +
               "<postal-code>L5J2B5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Clean the kitchen.</responsibility>" +
               "<responsibility>Make the coffee.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1993-01-01</start-date>" +
            "<end-date>1996-01-01</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>35000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"62\">" +
            "<first-name>John</first-name>" +
            "<last-name>Way</last-name>" +
            "<address address-id=\"54\">" +
               "<street>12 Merival Rd., suite 5</street>" +
               "<city>Ottawa</city>" +
               "<province>ONT</province>" +
               "<postal-code>K5J2B5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>905</area-code>" +
                  "<phonenumber>5553691</phonenumber>" +
                  "<type>ISDN</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>2258812</phonenumber>" +
                  "<type>Work</type>" +
               "</phone>" +
            "</phones>" +
            "<responsibilities>" +
               "<responsibility>Fire people for goofing off.</responsibility>" +
               "<responsibility>Hire people when more people are required.</responsibility>" +
            "</responsibilities>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1991-11-11</start-date>" +
            "<end-date>2006-08-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>53000</salary>" +
            "<version>1</version>" +
         "</employee>" +
         "<employee emp-id=\"63\">" +
            "<first-name>Jim-bob</first-name>" +
            "<last-name>Jefferson</last-name>" +
            "<address address-id=\"60\">" +
               "<street>1112 Gold Rush rd.</street>" +
               "<city>Yellow Knife</city>" +
               "<province>YK</province>" +
               "<postal-code>Y5J2N5</postal-code>" +
               "<country>Canada</country>" +
            "</address>" +
            "<phones>" +
               "<phone>" +
                  "<area-code>416</area-code>" +
                  "<phonenumber>5551111</phonenumber>" +
                  "<type>Cellular</type>" +
               "</phone>" +
               "<phone>" +
                  "<area-code>613</area-code>" +
                  "<phonenumber>5551234</phonenumber>" +
                  "<type>Home</type>" +
               "</phone>" +
            "</phones>" +
            "<start-time>00:00:00</start-time>" +
            "<start-date>1995-01-12</start-date>" +
            "<end-date>2001-12-31</end-date>" +
            "<end-time>00:00:00</end-time>" +
            "<gender>M</gender>" +
            "<salary>50000</salary>" +
            "<version>1</version>" +
         "</employee>" +
        "</employee-collection>";
}