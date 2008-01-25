/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;

public class Mac extends PC {
public static void addToDescriptor(ClassDescriptor descriptor) {
	descriptor.getInheritancePolicy().setParentClass(PC.class);

	ExpressionBuilder builder = new ExpressionBuilder();
	descriptor.getInheritancePolicy().setOnlyInstancesExpression(
		(builder.getField("INH_COMP.CTYPE").equal("PC")).and(builder.getField("INH_COMP.PCTYPE").equal("MAC")));
}
@Override
public String getPCType()
{
	return "MAC";
}
}
