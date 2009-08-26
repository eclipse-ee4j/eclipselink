package org.eclipse.persistence.testing.sdo.model.dataobject;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.dataobjects.OpenSequencedTypeImpl;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;

public class OpenSequencedTypeTestCases extends SDOTestCase {

    public OpenSequencedTypeTestCases(String name) {
        super(name);
    }
    
    public void testClassIdentity() {
        TypeHelper helper = TypeHelper.INSTANCE;
        SDOType typeFromTypeHelper = (SDOType) helper.getType(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType");
        
        assertNotNull("OpenSequencedType not found in TypeHelper", typeFromTypeHelper);
        
        // Test that the implClass of the Type found in TypeHelper matches the
        // Class in our library (therefore not generated dynamically).
        Class classFromLib = OpenSequencedTypeImpl.class;
        assertEquals("Classes did not match", classFromLib, typeFromTypeHelper.getImplClass());
    }
    
}