package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class BaseObject {
    public String name;

    public BaseObject() {
        super();
    }

    public BaseObject(String name) {
        this();
        this.name = name;
    }
    
    public boolean isComplex() {
        return false;
    }
    
    public boolean isJDBC() {
        return false;
    }
    
    public String toString() {
        return "BaseObject(name="+name+")";
    }
    
    public boolean equals(Object obj) {
        try {
            return ((BaseObject) obj).name.equals(this.name);
        } catch (Exception x) {}
        return false;
    }
}