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
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.sessions.factories.MissingDescriptorListener;

// domain-specific (testing) imports
import static dbws.testing.DBWSTestSuite.DATABASE_DRIVER_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;

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
    static final String RELATIONSHIPS_OR_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<eclipselink:object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\"\n" +
        "  >\n" +
        "   <eclipselink:name>relationships</eclipselink:name>" +
        "   <eclipselink:class-mapping-descriptors>" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsAddress</eclipselink:class>" +
        "         <eclipselink:alias>address</eclipselink:alias>" +
        "         <eclipselink:primary-key>" +
        "            <eclipselink:field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>" +
        "         </eclipselink:primary-key>" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>" +
        "         <eclipselink:attribute-mappings>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>addressId</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>city</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"CITY\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>country</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"COUNTRY\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>postalCode</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"P_CODE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>province</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"PROVINCE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>street</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_ADDRESS\" name=\"STREET\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "         </eclipselink:attribute-mappings>" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>" +
        "         <eclipselink:instantiation/>" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>" +
        "         <eclipselink:tables>" +
        "            <eclipselink:table name=\"XR_ADDRESS\"/>" +
        "         </eclipselink:tables>" +
        "      </eclipselink:class-mapping-descriptor>" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsEmployee</eclipselink:class>" +
        "         <eclipselink:alias>employee</eclipselink:alias>" +
        "         <eclipselink:primary-key>" +
        "            <eclipselink:field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "         </eclipselink:primary-key>" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\">" +
        "            <eclipselink:queries>" +
        "               <eclipselink:query name=\"getAllEmployees\" xsi:type=\"eclipselink:read-all-query\">" +
        "                  <eclipselink:reference-class>dbws.testing.relationships.RelationshipsEmployee</eclipselink:reference-class>" +
        "                  <eclipselink:outer-join-subclasses>false</eclipselink:outer-join-subclasses>" +
        "                  <eclipselink:container xsi:type=\"eclipselink:list-container-policy\">" +
        "                     <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>" +
        "                  </eclipselink:container>" +
        "                  <eclipselink:order-by-expressions>" +
        "                     <eclipselink:expression function=\"ascending\" xsi:type=\"eclipselink:function-expression\">" +
        "                        <eclipselink:arguments>" +
        "                           <eclipselink:argument name=\"empId\" xsi:type=\"eclipselink:query-key-expression\">" +
        "                              <eclipselink:base xsi:type=\"eclipselink:base-expression\"/>" +
        "                           </eclipselink:argument>" +
        "                        </eclipselink:arguments>" +
        "                     </eclipselink:expression>" +
        "                  </eclipselink:order-by-expressions>" +
        "               </eclipselink:query>" +
        "            </eclipselink:queries>" +
        "         </eclipselink:querying>" +
        "         <eclipselink:attribute-mappings>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-one-mapping\">" +
        "               <eclipselink:attribute-name>address</eclipselink:attribute-name>" +
        "               <eclipselink:reference-class>dbws.testing.relationships.RelationshipsAddress</eclipselink:reference-class>" +
        "               <eclipselink:private-owned>true</eclipselink:private-owned>" +
        "               <eclipselink:foreign-key>" +
        "                  <eclipselink:field-reference>" +
        "                     <eclipselink:source-field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                     <eclipselink:target-field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                  </eclipselink:field-reference>" +
        "               </eclipselink:foreign-key>" +
        "               <eclipselink:foreign-key-fields>" +
        "                  <eclipselink:field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>" +
        "               </eclipselink:foreign-key-fields>" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:read-object-query\"/>" +
        "               <eclipselink:join-fetch>inner-join</eclipselink:join-fetch>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>empId</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>endDate</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"END_DATE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>endTime</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"END_TIME\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>firstName</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"F_NAME\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>gender</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"GENDER\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>lastName</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"L_NAME\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-many-mapping\">" +
        "               <eclipselink:attribute-name>phones</eclipselink:attribute-name>" +
        "               <eclipselink:reference-class>dbws.testing.relationships.RelationshipsPhone</eclipselink:reference-class>" +
        "               <eclipselink:private-owned>true</eclipselink:private-owned>" +
        "               <eclipselink:target-foreign-key>" +
        "                  <eclipselink:field-reference>" +
        "                     <eclipselink:source-field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                     <eclipselink:target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                  </eclipselink:field-reference>" +
        "               </eclipselink:target-foreign-key>" +
        "               <eclipselink:batch-reading>true</eclipselink:batch-reading>" +
        "               <eclipselink:container xsi:type=\"eclipselink:container-policy\">" +
        "                  <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>" +
        "               </eclipselink:container>" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:read-all-query\">" +
        "                  <eclipselink:container xsi:type=\"eclipselink:container-policy\">" +
        "                     <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>" +
        "                  </eclipselink:container>" +
        "                  <eclipselink:order-by-expressions>" +
        "                     <eclipselink:expression function=\"ascending\" xsi:type=\"eclipselink:function-expression\">" +
        "                        <eclipselink:arguments>" +
        "                           <eclipselink:argument name=\"type\" xsi:type=\"eclipselink:query-key-expression\">" +
        "                              <eclipselink:base xsi:type=\"eclipselink:base-expression\"/>" +
        "                           </eclipselink:argument>" +
        "                        </eclipselink:arguments>" +
        "                     </eclipselink:expression>" +
        "                  </eclipselink:order-by-expressions>" +
        "               </eclipselink:selection-query>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-collection-mapping\">" +
        "               <eclipselink:attribute-name>responsibilities</eclipselink:attribute-name>" +
        "               <eclipselink:batch-reading>true</eclipselink:batch-reading>" +
        "               <eclipselink:container xsi:type=\"eclipselink:container-policy\">" +
        "                  <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>" +
        "               </eclipselink:container>" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:direct-read-query\">" +
        "                  <eclipselink:maintain-cache>false</eclipselink:maintain-cache>" +
        "                  <eclipselink:container xsi:type=\"eclipselink:container-policy\">" +
        "                     <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>" +
        "                  </eclipselink:container>" +
        "               </eclipselink:selection-query>" +
        "               <eclipselink:reference-table>XR_RESPONS</eclipselink:reference-table>" +
        "               <eclipselink:direct-field table=\"XR_RESPONS\" name=\"DESCRIP\" xsi:type=\"eclipselink:column\"/>" +
        "               <eclipselink:reference-foreign-key>" +
        "                  <eclipselink:field-reference>" +
        "                     <eclipselink:source-field table=\"XR_RESPONS\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                     <eclipselink:target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "                  </eclipselink:field-reference>" +
        "               </eclipselink:reference-foreign-key>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>salary</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_SALARY\" name=\"SALARY\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>startDate</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"START_DATE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>startTime</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"START_TIME\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>version</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_EMPLOYEE\" name=\"VERSION\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "         </eclipselink:attribute-mappings>" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>" +
        "         <eclipselink:instantiation/>" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>" +
        "         <eclipselink:tables>" +
        "            <eclipselink:table name=\"XR_EMPLOYEE\"/>" +
        "            <eclipselink:table name=\"XR_SALARY\"/>" +
        "         </eclipselink:tables>" +
        "      </eclipselink:class-mapping-descriptor>" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsPhone</eclipselink:class>" +
        "         <eclipselink:alias>phone</eclipselink:alias>" +
        "         <eclipselink:primary-key>" +
        "            <eclipselink:field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "            <eclipselink:field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"eclipselink:column\"/>" +
        "         </eclipselink:primary-key>" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>" +
        "         <eclipselink:attribute-mappings>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>areaCode</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_PHONE\" name=\"AREA_CODE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>empId</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>phonenumber</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_PHONE\" name=\"P_NUMBER\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
        "               <eclipselink:attribute-name>type</eclipselink:attribute-name>" +
        "               <eclipselink:field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"eclipselink:column\"/>" +
        "            </eclipselink:attribute-mapping>" +
        "         </eclipselink:attribute-mappings>" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>" +
        "         <eclipselink:instantiation/>" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>" +
        "         <eclipselink:tables>" +
        "            <eclipselink:table name=\"XR_PHONE\"/>" +
        "         </eclipselink:tables>" +
        "      </eclipselink:class-mapping-descriptor>" +
        "   </eclipselink:class-mapping-descriptors>" +
        "   <eclipselink:login xsi:type=\"eclipselink:database-login\">\n" +
        "      <eclipselink:bind-all-parameters>true</eclipselink:bind-all-parameters>\n" +
        "   </eclipselink:login>\n" +
        "</eclipselink:object-persistence>";

    static final String RELATIONSHIPS_OX_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<eclipselink:object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\"\n" +
        "  >\n" +
        "  <eclipselink:name>relationships</eclipselink:name>\n" +
        "  <eclipselink:class-mapping-descriptors>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsAddress</eclipselink:class>\n" +
        "         <eclipselink:alias>address</eclipselink:alias>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>addressId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"@address-id/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>street</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:street/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>city</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:city/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>province</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:province/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>postalCode</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:postal-code/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>country</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:country/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>aggregate</eclipselink:descriptor-type>\n" +
        "         <eclipselink:default-root-element>ns1:address</eclipselink:default-root-element>\n" +
        "         <eclipselink:default-root-element-field name=\"ns1:address\" xsi:type=\"eclipselink:node\"/>\n" +
        "         <eclipselink:namespace-resolver>\n" +
        "            <eclipselink:namespaces>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsd</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>ns1</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>urn:relationships</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsEmployee</eclipselink:class>\n" +
        "         <eclipselink:alias>employee</eclipselink:alias>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>empId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"@emp-id/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>firstName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:first-name/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>lastName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:last-name/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-composite-object-mapping\">\n" +
        "               <eclipselink:attribute-name>address</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.relationships.RelationshipsAddress</eclipselink:reference-class>\n" +
        "               <eclipselink:field name=\"ns1:address\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-composite-collection-mapping\">\n" +
        "               <eclipselink:attribute-name>phones</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.relationships.RelationshipsPhone</eclipselink:reference-class>\n" +
        "               <eclipselink:field name=\"ns1:phones/ns1:phone\" xsi:type=\"eclipselink:node\"/>\n" +
        "               <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "                  <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>\n" +
        "               </eclipselink:container>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-composite-direct-collection-mapping\">\n" +
        "               <eclipselink:attribute-name>responsibilities</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:responsibilities/ns1:responsibility/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "               <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "                  <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>\n" +
        "               </eclipselink:container>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>startTime</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:start-time/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}time</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>startDate</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:start-date/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}date</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>endDate</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:end-date/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}date</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>endTime</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:end-time/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}time</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>gender</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:gender/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>salary</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:salary/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>version</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:version/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>aggregate</eclipselink:descriptor-type>\n" +
        "         <eclipselink:default-root-element>ns1:employee</eclipselink:default-root-element>\n" +
        "         <eclipselink:default-root-element-field name=\"ns1:employee\" xsi:type=\"eclipselink:node\"/>\n" +
        "         <eclipselink:namespace-resolver>\n" +
        "            <eclipselink:namespaces>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsd</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>ns1</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>urn:relationships</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.relationships.RelationshipsPhone</eclipselink:class>\n" +
        "         <eclipselink:alias>phone</eclipselink:alias>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>areaCode</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:area-code/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>phonenumber</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:phonenumber/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>type</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:type/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>aggregate</eclipselink:descriptor-type>\n" +
        "         <eclipselink:default-root-element>ns1:phone</eclipselink:default-root-element>\n" +
        "         <eclipselink:default-root-element-field name=\"ns1:phone\" xsi:type=\"eclipselink:node\"/>\n" +
        "         <eclipselink:namespace-resolver>\n" +
        "            <eclipselink:namespaces>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsd</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>ns1</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>urn:relationships</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "   </eclipselink:class-mapping-descriptors>\n" +
        "   <eclipselink:login xsi:type=\"eclipselink:xml-login\">\n" +
        "      <eclipselink:platform-class>org.eclipse.persistence.oxm.platform.SAXPlatform</eclipselink:platform-class>\n" +
        "   </eclipselink:login>\n" +
        "</eclipselink:object-persistence>";
    
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
                Project elProject = new EclipseLinkObjectPersistenceRuntimeXMLProject();
                XMLLogin xmlLogin = new XMLLogin();
                xmlLogin.setDatasourcePlatform(new DOMPlatform());
                elProject.setDatasourceLogin(xmlLogin);
                elProject.getDatasourceLogin().getDatasourcePlatform().getConversionManager().
                    setLoader(parentClassLoader);
                XMLContext context = new XMLContext(elProject);
                context.getSession(Project.class).getEventManager().addListener(
                    new MissingDescriptorListener());
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