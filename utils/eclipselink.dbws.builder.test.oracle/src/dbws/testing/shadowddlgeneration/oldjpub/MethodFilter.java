/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.util.List;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.ProcedureMethod;

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
