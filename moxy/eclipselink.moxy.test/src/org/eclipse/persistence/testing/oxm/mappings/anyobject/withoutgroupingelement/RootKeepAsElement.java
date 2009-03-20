package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.w3c.dom.Element;

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
                if ((value1 instanceof Element) && (value2 instanceof Element)) {
                    Element elem1 = (Element )value1;
                    Element elem2 = (Element) value2;
                    
                    if(!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                        return false;
                    }
                    
                    return true;
                } else {
                    return this.t1.equals(((RootKeepAsElement) object).getT1());
                }
            }
        }
        return false;
    }    

}