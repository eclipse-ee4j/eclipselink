package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplexPLSQLObject extends BaseObject {
    public List<PLSQLargument> fields = new ArrayList<PLSQLargument>();
    
    public ComplexPLSQLObject() {
        super();
    }

    public ComplexPLSQLObject(String name) {
        super(name);
    }

    @Override
    public boolean isComplex() {
        return true;
    }
    
    public String toString() {
        return "ComplexPLSQLObject(name="+name+", fields="+fields+")";
    }
    
    public boolean equals(Object obj) {
        ComplexPLSQLObject complexObj;
        try {
            complexObj = (ComplexPLSQLObject) obj;
        } catch (Exception x) {
            return false;
        }
        if (!complexObj.name.equals(this.name)) {
            return false;
        }
        if (complexObj.fields == null) {
            return fields == null;
        }
        if (fields == null) {
            return false;
        }
        if (fields.size() != complexObj.fields.size()) {
            return false;
        }
        Iterator<PLSQLargument> fieldIt = fields.iterator();
        while (fieldIt.hasNext()) {
            PLSQLargument arg = fieldIt.next();
            if (!complexObj.fields.contains(arg)) {
                return false;
            }
        }
        return true;
    }
}