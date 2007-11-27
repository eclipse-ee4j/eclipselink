package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class JDBCObject extends BaseObject {

    public JDBCObject() {
        super();
    }

    public JDBCObject(String name) {
        super(name);
    }

    @Override
    public boolean isJDBC() {
        return true;
    }

    public String toString() {
        return "JDBCObject(name="+name+")";
    }
    
    public boolean equals(Object obj) {
        try {
            return ((JDBCObject) obj).name.equals(this.name);
        } catch (Exception x) {}
        return false;
    }
}