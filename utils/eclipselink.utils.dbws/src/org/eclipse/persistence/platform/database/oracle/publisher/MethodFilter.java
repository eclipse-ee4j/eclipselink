package org.eclipse.persistence.platform.database.oracle.publisher;

import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Method;

//TODO
// (1) have a Filter instance contains all the criteria
//     to avoid the majority of the static methods.
// (2) generated PL/SQL wrapper needs not to include types for
//     pruned methods

public abstract class MethodFilter {
    /*
     * @param method the method to be checked
     * 
     * @param preApprove true: the method name will be used to initailly check whether the method
     * can be possibly accepted
     */
    public abstract boolean acceptMethod(Method method, boolean preApprove);

    public boolean isSingleMethod() {
        return false;
    }

    public String getSingleMethodName() {
        return null;
    }

    public String[] getMethodNames() {
        return new String[0];
    }
}
