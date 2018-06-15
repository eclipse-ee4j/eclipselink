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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public final class MWZeroArgumentConstructorCodeGenPolicy
    extends MWMethodCodeGenPolicy
{
    volatile MWDescriptor descriptor;

    public MWZeroArgumentConstructorCodeGenPolicy(MWMethod method, MWDescriptor descriptor, MWClassCodeGenPolicy classCodeGenPolicy) {
        super(method, classCodeGenPolicy);
        this.descriptor = descriptor;
    }

    /**
     * call "super();" first
     * then initialize all instance variables that need to be initialized
     */
    void insertMethodBody(NonreflectiveMethodDefinition methodDef) {
        if (!this.descriptor.getMWClass().getSuperclass().isObject()) {
            methodDef.addLine("super();");
        }

        Collection allInstanceVariables = CollectionTools.collection(this.descriptor.getMWClass().instanceVariables());

        for (Iterator it = this.descriptor.mappings(); it.hasNext(); ) {
            MWMapping mapping = (MWMapping) it.next();
            MWClassAttribute attribute = mapping.getInstanceVariable();

            if (allInstanceVariables.contains(attribute)) {
                allInstanceVariables.remove(attribute);
                String initialValue = mapping.initialValue(getClassCodeGenPolicy());

                if (initialValue != null && ! initialValue.equals("")) {
                    methodDef.addLine("this."  + attribute.getName() + " = "+ initialValue + ";" );
                }
            }
        }
    }
}
