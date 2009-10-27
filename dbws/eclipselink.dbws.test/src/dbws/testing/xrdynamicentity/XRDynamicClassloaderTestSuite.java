package dbws.testing.xrdynamicentity;

//javase imports
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

//JUnit4 imports
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.xr.IndexInfo;
import org.eclipse.persistence.internal.xr.XRClassWriter;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;

public class XRDynamicClassloaderTestSuite {

    static final String PACKAGE_PREFIX = 
        XRDynamicClassloaderTestSuite.class.getPackage().getName();
    static final String TEST_CLASSNAME = PACKAGE_PREFIX + ".TestClass";
    
    @Test
    public void nullParrent() throws Exception {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        assertNull(xrdcl.getParent());
    }

    @Test
    public void defaultWriter() throws Exception {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        assertEquals(XRClassWriter.class, xrdcl.getDefaultWriter().getClass());
    }

    @Test
    public void coreClass() throws ClassNotFoundException {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        Class<?> stringClass = xrdcl.loadClass("java.lang.String");
        assertTrue("core class java.lang.String not found", String.class == stringClass);
    }

    @Test
    public void buildTestClass() throws ClassNotFoundException {
        //Needs non-null parent for createDynamicClass to work
        XRDynamicClassLoader xrdcl = 
            new XRDynamicClassLoader(XRDynamicClassloaderTestSuite.class.getClassLoader());
        Class<?> testClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertEquals("test class wrong name", testClass.getName(), TEST_CLASSNAME);
        assertTrue("test class not assignableFrom DynamicEntity",
            DynamicEntity.class.isAssignableFrom(testClass));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void buildTestEntity() {
        XRDynamicClassLoader xrdcl = 
            new XRDynamicClassLoader(XRDynamicClassloaderTestSuite.class.getClassLoader());
        Class<DynamicEntity> testClass = 
            (Class<DynamicEntity>)xrdcl.createDynamicClass(TEST_CLASSNAME);
        
        //build instance incorrectly
        DynamicEntity testEntity = null;
        boolean newInstanceFailed = false;
        try {
            testEntity = testClass.newInstance();
        }
        catch (Exception e) {
            newInstanceFailed = true;
        }
        assertTrue("opps! default constructor shouldn't have worked", newInstanceFailed);
        Constructor<DynamicEntity>[] constructors = 
            (Constructor<DynamicEntity>[])testClass.getConstructors();
        assertEquals(1, constructors.length);
        Constructor<DynamicEntity> testClassConstructor = constructors[0];
        assertEquals(1, testClassConstructor.getParameterTypes().length);
        assertEquals(Map.class, testClassConstructor.getParameterTypes()[0]);
        
        //another incorrectly built instance
        newInstanceFailed = false;
        try {
            testEntity = testClassConstructor.newInstance((Map)null);
        }
        catch (Exception e) {
            newInstanceFailed = true;
        }
        assertTrue("constructor with null Map shouldn't have worked", newInstanceFailed);
        
        //finally, build it correctly
        Map<String, IndexInfo> propertyNames2indexes = new HashMap<String, IndexInfo>();
        propertyNames2indexes.put("f1", new IndexInfo(0, false));
        boolean newInstanceWorked = true;
        try {
            testEntity = testClassConstructor.newInstance(propertyNames2indexes);
        }
        catch (Exception e) {
            newInstanceWorked = false;
        }
        assertTrue("constructor with read Map should have worked", newInstanceWorked);
        assertFalse(testEntity.isSet("f1"));
        assertNull(testEntity.get("f1"));
        testEntity.set("f1", "this is a test");
        String s = testEntity.<String>get("f1");
        assertNotNull(s);
        assertEquals("this is a test", s);
        // more tests in XRDynamicEntityTestSuite
    }

    @Test
    public void createTestClassTwice() throws Exception {
        XRDynamicClassLoader xrdcl = 
            new XRDynamicClassLoader(XRDynamicClassloaderTestSuite.class.getClassLoader());
        Class<?> dynamicClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertNotNull(dynamicClass);
        assertEquals(TEST_CLASSNAME, dynamicClass.getName());
        Class<?> dynamicClass2 = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertSame(dynamicClass, dynamicClass2);
    }

}