/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public final class MWValueSetMethodCodeGenPolicy 
	extends MWAccessorCodeGenPolicy
{
	MWValueSetMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, classCodeGenPolicy);
	}
	
	void insertArguments(NonreflectiveMethodDefinition methodDef)
	{
		methodDef.addArgument(getAccessedAttribute().getValueType().getName(), getAccessedAttribute().getName());
	}
	
	/**
	 * Return "this.<attribute name>.setValue(<parameter name>);"
	 */ 
	void insertMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		methodDef.addLine("this." + getAccessedAttribute().getName() 
						  + ".setValue("  + methodDef.argumentNames().next() + ");" );
	}
}
