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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;

public class Mac extends PC {
    public static void addToDescriptor(ClassDescriptor descriptor) {
        descriptor.getInheritancePolicy().setParentClass(PC.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        descriptor.getInheritancePolicy().setOnlyInstancesExpression((builder.getField("INH_COMP.CTYPE").equal("PC")).and(builder.getField("INH_COMP.PCTYPE").equal("MAC")));
    }

    public String getPCType() {
        return "MAC";
    }
}
