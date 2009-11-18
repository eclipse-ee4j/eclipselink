package dbws.testing.xrdynamicentity;

//javase imports
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.xr.IndexInfo;
import org.eclipse.persistence.internal.xr.XRClassWriter;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;

public class XRDynamicEntityTestSuite {

    static final String PACKAGE_PREFIX = 
        XRDynamicEntityTestSuite.class.getPackage().getName();
    static final String TEST_CLASSNAME = PACKAGE_PREFIX + ".TestClass";
    static final String FIELD_1 = "field1";
    static final String FIELD_2 = "field2";
    static final String TEST_STRING = "this is a test";
    
    //test fixtures
    static Map<String, IndexInfo> propertyNames2indexes = new HashMap<String, IndexInfo>();
    static DynamicEntity entity1 = null;
    @BeforeClass
    public static void setUp() {
        propertyNames2indexes.put(FIELD_1, new IndexInfo(0, false));
        propertyNames2indexes.put(FIELD_2, new IndexInfo(1, false));
        entity1 = new XRDynamicEntity(propertyNames2indexes);
    }
    
    @Test
    public void nullParent() throws Exception {
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
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<?> testClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertEquals("test class wrong name", testClass.getName(), TEST_CLASSNAME);
        assertTrue("test class not assignableFrom DynamicEntity",
            DynamicEntity.class.isAssignableFrom(testClass));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void buildTestEntity() {
        XRDynamicClassLoader xrdcl = 
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
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
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<?> dynamicClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertNotNull(dynamicClass);
        assertEquals(TEST_CLASSNAME, dynamicClass.getName());
        Class<?> dynamicClass2 = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertSame(dynamicClass, dynamicClass2);
    }

    //stupid naming convention to get tests run in order
    
    @Test
    public void test1() {
        Object field = entity1.get(FIELD_1);
        assertNull(FIELD_1 + " should be null", field);
        assertFalse(FIELD_2 + " shouldn't be set", entity1.isSet(FIELD_2));
    }

    @Test
    public void test2() {
        DynamicEntity e = entity1.set(FIELD_1, TEST_STRING);
        assertSame(e, entity1);
        e = entity1.set(FIELD_2, Integer.valueOf(17));
        assertSame(e, entity1);
    }

    @Test
    public void test3() {
        String test = entity1.<String>get(FIELD_1);
        assertEquals(FIELD_1 + " incorrect value", test, TEST_STRING);
        Integer i = entity1.<Integer>get(FIELD_2);
        assertEquals(FIELD_2 + " incorrect value", i, Integer.valueOf(17));
    }

    @Test(expected=ClassCastException.class)
    public void test4() {
        String s = entity1.<String>get("field2");
        System.identityHashCode(s);
    }

    @Test(expected=DynamicException.class)
    public void test5() {
        entity1.<String>get("field3");
    }
}