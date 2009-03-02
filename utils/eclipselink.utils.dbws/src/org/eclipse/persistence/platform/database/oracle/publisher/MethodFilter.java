/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/

package org.eclipse.persistence.platform.database.oracle.publisher;

import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;

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
    public abstract boolean acceptMethod(ProcedureMethod method, boolean preApprove);

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
