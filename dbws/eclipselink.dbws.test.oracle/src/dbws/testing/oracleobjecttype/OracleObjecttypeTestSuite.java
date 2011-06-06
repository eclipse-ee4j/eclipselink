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

package dbws.testing.oracleobjecttype;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.oracle.Oracle11Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;

import static dbws.testing.DBWSTestHelper.CONSTANT_PROJECT_BUILD_VERSION;

public class OracleObjecttypeTestSuite {

    static final String DATABASE_USERNAME_KEY = "db.user";
    static final String DATABASE_PASSWORD_KEY = "db.pwd";
    static final String DATABASE_URL_KEY = "db.url";
    static final String DATABASE_DRIVER_KEY = "db.driver";
    static final String OBJECTTYPE_SCHEMA =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<xsd:schema\n" +
        "  targetNamespace=\"urn:oracleobjecttype\" xmlns=\"urn:oracleobjecttype\" elementFormDefault=\"qualified\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  >\n" +
        "  <xsd:complexType name=\"addressType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"street\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"city\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"province\" type=\"xsd:string\" />\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:complexType name=\"employeeType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"id\" type=\"xsd:int\" />\n" +
        "      <xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"address\" type=\"addressType\" />\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "</xsd:schema>";
    static final String OBJECTTYPE_XRMODEL =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<dbws\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:ns1=\"urn:oracleobjecttype\"\n" +
        "  >\n" +
        "  <name>oracleobjecttype</name>\n" +
        "  <query>\n" +
        "    <name>getEmployeesByProv</name>\n" +
        "    <parameter>\n" +
        "      <name>X</name>\n" +
        "      <type>ns1:addressType</type>\n" +
        "    </parameter>\n" +
        "    <result isCollection=\"true\">\n" +
        "      <type>ns1:employeeType</type>\n" +
        "    </result>\n" +
        "    <named-query>\n" +
        "      <name>getEmployeesByProv</name>\n" +
        "      <descriptor>employee</descriptor>\n" +
        "    </named-query>\n" +
        "  </query>\n" +
        "</dbws>";
    static final String OBJECTTYPE_OR_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\" xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\">" +
           "<name>or-oracleobjecttype</name>" +
           "<class-mapping-descriptors>" +
              "<class-mapping-descriptor xsi:type=\"object-relational-class-mapping-descriptor\">" +
                 "<class>dbws.testing.oracleobjecttype.Address</class>" +
                 "<alias>address</alias>" +
                 "<events/>" +
                 "<querying/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>street</attribute-name>" +
                       "<field name=\"STREET\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>city</attribute-name>" +
                       "<field name=\"CITY\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>province</attribute-name>" +
                       "<field name=\"PROV\" xsi:type=\"column\"/>" +
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
                 "<structure>XR_ADDRESS_TYPE</structure>" +
                 "<field-order>" +
                    "<field name=\"STREET\" xsi:type=\"column\"/>" +
                    "<field name=\"CITY\" xsi:type=\"column\"/>" +
                    "<field name=\"PROV\" xsi:type=\"column\"/>" +
                 "</field-order>" +
              "</class-mapping-descriptor>" +
              "<class-mapping-descriptor xsi:type=\"object-relational-class-mapping-descriptor\">" +
                 "<class>dbws.testing.oracleobjecttype.EmployeeWithAddress</class>" +
                 "<alias>employee</alias>" +
                 "<primary-key>" +
                    "<field table=\"XR_EMP_ADDR\" name=\"EMPNO\" xsi:type=\"column\"/>" +
                 "</primary-key>" +
                 "<events/>" +
                 "<querying>" +
                    "<queries>" +
                       "<query name=\"getEmployeesByProv\" xsi:type=\"read-all-query\">" +
                          "<arguments>" +
                             "<argument name=\"X\">" +
                                "<type>java.lang.Object</type>" +
                             "</argument>" +
                          "</arguments>" +
                          "<call xsi:type=\"stored-procedure-call\">" +
                             "<procedure-name>GET_EMPLOYEES_BY_PROV</procedure-name>" +
                             "<cursor-output-procedure>true</cursor-output-procedure>" +
                             "<arguments>" +
                                "<argument>" +
                                   "<procedure-argument-name>X</procedure-argument-name>" +
                                   "<argument-name>X</argument-name>" +
                                   "<procedure-argument-sqltype>2002</procedure-argument-sqltype>" +
                                   "<procedure-argument-sqltype-name>XR_ADDRESS_TYPE</procedure-argument-sqltype-name>" +
                                "</argument>" +
                                "<argument xsi:type=\"procedure-output-cursor-argument\">" +
                                   "<procedure-argument-name>Y</procedure-argument-name>" +
                                   "<argument-name>Y</argument-name>" +
                                "</argument>" +
                             "</arguments>" +
                          "</call>" +
                          "<reference-class>dbws.testing.oracleobjecttype.EmployeeWithAddress</reference-class>" +
                          "<container xsi:type=\"list-container-policy\">" +
                             "<collection-type>java.util.Vector</collection-type>" +
                          "</container>" +
                       "</query>" +
                    "</queries>" +
                 "</querying>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>id</attribute-name>" +
                       "<field table=\"XR_EMP_ADDR\" name=\"EMPNO\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>firstName</attribute-name>" +
                       "<field table=\"XR_EMP_ADDR\" name=\"FNAME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">" +
                       "<attribute-name>lastName</attribute-name>" +
                       "<field table=\"XR_EMP_ADDR\" name=\"LNAME\" xsi:type=\"column\"/>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"structure-mapping\">" +
                       "<attribute-name>address</attribute-name>" +
                       "<reference-class>dbws.testing.oracleobjecttype.Address</reference-class>" +
                       "<field name=\"ADDRESS\" xsi:type=\"object-relational-field\"/>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>independent</descriptor-type>" +
                 "<caching>" +
                    "<cache-size>-1</cache-size>" +
                 "</caching>" +
                 "<remote-caching>" +
                    "<cache-size>-1</cache-size>" +
                 "</remote-caching>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<tables>" +
                    "<table name=\"XR_EMP_ADDR\"/>" +
                 "</tables>" +
              "</class-mapping-descriptor>" +
           "</class-mapping-descriptors>" +
        "</object-persistence>";
    static final String OBJECTTYPE_OX_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\" xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\">" +
           "<name>ox-oracleobjecttype</name>" +
           "<class-mapping-descriptors>" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">" +
                 "<class>dbws.testing.oracleobjecttype.Address</class>" +
                 "<alias>address</alias>" +
                 "<events/>" +
                 "<querying/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>street</attribute-name>" +
                       "<field name=\"street/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>city</attribute-name>" +
                       "<field name=\"city/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>province</attribute-name>" +
                       "<field name=\"province/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>aggregate</descriptor-type>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<default-root-element>address</default-root-element>" +
                 "<default-root-element-field name=\"address\"/>" +
                 "<namespace-resolver>" +
                    "<default-namespace-uri>urn:oracleobjecttype</default-namespace-uri>" +
                 "</namespace-resolver>" +
                 "<schema xsi:type=\"schema-url-reference\">" +
                    "<schema-context>/addressType</schema-context>" +
                    "<node-type>complex-type</node-type>" +
                 "</schema>" +
              "</class-mapping-descriptor>" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">" +
                 "<class>dbws.testing.oracleobjecttype.EmployeeWithAddress</class>" +
                 "<alias>employee</alias>" +
                 "<events/>" +
                 "<querying/>" +
                 "<attribute-mappings>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>id</attribute-name>" +
                       "<field name=\"id/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}int</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>firstName</attribute-name>" +
                       "<field name=\"first-name/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">" +
                       "<attribute-name>lastName</attribute-name>" +
                       "<field name=\"last-name/text()\" xsi:type=\"node\">" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>" +
                       "</field>" +
                       "<null-policy xsi:type=\"null-policy\">" +
                          "<empty-node-represents-null>true</empty-node-represents-null>" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>" +
                       "</null-policy>" +
                    "</attribute-mapping>" +
                    "<attribute-mapping xsi:type=\"xml-composite-object-mapping\">" +
                       "<attribute-name>address</attribute-name>" +
                       "<reference-class>dbws.testing.oracleobjecttype.Address</reference-class>" +
                       "<field name=\"address\" xsi:type=\"node\"/>" +
                    "</attribute-mapping>" +
                 "</attribute-mappings>" +
                 "<descriptor-type>aggregate</descriptor-type>" +
                 "<instantiation/>" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>" +
                 "<default-root-element>employee</default-root-element>" +
                 "<default-root-element-field name=\"employee\"/>" +
                 "<namespace-resolver>" +
                    "<default-namespace-uri>urn:oracleobjecttype</default-namespace-uri>" +
                 "</namespace-resolver>" +
                 "<schema xsi:type=\"schema-url-reference\">" +
                    "<schema-context>/employeeType</schema-context>" +
                    "<node-type>complex-type</node-type>" +
                 "</schema>" +
              "</class-mapping-descriptor>" +
           "</class-mapping-descriptors>" +
           "<login xsi:type=\"xml-login\">" +
              "<platform-class>org.eclipse.persistence.oxm.platform.DOMPlatform</platform-class>" +
           "</login>" +
        "</object-persistence>";

    // test fixtures
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUp() throws SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
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
        Project orProject = new Project();
        orProject.setName("or-oracleobjecttype");

        ObjectRelationalDataTypeDescriptor addressORDescriptor =
            new ObjectRelationalDataTypeDescriptor();
        addressORDescriptor.setAlias("address");
        addressORDescriptor.useSoftCacheWeakIdentityMap();
        addressORDescriptor.setJavaClass(Address.class);
        addressORDescriptor.descriptorIsAggregate();
        addressORDescriptor.setStructureName("XR_ADDRESS_TYPE");
        addressORDescriptor.addFieldOrdering("STREET");
        addressORDescriptor.addFieldOrdering("CITY");
        addressORDescriptor.addFieldOrdering("PROV");
        addressORDescriptor.addDirectMapping("street", "STREET");
        addressORDescriptor.addDirectMapping("city", "CITY");
        addressORDescriptor.addDirectMapping("province", "PROV");
        orProject.addDescriptor(addressORDescriptor);

        ObjectRelationalDataTypeDescriptor employeeORDescriptor =
            new ObjectRelationalDataTypeDescriptor();
        employeeORDescriptor.useSoftCacheWeakIdentityMap();
        employeeORDescriptor.getQueryManager().checkCacheForDoesExist();
        employeeORDescriptor.setAlias("employee");
        employeeORDescriptor.setJavaClass(EmployeeWithAddress.class);
        employeeORDescriptor.addTableName("XR_EMP_ADDR");
        employeeORDescriptor.addPrimaryKeyFieldName("XR_EMP_ADDR.EMPNO");
        orProject.addDescriptor(employeeORDescriptor);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("XR_EMP_ADDR.EMPNO");
        employeeORDescriptor.addMapping(idMapping);

        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("XR_EMP_ADDR.FNAME");
        employeeORDescriptor.addMapping(firstNameMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("XR_EMP_ADDR.LNAME");
        employeeORDescriptor.addMapping(lastNameMapping);

        StructureMapping addressMapping = new StructureMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setFieldName("ADDRESS");
        employeeORDescriptor.addMapping(addressMapping);

        ReadAllQuery readQuery = new ReadAllQuery(EmployeeWithAddress.class);
        readQuery.setName("getEmployeesByProv");
        readQuery.addArgument("X");
        StoredProcedureCall spCall = new StoredProcedureCall();
        spCall.setProcedureName("GET_EMPLOYEES_BY_PROV");
        spCall.addNamedArgument("X", "X", Types.STRUCT, "XR_ADDRESS_TYPE");
        spCall.useNamedCursorOutputAsResultSet("Y");
        readQuery.setCall(spCall);
        employeeORDescriptor.getQueryManager().addQuery("getEmployeesByProv", readQuery);

        ObjectPersistenceWorkbenchXMLProject runtimeProject =
            new ObjectPersistenceWorkbenchXMLProject();
        XMLTransformationMapping versionMapping =
            (XMLTransformationMapping)runtimeProject.getDescriptor(Project.class).
                getMappings().firstElement();
        TransformerBasedFieldTransformation  versionTransformer =
            (TransformerBasedFieldTransformation)versionMapping.getFieldTransformations().get(0);
        Field transformerField =
            TransformerBasedFieldTransformation.class.getDeclaredField("transformer");
        transformerField.setAccessible(true);
        ConstantTransformer constantTransformer =
            (ConstantTransformer)transformerField.get(versionTransformer);
        constantTransformer.setValue(CONSTANT_PROJECT_BUILD_VERSION);
        XMLContext context = new XMLContext(runtimeProject);
        XMLMarshaller marshaller = context.createMarshaller();
        Document orProjectDoc = xmlPlatform.createDocument();
        marshaller.marshal(orProject, orProjectDoc);
        Document orProjectXMLDoc = xmlParser.parse(new StringReader(OBJECTTYPE_OR_PROJECT));
        assertTrue("OracleObjecttype java-built OR project not same as XML-built OR project",
            comparer.isNodeEqual(orProjectXMLDoc, orProjectDoc));

        NamespaceResolver ns = new NamespaceResolver();
        ns.setDefaultNamespaceURI("urn:oracleobjecttype");
        Project oxProject = new Project();
        oxProject.setName("ox-oracleobjecttype");
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.setPlatform(new DOMPlatform());
        xmlLogin.getProperties().remove("user");
        xmlLogin.getProperties().remove("password");
        oxProject.setLogin(xmlLogin);

        XMLDescriptor addressOXDescriptor = new XMLDescriptor();
        addressOXDescriptor.setAlias("address");
        addressOXDescriptor.setJavaClass(Address.class);
        addressOXDescriptor.setDefaultRootElement("address");
        addressOXDescriptor.setNamespaceResolver(ns);
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext("/addressType");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        addressOXDescriptor.setSchemaReference(schemaReference);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        XMLField streetField = new XMLField();
        streetField.setName("street/text()");
        streetField.setSchemaType(STRING_QNAME);
        streetMapping.setField(streetField);
        addressOXDescriptor.addMapping(streetMapping);
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        XMLField cityField = new XMLField();
        cityField.setName("city/text()");
        cityField.setSchemaType(STRING_QNAME);
        cityMapping.setField(cityField);
        addressOXDescriptor.addMapping(cityMapping);
        XMLDirectMapping provinceMapping = new XMLDirectMapping();
        provinceMapping.setAttributeName("province");
        XMLField provinceField = new XMLField();
        provinceField.setName("province/text()");
        provinceField.setSchemaType(STRING_QNAME);
        provinceMapping.setField(provinceField);
        addressOXDescriptor.addMapping(provinceMapping);
        oxProject.addDescriptor(addressOXDescriptor);

        XMLDescriptor employeeOXDescriptor = new XMLDescriptor();
        employeeOXDescriptor.setAlias("employee");
        employeeOXDescriptor.setJavaClass(EmployeeWithAddress.class);
        employeeOXDescriptor.setDefaultRootElement("employee");
        employeeOXDescriptor.setNamespaceResolver(ns);
        schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext("/employeeType");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        employeeOXDescriptor.setSchemaReference(schemaReference);
        XMLDirectMapping xmlIdMapping = new XMLDirectMapping();
        xmlIdMapping.setAttributeName("id");
        XMLField idField = new XMLField();
        idField.setName("id/text()");
        idField.setSchemaType(INT_QNAME);
        xmlIdMapping.setField(idField);
        employeeOXDescriptor.addMapping(xmlIdMapping);
        XMLDirectMapping xmlFirstNameMapping = new XMLDirectMapping();
        xmlFirstNameMapping.setAttributeName("firstName");
        XMLField firstNameField = new XMLField();
        firstNameField.setName("first-name/text()");
        firstNameField.setSchemaType(STRING_QNAME);
        xmlFirstNameMapping.setField(firstNameField);
        employeeOXDescriptor.addMapping(xmlFirstNameMapping);
        XMLDirectMapping xmlLastNameMapping = new XMLDirectMapping();
        xmlLastNameMapping.setAttributeName("lastName");
        XMLField lastNameField = new XMLField();
        lastNameField.setName("last-name/text()");
        lastNameField.setSchemaType(STRING_QNAME);
        xmlLastNameMapping.setField(lastNameField);
        employeeOXDescriptor.addMapping(xmlLastNameMapping);
        XMLCompositeObjectMapping xmlAddressMapping = new XMLCompositeObjectMapping();
        xmlAddressMapping.setAttributeName("address");
        xmlAddressMapping.setReferenceClass(Address.class);
        xmlAddressMapping.setXPath("address");
        employeeOXDescriptor.addMapping(xmlAddressMapping);
        oxProject.addDescriptor(employeeOXDescriptor);

        Document oxProjectDoc = xmlPlatform.createDocument();
        marshaller.marshal(oxProject, oxProjectDoc);
        Document oxProjectXMLDoc = xmlParser.parse(new StringReader(OBJECTTYPE_OX_PROJECT));
        assertTrue("OracleObjecttype java-built OX project not same as XML-built OX project",
            comparer.isNodeEqual(oxProjectXMLDoc, oxProjectDoc));

        XRServiceFactory factory = new XRServiceFactory() {
            XRServiceFactory init() {
                parentClassLoader = ClassLoader.getSystemClassLoader();
                xrSchemaStream = new ByteArrayInputStream(OBJECTTYPE_SCHEMA.getBytes());
                return this;
            }
            @Override
            public void buildSessions() {
                Project orProject = XMLProjectReader.read(
                    new StringReader(OBJECTTYPE_OR_PROJECT), parentClassLoader);
                DatabaseLogin login = new DatabaseLogin();
                login.setUserName(username);
                login.setPassword(password);
                login.setConnectionString(url);
                login.setDriverClassName(driver);
                login.setDatasourcePlatform(new Oracle11Platform());
                login.bindAllParameters();
                orProject.setDatasourceLogin(login);
                DatabaseSession ds = orProject.createDatabaseSession();
                ds.dontLogMessages();
                xrService.setORSession(ds);
                Project oxProject = XMLProjectReader.read(
                    new StringReader(OBJECTTYPE_OX_PROJECT), parentClassLoader);
                xrService.setXMLContext(new XMLContext(oxProject));
                xrService.setOXSession(xrService.getXMLContext().getSession(0));
            }
        }.init();
        context = new XMLContext(new DBWSModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSModel model = (DBWSModel)unmarshaller.unmarshal(new StringReader(OBJECTTYPE_XRMODEL));
        xrService = factory.buildService(model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getEmployeesByProvWithAddress() throws SQLException  {
        Invocation invocation = new Invocation("getEmployeesByProv");
        Operation op = xrService.getOperation(invocation.getName());
        Address a = new Address();
        a.province = "Ont%";
        invocation.setParameter("X", a);
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("employee-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEE_COLLECTION_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getEmployeesByProvWithStruct() throws SQLException  {
        Invocation invocation = new Invocation("getEmployeesByProv");
        Operation op = xrService.getOperation(invocation.getName());
        Object[] criteria = new Object[3];
        criteria[2] = "Ont%";
        Session orSession = xrService.getORSession();
        Connection connection = ((AbstractSession)orSession).getAccessor().getConnection();
        Struct struct = orSession.getPlatform().createStruct("XR_ADDRESS_TYPE", criteria,
            (AbstractSession)orSession, connection);
        invocation.setParameter("X", struct);
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("employee-collection");
        doc.appendChild(ec);
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
            "<employee xmlns=\"urn:oracleobjecttype\">" +
               "<id>1</id>" +
               "<first-name>Mike</first-name>" +
               "<last-name>Norman</last-name>" +
               "<address>" +
                  "<street>Pinetrail</street>" +
                  "<city>Nepean</city>" +
                  "<province>Ont</province>" +
               "</address>" +
            "</employee>" +
            "<employee xmlns=\"urn:oracleobjecttype\">" +
               "<id>2</id>" +
               "<first-name>Rick</first-name>" +
               "<last-name>Barkhouse</last-name>" +
               "<address>" +
                  "<street>Davis Side Rd</street>" +
                  "<city>Carleton Place</city>" +
                  "<province>Ont</province>" +
               "</address>" +
            "</employee>" +
         "</employee-collection>";
}
