package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class PLSQLObject extends BaseObject {

    public PLSQLObject() {
        super();
    }

    public PLSQLObject(String name) {
        super(name);
    }

    @Override
    public boolean isJDBC() {
        return false;
    }

    public String toString() {
        return "PLSQLObject(name="+name+")";
    }
    
    public boolean equals(Object obj) {
        try {
            return ((PLSQLObject) obj).name.equals(this.name);
        } catch (Exception x) {}
        return false;
    }
}