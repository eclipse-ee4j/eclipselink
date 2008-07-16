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

package dbws.testing.oracleobjecttype;

// javase imports
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Java extension imports
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;

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
        "  <xsd:element name=\"address\" type=\"addressType\"/>\n" +
        "  <xsd:complexType name=\"employeeType\">\n" +
        "    <xsd:sequence>\n" +
        "      <xsd:element name=\"id\" type=\"xsd:int\" />\n" +
        "      <xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
        "      <xsd:element name=\"address\" type=\"addressType\" />\n" +
        "    </xsd:sequence>\n" +
        "  </xsd:complexType>\n" +
        "  <xsd:element name=\"employee\" type=\"employeeType\"/>\n" +
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

    // test fixtures
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUp() {
        String username = System.getProperty(DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        String password = System.getProperty(DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        String url = System.getProperty(DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        String driver = System.getProperty(DATABASE_DRIVER_KEY);
        if (driver == null) {
            fail("error retrieving database driver");
        }
        Project orProject = new Project();
        orProject.setName("or-oracleobjecttype");
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setDatasourcePlatform(new Oracle10Platform());
        login.bindAllParameters();
        orProject.setDatasourceLogin(login);

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

        NamespaceResolver ns = new NamespaceResolver();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);
        ns.put("ns1", "urn:oracleobjecttype");
        Project oxProject = new Project();
        oxProject.setName("ox-oracleobjecttype");
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.getProperties().remove("user");
        xmlLogin.getProperties().remove("password");
        oxProject.setLogin(xmlLogin);

        XMLDescriptor addressOXDescriptor = new XMLDescriptor();
        addressOXDescriptor.setAlias("address");
        addressOXDescriptor.setJavaClass(Address.class);
        addressOXDescriptor.setDefaultRootElement("ns1:address");
        addressOXDescriptor.setNamespaceResolver(ns);
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        XMLField streetField = new XMLField();
        streetField.setName("ns1:street/text()");
        streetField.setSchemaType(STRING_QNAME);
        streetMapping.setField(streetField);
        addressOXDescriptor.addMapping(streetMapping);
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        XMLField cityField = new XMLField();
        cityField.setName("ns1:city/text()");
        cityField.setSchemaType(STRING_QNAME);
        cityMapping.setField(cityField);
        addressOXDescriptor.addMapping(cityMapping);
        XMLDirectMapping provinceMapping = new XMLDirectMapping();
        provinceMapping.setAttributeName("province");
        XMLField provinceField = new XMLField();
        provinceField.setName("ns1:province/text()");
        provinceField.setSchemaType(STRING_QNAME);
        provinceMapping.setField(provinceField);
        addressOXDescriptor.addMapping(provinceMapping);
        oxProject.addDescriptor(addressOXDescriptor);

        XMLDescriptor employeeOXDescriptor = new XMLDescriptor();
        employeeOXDescriptor.setAlias("employee");
        employeeOXDescriptor.setJavaClass(EmployeeWithAddress.class);
        employeeOXDescriptor.setDefaultRootElement("ns1:employee");
        employeeOXDescriptor.setNamespaceResolver(ns);
        XMLDirectMapping xmlIdMapping = new XMLDirectMapping();
        xmlIdMapping.setAttributeName("id");
        XMLField idField = new XMLField();
        idField.setName("ns1:id/text()");
        idField.setSchemaType(INT_QNAME);
        xmlIdMapping.setField(idField);
        employeeOXDescriptor.addMapping(xmlIdMapping);
        XMLDirectMapping xmlFirstNameMapping = new XMLDirectMapping();
        xmlFirstNameMapping.setAttributeName("firstName");
        XMLField firstNameField = new XMLField();
        firstNameField.setName("ns1:first-name/text()");
        firstNameField.setSchemaType(STRING_QNAME);
        xmlFirstNameMapping.setField(firstNameField);
        employeeOXDescriptor.addMapping(xmlFirstNameMapping);
        XMLDirectMapping xmlLastNameMapping = new XMLDirectMapping();
        xmlLastNameMapping.setAttributeName("lastName");
        XMLField lastNameField = new XMLField();
        lastNameField.setName("ns1:last-name/text()");
        lastNameField.setSchemaType(STRING_QNAME);
        xmlLastNameMapping.setField(lastNameField);
        employeeOXDescriptor.addMapping(xmlLastNameMapping);
        XMLCompositeObjectMapping xmlAddressMapping = new XMLCompositeObjectMapping();
        xmlAddressMapping.setAttributeName("address");
        xmlAddressMapping.setReferenceClass(Address.class);
        xmlAddressMapping.setXPath("ns1:address");
        employeeOXDescriptor.addMapping(xmlAddressMapping);
        oxProject.addDescriptor(employeeOXDescriptor);

        XRServiceFactory factory = new XRServiceFactory() {
            Project orProject;
            Project oxProject;
            XRServiceFactory setProject(Project orProject, Project oxProject) {
                this.orProject = orProject;
                this.oxProject = oxProject;
                parentClassLoader = ClassLoader.getSystemClassLoader();
                xrSchemaStream = new ByteArrayInputStream(OBJECTTYPE_SCHEMA.getBytes());
                return this;
            }
            @Override
            public void buildSessions() {
                xrService.setORSession(orProject.createDatabaseSession());
                xrService.setXMLContext(new XMLContext(oxProject));
                xrService.setOXSession(xrService.getXMLContext().getSession(0));
            }
        }.setProject(orProject, oxProject);
        XMLContext context = new XMLContext(new DBWSModelProject());
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
            "<ns1:employee xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:oracleobjecttype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "<ns1:id>1</ns1:id>" +
               "<ns1:first-name>Mike</ns1:first-name>" +
               "<ns1:last-name>Norman</ns1:last-name>" +
               "<ns1:address>" +
                  "<ns1:street>Pinetrail</ns1:street>" +
                  "<ns1:city>Nepean</ns1:city>" +
                  "<ns1:province>Ont</ns1:province>" +
               "</ns1:address>" +
            "</ns1:employee>" +
            "<ns1:employee xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:oracleobjecttype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "<ns1:id>2</ns1:id>" +
               "<ns1:first-name>Rick</ns1:first-name>" +
               "<ns1:last-name>Barkhouse</ns1:last-name>" +
               "<ns1:address>" +
                  "<ns1:street>Davis Side Rd</ns1:street>" +
                  "<ns1:city>Carleton Place</ns1:city>" +
                  "<ns1:province>Ont</ns1:province>" +
               "</ns1:address>" +
            "</ns1:employee>" +
         "</employee-collection>";
}
