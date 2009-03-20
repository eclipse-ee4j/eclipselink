package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class RootKeepAsElement {

    protected Object t1;

    public Object getT1() {
        return t1;
    }
 
    public void setT1(Object value) {
        this.t1 = value;
    }
    
    public boolean equals(Object object) {
        if (object instanceof RootKeepAsElement) {
            if (t1 == null && ((RootKeepAsElement) object).getT1() == null) {
                return true;
            } else if (t1 == null && ((RootKeepAsElement) object).getT1() != null) {
                return false;
            } else {
                Object value1 = t1;
                Object value2 = ((RootKeepAsElement) object).getT1();
                if ((value1 instanceof ElementNSImpl) && (value2 instanceof ElementNSImpl)) {
                    ElementNSImpl elem1 = (ElementNSImpl) value1;
                    ElementNSImpl elem2 = (ElementNSImpl) value2;
                    
                    XMLMappingTestCases.assertEquals(elem1.getLocalName(), elem2.getLocalName());
                    XMLMappingTestCases.assertEquals(elem1.getNamespaceURI(), elem2.getNamespaceURI());
                    XMLMappingTestCases.assertEquals(elem1.getUserData(), elem2.getUserData());
                    
                    return true;
                } else {
                    return this.t1.equals(((RootKeepAsElement) object).getT1());
                }
            }
        }
        return false;
    }    

}