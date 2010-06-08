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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public final class MWRemoveMethodCodeGenPolicy 
	extends MWContainerAccessorCodeGenPolicy
{
	private boolean isMappingPrivateOwned;
	
	MWRemoveMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, classCodeGenPolicy);
	}
	
	MWRemoveMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassAttribute backPointerAttribute, boolean isMappingPrivateOwned, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, backPointerAttribute, classCodeGenPolicy);
		this.isMappingPrivateOwned = isMappingPrivateOwned;
	}
	
	void insertArguments(NonreflectiveMethodDefinition methodDef)
	{
		if (getMethod().methodParametersSize() != 1)
			super.insertArguments(methodDef);
		
		if (getAccessedAttribute().canHaveMapKeyAndValueTypes())
			methodDef.addArgument(getMethod().getMethodParameter(0).declaration(), "key");
		else
			addArgument(methodDef, getMethod().getMethodParameter(0));
	}
	
	/**
	 * Return
	 * 	"this.<attribute name>.remove(<argument name>);"
	 * 		-or-
	 * 	"<value get method>.remove(<argument name>);"
	 * 
	 * if back pointer, add
	 * 	"<argument name>.<set method name>(null);"
	 */ 
	protected void insertCollectionMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		if (methodDef.argumentNamesSize() != 1)
			super.insertMethodBody(methodDef);
		
		String argumentName = methodDef.getArgumentName(0);
		methodDef.addLine(attributeValueCode()
						  + ".remove("  + argumentName
						  + ");" );
		
		if (getBackPointerSetMethod() != null && !isMappingPrivateOwned)
			methodDef.addLine(argumentName + "." 
							  + getBackPointerSetMethod().getName() + "(null);");
	}
	
	/**
	 * Return
	 * 	"this.<attribute name>.remove(<argument name>);"
	 * 		-or-
	 * 	"<value get method>.remove(<argument name>);"
	 * 
	 * *or* if back pointer, return
	 * 	"((<attribute type>) <attribute name>.remove(<argument name>)).<set method name>(null);"
	 * 		-or-
	 * 	"((<attribute type>) <value get method>.remove(<argument name>)).<set method name>(null);"
	 */ 
	protected void insertMapMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		if (methodDef.argumentNamesSize() != 1)
			super.insertMethodBody(methodDef);
		
		String argumentName = methodDef.getArgumentName(0);
		
		if (getBackPointerSetMethod() == null) {
			methodDef.addLine(attributeValueCode()
							  + ".remove(" + argumentName
							  + ");" );
		} else if (!isMappingPrivateOwned) {
			methodDef.addLine("(("  + getAccessedAttribute().getType().getName()
							  + ") "  + attributeValueCode()
							  + ".remove(" + argumentName
							  + "))."  + getBackPointerSetMethod().getName()
							  + "(null);" );
		} else {
			methodDef.addLine("(("  + getAccessedAttribute().getType().getName()
					  + ") "  + attributeValueCode()
					  + ".remove(" + argumentName
					  + "))." );
		}
	}
}
