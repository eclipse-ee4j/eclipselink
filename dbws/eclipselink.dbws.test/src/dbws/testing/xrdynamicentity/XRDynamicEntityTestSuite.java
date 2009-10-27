package dbws.testing.xrdynamicentity;

//javase imports
import java.util.HashMap;
import java.util.Map;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.xr.IndexInfo;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;

public class XRDynamicEntityTestSuite {

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