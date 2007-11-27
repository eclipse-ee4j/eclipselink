package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PLSQLCall {
    public int id;
    public List<PLSQLargument> arguments = new ArrayList<PLSQLargument>();
    
    public String toString() {
        return "PLSQLCall(id="+id+", arguments="+arguments+")";
    }
    
    public boolean equals(Object obj) {
        PLSQLCall callObj;
        try {
            callObj = (PLSQLCall) obj;
        } catch (Exception x) {
            return false;
        }
        if (callObj.id != this.id) {
            return false;
        }
        if (callObj.arguments == null) {
            return arguments == null;
        }
        if (arguments == null) {
            return false;
        }
        if (arguments.size() != callObj.arguments.size()) {
            return false;
        }
        Iterator<PLSQLargument> argumentIt = arguments.iterator();
        while (argumentIt.hasNext()) {
            PLSQLargument arg = argumentIt.next();
            if (!callObj.arguments.contains(arg)) {
                return false;
            }
        }
        return true;
    }
    
}