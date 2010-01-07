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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;

public class IBMPC extends PC {
	public boolean isClone;
	
public static void addToDescriptor(ClassDescriptor descriptor) {
	descriptor.getInheritancePolicy().setParentClass(PC.class);

	ExpressionBuilder builder = new ExpressionBuilder();
	descriptor.getInheritancePolicy().setOnlyInstancesExpression(
		(builder.getField("INH_COMP.CTYPE").equal("PC")).and(builder.getField("INH_COMP.PCTYPE").equal("IBM")));
}

@Override
public String getPCType()
{
	return "IBM";
}
}
