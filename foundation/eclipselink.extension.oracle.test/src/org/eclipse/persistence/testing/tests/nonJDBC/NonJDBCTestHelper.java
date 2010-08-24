package org.eclipse.persistence.testing.tests.nonJDBC;

// javase imports
import java.lang.reflect.Field;
import java.util.Properties;

// JUnit imports
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;

public class NonJDBCTestHelper {

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
        project.setName("nonJDBCTestProject");
        RelationalDescriptor emptyDescriptor = new RelationalDescriptor();
        emptyDescriptor.setAlias("Empty");
        emptyDescriptor.setJavaClass(Empty.class);
        emptyDescriptor.addTableName("EMPTY");
        emptyDescriptor.addPrimaryKeyFieldName("EMPTY.ID");
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPTY.ID");
        emptyDescriptor.addMapping(idMapping);
        project.addDescriptor(emptyDescriptor);
        DatabaseLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        login.setConnectionString(url);
        login.setDriverClassName(driver);
        login.setDatasourcePlatform(new Oracle10Platform());
        login.useBatchWriting();
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
