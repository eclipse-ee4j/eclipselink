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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public abstract class MWContainerAccessorCodeGenPolicy
    extends MWAccessorCodeGenPolicy
{
    private MWClassAttribute backPointerAttribute;

    MWContainerAccessorCodeGenPolicy(MWMethod method, MWClassAttribute attribute,  MWClassCodeGenPolicy classCodeGenPolicy)
    {
        super(method, attribute, classCodeGenPolicy);
    }

    MWContainerAccessorCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassAttribute backPointerAttribute, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        this(method, attribute, classCodeGenPolicy);
        this.backPointerAttribute = backPointerAttribute;
    }

    protected MWClassAttribute getBackPointerAttribute()
    {
        return this.backPointerAttribute;
    }

    protected MWMethod getBackPointerSetMethod()
    {
        if (this.backPointerAttribute == null)
            return null;

        if (this.backPointerAttribute.isValueHolder()) {
            return this.backPointerAttribute.getValueSetMethod();
        }
        return this.backPointerAttribute.getSetMethod();
    }

    void insertMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        if (getAccessedAttribute().isAssignableToMap())
            insertMapMethodBody(methodDef);
        else
            insertCollectionMethodBody(methodDef);
    }

    protected abstract void insertCollectionMethodBody(NonreflectiveMethodDefinition methodDef);

    protected abstract void insertMapMethodBody(NonreflectiveMethodDefinition methodDef);
}
