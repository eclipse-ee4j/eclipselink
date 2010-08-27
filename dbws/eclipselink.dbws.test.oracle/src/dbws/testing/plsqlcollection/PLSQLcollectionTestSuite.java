package dbws.testing.plsqlcollection;

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Array;
import org.w3c.dom.Document;

//Java extension libraries

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

public class PLSQLcollectionTestSuite {

    static final String CONSTANT_PROJECT_BUILD_VERSION = 
        "Eclipse Persistence Services - some version (some build date)";
    static final String QUERY_NAME = "PLSQLcollectionQuery";

    static final String DATABASE_USERNAME_KEY = "db.user";
    static final String DATABASE_PASSWORD_KEY = "db.pwd";
    static final String DATABASE_URL_KEY = "db.url";

    static XMLComparer comparer = new XMLComparer();
    static XMLParser xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
    
    // testsuite fixture(s)
    static Project project = null;
    static Session session = null;
    static String username = null;
    static String password = null;
    static String url = null;
    
    @BeforeClass
    public static void setUpProject() {
        username = System.getProperty(DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        password = System.getProperty(DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        url = System.getProperty(DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        project = new Project();
        project.setName("PLSQLcollectionTestSuite");
        ObjectRelationalDataTypeDescriptor t1Descriptor = new ObjectRelationalDataTypeDescriptor();
        t1Descriptor.setAlias("T1");
        t1Descriptor.setJavaClassName("org.eclipse.persistence.testing.tests.plsqlcollection.T1");
        t1Descriptor.descriptorIsAggregate();
        t1Descriptor.getQueryManager();
        project.addDescriptor(t1Descriptor);
        PLSQLCollection simplArray = new PLSQLCollection();
        simplArray.setTypeName("SOMEPACKAGE.TBL1");
        simplArray.setCompatibleType("SOMEPACKAGE_TBL1");
        simplArray.setNestedType(JDBCTypes.VARCHAR_TYPE);
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("SOMEPACKAGE.P1");
        call.addNamedArgument("SIMPLARRAY", simplArray);
        call.addNamedArgument("FOO", JDBCTypes.VARCHAR_TYPE, 10);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("SIMPLARRAY", Array.class);
        query.addArgument("FOO", String.class);
        query.setCall(call);
        t1Descriptor.getQueryManager().addQuery(QUERY_NAME, query);
    }
    
    @AfterClass
    public static void tearDown() {
        if (session != null) {
            ((DatabaseSession)session).logout();
        }
    }

    static final String TEST_PROJECT_CONTROL_DOC = 
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\" " + 
           "xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" " +
           "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
           "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
           "xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\">" +
           "<name>PLSQLcollectionTestSuite</name>" +
           "<class-mapping-descriptors>" +
                 "<class-mapping-descriptor xsi:type=\"object-relational-class-mapping-descriptor\">" +
                    "<class>org.eclipse.persistence.testing.tests.plsqlcollection.T1</class>" +
                    "<alias>T1</alias>" +
                    "<events/>" +
                    "<querying>" +
                       "<queries>" +
                          "<query name=\"PLSQLcollectionQuery\" xsi:type=\"data-modify-query\">" +
                             "<arguments>" +
                                "<argument name=\"SIMPLARRAY\">" +
                                   "<type>java.sql.Array</type>" +
                                "</argument>" +
                                "<argument name=\"FOO\">" +
                                   "<type>java.lang.String</type>" +
                                "</argument>" +
                             "</arguments>" +
                             "<call xsi:type=\"plsql-stored-procedure-call\">" +
                                "<procedure-name>SOMEPACKAGE.P1</procedure-name>" +
                                "<arguments>" +
                                    "<argument xsi:type=\"plsql-collection\">" +
                                       "<name>SIMPLARRAY</name>" +
                                       "<index>0</index>" +
                                       "<type-name>SOMEPACKAGE.TBL1</type-name>" +
                                       "<compatible-type>SOMEPACKAGE_TBL1</compatible-type>" +
                                       "<nested-type type-name=\"VARCHAR_TYPE\" xsi:type=\"jdbc-type\"/>" +
                                    "</argument>" +
                                    "<argument type-name=\"VARCHAR_TYPE\" xsi:type=\"jdbc-type\">" +
                                      "<name>FOO</name>" +
                                      "<index>1</index>" +
                                      "<length>10</length>" +
                                    "</argument>" +
                                "</arguments>" +
                             "</call>" +
                         "</query>" +
                    "</queries>" +
              "</querying>" +
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
        "</object-persistence>";
    @Test
    public void toProjectXML() throws IllegalArgumentException, IllegalAccessException,
        SecurityException, NoSuchFieldException {
        ObjectPersistenceWorkbenchXMLProject runtimeProject = 
            new ObjectPersistenceWorkbenchXMLProject();
        XMLTransformationMapping versionMapping = 
            (XMLTransformationMapping)runtimeProject.getDescriptor(Project.class).
                getMappings().get(0);
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
        Document testDoc = marshaller.objectToXML(project);
        Document controlDoc = xmlParser.parse(new StringReader(TEST_PROJECT_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, testDoc));
    }

    @Test
    public void fromProjectXML() {
        XRDynamicClassLoader xrdecl = 
            new XRDynamicClassLoader(PLSQLcollectionTestSuite.class.getClassLoader());
        Project projectFromXML = XMLProjectReader.read(new StringReader(TEST_PROJECT_CONTROL_DOC),
            xrdecl);
        DatasourceLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        ((DatabaseLogin)login).setConnectionString(url);
        ((DatabaseLogin)login).setDriverClassName("oracle.jdbc.OracleDriver");
        Platform platform = new Oracle10Platform();
        ConversionManager cm = platform.getConversionManager();
        cm.setLoader(xrdecl);
        login.setDatasourcePlatform(platform);
        ((DatabaseLogin)login).bindAllParameters();
        projectFromXML.setDatasourceLogin(login);
        ProjectHelper.fixOROXAccessors(projectFromXML, null);
        xrdecl.dontGenerateSubclasses();
        ClassDescriptor t1Descriptor = projectFromXML.getDescriptorForAlias("T1");
        DatabaseQuery query = t1Descriptor.getQueryManager().getQuery(QUERY_NAME);
        assertTrue(QUERY_NAME + " is wrong type of query: " + query.getClass().getSimpleName(),
            query.isDataModifyQuery());
        project = projectFromXML;
    }
    
    @Test
    public void testTbl1() {
        session = project.createDatabaseSession();
        session.dontLogMessages();
        ClassDescriptor t1Descriptor = session.getDescriptorForAlias("T1");
        Class<?> t1Clz = t1Descriptor.getJavaClass();
        ((DatabaseSession)session).login();
        String[] elements = {"first string", "second string", "third string"};
        NonSynchronizedVector queryArgs = new NonSynchronizedVector();
        queryArgs.add(elements);
        queryArgs.add("barf");
        boolean worked = false;
        String msg = null;
        try {
            session.executeQuery(QUERY_NAME, t1Clz, queryArgs);
            worked = true;
        }
        catch (Exception e) {
            msg = e.getMessage();
        }
        assertTrue("invocation somePackage.p1 failed: " + msg, worked);
    }
}