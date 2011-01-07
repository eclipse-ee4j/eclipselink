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

package dbws.testing.veearray;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.SQLException;
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
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;

public class VeearrayTestSuite {

    static final String DATABASE_USERNAME_KEY = "db.user";
    static final String DATABASE_PASSWORD_KEY = "db.pwd";
    static final String DATABASE_URL_KEY = "db.url";
    static final String DATABASE_DRIVER_KEY = "db.driver";
    static final String VEEARRAY_SCHEMA =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xsd:schema\n" +
        "  targetNamespace=\"urn:veearray\" xmlns=\"urn:veearray\" elementFormDefault=\"qualified\"\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  >\n" +
        "   <xsd:complexType name=\"phoneType\">\n" +
        "      <xsd:sequence>\n" +
        "        <xsd:element name=\"areaCode\" type=\"xsd:string\" />\n" +
        "        <xsd:element name=\"phoneNumber\" type=\"xsd:string\" />\n" +
        "        <xsd:element name=\"type\" type=\"xsd:string\" />\n" +
        "      </xsd:sequence>\n" +
        "   </xsd:complexType>\n" +
        "   <xsd:complexType name=\"employeeType\">\n" +
        "      <xsd:sequence>\n" +
        "        <xsd:element name=\"id\" type=\"xsd:int\" />\n" +
        "        <xsd:element name=\"first-name\" type=\"xsd:string\" />\n" +
        "        <xsd:element name=\"last-name\" type=\"xsd:string\" />\n" +
        "        <xsd:sequence>\n" +
        "           <xsd:element name=\"phones\" type=\"phoneType\" minOccurs=\"0\" />\n" +
        "        </xsd:sequence>\n" +
        "      </xsd:sequence>\n" +
        "   </xsd:complexType>\n" +
        "</xsd:schema>";
    static final String VEEARRAY_XRMODEL =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<dbws\n" +
        "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xmlns:ns1=\"urn:veearray\"\n" +
        "  >\n" +
        "  <name>veearray</name>\n" +
        "  <query>\n" +
        "     <name>getVeeArrayEmployees</name>\n" +
        "     <result isCollection=\"true\">\n" +
        "       <type>ns1:employeeType</type>\n" +
        "     </result>\n" +
        "     <named-query>\n" +
        "       <name>getVeeArrayEmployees</name>\n" +
        "       <descriptor>employee</descriptor>\n" +
        "     </named-query>\n" +
        "  </query>\n" +
        "  <query>\n" +
        "     <name>getVeeArrayEmployee</name>\n" +
        "     <parameter>\n" +
        "        <name>X</name>\n" +
        "        <type>xsd:int</type>\n" +
        "     </parameter>\n" +
        "     <result isCollection=\"true\">\n" +
        "       <type>ns1:employeeType</type>\n" +
        "     </result>\n" +
        "     <named-query>\n" +
        "       <name>getVeeArrayEmployee</name>\n" +
        "       <descriptor>employee</descriptor>\n" +
        "     </named-query>\n" +
        "  </query>\n" +
        "  <query>\n" +
        "     <name>updateVeeArrayPhones</name>\n" +
        "     <parameter>\n" +
        "        <name>X</name>\n" +
        "        <type>xsd:int</type>\n" +
        "     </parameter>\n" +
        "     <parameter>\n" +
        "        <name>Y</name>\n" +
        "        <type>ns1:phoneType</type>\n" +
        "     </parameter>\n" +
        "     <result>\n" +
        "        <type>xsd:int</type>\n" +
        "     </result>\n" +
        "     <named-query>\n" +
        "       <name>updateVeeArrayPhones</name>\n" +
        "       <descriptor>employee</descriptor>\n" +
        "     </named-query>\n" +
        "  </query>\n" +
        "</dbws>\n";

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
        orProject.setName("or-veearray");
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setDatasourcePlatform(new Oracle10Platform());
        login.bindAllParameters();
        orProject.setDatasourceLogin(login);

        ObjectRelationalDataTypeDescriptor phoneORDescriptor =
            new ObjectRelationalDataTypeDescriptor();
        phoneORDescriptor.setAlias("phone");
        phoneORDescriptor.useSoftCacheWeakIdentityMap();
        phoneORDescriptor.setJavaClass(Phone.class);
        phoneORDescriptor.descriptorIsAggregate();
        phoneORDescriptor.setStructureName("XR_VEE_ARRAY_PHONE");
        phoneORDescriptor.addFieldOrdering("AREACODE");
        phoneORDescriptor.addFieldOrdering("PHONENUMBER");
        phoneORDescriptor.addFieldOrdering("PHONETYPE");
        phoneORDescriptor.addDirectMapping("areaCode", "AREACODE");
        phoneORDescriptor.addDirectMapping("phonenumber", "PHONENUMBER");
        phoneORDescriptor.addDirectMapping("type", "PHONETYPE");
        orProject.addDescriptor(phoneORDescriptor);

        ObjectRelationalDataTypeDescriptor employeeORDescriptor =
            new ObjectRelationalDataTypeDescriptor();
        employeeORDescriptor.useSoftCacheWeakIdentityMap();
        employeeORDescriptor.getQueryManager().checkCacheForDoesExist();
        employeeORDescriptor.setAlias("employee");
        employeeORDescriptor.setJavaClass(Employee.class);
        employeeORDescriptor.addTableName("XR_VEE_ARRAY_EMP");
        employeeORDescriptor.addPrimaryKeyFieldName("XR_VEE_ARRAY_EMP.EMPNO");
        orProject.addDescriptor(employeeORDescriptor);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("XR_VEE_ARRAY_EMP.EMPNO");
        employeeORDescriptor.addMapping(idMapping);

        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("XR_VEE_ARRAY_EMP.FNAME");
        employeeORDescriptor.addMapping(firstNameMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("XR_VEE_ARRAY_EMP.LNAME");
        employeeORDescriptor.addMapping(lastNameMapping);

        ObjectArrayMapping phonesMapping = new ObjectArrayMapping();
        phonesMapping.setAttributeName("phones");
        phonesMapping.setStructureName("XR_VEE_ARRAY_PHONES");
        phonesMapping.setReferenceClass(Phone.class);
        phonesMapping.setFieldName("PHONES");
        employeeORDescriptor.addMapping(phonesMapping);

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setName("getVeeArrayEmployees");
        raq.refreshIdentityMapResult();
        StoredProcedureCall spCall = new StoredProcedureCall();
        spCall.setProcedureName("GET_VEE_ARRAY_EMPS");
        spCall.useNamedCursorOutputAsResultSet("X");
        raq.setCall(spCall);
        employeeORDescriptor.getQueryManager().addQuery("getVeeArrayEmployees", raq);

        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
        roq.setName("getVeeArrayEmployee");
        roq.refreshIdentityMapResult();
        roq.addArgument("X");
        spCall = new StoredProcedureCall();
        spCall.setProcedureName("GET_VEE_ARRAY_EMP");
        spCall.addNamedArgument("X", "X", Types.INTEGER);
        spCall.useNamedCursorOutputAsResultSet("Y");
        roq.setCall(spCall);
        employeeORDescriptor.getQueryManager().addQuery("getVeeArrayEmployee", roq);

        ObjectRelationalDatabaseField ordf = new ObjectRelationalDatabaseField("");
        ordf.setSqlType(Types.STRUCT);
        ordf.setSqlTypeName("XR_VEE_ARRAY_PHONE");
        ordf.setType(Phone.class);
        DataModifyQuery dataModifyQuery = new DataModifyQuery();
        dataModifyQuery.setName("updateVeeArrayPhones");
        dataModifyQuery.addArgument("X");
        dataModifyQuery.addArgument("Y");
        spCall = new StoredProcedureCall();
        spCall.setProcedureName("UPDATE_VEE_ARRAY_PHS");
        spCall.addNamedArgument("X", "X", Types.INTEGER);
        spCall.addNamedArgument("Y", "Y", Types.ARRAY, "XR_VEE_ARRAY_PHONES", ordf);
        dataModifyQuery.setCall(spCall);
        employeeORDescriptor.getQueryManager().addQuery("updateVeeArrayPhones", dataModifyQuery);

        NamespaceResolver ns = new NamespaceResolver();
        ns.setDefaultNamespaceURI("urn:veearray");
        Project oxProject = new Project();
        oxProject.setName("ox-veearray");
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.getProperties().remove("user");
        xmlLogin.getProperties().remove("password");
        oxProject.setLogin(xmlLogin);

        XMLDescriptor employeeOXDescriptor = new XMLDescriptor();
        employeeOXDescriptor.setAlias("employee");
        employeeOXDescriptor.setJavaClass(Employee.class);
        employeeOXDescriptor.setDefaultRootElement("employee");
        employeeOXDescriptor.setNamespaceResolver(ns);
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext("/employeeType");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        employeeOXDescriptor.setSchemaReference(schemaReference);

        XMLDirectMapping xmlIdMapping = new XMLDirectMapping();
        xmlIdMapping.setAttributeName("id");
        XMLField idField = new XMLField();
        idField.setName("@id/text()");
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
        XMLCompositeCollectionMapping xmlPhonesMapping = new XMLCompositeCollectionMapping();
        xmlPhonesMapping.setAttributeName("phones");
        xmlPhonesMapping.setReferenceClass(Phone.class);
        xmlPhonesMapping.setXPath("phones/phone");
        employeeOXDescriptor.addMapping(xmlPhonesMapping);
        oxProject.addDescriptor(employeeOXDescriptor);

        XMLDescriptor phoneOXDescriptor = new XMLDescriptor();
        phoneOXDescriptor.setAlias("phone");
        phoneOXDescriptor.setJavaClass(Phone.class);
        phoneOXDescriptor.setDefaultRootElement("phone");
        phoneOXDescriptor.setNamespaceResolver(ns);
        schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext("/phoneType");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        phoneOXDescriptor.setSchemaReference(schemaReference);
        XMLDirectMapping areaCodeMapping = new XMLDirectMapping();
        areaCodeMapping.setAttributeName("areaCode");
        XMLField areaCodeField = new XMLField();
        areaCodeField.setName("area-code/text()");
        areaCodeField.setSchemaType(STRING_QNAME);
        areaCodeMapping.setField(areaCodeField);
        phoneOXDescriptor.addMapping(areaCodeMapping);
        XMLDirectMapping phonenumberMapping = new XMLDirectMapping();
        phonenumberMapping.setAttributeName("phonenumber");
        XMLField phonenumberField = new XMLField();
        phonenumberField.setName("phonenumber/text()");
        phonenumberField.setSchemaType(STRING_QNAME);
        phonenumberMapping.setField(phonenumberField);
        phoneOXDescriptor.addMapping(phonenumberMapping);
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        XMLField typeField = new XMLField();
        typeField.setName("type/text()");
        typeField.setSchemaType(STRING_QNAME);
        typeMapping.setField(typeField);
        phoneOXDescriptor.addMapping(typeMapping);
        oxProject.addDescriptor(phoneOXDescriptor);

        XRServiceFactory factory = new XRServiceFactory() {
            Project orProject;
            Project oxProject;
            XRServiceFactory setProject(Project orProject, Project oxProject) {
                this.orProject = orProject;
                this.oxProject = oxProject;
                parentClassLoader = ClassLoader.getSystemClassLoader();
                xrSchemaStream = new ByteArrayInputStream(VEEARRAY_SCHEMA.getBytes());
                return this;
            }
            @Override
            public void buildSessions() {
                DatabaseSession ds = orProject.createDatabaseSession();
                ds.dontLogMessages();
                xrService.setORSession(ds);
                xrService.setXMLContext(new XMLContext(oxProject));
                xrService.setOXSession(xrService.getXMLContext().getSession(0));
            }
        }.setProject(orProject, oxProject);
        XMLContext context = new XMLContext(new DBWSModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSModel model = (DBWSModel)unmarshaller.unmarshal(new StringReader(VEEARRAY_XRMODEL));
        xrService = factory.buildService(model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getVeeArrayEmployees() {
        Invocation invocation = new Invocation("getVeeArrayEmployees");
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
        Document controlDoc = xmlParser.parse(new StringReader(INITIAL_EMPLOYEE_COLLECTION_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INITIAL_EMPLOYEE_COLLECTION_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<employee-collection>" +
          "<employee id=\"1\" xmlns=\"urn:veearray\">" +
             "<first-name>Mike</first-name>" +
             "<last-name>Norman</last-name>" +
             "<phones>" +
                "<phone>" +
                   "<area-code>613</area-code>" +
                   "<phonenumber>288-4638</phonenumber>" +
                   "<type>Work</type>" +
                "</phone>" +
                "<phone>" +
                   "<area-code>613</area-code>" +
                   "<phonenumber>228-1808</phonenumber>" +
                   "<type>Home</type>" +
                "</phone>" +
             "</phones>" +
          "</employee>" +
          "<employee id=\"2\" xmlns=\"urn:veearray\">" +
             "<first-name>Rick</first-name>" +
             "<last-name>Barkhouse</last-name>" +
             "<phones>" +
                "<phone>" +
                   "<area-code>613</area-code>" +
                   "<phonenumber>288-zzzz</phonenumber>" +
                   "<type>Work</type>" +
                "</phone>" +
                "<phone>" +
                   "<area-code>613</area-code>" +
                   "<phonenumber>aaa-bbbb</phonenumber>" +
                   "<type>Home</type>" +
                "</phone>" +
             "</phones>" +
          "</employee>" +
        "</employee-collection>";

    @SuppressWarnings("unchecked")
    @Test
    public void updateVeeArrayPhones() throws SQLException {
        Invocation invocation = new Invocation("updateVeeArrayPhones");
        invocation.setParameter("X", 2);
        Phone newPhone1 = new Phone();
        newPhone1.areaCode = "613";
        newPhone1.phonenumber = "288-4613";
        newPhone1.type = "Work";
        Phone newPhone2 = new Phone();
        newPhone2.areaCode = "613";
        newPhone2.phonenumber = "230-1579";
        newPhone2.type = "Home";
        Vector newPhones = new NonSynchronizedVector();
        newPhones.add(newPhone1);
        newPhones.add(newPhone2);
        invocation.setParameter("Y", newPhones);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String VALUE_1_XML =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<value>1</value>";

    @Test
    public void checkUpdatedVeeArrayPhones() {
        Invocation invocation = new Invocation("getVeeArrayEmployee");
        invocation.setParameter("X", 2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(UPDATED_EMPLOYEE_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String UPDATED_EMPLOYEE_XML =
    "<?xml version = '1.0' encoding = 'UTF-8'?>" +
    "<employee id=\"2\" xmlns=\"urn:veearray\">" +
      "<first-name>Rick</first-name>" +
      "<last-name>Barkhouse</last-name>" +
      "<phones>" +
        "<phone>" +
          "<area-code>613</area-code>" +
          "<phonenumber>288-4613</phonenumber>" +
          "<type>Work</type>" +
        "</phone>" +
        "<phone>" +
          "<area-code>613</area-code>" +
          "<phonenumber>230-1579</phonenumber>" +
          "<type>Home</type>" +
        "</phone>" +
      "</phones>" +
    "</employee>";

    @SuppressWarnings("unchecked")
    @Test
    public void resetVeeArrayPhones() {
        Invocation invocation = new Invocation("updateVeeArrayPhones");
        invocation.setParameter("X", 2);
        Phone newPhone1 = new Phone();
        newPhone1.areaCode = "613";
        newPhone1.phonenumber = "288-zzzz";
        newPhone1.type = "Work";
        Phone newPhone2 = new Phone();
        newPhone2.areaCode = "613";
        newPhone2.phonenumber = "aaa-bbbb";
        newPhone2.type = "Home";
        Vector newPhones = new NonSynchronizedVector();
        newPhones.add(newPhone1);
        newPhones.add(newPhone2);
        invocation.setParameter("Y", newPhones);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
}
