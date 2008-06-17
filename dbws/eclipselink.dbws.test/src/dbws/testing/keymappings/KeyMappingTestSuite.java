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

package dbws.testing.keymappings;

// javase imports
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.internal.xr.BaseEntity;
import org.eclipse.persistence.internal.xr.BaseEntityClassLoader;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.ProjectHelper;
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
import dbws.testing.RootHelper;
import static dbws.testing.DBWSTestSuite.DATABASE_DRIVER_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;

public class KeyMappingTestSuite {

    static final String KEYMAPPINGS_SCHEMA =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xsd:schema targetNamespace=\"urn:keymappings\" xmlns=\"urn:keymappings\" elementFormDefault=\"qualified\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  >\n" +
        "  <xsd:complexType name=\"phoneType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"area-code\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"phone-number\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"type\" type=\"xsd:string\" />\n" +
        "    </xsd:sequence>\n" +
        "    <xsd:attribute name=\"phone-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "    <xsd:attribute name=\"owner-ref-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"phone\" type=\"phoneType\"/>\n" +
        "  <xsd:complexType name=\"addressType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"street\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"city\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"province\" type=\"xsd:string\" />\n" +
        "    </xsd:sequence>\n" +
        "    <xsd:attribute name=\"address-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"address\" type=\"addressType\"/>\n" +
        "  <xsd:complexType name=\"employeeType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"address\" type=\"addressType\" minOccurs=\"0\" />\n" +
        "      <xsd:element name=\"phones\">\n" +
        "        <xsd:complexType>\n" +
        "          <xsd:sequence>\n" +
        "            <xsd:element maxOccurs=\"unbounded\" name=\"phone-ref\">\n" +
        "              <xsd:complexType>\n" +
        "                <xsd:attribute name=\"phone-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "              </xsd:complexType>\n" +
        "            </xsd:element>\n" +
        "          </xsd:sequence>\n" +
        "        </xsd:complexType>\n" +
        "      </xsd:element>\n" +
        "    </xsd:sequence>\n" +
        "    <xsd:attribute name=\"employee-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "    <xsd:attribute name=\"address-ref-id\" type=\"xsd:int\" use=\"required\" />\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"employee\" type=\"employeeType\"/>\n" +
        "</xsd:schema>";
    static final String KEYMAPPINGS_DBWS =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<dbws\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:ns1=\"urn:keymappings\"\n" +
        "  >\n" +
        "  <name>keymappings</name>\n" +
        "  <query>\n" +
        "    <name>getAllEmployees</name>\n" +
        "    <result isCollection=\"true\">\n" +
        "      <type>ns1:employeeType</type>\n" +
        "    </result>\n" +
        "    <sql><![CDATA[select * from XR_KEYMAP_EMPLOYEE]]></sql>\n" +
        "  </query>\n" +
        "</dbws>\n";
    static final String KEYMAPPINGS_OR_PROJECT = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<eclipselink:object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "   xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\"\n" +
        "   >\n" +
        "   <eclipselink:name>keymappings</eclipselink:name>\n" +
        "   <eclipselink:class-mapping-descriptors>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Address</eclipselink:class>\n" +
        "         <eclipselink:alias>address</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field table=\"XR_KEYMAP_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>addressId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>street</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_ADDRESS\" name=\"STREET\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>city</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_ADDRESS\" name=\"CITY\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>province</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_ADDRESS\" name=\"PROVINCE\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "         <eclipselink:instantiation/>\n" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>\n" +
        "         <eclipselink:tables>\n" +
        "            <eclipselink:table name=\"XR_KEYMAP_ADDRESS\"/>\n" +
        "         </eclipselink:tables>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Employee</eclipselink:class>\n" +
        "         <eclipselink:alias>employee</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field table=\"XR_KEYMAP_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\">\n" +
        "         </eclipselink:querying>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-one-mapping\">\n" +
        "               <eclipselink:attribute-name>address</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Address</eclipselink:reference-class>\n" +
        "               <eclipselink:foreign-key>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field table=\"XR_KEYMAP_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                     <eclipselink:target-field table=\"XR_KEYMAP_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:foreign-key>\n" +
        "               <eclipselink:foreign-key-fields>\n" +
        "                  <eclipselink:field table=\"XR_KEYMAP_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "               </eclipselink:foreign-key-fields>\n" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:read-object-query\"/>\n" +
        "               <eclipselink:join-fetch>inner-join</eclipselink:join-fetch>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>employeeId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>firstName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_EMPLOYEE\" name=\"F_NAME\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>lastName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_EMPLOYEE\" name=\"L_NAME\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-many-mapping\">\n" +
        "               <eclipselink:attribute-name>phones</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Phone</eclipselink:reference-class>\n" +
        "               <eclipselink:target-foreign-key>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field table=\"XR_KEYMAP_PHONE\" name=\"OWNER_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                     <eclipselink:target-field table=\"XR_KEYMAP_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:target-foreign-key>\n" +
        "               <eclipselink:container xsi:type=\"eclipselink:list-container-policy\">\n" +
        "                  <eclipselink:collection-type>org.eclipse.persistence.indirection.IndirectList</eclipselink:collection-type>\n" +
        "               </eclipselink:container>\n" +
        "               <eclipselink:indirection xsi:type=\"eclipselink:transparent-collection-indirection-policy\"/>\n" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:read-all-query\">\n" +
        "                  <eclipselink:container xsi:type=\"eclipselink:list-container-policy\">\n" +
        "                     <eclipselink:collection-type>org.eclipse.persistence.indirection.IndirectList</eclipselink:collection-type>\n" +
        "                  </eclipselink:container>\n" +
        "               </eclipselink:selection-query>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "         <eclipselink:instantiation/>\n" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>\n" +
        "         <eclipselink:tables>\n" +
        "            <eclipselink:table name=\"XR_KEYMAP_EMPLOYEE\"/>\n" +
        "         </eclipselink:tables>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Phone</eclipselink:class>\n" +
        "         <eclipselink:alias>phone</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"PHONE_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>phoneId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"PHONE_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>areaCode</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"AREA_CODE\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>phonenumber</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"P_NUMBER\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "               <eclipselink:attribute-name>type</eclipselink:attribute-name>\n" +
        "               <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"TYPE\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-one-mapping\">\n" +
        "               <eclipselink:attribute-name>owner</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Employee</eclipselink:reference-class>\n" +
        "               <eclipselink:private-owned>false</eclipselink:private-owned>\n" +
        "               <eclipselink:foreign-key>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field table=\"XR_KEYMAP_PHONE\" name=\"OWNER_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                     <eclipselink:target-field table=\"XR_KEYMAP_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:foreign-key>\n" +
        "               <eclipselink:foreign-key-fields>\n" +
        "                  <eclipselink:field table=\"XR_KEYMAP_PHONE\" name=\"OWNER_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "               </eclipselink:foreign-key-fields>\n" +
        "               <eclipselink:batch-reading>true</eclipselink:batch-reading>\n" +
        "               <eclipselink:selection-query xsi:type=\"eclipselink:read-object-query\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "         <eclipselink:instantiation/>\n" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>\n" +
        "         <eclipselink:tables>\n" +
        "            <eclipselink:table name=\"XR_KEYMAP_PHONE\"/>\n" +
        "         </eclipselink:tables>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "   </eclipselink:class-mapping-descriptors>\n" +
        "   <eclipselink:login xsi:type=\"eclipselink:database-login\">\n" +
        "      <eclipselink:bind-all-parameters>true</eclipselink:bind-all-parameters>\n" +
        "   </eclipselink:login>\n" +
        "</eclipselink:object-persistence>";
    static final String KEYMAPPINGS_OX_PROJECT = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<eclipselink:object-persistence version=\"Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)\"\n" +
        "   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "   xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\"\n" +
        "   >\n" +
        "   <eclipselink:name>keymappings</eclipselink:name>\n" +
        "   <eclipselink:class-mapping-descriptors>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Phone</eclipselink:class>\n" +
        "         <eclipselink:alias>phone</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field name=\"@phone-id\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>phoneId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"@phone-id\" xsi:type=\"eclipselink:node\">\n" +
        "               </eclipselink:field>\n" +
        "               <eclipselink:converter xsi:type=\"eclipselink:type-conversion-converter\">\n" +
        "                  <eclipselink:object-class>java.lang.Integer</eclipselink:object-class>\n" +
        "                  <eclipselink:data-class>java.lang.String</eclipselink:data-class>\n" +
        "               </eclipselink:converter>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>areaCode</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:area-code/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>phonenumber</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:phone-number/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>type</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:type/text()\" xsi:type=\"eclipselink:node\"/>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-object-reference-mapping\">\n" +
        "               <eclipselink:attribute-name>owner</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Employee</eclipselink:reference-class>\n" +
        "               <eclipselink:source-to-target-key-field-association>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field name=\"@owner-ref-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                     <eclipselink:target-field name=\"@employee-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:source-to-target-key-field-association>\n" +
        "               <eclipselink:source-to-target-key-fields>\n" +
        "                  <eclipselink:field name=\"@owner-ref-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "               </eclipselink:source-to-target-key-fields>\n" +
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
        "                  <eclipselink:namespace-uri>urn:keymappings</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Address</eclipselink:class>\n" +
        "         <eclipselink:alias>address</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field name=\"@address-id\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>addressId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"@address-id\" xsi:type=\"eclipselink:node\">\n" +
        "               </eclipselink:field>\n" +
        "               <eclipselink:converter xsi:type=\"eclipselink:type-conversion-converter\">\n" +
        "                  <eclipselink:object-class>java.lang.Integer</eclipselink:object-class>\n" +
        "                  <eclipselink:data-class>java.lang.String</eclipselink:data-class>\n" +
        "               </eclipselink:converter>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>street</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:street/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>city</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:city/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>province</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:province/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
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
        "                  <eclipselink:namespace-uri>urn:keymappings</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.keymappings.Employee</eclipselink:class>\n" +
        "         <eclipselink:alias>employee</eclipselink:alias>\n" +
        "         <eclipselink:primary-key>\n" +
        "            <eclipselink:field name=\"@employee-id\" xsi:type=\"eclipselink:column\"/>\n" +
        "         </eclipselink:primary-key>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>employeeId</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"@employee-id\" xsi:type=\"eclipselink:node\">\n" +
        "               </eclipselink:field>\n" +
        "               <eclipselink:converter xsi:type=\"eclipselink:type-conversion-converter\">\n" +
        "                  <eclipselink:object-class>java.lang.Integer</eclipselink:object-class>\n" +
        "                  <eclipselink:data-class>java.lang.String</eclipselink:data-class>\n" +
        "               </eclipselink:converter>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>firstName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:first-name/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-direct-mapping\">\n" +
        "               <eclipselink:attribute-name>lastName</eclipselink:attribute-name>\n" +
        "               <eclipselink:field name=\"ns1:last-name/text()\" xsi:type=\"eclipselink:node\">\n" +
        "                  <eclipselink:schema-type>{http://www.w3.org/2001/XMLSchema}string</eclipselink:schema-type>\n" +
        "               </eclipselink:field>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-object-reference-mapping\">\n" +
        "               <eclipselink:attribute-name>address</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Address</eclipselink:reference-class>\n" +
        "               <eclipselink:source-to-target-key-field-association>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field name=\"@address-ref-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                     <eclipselink:target-field name=\"@address-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:source-to-target-key-field-association>\n" +
        "               <eclipselink:source-to-target-key-fields>\n" +
        "                  <eclipselink:field name=\"@address-ref-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "               </eclipselink:source-to-target-key-fields>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-collection-reference-mapping\">\n" +
        "               <eclipselink:attribute-name>phones</eclipselink:attribute-name>\n" +
        "               <eclipselink:reference-class>dbws.testing.keymappings.Phone</eclipselink:reference-class>\n" +
        "               <eclipselink:source-to-target-key-field-association>\n" +
        "                  <eclipselink:field-reference>\n" +
        "                     <eclipselink:source-field name=\"ns1:phones/ns1:phone-ref/@phone-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                     <eclipselink:target-field name=\"@phone-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "                  </eclipselink:field-reference>\n" +
        "               </eclipselink:source-to-target-key-field-association>\n" +
        "               <eclipselink:source-to-target-key-fields>\n" +
        "                  <eclipselink:field name=\"ns1:phones/ns1:phone-ref/@phone-id\" xsi:type=\"eclipselink:node\"/>\n" +
        "               </eclipselink:source-to-target-key-fields>\n" +
        "               <eclipselink:containerpolicy xsi:type=\"eclipselink:list-container-policy\">\n" +
        "                  <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>\n" +
        "               </eclipselink:containerpolicy>\n" +
        "               <eclipselink:uses-single-node>false</eclipselink:uses-single-node>\n" +
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
        "                  <eclipselink:namespace-uri>urn:keymappings</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsi</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema-instance</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "            </eclipselink:namespaces>\n" +
        "         </eclipselink:namespace-resolver>\n" +
        "      </eclipselink:class-mapping-descriptor>\n" +
        "      <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:xml-class-mapping-descriptor\">\n" +
        "         <eclipselink:class>dbws.testing.RootHelper</eclipselink:class>\n" +
        "         <eclipselink:alias>RootHelper</eclipselink:alias>\n" +
        "         <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "         <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "         <eclipselink:attribute-mappings>\n" +
        "            <eclipselink:attribute-mapping xsi:type=\"eclipselink:xml-any-collection-mapping\">\n" +
        "               <eclipselink:attribute-name>roots</eclipselink:attribute-name>\n" +
        "               <eclipselink:container xsi:type=\"eclipselink:list-container-policy\">\n" +
        "                  <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>\n" +
        "               </eclipselink:container>\n" +
        "               <eclipselink:keep-as-element-policy>KEEP_NONE_AS_ELEMENT</eclipselink:keep-as-element-policy>\n" +
        "            </eclipselink:attribute-mapping>\n" +
        "         </eclipselink:attribute-mappings>\n" +
        "         <eclipselink:descriptor-type>aggregate</eclipselink:descriptor-type>\n" +
        "         <eclipselink:instantiation/>\n" +
        "         <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "         <eclipselink:change-policy xsi:type=\"eclipselink:deferred-detection-change-policy\"/>\n" +
        "         <eclipselink:default-root-element>ns1:employee-address-phone-system</eclipselink:default-root-element>\n" +
        "         <eclipselink:default-root-element-field name=\"ns1:employee-address-phone-system\" xsi:type=\"eclipselink:node\"/>\n" +
        "         <eclipselink:namespace-resolver>\n" +
        "            <eclipselink:namespaces>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>xsd</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>http://www.w3.org/2001/XMLSchema</eclipselink:namespace-uri>\n" +
        "               </eclipselink:namespace>\n" +
        "               <eclipselink:namespace>\n" +
        "                  <eclipselink:prefix>ns1</eclipselink:prefix>\n" +
        "                  <eclipselink:namespace-uri>urn:keymappings</eclipselink:namespace-uri>\n" +
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
                xrSchemaStream = new ByteArrayInputStream(KEYMAPPINGS_SCHEMA.getBytes());
                return super.buildService(xrServiceModel);
            }
            @Override
            public void buildSessions() {
                BaseEntityClassLoader becl = new BaseEntityClassLoader(parentClassLoader);
                XMLContext context = new XMLContext(
                    new EclipseLinkObjectPersistenceRuntimeXMLProject(),becl);
                XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                Project orProject = (Project)unmarshaller.unmarshal(
                    new StringReader(KEYMAPPINGS_OR_PROJECT));
                DatasourceLogin login = new DatabaseLogin();
                login.setUserName(username);
                login.setPassword(password);
                ((DatabaseLogin)login).setConnectionString(url);
                ((DatabaseLogin)login).setDriverClassName(driver);
                Platform platform = new OraclePlatform();
                ConversionManager conversionManager = platform.getConversionManager();
                if (conversionManager != null) {
                    conversionManager.setLoader(becl);
                }
                login.setDatasourcePlatform(platform);
                ((DatabaseLogin)login).bindAllParameters();
                orProject.setDatasourceLogin(login);
                Project oxProject = (Project)unmarshaller.unmarshal(
                    new StringReader(KEYMAPPINGS_OX_PROJECT));
                login = (DatasourceLogin)oxProject.getDatasourceLogin();
                if (login != null) {
                    platform = login.getDatasourcePlatform();
                    if (platform != null) {
                        conversionManager = platform.getConversionManager();
                        if (conversionManager != null) {
                            conversionManager.setLoader(becl);
                        }
                    }
                }
                ProjectHelper.fixOROXAccessors(orProject, oxProject);
                xrService.setORSession(orProject.createDatabaseSession());
                xrService.getORSession().dontLogMessages();
                xrService.setXMLContext(new XMLContext(oxProject));
                xrService.setOXSession(xrService.getXMLContext().getSession(0));
            }
        };
        XMLContext context = new XMLContext(new DBWSModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSModel model = (DBWSModel)unmarshaller.unmarshal(new StringReader(KEYMAPPINGS_DBWS));
        xrService = factory.buildService(model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAllEmployees() {
        Invocation invocation = new Invocation("getAllEmployees");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Vector<BaseEntity> resultVector = (Vector<BaseEntity>)result;
        RootHelper rootHelper = new RootHelper();
        for (BaseEntity employee : resultVector) {
          rootHelper.roots.add(employee);
          rootHelper.roots.add(employee.get(0)); // address
          Vector<BaseEntity> phones = (Vector<BaseEntity>)employee.get(4); // phones
          phones.size(); // trigger IndirectList
          for (BaseEntity phone : phones) {
            rootHelper.roots.add(phone);
          }
        }
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(rootHelper, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEE_COLLECTION_XML));
        assertTrue("control document not same as XRService instance document", 
            comparer.isNodeEqual(controlDoc, doc));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void buildEmployees() {
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(EMPLOYEE_COLLECTION_XML);
        InputSource inputSource = new InputSource(reader);
        Object result = unMarshaller.unmarshal(inputSource);
        assertNotNull("result is null", result);
        RootHelper rootHelper = (RootHelper)result;
        BaseEntity employee1 = (BaseEntity)rootHelper.roots.firstElement();
        assertNotNull("employee1 address is null", employee1.get(0));
        assertTrue("employee1 __pk incorrent", Integer.valueOf(1).equals(employee1.get(1)));
        assertTrue("employee1 first name incorrent", "Mike".equals(employee1.get(2)));
        assertTrue("employee1 last name incorrent", "Norman".equals(employee1.get(3)));
        Vector<BaseEntity> phones = (Vector<BaseEntity>)employee1.get(4); // phones
        assertTrue("employee1 has wrong number of phones", phones.size() == 2);
    }

    public static final String EMPLOYEE_COLLECTION_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<ns1:employee-address-phone-system xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:keymappings\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
           "<ns1:employee employee-id=\"1\" address-ref-id=\"1\">" +
              "<ns1:first-name>Mike</ns1:first-name>" +
              "<ns1:last-name>Norman</ns1:last-name>" +
              "<ns1:phones>" +
                 "<ns1:phone-ref phone-id=\"1\"/>" +
                 "<ns1:phone-ref phone-id=\"2\"/>" +
              "</ns1:phones>" +
           "</ns1:employee>" +
           "<ns1:address address-id=\"1\">" +
              "<ns1:street>20 Pinetrail Cres.</ns1:street>" +
              "<ns1:city>Nepean</ns1:city>" +
              "<ns1:province>Ont</ns1:province>" +
           "</ns1:address>" +
           "<ns1:phone phone-id=\"1\" owner-ref-id=\"1\">" +
              "<ns1:area-code>613</ns1:area-code>" +
              "<ns1:phone-number>2281808</ns1:phone-number>" +
              "<ns1:type>Home</ns1:type>" +
           "</ns1:phone>" +
           "<ns1:phone phone-id=\"2\" owner-ref-id=\"1\">" +
              "<ns1:area-code>613</ns1:area-code>" +
              "<ns1:phone-number>2884638</ns1:phone-number>" +
              "<ns1:type>Work</ns1:type>" +
           "</ns1:phone>" +
           "<ns1:employee employee-id=\"2\" address-ref-id=\"2\">" +
              "<ns1:first-name>Rick</ns1:first-name>" +
              "<ns1:last-name>Barkhouse</ns1:last-name>" +
              "<ns1:phones>" +
                 "<ns1:phone-ref phone-id=\"3\"/>" +
                 "<ns1:phone-ref phone-id=\"4\"/>" +
              "</ns1:phones>" +
           "</ns1:employee>" +
           "<ns1:address address-id=\"2\">" +
              "<ns1:street>Davis Side Rd.</ns1:street>" +
              "<ns1:city>Carleton Place</ns1:city>" +
              "<ns1:province>Ont</ns1:province>" +
           "</ns1:address>" +
           "<ns1:phone phone-id=\"3\" owner-ref-id=\"2\">" +
              "<ns1:area-code>613</ns1:area-code>" +
              "<ns1:phone-number>2832684</ns1:phone-number>" +
              "<ns1:type>Home</ns1:type>" +
           "</ns1:phone>" +
           "<ns1:phone phone-id=\"4\" owner-ref-id=\"2\">" +
              "<ns1:area-code>613</ns1:area-code>" +
              "<ns1:phone-number>2884613</ns1:phone-number>" +
              "<ns1:type>Work</ns1:type>" +
           "</ns1:phone>" +
        "</ns1:employee-address-phone-system>";
}