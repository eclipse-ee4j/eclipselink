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
	 * 	- return the short name of the value type, as the type should be in the return type of the method,
	 * 	  so the import should be taken care of already.  Don't worry about name collisions at this point.
	 */ 
	void insertMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		methodDef.addLine("return ("  + getAccessedAttribute().getValueType().shortName() 
						  + ") this."  + getAccessedAttribute().getName() 
						  + ".getValue();");
	}
}
