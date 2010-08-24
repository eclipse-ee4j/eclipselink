package org.eclipse.persistence.testing.tests.plsqlrecord;

// javase imports
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Properties;

// JUnit imports
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;

public class PLSQLrecordTestHelper {

    public static XMLComparer comparer = new XMLComparer();
    public static XMLParser xmlParser =
        XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();

    public final static String TEST_DOT_PROPERTIES_KEY = "test.properties";
    public final static String DATABASE_USERNAME_KEY = "db.user";
    public final static String DATABASE_PASSWORD_KEY = "db.pwd";
    public final static String DATABASE_URL_KEY = "db.url";
    public final static String DATABASE_DRIVER_KEY = "db.driver";
    public final static  String CONSTANT_PROJECT_BUILD_VERSION =
        "Eclipse Persistence Services - @VERSION@ (Build @BUILD_NUMBER@)";

    public static Project buildTestProject(Properties p) {
        String username = p.getProperty(DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        String password = p.getProperty(DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        String url = p.getProperty(DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        String driver = p.getProperty(DATABASE_DRIVER_KEY);
        if (driver == null) {
            fail("error retrieving database driver");
        }
        Project project = new Project();
        project.setName("PLSQLrecordProject");
        ObjectRelationalDataTypeDescriptor plsqlempORDescriptor =
            new ObjectRelationalDataTypeDescriptor();
        plsqlempORDescriptor.setJavaClass(PLSQLEmployeeType.class);
        plsqlempORDescriptor.setTableName("EMP");
        plsqlempORDescriptor.setStructureName("EMP_TYPE");

        plsqlempORDescriptor.addPrimaryKeyFieldName("EMPNO");
        plsqlempORDescriptor.addFieldOrdering("EMPNO");
        DirectToFieldMapping employeeNumberMapping = new DirectToFieldMapping();
        employeeNumberMapping.setAttributeName("employeeNumber");
        employeeNumberMapping.setFieldName("EMPNO");
        employeeNumberMapping.setAttributeClassification(BigDecimal.class);
        plsqlempORDescriptor.addMapping(employeeNumberMapping);

        plsqlempORDescriptor.addFieldOrdering("ENAME");
        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("ENAME");
        plsqlempORDescriptor.addMapping(nameMapping);

        plsqlempORDescriptor.addFieldOrdering("JOB");
        DirectToFieldMapping jobMapping = new DirectToFieldMapping();
        jobMapping.setAttributeName("job");
        jobMapping.setFieldName("JOB");
        plsqlempORDescriptor.addMapping(jobMapping);

        plsqlempORDescriptor.addFieldOrdering("MGR");
        DirectToFieldMapping managerMapping = new DirectToFieldMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setFieldName("MGR");
        managerMapping.setAttributeClassification(BigDecimal.class);
        plsqlempORDescriptor.addMapping(managerMapping);

        plsqlempORDescriptor.addFieldOrdering("HIREDATE");
        DirectToFieldMapping hireDateMapping = new DirectToFieldMapping();
        hireDateMapping.setAttributeName("hireDate");
        hireDateMapping.setFieldName("HIREDATE");
        plsqlempORDescriptor.addMapping(hireDateMapping);

        plsqlempORDescriptor.addFieldOrdering("SAL");
        DirectToFieldMapping salaryMapping = new DirectToFieldMapping();
        salaryMapping.setAttributeName("salary");
        salaryMapping.setFieldName("SAL");
        salaryMapping.setAttributeClassification(Float.class);
        plsqlempORDescriptor.addMapping(salaryMapping);

        plsqlempORDescriptor.addFieldOrdering("COMM");
        DirectToFieldMapping commissionMapping = new DirectToFieldMapping();
        commissionMapping.setAttributeName("commission");
        commissionMapping.setFieldName("COMM");
        commissionMapping.setAttributeClassification(Float.class);
        plsqlempORDescriptor.addMapping(commissionMapping);

        plsqlempORDescriptor.addFieldOrdering("DEPTNO");
        DirectToFieldMapping departmentMapping = new DirectToFieldMapping();
        departmentMapping.setAttributeName("department");
        departmentMapping.setFieldName("DEPTNO");
        departmentMapping.setAttributeClassification(BigDecimal.class);
        plsqlempORDescriptor.addMapping(departmentMapping);
        project.addDescriptor(plsqlempORDescriptor);
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setDatasourcePlatform(new Oracle10Platform());
        project.setDatasourceLogin(login);
        return project;
    }

    public static ObjectPersistenceWorkbenchXMLProject buildWorkbenchXMLProject() {

        ObjectPersistenceWorkbenchXMLProject workbenchXMLProject = null;
        try {
            workbenchXMLProject = new ObjectPersistenceWorkbenchXMLProject();
            XMLTransformationMapping versionMapping =
                (XMLTransformationMapping)workbenchXMLProject.getDescriptor(Project.class).
                    getMappings().firstElement();
            TransformerBasedFieldTransformation  versionTransformer =
                (TransformerBasedFieldTransformation)versionMapping.getFieldTransformations().get(0);
            Field transformerField =
                TransformerBasedFieldTransformation.class.getDeclaredField("transformer");
            transformerField.setAccessible(true);
            ConstantTransformer constantTransformer =
                (ConstantTransformer)transformerField.get(versionTransformer);
            constantTransformer.setValue(CONSTANT_PROJECT_BUILD_VERSION);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return workbenchXMLProject;
    }
}