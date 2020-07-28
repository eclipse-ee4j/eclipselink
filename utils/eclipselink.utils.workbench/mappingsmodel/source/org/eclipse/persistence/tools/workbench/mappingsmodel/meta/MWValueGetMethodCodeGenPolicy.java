/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public final class MWValueGetMethodCodeGenPolicy
    extends MWAccessorCodeGenPolicy
{
    MWValueGetMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        super(method, attribute, classCodeGenPolicy);
    }

    void insertArguments(NonreflectiveMethodDefinition methodDef)
    {
        // should have no arguments
    }

    /**
     * Return "return (<value type short name>) this.<attribute name>.getValue();"
     *     - return the short name of the value type, as the type should be in the return type of the method,
     *       so the import should be taken care of already.  Don't worry about name collisions at this point.
     */
    void insertMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        methodDef.addLine("return ("  + getAccessedAttribute().getValueType().shortName()
                          + ") this."  + getAccessedAttribute().getName()
                          + ".getValue();");
    }
}
