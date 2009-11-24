package dbws.testing.xrdynamicentity;

//javase imports
import java.lang.reflect.Field;

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
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.xr.XRClassWriter;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.internal.xr.XRFieldInfo;
import static org.eclipse.persistence.internal.xr.XRDynamicEntity.XR_FIELD_INFO_STATIC;

public class XRDynamicEntityTestSuite {

    static final String PACKAGE_PREFIX = 
        XRDynamicEntityTestSuite.class.getPackage().getName();
    static final String TEST_CLASSNAME = PACKAGE_PREFIX + ".TestClass";
    static final String FIELD_1 = "field1";
    static final String FIELD_2 = "field2";
    static final String TEST_STRING = "this is a test";
    
    //test fixtures
    static XRDynamicEntity entity1 = null;
    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUp() throws NoSuchFieldException, IllegalArgumentException, 
        IllegalAccessException {
        Field xrfiField = Helper.getField(XRCustomer.class, XR_FIELD_INFO_STATIC);
        XRFieldInfo xrfi = (XRFieldInfo)xrfiField.get(null);
        xrfi.addFieldInfo(FIELD_1, 0);
        xrfi.addFieldInfo(FIELD_2, 1);
        entity1 = new XRCustomer();
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
    public void buildTestEntity() throws InstantiationException, IllegalAccessException {
        XRDynamicClassLoader xrdcl = 
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<DynamicEntity> testClass = 
            (Class<DynamicEntity>)xrdcl.createDynamicClass(TEST_CLASSNAME);
        //build instance
        @SuppressWarnings("unused")
        DynamicEntity testEntity = testClass.newInstance();
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