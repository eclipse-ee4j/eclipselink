/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS Oracle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

//java eXtension imports

//JUnit imports
import static org.junit.Assert.fail;
import org.junit.BeforeClass;

//EclipseLink imports
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.platform.database.oracle.Oracle11Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

// domain-specific (testing) imports
import static dbws.testing.visit.WebServiceTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.visit.WebServiceTestSuite.DATABASE_URL_KEY;
import static dbws.testing.visit.WebServiceTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.visit.WebServiceTestSuite.DEFAULT_DATABASE_DRIVER;

public class BuilderTestSuite {

    public static final String CONSTANT_PROJECT_BUILD_VERSION =
        "Eclipse Persistence Services - some version (some build date)";

    // JUnit test fixtures
    public static Connection conn;
    public static String username;
    public static String password;
    public static String url;
    public static Oracle11Platform ora11Platform = new Oracle11Platform();
    public static DBWSBuilder builder = new DBWSBuilder();
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static ObjectPersistenceWorkbenchXMLProject writeObjectPersistenceProject;
    public static EclipseLinkObjectPersistenceRuntimeXMLProject readObjectPersistenceProject;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException, SecurityException,
        NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
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
        @SuppressWarnings("unused")
        Class<?> driverClass = Class.forName(DEFAULT_DATABASE_DRIVER);
        Properties props = new Properties();
        props.put("user", username);
        props.put("password", password);
        conn = DriverManager.getConnection(url, props);

        writeObjectPersistenceProject = new ObjectPersistenceWorkbenchXMLProject();
        XMLTransformationMapping versionMapping =
            (XMLTransformationMapping)writeObjectPersistenceProject.getDescriptor(Project.class).
                getMappings().firstElement();
        TransformerBasedFieldTransformation  versionTransformer =
            (TransformerBasedFieldTransformation)versionMapping.getFieldTransformations().get(0);
        Field transformerField =
            TransformerBasedFieldTransformation.class.getDeclaredField("transformer");
        transformerField.setAccessible(true);
        ConstantTransformer constantTransformer =
            (ConstantTransformer)transformerField.get(versionTransformer);
        constantTransformer.setValue(CONSTANT_PROJECT_BUILD_VERSION);

        readObjectPersistenceProject = new EclipseLinkObjectPersistenceRuntimeXMLProject();
    }
}
