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
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;

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
        "  <eclipselink:name>relationships</eclipselink:name>\n" +
        "  <eclipselink:class-mapping-descriptors>\n" +
        "    <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "      <eclipselink:class>dbws.testing.relationships.RelationshipsAddress</eclipselink:class>\n" +
        "      <eclipselink:alias>address</eclipselink:alias>\n" +
        "      <eclipselink:primary-key>\n" +
        "        <eclipselink:field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "      </eclipselink:primary-key>\n" +
        "      <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "      <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "      <eclipselink:attribute-mappings>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>addressId</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>city</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"CITY\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>country</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"COUNTRY\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>postalCode</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"P_CODE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>province</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"PROVINCE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>street</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_ADDRESS\" name=\"STREET\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "      </eclipselink:attribute-mappings>\n" +
        "      <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "      <eclipselink:instantiation/>\n" +
        "      <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "      <eclipselink:tables>\n" +
        "        <eclipselink:table name=\"XR_ADDRESS\"/>\n" +
        "      </eclipselink:tables>\n" +
        "    </eclipselink:class-mapping-descriptor>\n" +
        "    <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "      <eclipselink:class>dbws.testing.relationships.RelationshipsEmployee</eclipselink:class>\n" +
        "      <eclipselink:alias>employee</eclipselink:alias>\n" +
        "      <eclipselink:primary-key>\n" +
        "        <eclipselink:field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "      </eclipselink:primary-key>\n" +
        "      <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "      <eclipselink:querying xsi:type=\"eclipselink:query-policy\">\n" +
        "        <eclipselink:queries>\n" +
        "          <eclipselink:query name=\"getAllEmployees\" xsi:type=\"eclipselink:read-all-query\">\n" +
        "            <eclipselink:bind-all-parameters>true</eclipselink:bind-all-parameters>\n" +
        "            <eclipselink:reference-class>dbws.testing.relationships.RelationshipsEmployee</eclipselink:reference-class>\n" +
        "            <eclipselink:outer-join-subclasses>false</eclipselink:outer-join-subclasses>\n" +
        "            <eclipselink:container xsi:type=\"eclipselink:list-container-policy\">\n" +
        "              <eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>\n" +
        "            </eclipselink:container>\n" +
        "            <eclipselink:order-by-expressions>\n" +
        "              <eclipselink:expression function=\"ascending\" xsi:type=\"eclipselink:function-expression\">\n" +
        "                <eclipselink:arguments>\n" +
        "                  <eclipselink:argument name=\"empId\" xsi:type=\"eclipselink:query-key-expression\">\n" +
        "                    <eclipselink:base xsi:type=\"eclipselink:base-expression\"/>\n" +
        "                  </eclipselink:argument>\n" +
        "                </eclipselink:arguments>\n" +
        "              </eclipselink:expression>\n" +
        "            </eclipselink:order-by-expressions>\n" +
        "          </eclipselink:query>\n" +
        "        </eclipselink:queries>\n" +
        "      </eclipselink:querying>\n" +
        "      <eclipselink:attribute-mappings>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-one-mapping\">\n" +
        "          <eclipselink:attribute-name>address</eclipselink:attribute-name>\n" +
        "          <eclipselink:reference-class>dbws.testing.relationships.RelationshipsAddress</eclipselink:reference-class>\n" +
        "          <eclipselink:foreign-key>\n" +
        "            <eclipselink:field-reference>\n" +
        "              <eclipselink:source-field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "              <eclipselink:target-field table=\"XR_ADDRESS\" name=\"ADDRESS_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:field-reference>\n" +
        "          </eclipselink:foreign-key>\n" +
        "          <eclipselink:foreign-key-fields>\n" +
        "            <eclipselink:field table=\"XR_EMPLOYEE\" name=\"ADDR_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "          </eclipselink:foreign-key-fields>\n" +
        "          <eclipselink:selection-query xsi:type=\"eclipselink:read-object-query\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>empId</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>endDate</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"END_DATE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>endTime</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"END_TIME\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>firstName</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"L_NAME\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>gender</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"GENDER\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>lastName</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"F_NAME\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:one-to-many-mapping\">\n" +
        "          <eclipselink:attribute-name>phones</eclipselink:attribute-name>\n" +
        "          <eclipselink:reference-class>dbws.testing.relationships.RelationshipsPhone</eclipselink:reference-class>\n" +
        "          <eclipselink:target-foreign-key>\n" +
        "            <eclipselink:field-reference>\n" +
        "              <eclipselink:source-field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "              <eclipselink:target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:field-reference>\n" +
        "          </eclipselink:target-foreign-key>\n" +
        "          <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "            <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>\n" +
        "          </eclipselink:container>\n" +
        "          <eclipselink:selection-query xsi:type=\"eclipselink:read-all-query\">\n" +
        "            <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "              <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>\n" +
        "            </eclipselink:container>\n" +
        "          </eclipselink:selection-query>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-collection-mapping\">\n" +
        "          <eclipselink:attribute-name>responsibilities</eclipselink:attribute-name>\n" +
        "          <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "            <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>\n" +
        "          </eclipselink:container>\n" +
        "          <eclipselink:selection-query xsi:type=\"eclipselink:direct-read-query\">\n" +
        "            <eclipselink:maintain-cache>false</eclipselink:maintain-cache>\n" +
        "            <eclipselink:container xsi:type=\"eclipselink:container-policy\">\n" +
        "              <eclipselink:collection-type>java.util.ArrayList</eclipselink:collection-type>\n" +
        "            </eclipselink:container>\n" +
        "          </eclipselink:selection-query>\n" +
        "          <eclipselink:reference-table>XR_RESPONS</eclipselink:reference-table>\n" +
        "          <eclipselink:direct-field table=\"XR_RESPONS\" name=\"DESCRIP\" xsi:type=\"eclipselink:column\"/>\n" +
        "          <eclipselink:reference-foreign-key>\n" +
        "            <eclipselink:field-reference>\n" +
        "              <eclipselink:source-field table=\"XR_RESPONS\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "              <eclipselink:target-field table=\"XR_EMPLOYEE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "            </eclipselink:field-reference>\n" +
        "          </eclipselink:reference-foreign-key>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>salary</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_SALARY\" name=\"SALARY\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>startDate</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"START_DATE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>startTime</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"START_TIME\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>version</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_EMPLOYEE\" name=\"VERSION\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "      </eclipselink:attribute-mappings>\n" +
        "      <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "      <eclipselink:instantiation/>\n" +
        "      <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "      <eclipselink:tables>\n" +
        "        <eclipselink:table name=\"XR_EMPLOYEE\"/>\n" +
        "        <eclipselink:table name=\"XR_RESPONS\"/>\n" +
        "        <eclipselink:table name=\"XR_SALARY\"/>\n" +
        "      </eclipselink:tables>\n" +
        "    </eclipselink:class-mapping-descriptor>\n" +
        "    <eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">\n" +
        "      <eclipselink:class>dbws.testing.relationships.RelationshipsPhone</eclipselink:class>\n" +
        "      <eclipselink:alias>phone</eclipselink:alias>\n" +
        "      <eclipselink:primary-key>\n" +
        "        <eclipselink:field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        <eclipselink:field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "      </eclipselink:primary-key>\n" +
        "      <eclipselink:events xsi:type=\"eclipselink:event-policy\"/>\n" +
        "      <eclipselink:querying xsi:type=\"eclipselink:query-policy\"/>\n" +
        "      <eclipselink:attribute-mappings>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>areaCode</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_PHONE\" name=\"AREA_CODE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>empId</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_PHONE\" name=\"EMP_ID\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>phonenumber</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_PHONE\" name=\"P_NUMBER\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "        <eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">\n" +
        "          <eclipselink:attribute-name>type</eclipselink:attribute-name>\n" +
        "          <eclipselink:field table=\"XR_PHONE\" name=\"TYPE\" xsi:type=\"eclipselink:column\"/>\n" +
        "        </eclipselink:attribute-mapping>\n" +
        "      </eclipselink:attribute-mappings>\n" +
        "      <eclipselink:descriptor-type>independent</eclipselink:descriptor-type>\n" +
        "      <eclipselink:instantiation/>\n" +
        "      <eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>\n" +
        "      <eclipselink:tables>\n" +
        "        <eclipselink:table name=\"XR_PHONE\"/>\n" +
        "      </eclipselink:tables>\n" +
        "    </eclipselink:class-mapping-descriptor>\n" +
        "  </eclipselink:class-mapping-descriptors>\n" +
        "  <eclipselink:login xsi:type=\"eclipselink:database-login\">\n" +
        "    <eclipselink:bind-all-parameters>true</eclipselink:bind-all-parameters>\n" +
        "  </eclipselink:login>\n" +
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
                XMLContext context = new XMLContext(
                    new EclipseLinkObjectPersistenceRuntimeXMLProject());
                XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                /*
                Project orProject = (Project)unmarshaller.unmarshal(
                    new StringReader(RELATIONSHIPS_OR_PROJECT));
                */
                // something's wrong with the RELATIONSHIPS_OR_PROJECT; build using API
                Project orProject = new Project();
                orProject.setName("relationships");
                RelationalDescriptor addressDescriptor = new RelationalDescriptor();
                addressDescriptor.setAlias("address");
                addressDescriptor.setJavaClass(RelationshipsAddress.class);
                addressDescriptor.addTableName("XR_ADDRESS");
                addressDescriptor.addPrimaryKeyFieldName("XR_ADDRESS.ADDRESS_ID");
                DirectToFieldMapping addressIdMapping = new DirectToFieldMapping();
                addressIdMapping.setAttributeName("addressId");
                addressIdMapping.setFieldName("XR_ADDRESS.ADDRESS_ID");
                addressDescriptor.addMapping(addressIdMapping);
                DirectToFieldMapping cityMapping = new DirectToFieldMapping();
                cityMapping.setAttributeName("city");
                cityMapping.setFieldName("XR_ADDRESS.CITY");
                addressDescriptor.addMapping(cityMapping);
                DirectToFieldMapping countryMapping = new DirectToFieldMapping();
                countryMapping.setAttributeName("country");
                countryMapping.setFieldName("XR_ADDRESS.COUNTRY");
                addressDescriptor.addMapping(countryMapping);
                DirectToFieldMapping postalCodeMapping = new DirectToFieldMapping();
                postalCodeMapping.setAttributeName("postalCode");
                postalCodeMapping.setFieldName("XR_ADDRESS.P_CODE");
                addressDescriptor.addMapping(postalCodeMapping);
                DirectToFieldMapping provinceMapping = new DirectToFieldMapping();
                provinceMapping.setAttributeName("province");
                provinceMapping.setFieldName("XR_ADDRESS.PROVINCE");
                addressDescriptor.addMapping(provinceMapping);
                DirectToFieldMapping streetMapping = new DirectToFieldMapping();
                streetMapping.setAttributeName("street");
                streetMapping.setFieldName("XR_ADDRESS.STREET");
                addressDescriptor.addMapping(streetMapping);
                orProject.addDescriptor(addressDescriptor);
                RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
                employeeDescriptor.setAlias("employee");
                employeeDescriptor.setJavaClass(RelationshipsEmployee.class);
                employeeDescriptor.addTableName("XR_EMPLOYEE");
                employeeDescriptor.addTableName("XR_RESPONS");
                employeeDescriptor.addTableName("XR_SALARY");
                employeeDescriptor.addPrimaryKeyFieldName("XR_EMPLOYEE.EMP_ID");
                // Named Query -- getAllEmployees
                ReadAllQuery getAllEmployeesQuery = new ReadAllQuery(RelationshipsEmployee.class);
                getAllEmployeesQuery.setName("getAllEmployees");
                getAllEmployeesQuery.setShouldBindAllParameters(true);
                ExpressionBuilder builder = getAllEmployeesQuery.getExpressionBuilder();
                getAllEmployeesQuery.addOrdering(builder.get("empId").ascending());
                employeeDescriptor.getQueryManager().addQuery("getAllEmployees", getAllEmployeesQuery);
                DirectToFieldMapping empIdMapping = new DirectToFieldMapping();
                empIdMapping.setAttributeName("empId");
                empIdMapping.setFieldName("XR_EMPLOYEE.EMP_ID");
                employeeDescriptor.addMapping(empIdMapping);
                DirectToFieldMapping endDateMapping = new DirectToFieldMapping();
                endDateMapping.setAttributeName("endDate");
                endDateMapping.setFieldName("XR_EMPLOYEE.END_DATE");
                employeeDescriptor.addMapping(endDateMapping);
                DirectToFieldMapping endTimeMapping = new DirectToFieldMapping();
                endTimeMapping.setAttributeName("endTime");
                endTimeMapping.setFieldName("XR_EMPLOYEE.END_TIME");
                employeeDescriptor.addMapping(endTimeMapping);
                DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
                firstNameMapping.setAttributeName("firstName");
                firstNameMapping.setFieldName("XR_EMPLOYEE.L_NAME");
                employeeDescriptor.addMapping(firstNameMapping);
                DirectToFieldMapping genderMapping = new DirectToFieldMapping();
                genderMapping.setAttributeName("gender");
                genderMapping.setFieldName("XR_EMPLOYEE.GENDER");
                employeeDescriptor.addMapping(genderMapping);
                DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
                lastNameMapping.setAttributeName("lastName");
                lastNameMapping.setFieldName("XR_EMPLOYEE.F_NAME");
                employeeDescriptor.addMapping(lastNameMapping);
                DirectToFieldMapping salaryMapping = new DirectToFieldMapping();
                salaryMapping.setAttributeName("salary");
                salaryMapping.setFieldName("XR_SALARY.SALARY");
                employeeDescriptor.addMapping(salaryMapping);
                DirectToFieldMapping startDateMapping = new DirectToFieldMapping();
                startDateMapping.setAttributeName("startDate");
                startDateMapping.setFieldName("XR_EMPLOYEE.START_DATE");
                employeeDescriptor.addMapping(startDateMapping);
                DirectToFieldMapping startTimeMapping = new DirectToFieldMapping();
                startTimeMapping.setAttributeName("startTime");
                startTimeMapping.setFieldName("XR_EMPLOYEE.START_TIME");
                employeeDescriptor.addMapping(startTimeMapping);
                DirectToFieldMapping versionMapping = new DirectToFieldMapping();
                versionMapping.setAttributeName("version");
                versionMapping.setFieldName("XR_EMPLOYEE.VERSION");
                employeeDescriptor.addMapping(versionMapping);
                DirectCollectionMapping responsibilitiesMapping = new DirectCollectionMapping();
                responsibilitiesMapping.setAttributeName("responsibilities");
                responsibilitiesMapping.dontUseIndirection();
                responsibilitiesMapping.useCollectionClass(java.util.ArrayList.class);
                responsibilitiesMapping.setReferenceTableName("XR_RESPONS");
                responsibilitiesMapping.setDirectFieldName("XR_RESPONS.DESCRIP");
                responsibilitiesMapping.addReferenceKeyFieldName("XR_RESPONS.EMP_ID", "XR_EMPLOYEE.EMP_ID");
                employeeDescriptor.addMapping(responsibilitiesMapping);
                OneToOneMapping addressMapping = new OneToOneMapping();
                addressMapping.setAttributeName("address");
                addressMapping.setReferenceClass(RelationshipsAddress.class);
                addressMapping.dontUseIndirection();
                addressMapping.addForeignKeyFieldName("XR_EMPLOYEE.ADDR_ID", "XR_ADDRESS.ADDRESS_ID");
                employeeDescriptor.addMapping(addressMapping);
                OneToManyMapping phonesMapping = new OneToManyMapping();
                phonesMapping.setAttributeName("phones");
                phonesMapping.setReferenceClass(RelationshipsPhone.class);
                phonesMapping.dontUseIndirection();
                phonesMapping.useCollectionClass(java.util.ArrayList.class);
                phonesMapping.addTargetForeignKeyFieldName("XR_PHONE.EMP_ID", "XR_EMPLOYEE.EMP_ID");
                employeeDescriptor.addMapping(phonesMapping);
                orProject.addDescriptor(employeeDescriptor);
                RelationalDescriptor phoneDescriptor = new RelationalDescriptor();
                phoneDescriptor.setAlias("phone");
                phoneDescriptor.setJavaClass(RelationshipsPhone.class);
                phoneDescriptor.addTableName("XR_PHONE");
                phoneDescriptor.addPrimaryKeyFieldName("XR_PHONE.EMP_ID");
                phoneDescriptor.addPrimaryKeyFieldName("XR_PHONE.TYPE");
                DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
                areaCodeMapping.setAttributeName("areaCode");
                areaCodeMapping.setFieldName("XR_PHONE.AREA_CODE");
                phoneDescriptor.addMapping(areaCodeMapping);
                DirectToFieldMapping empIdMappingPh = new DirectToFieldMapping();
                empIdMappingPh.setAttributeName("empId");
                empIdMappingPh.setFieldName("XR_PHONE.EMP_ID");
                phoneDescriptor.addMapping(empIdMappingPh);
                DirectToFieldMapping phonenumberMapping = new DirectToFieldMapping();
                phonenumberMapping.setAttributeName("phonenumber");
                phonenumberMapping.setFieldName("XR_PHONE.P_NUMBER");
                phoneDescriptor.addMapping(phonenumberMapping);
                DirectToFieldMapping typeMapping = new DirectToFieldMapping();
                typeMapping.setAttributeName("type");
                typeMapping.setFieldName("XR_PHONE.TYPE");
                phoneDescriptor.addMapping(typeMapping);
                orProject.addDescriptor(phoneDescriptor);
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
        "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
        "<employee-collection>" +
           "<ns1:employee emp-id=\"53\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
              "<ns1:first-name>Way</ns1:first-name>" +
              "<ns1:last-name>Sarah</ns1:last-name>" +
              "<ns1:address address-id=\"52\">" +
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
                    "<ns1:phonenumber>5551234</ns1:phonenumber>" +
                    "<ns1:type>Home</ns1:type>" +
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
              "<ns1:first-name>Smith</ns1:first-name>" +
              "<ns1:last-name>Emanual</ns1:last-name>" +
              "<ns1:address address-id=\"53\">" +
                 "<ns1:street>1111 Mountain Blvd. Floor 53, suite 6</ns1:street>" +
                 "<ns1:city>Vancouver</ns1:city>" +
                 "<ns1:province>BC</ns1:province>" +
                 "<ns1:postal-code>N5J2N5</ns1:postal-code>" +
                 "<ns1:country>Canada</ns1:country>" +
              "</ns1:address>" +
              "<ns1:phones>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>613</ns1:area-code>" +
                    "<ns1:phonenumber>2255943</ns1:phonenumber>" +
                    "<ns1:type>Work Fax</ns1:type>" +
                 "</ns1:phone>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>976</ns1:area-code>" +
                    "<ns1:phonenumber>5556666</ns1:phonenumber>" +
                    "<ns1:type>Pager</ns1:type>" +
                 "</ns1:phone>" +
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
              "<ns1:first-name>Saunders</ns1:first-name>" +
              "<ns1:last-name>Marcus</ns1:last-name>" +
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
           "<ns1:employee emp-id=\"59\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
              "<ns1:first-name>Chanley</ns1:first-name>" +
              "<ns1:last-name>Charles</ns1:last-name>" +
              "<ns1:address address-id=\"59\">" +
                 "<ns1:street>1 Habs Place</ns1:street>" +
                 "<ns1:city>Montreal</ns1:city>" +
                 "<ns1:province>QUE</ns1:province>" +
                 "<ns1:postal-code>Q2S5Z5</ns1:postal-code>" +
                 "<ns1:country>Canada</ns1:country>" +
              "</ns1:address>" +
              "<ns1:phones>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>976</ns1:area-code>" +
                    "<ns1:phonenumber>5556666</ns1:phonenumber>" +
                    "<ns1:type>Pager</ns1:type>" +
                 "</ns1:phone>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>905</ns1:area-code>" +
                    "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                    "<ns1:type>ISDN</ns1:type>" +
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
           "<ns1:employee emp-id=\"61\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
              "<ns1:first-name>Smith</ns1:first-name>" +
              "<ns1:last-name>Bob</ns1:last-name>" +
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
           "<ns1:employee emp-id=\"61\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
              "<ns1:first-name>Smith</ns1:first-name>" +
              "<ns1:last-name>Bob</ns1:last-name>" +
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
              "<ns1:first-name>Way</ns1:first-name>" +
              "<ns1:last-name>John</ns1:last-name>" +
              "<ns1:address address-id=\"54\">" +
                 "<ns1:street>12 Merival Rd., suite 5</ns1:street>" +
                 "<ns1:city>Ottawa</ns1:city>" +
                 "<ns1:province>ONT</ns1:province>" +
                 "<ns1:postal-code>K5J2B5</ns1:postal-code>" +
                 "<ns1:country>Canada</ns1:country>" +
              "</ns1:address>" +
              "<ns1:phones>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>613</ns1:area-code>" +
                    "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                    "<ns1:type>Work</ns1:type>" +
                 "</ns1:phone>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>905</ns1:area-code>" +
                    "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                    "<ns1:type>ISDN</ns1:type>" +
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
           "<ns1:employee emp-id=\"62\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:relationships\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
              "<ns1:first-name>Way</ns1:first-name>" +
              "<ns1:last-name>John</ns1:last-name>" +
              "<ns1:address address-id=\"54\">" +
                 "<ns1:street>12 Merival Rd., suite 5</ns1:street>" +
                 "<ns1:city>Ottawa</ns1:city>" +
                 "<ns1:province>ONT</ns1:province>" +
                 "<ns1:postal-code>K5J2B5</ns1:postal-code>" +
                 "<ns1:country>Canada</ns1:country>" +
              "</ns1:address>" +
              "<ns1:phones>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>613</ns1:area-code>" +
                    "<ns1:phonenumber>2258812</ns1:phonenumber>" +
                    "<ns1:type>Work</ns1:type>" +
                 "</ns1:phone>" +
                 "<ns1:phone>" +
                    "<ns1:area-code>905</ns1:area-code>" +
                    "<ns1:phonenumber>5553691</ns1:phonenumber>" +
                    "<ns1:type>ISDN</ns1:type>" +
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
        "</employee-collection>";

    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<empty/>";
        }
    }
}