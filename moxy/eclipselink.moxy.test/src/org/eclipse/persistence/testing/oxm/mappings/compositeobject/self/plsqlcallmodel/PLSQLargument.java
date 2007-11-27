package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class PLSQLargument extends BaseObject {
    public BaseObject type;
    public BaseObject secondarytype;

    public PLSQLargument() {
        super();
    }

    public PLSQLargument(String name, BaseObject type, BaseObject secondarytype) {
        super(name);
        this.type = type;
        this.secondarytype = secondarytype;
    }

    public String toString() {
        return "PLSQLargument(name="+name+", type="+type+", secondary type="+secondarytype+")";
    }
    
    public boolean equals(Object obj) {
        PLSQLargument argObj;
        try {
            argObj = (PLSQLargument) obj;
        } catch (Exception x) {
            return false;
        }
        if (!argObj.name.equals(this.name)) {
            return false;
        }
        if (!argObj.type.equals(this.type)) {
            return false;
        }
        if (!argObj.secondarytype.equals(this.secondarytype)) {
            return false;
        }
        return true;
    }
}