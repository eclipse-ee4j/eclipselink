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
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Mainframe extends Computer {
    public int numberOfProcessors;
public static void addToDescriptor(ClassDescriptor descriptor) {
    ExpressionBuilder builder = new ExpressionBuilder();
    descriptor.getInheritancePolicy().setOnlyInstancesExpression(builder.getField("INH_COMP.CTYPE").equal("MF"));
}
@Override
public String getComputerType()
{
    return "MF";
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition() {
    TableDefinition definition = new TableDefinition();
    definition.setName("INH_MF");
    definition.addIdentityField("MF_ID", Integer.class);
    definition.addField("procs", Integer.class);
    return definition;
}
}
