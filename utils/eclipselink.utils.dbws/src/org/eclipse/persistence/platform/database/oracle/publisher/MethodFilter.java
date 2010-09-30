/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

//javase imports
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;

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

    public List<String> getMethodNames() {
        return null;
    }
}
