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
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/

package dbws.testing.relationships;

// Javase imports
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Java extension imports

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;

// domain-specific (testing) imports
import static dbws.testing.DBWSTestHelper.DATABASE_DRIVER_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_USERNAME_KEY;

public class RelationshipsTestSuite {

    static final String RELATIONSHIPS_SCHEMA =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<xsd:schema\n" +
        "  targetNamespace=\"urn:relationships\" xmlns=\"urn:relationships\" elementFormDefault=\"qualified\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  >\n" +
        "  <xsd:complexType name=\"phoneType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"area-code\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"phonenumber\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"type\" type=\"xsd:string\" />\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"phone\" type=\"phoneType\"/>\n" +
        "  <xsd:complexType name=\"addressType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"id\" type=\"xsd:int\" />\n" +
        "      <xsd:element name=\"street\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"city\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"province\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"postal-code\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"country\" type=\"xsd:string\" />\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"address\" type=\"addressType\"/>\n" +
        "  <xsd:complexType name=\"employeeType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"id\" type=\"xsd:int\" />\n" +
        "      <xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
        "     <xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"address\" type=\"addressType\" minOccurs=\"0\" />\n" +
        "      <xsd:sequence>\n" +
        "        <xsd:element name=\"phones\" type=\"phoneType\" minOccurs=\"0\" />\n" +
        "      </xsd:sequence>\n" +
        "      <xsd:sequence>\n" +
        "        <xsd:element name=\"responsibilities\" type=\"xsd:string\" minOccurs=\"0\" />\n" +
        "      </xsd:sequence>\n" +
        "      <xsd:element name=\"start-time\" type=\"xsd:time\" />\n" +
        "      <xsd:element name=\"end-time\" type=\"xsd:time\" />\n" +
        "      <xsd:element name=\"start-date\" type=\"xsd:date\" />\n" +
        "      <xsd:element name=\"end-date\" type=\"xsd:date\" />\n" +
        "      <xsd:element name=\"gender\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"salary\" type=\"xsd:decimal\" />\n" +
        "      <xsd:element name=\"version\" type=\"xsd:int\"/>\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"employee\" type=\"employeeType\"/>\n" +
        "</xsd:schema>";
    static final String RELATIONSHIPS_DBWS =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<dbws\n" +
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        " xmlns:ns1=\"urn:relationships\"\n" +
        " >\n" +
        " <name>relationships</name>\n" +
        "  <query>\n" +
        "   <name>getAllEmployees</name>\n" +
        "   <result isCollection=\"true\">\n" +
        "     <type>ns1:employeeType</type>\n" +
        "   </result>\n" +
        "   <named-query>\n" +
        "     <name>getAllEmployees</name>\n" +
        "     <descriptor>employee</descriptor>\n" +
        "   </named-query>\n" +
        "  </query>\n" +
        "</dbws>";
    public static final String RELATIONSHIPS_OR_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\"\n" +
        "  >\n" +
        "   <name>relationships</name>" +
        "   <class-mapping-descriptors>" +
        "      <class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
        "         <class>dbws.testing.relationships.RelationshipsAddress</class>" +
        "         <alias>address</alias>" +
        "         <primary-key>" +
        "            <field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
        "         </primary-key>" +
        "         <events xsi:type=\"event-policy\"/>" +
        "         <querying xsi:type=\"query-policy\"/>" +
        "         <attribute-mappings>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>addressId</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>city</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"CITY\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>country</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"COUNTRY\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>postalCode</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"P_CODE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>province</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"PROVINCE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>street</attribute-name>" +
        "               <field table=\"XR_ADDRESS\" name=\"STREET\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "         </attribute-mappings>" +
        "         <descriptor-type>independent</descriptor-type>" +
        "         <instantiation/>" +
        "         <copying xsi:type=\"instantiation-copy-policy\"/>" +
        "         <change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
        "         <tables>" +
        "            <table name=\"XR_ADDRESS\"/>" +
        "         </tables>" +
        "      </class-mapping-descriptor>" +
        "      <class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
        "         <class>dbws.testing.relationships.RelationshipsEmployee</class>" +
        "         <alias>employee</alias>" +
        "         <primary-key>" +
        "            <field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "         </primary-key>" +
        "         <events xsi:type=\"event-policy\"/>" +
        "         <querying xsi:type=\"query-policy\">" +
        "            <queries>" +
        "               <query name=\"getAllEmployees\" xsi:type=\"read-all-query\">" +
        "                  <reference-class>dbws.testing.relationships.RelationshipsEmployee</reference-class>" +
        "                  <outer-join-subclasses>false</outer-join-subclasses>" +
        "                  <container xsi:type=\"list-container-policy\">" +
        "                     <collection-type>java.util.Vector</collection-type>" +
        "                  </container>" +
        "                  <order-by-expressions>" +
        "                     <expression function=\"ascending\" xsi:type=\"function-expression\">" +
        "                        <arguments>" +
        "                           <argument name=\"empId\" xsi:type=\"query-key-expression\">" +
        "                              <base xsi:type=\"base-expression\"/>" +
        "                           </argument>" +
        "                        </arguments>" +
        "                     </expression>" +
        "                  </order-by-expressions>" +
        "               </query>" +
        "            </queries>" +
        "         </querying>" +
        "         <attribute-mappings>" +
        "            <attribute-mapping xsi:type=\"one-to-one-mapping\">" +
        "               <attribute-name>address</attribute-name>" +
        "               <reference-class>dbws.testing.relationships.RelationshipsAddress</reference-class>" +
        "               <private-owned>true</private-owned>" +
        "               <foreign-key>" +
        "                  <field-reference>" +
        "                     <source-field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"column\"/>" +
        "                     <target-field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"column\"/>" +
        "                  </field-reference>" +
        "               </foreign-key>" +
        "               <foreign-key-fields>" +
        "                  <field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"column\"/>" +
        "               </foreign-key-fields>" +
        "               <selection-query xsi:type=\"read-object-query\"/>" +
        "               <join-fetch>inner-join</join-fetch>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>empId</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>endDate</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"END_DATE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>endTime</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"END_TIME\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>firstName</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"F_NAME\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>gender</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"GENDER\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>lastName</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"L_NAME\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"one-to-many-mapping\">" +
        "               <attribute-name>phones</attribute-name>" +
        "               <reference-class>dbws.testing.relationships.RelationshipsPhone</reference-class>" +
        "               <private-owned>true</private-owned>" +
        "               <target-foreign-key>" +
        "                  <field-reference>" +
        "                     <source-field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "                     <target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "                  </field-reference>" +
        "               </target-foreign-key>" +
        "               <batch-reading>true</batch-reading>" +
        "               <container xsi:type=\"container-policy\">" +
        "                  <collection-type>java.util.ArrayList</collection-type>" +
        "               </container>" +
        "               <selection-query xsi:type=\"read-all-query\">" +
        "                  <container xsi:type=\"container-policy\">" +
        "                     <collection-type>java.util.ArrayList</collection-type>" +
        "                  </container>" +
        "                  <order-by-expressions>" +
        "                     <expression function=\"ascending\" xsi:type=\"function-expression\">" +
        "                        <arguments>" +
        "                           <argument name=\"type\" xsi:type=\"query-key-expression\">" +
        "                              <base xsi:type=\"base-expression\"/>" +
        "                           </argument>" +
        "                        </arguments>" +
        "                     </expression>" +
        "                  </order-by-expressions>" +
        "               </selection-query>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-collection-mapping\">" +
        "               <attribute-name>responsibilities</attribute-name>" +
        "               <batch-reading>true</batch-reading>" +
        "               <container xsi:type=\"container-policy\">" +
        "                  <collection-type>java.util.ArrayList</collection-type>" +
        "               </container>" +
        "               <selection-query xsi:type=\"direct-read-query\">" +
        "                  <maintain-cache>false</maintain-cache>" +
        "                  <container xsi:type=\"container-policy\">" +
        "                     <collection-type>java.util.ArrayList</collection-type>" +
        "                  </container>" +
        "               </selection-query>" +
        "               <reference-table>XR_RESPONS</reference-table>" +
        "               <direct-field table=\"XR_RESPONS\" name=\"DESCRIP\" xsi:type=\"column\"/>" +
        "               <reference-foreign-key>" +
        "                  <field-reference>" +
        "                     <source-field table=\"XR_RESPONS\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "                     <target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "                  </field-reference>" +
        "               </reference-foreign-key>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>salary</attribute-name>" +
        "               <field table=\"XR_SALARY\" name=\"SALARY\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>startDate</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"START_DATE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>startTime</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"START_TIME\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>version</attribute-name>" +
        "               <field table=\"XR_EMPLOYEE\" name=\"VERSION\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "         </attribute-mappings>" +
        "         <descriptor-type>independent</descriptor-type>" +
        "         <instantiation/>" +
        "         <copying xsi:type=\"instantiation-copy-policy\"/>" +
        "         <change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
        "         <tables>" +
        "            <table name=\"XR_EMPLOYEE\"/>" +
        "            <table name=\"XR_SALARY\"/>" +
        "         </tables>" +
        "      </class-mapping-descriptor>" +
        "      <class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">" +
        "         <class>dbws.testing.relationships.RelationshipsPhone</class>" +
        "         <alias>phone</alias>" +
        "         <primary-key>" +
        "            <field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "            <field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"column\"/>" +
        "         </primary-key>" +
        "         <events xsi:type=\"event-policy\"/>" +
        "         <querying xsi:type=\"query-policy\"/>" +
        "         <attribute-mappings>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>areaCode</attribute-name>" +
        "               <field table=\"XR_PHONE\" name=\"AREA_CODE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>empId</attribute-name>" +
        "               <field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>phonenumber</attribute-name>" +
        "               <field table=\"XR_PHONE\" name=\"P_NUMBER\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "            <attribute-mapping xsi:type=\"direct-mapping\">" +
        "               <attribute-name>type</attribute-name>" +
        "               <field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"column\"/>" +
        "            </attribute-mapping>" +
        "         </attribute-mappings>" +
        "         <descriptor-type>independent</descriptor-type>" +
        "         <instantiation/>" +
        "         <copying xsi:type=\"instantiation-copy-policy\"/>" +
        "         <change-policy xsi:type=\"deferred-detection-change-policy\"/>" +
        "         <tables>" +
        "            <table name=\"XR_PHONE\"/>" +
        "         </tables>" +
        "      </class-mapping-descriptor>" +
        "   </class-mapping-descriptors>" +
        "   <login xsi:type=\"database-login\">\n" +
        "      <bind-all-parameters>true</bind-all-parameters>\n" +
        "   </login>\n" +
        "</object-persistence>";

    static final String RELATIONSHIPS_OX_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\"\n" +
        "  >\n" +
        "  <name>relationships</name>\n" +
        "  <class-mapping-descriptors>\n" +
        "      <class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
        "         <class>dbws.testing.relationships.RelationshipsAddress</class>\n" +
        "         <alias>address</alias>\n" +
        "         <events xsi:type=\"event-policy\"/>\n" +
        "         <querying xsi:type=\"query-policy\"/>\n" +
        "         <attribute-mappings>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>addressId</attribute-name>\n" +
        "               <field name=\"@address-id/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>street</attribute-name>\n" +
        "               <field name=\"ns1:street/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>city</attribute-name>\n" +
        "               <field name=\"ns1:city/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>province</attribute-name>\n" +
        "               <field name=\"ns1:province/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>postalCode</attribute-name>\n" +
        "               <field name=\"ns1:postal-code/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>country</attribute-name>\n" +
        "               <field name=\"ns1:country/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "         </attribute-mappings>\n" +
        "         <descriptor-type>aggregate</descriptor-type>\n" +
        "         <default-root-element>ns1:address</default-root-element>\n" +
        "         <default-root-element-field name=\"ns1:address\" xsi:type=\"node\"/>\n" +
        "         <namespace-resolver>\n" +
        "            <namespaces>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsd</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>ns1</prefix>\n" +
        "                  <namespace-uri>urn:relationships</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsi</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema-instance</namespace-uri>\n" +
        "               </namespace>\n" +
        "            </namespaces>\n" +
        "         </namespace-resolver>\n" +
        "      </class-mapping-descriptor>\n" +
        "      <class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
        "         <class>dbws.testing.relationships.RelationshipsEmployee</class>\n" +
        "         <alias>employee</alias>\n" +
        "         <events xsi:type=\"event-policy\"/>\n" +
        "         <querying xsi:type=\"query-policy\"/>\n" +
        "         <attribute-mappings>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>empId</attribute-name>\n" +
        "               <field name=\"@emp-id/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>firstName</attribute-name>\n" +
        "               <field name=\"ns1:first-name/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>lastName</attribute-name>\n" +
        "               <field name=\"ns1:last-name/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-composite-object-mapping\">\n" +
        "               <attribute-name>address</attribute-name>\n" +
        "               <reference-class>dbws.testing.relationships.RelationshipsAddress</reference-class>\n" +
        "               <field name=\"ns1:address\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-composite-collection-mapping\">\n" +
        "               <attribute-name>phones</attribute-name>\n" +
        "               <reference-class>dbws.testing.relationships.RelationshipsPhone</reference-class>\n" +
        "               <field name=\"ns1:phones/ns1:phone\" xsi:type=\"node\"/>\n" +
        "               <container xsi:type=\"container-policy\">\n" +
        "                  <collection-type>java.util.Vector</collection-type>\n" +
        "               </container>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-composite-direct-collection-mapping\">\n" +
        "               <attribute-name>responsibilities</attribute-name>\n" +
        "               <field name=\"ns1:responsibilities/ns1:responsibility/text()\" xsi:type=\"node\"/>\n" +
        "               <container xsi:type=\"container-policy\">\n" +
        "                  <collection-type>java.util.Vector</collection-type>\n" +
        "               </container>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>startTime</attribute-name>\n" +
        "               <field name=\"ns1:start-time/text()\" xsi:type=\"node\">\n" +
        "                  <schema-type>{http://www.w3.org/2001/XMLSchema}time</schema-type>\n" +
        "               </field>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>startDate</attribute-name>\n" +
        "               <field name=\"ns1:start-date/text()\" xsi:type=\"node\">\n" +
        "                  <schema-type>{http://www.w3.org/2001/XMLSchema}date</schema-type>\n" +
        "               </field>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>endDate</attribute-name>\n" +
        "               <field name=\"ns1:end-date/text()\" xsi:type=\"node\">\n" +
        "                  <schema-type>{http://www.w3.org/2001/XMLSchema}date</schema-type>\n" +
        "               </field>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>endTime</attribute-name>\n" +
        "               <field name=\"ns1:end-time/text()\" xsi:type=\"node\">\n" +
        "                  <schema-type>{http://www.w3.org/2001/XMLSchema}time</schema-type>\n" +
        "               </field>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>gender</attribute-name>\n" +
        "               <field name=\"ns1:gender/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>salary</attribute-name>\n" +
        "               <field name=\"ns1:salary/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>version</attribute-name>\n" +
        "               <field name=\"ns1:version/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "         </attribute-mappings>\n" +
        "         <descriptor-type>aggregate</descriptor-type>\n" +
        "         <default-root-element>ns1:employee</default-root-element>\n" +
        "         <default-root-element-field name=\"ns1:employee\" xsi:type=\"node\"/>\n" +
        "         <namespace-resolver>\n" +
        "            <namespaces>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsd</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>ns1</prefix>\n" +
        "                  <namespace-uri>urn:relationships</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsi</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema-instance</namespace-uri>\n" +
        "               </namespace>\n" +
        "            </namespaces>\n" +
        "         </namespace-resolver>\n" +
        "      </class-mapping-descriptor>\n" +
        "      <class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
        "         <class>dbws.testing.relationships.RelationshipsPhone</class>\n" +
        "         <alias>phone</alias>\n" +
        "         <events xsi:type=\"event-policy\"/>\n" +
        "         <querying xsi:type=\"query-policy\"/>\n" +
        "         <attribute-mappings>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>areaCode</attribute-name>\n" +
        "               <field name=\"ns1:area-code/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>phonenumber</attribute-name>\n" +
        "               <field name=\"ns1:phonenumber/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "            <attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
        "               <attribute-name>type</attribute-name>\n" +
        "               <field name=\"ns1:type/text()\" xsi:type=\"node\"/>\n" +
        "            </attribute-mapping>\n" +
        "         </attribute-mappings>\n" +
        "         <descriptor-type>aggregate</descriptor-type>\n" +
        "         <default-root-element>ns1:phone</default-root-element>\n" +
        "         <default-root-element-field name=\"ns1:phone\" xsi:type=\"node\"/>\n" +
        "         <namespace-resolver>\n" +
        "            <namespaces>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsd</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>ns1</prefix>\n" +
        "                  <namespace-uri>urn:relationships</namespace-uri>\n" +
        "               </namespace>\n" +
        "               <namespace>\n" +
        "                  <prefix>xsi</prefix>\n" +
        "                  <namespace-uri>http://www.w3.org/2001/XMLSchema-instance</namespace-uri>\n" +
        "               </namespace>\n" +
        "            </namespaces>\n" +
        "         </namespace-resolver>\n" +
        "      </class-mapping-descriptor>\n" +
        "   </class-mapping-descriptors>\n" +
        "   <login xsi:type=\"xml-login\">\n" +
        "      <platform-class>org.eclipse.persistence.oxm.platform.SAXPlatform</platform-class>\n" +
        "   </login>\n" +
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
                Platform platform = new OraclePlatform();
                login.setDatasourcePlatform(platform);
                ((DatabaseLogin)login).bindAllParameters();
                orProject.setDatasourceLogin(login);
                Project oxProject = (Project)unmarshaller.unmarshal(
                    new StringReader(RELATIONSHIPS_OX_PROJECT));
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
      Document doc = xmlPlatform.createDocument();
      Element ec = doc.createElement("employee-collection");
      doc.appendChild(ec);
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      for (Object r : (Vector)result) {
          marshaller.marshal(r, ec);
      }
      Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEE_COLLECTION_XML));
      assertTrue("control document not same as XRService instance document",
          comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String EMPLOYEE_COLLECTION_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<employee-collection>" +
         "<ns1:employee emp-id=\"52\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Betty</ns1:first-name>" +
            "<ns1:last-name>Jones</ns1:last-name>" +
            "<ns1:address address-id=\"56\">" +
               "<ns1:street>1 Chocolate Drive</ns1:street>" +
               "<ns1:city>Smith Falls</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>C6C6C6</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-01</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>F</ns1:gender>" +
            "<ns1:salary>500001</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"53\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Sarah</ns1:first-name>" +
            "<ns1:last-name>Way</ns1:last-name>" +
            "<ns1:address address-id=\"52\">" +
               "<ns1:street>12 Merival Rd., suite 5</ns1:street>" +
               "<ns1:city>Ottawa</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>K5J2B5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>5551234</ns1:phonenumber>" +
                  "<ns1:type>Home</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Write code documentation.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-05-01</ns1:start-date>" +
            "<ns1:end-date>2001-07-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>F</ns1:gender>" +
            "<ns1:salary>87000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"54\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Emanual</ns1:first-name>" +
            "<ns1:last-name>Smith</ns1:last-name>" +
            "<ns1:address address-id=\"53\">" +
               "<ns1:street>1111 Mountain Blvd. Floor 53, suite 6</ns1:street>" +
               "<ns1:city>Vancouver</ns1:city>" +
               "<ns1:province>BC</ns1:province>" +
               "<ns1:postal-code>N5J2N5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>416</ns1:area-code>" +
                  "<ns1:phonenumber>5551111</ns1:phonenumber>" +
                  "<ns1:type>Cellular</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>976</ns1:area-code>" +
                  "<ns1:phonenumber>5556666</ns1:phonenumber>" +
                  "<ns1:type>Pager</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2255943</ns1:phonenumber>" +
                  "<ns1:type>Work Fax</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Have to fix the Database problem.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-01</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>49631</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"55\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Marcus</ns1:first-name>" +
            "<ns1:last-name>Saunders</ns1:last-name>" +
            "<ns1:address address-id=\"58\">" +
               "<ns1:street>234 I'm Lost Lane</ns1:street>" +
               "<ns1:city>Perth</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>Y3Q2N9</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Write user specifications.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-12</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>54300</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"56\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Sarah-loo</ns1:first-name>" +
            "<ns1:last-name>Smitty</ns1:last-name>" +
            "<ns1:address address-id=\"55\">" +
               "<ns1:street>1 Nowhere Drive</ns1:street>" +
               "<ns1:city>Arnprior</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>W1A2B5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>416</ns1:area-code>" +
                  "<ns1:phonenumber>5551111</ns1:phonenumber>" +
                  "<ns1:type>Cellular</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>5551234</ns1:phonenumber>" +
                  "<ns1:type>Home</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2255943</ns1:phonenumber>" +
                  "<ns1:type>Work Fax</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1993-01-01</ns1:start-date>" +
            "<ns1:end-date>1996-01-01</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>F</ns1:gender>" +
            "<ns1:salary>75000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"57\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Nancy</ns1:first-name>" +
            "<ns1:last-name>White</ns1:last-name>" +
            "<ns1:address address-id=\"62\">" +
               "<ns1:street>2 Anderson Rd.</ns1:street>" +
               "<ns1:city>Metcalfe</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>Y4F7V6</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>5551234</ns1:phonenumber>" +
                  "<ns1:type>Home</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1993-01-01</ns1:start-date>" +
            "<ns1:end-date>1996-01-01</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>F</ns1:gender>" +
            "<ns1:salary>31000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"58\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Jill</ns1:first-name>" +
            "<ns1:last-name>May</ns1:last-name>" +
            "<ns1:address address-id=\"61\">" +
               "<ns1:street>1111 Moose Rd.</ns1:street>" +
               "<ns1:city>Calgary</ns1:city>" +
               "<ns1:province>ALB</ns1:province>" +
               "<ns1:postal-code>J5J2B5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2255943</ns1:phonenumber>" +
                  "<ns1:type>Work Fax</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1991-11-11</ns1:start-date>" +
            "<ns1:end-date>2006-08-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>F</ns1:gender>" +
            "<ns1:salary>56232</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"59\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Charles</ns1:first-name>" +
            "<ns1:last-name>Chanley</ns1:last-name>" +
            "<ns1:address address-id=\"59\">" +
               "<ns1:street>1 Habs Place</ns1:street>" +
               "<ns1:city>Montreal</ns1:city>" +
               "<ns1:province>QUE</ns1:province>" +
               "<ns1:postal-code>Q2S5Z5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>976</ns1:area-code>" +
                  "<ns1:phonenumber>5556666</ns1:phonenumber>" +
                  "<ns1:type>Pager</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Write lots of Java code.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-01</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>43000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"60\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Fred</ns1:first-name>" +
            "<ns1:last-name>Jones</ns1:last-name>" +
            "<ns1:address address-id=\"63\">" +
               "<ns1:street>382 Hyde Park</ns1:street>" +
               "<ns1:city>Victoria</ns1:city>" +
               "<ns1:province>BC</ns1:province>" +
               "<ns1:postal-code>Z5J2N5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>416</ns1:area-code>" +
                  "<ns1:phonenumber>5551111</ns1:phonenumber>" +
                  "<ns1:type>Cellular</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-01</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>500000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"61\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Bob</ns1:first-name>" +
            "<ns1:last-name>Smith</ns1:last-name>" +
            "<ns1:address address-id=\"57\">" +
               "<ns1:street>1450 Acme Cr., suite 4</ns1:street>" +
               "<ns1:city>Toronto</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>L5J2B5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Clean the kitchen.</ns1:responsibility>" +
               "<ns1:responsibility>Make the coffee.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1993-01-01</ns1:start-date>" +
            "<ns1:end-date>1996-01-01</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>35000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"62\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>John</ns1:first-name>" +
            "<ns1:last-name>Way</ns1:last-name>" +
            "<ns1:address address-id=\"54\">" +
               "<ns1:street>12 Merival Rd., suite 5</ns1:street>" +
               "<ns1:city>Ottawa</ns1:city>" +
               "<ns1:province>ONT</ns1:province>" +
               "<ns1:postal-code>K5J2B5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>905</ns1:area-code>" +
                  "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                  "<ns1:type>ISDN</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                  "<ns1:type>Work</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:responsibilities>" +
               "<ns1:responsibility>Fire people for goofing off.</ns1:responsibility>" +
               "<ns1:responsibility>Hire people when more people are required.</ns1:responsibility>" +
            "</ns1:responsibilities>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1991-11-11</ns1:start-date>" +
            "<ns1:end-date>2006-08-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>53000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
         "<ns1:employee emp-id=\"63\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<ns1:first-name>Jim-bob</ns1:first-name>" +
            "<ns1:last-name>Jefferson</ns1:last-name>" +
            "<ns1:address address-id=\"60\">" +
               "<ns1:street>1112 Gold Rush rd.</ns1:street>" +
               "<ns1:city>Yellow Knife</ns1:city>" +
               "<ns1:province>YK</ns1:province>" +
               "<ns1:postal-code>Y5J2N5</ns1:postal-code>" +
               "<ns1:country>Canada</ns1:country>" +
            "</ns1:address>" +
            "<ns1:phones>" +
               "<ns1:phone>" +
                  "<ns1:area-code>416</ns1:area-code>" +
                  "<ns1:phonenumber>5551111</ns1:phonenumber>" +
                  "<ns1:type>Cellular</ns1:type>" +
               "</ns1:phone>" +
               "<ns1:phone>" +
                  "<ns1:area-code>613</ns1:area-code>" +
                  "<ns1:phonenumber>5551234</ns1:phonenumber>" +
                  "<ns1:type>Home</ns1:type>" +
               "</ns1:phone>" +
            "</ns1:phones>" +
            "<ns1:start-time>00:00:00</ns1:start-time>" +
            "<ns1:start-date>1995-01-12</ns1:start-date>" +
            "<ns1:end-date>2001-12-31</ns1:end-date>" +
            "<ns1:end-time>00:00:00</ns1:end-time>" +
            "<ns1:gender>M</ns1:gender>" +
            "<ns1:salary>50000</ns1:salary>" +
            "<ns1:version>1</ns1:version>" +
         "</ns1:employee>" +
        "</employee-collection>";
}