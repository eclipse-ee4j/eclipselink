/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;

public class IBMPC extends PC {
    public String isClone;

    public static void addToDescriptor(ClassDescriptor descriptor) {
        descriptor.getInheritancePolicy().setParentClass(PC.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        descriptor.getInheritancePolicy().setOnlyInstancesExpression((builder.getField("INH_COMP.CTYPE").equal("PC")).and(builder.getField("INH_COMP.PCTYPE").equal("IBM")));
    }

    public String getPCType() {
        return "IBM";
    }
}