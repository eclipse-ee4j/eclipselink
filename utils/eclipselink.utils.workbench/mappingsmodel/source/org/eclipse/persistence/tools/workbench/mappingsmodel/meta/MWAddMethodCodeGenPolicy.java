/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

public final class MWAddMethodCodeGenPolicy 
	extends MWContainerAccessorCodeGenPolicy
{
	MWAddMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, classCodeGenPolicy);
	}
	
	MWAddMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassAttribute backPointerAttribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, backPointerAttribute, classCodeGenPolicy);
	}
	
	/**
	 * The assumption is that this method will have 2 arguments for a map
	 * and 1 argument for a collection.
	 * However, it is possible for this not to be the case, so we check
	 * and generate nothing if this assumption proves not to be accurate.
	 */
	void insertArguments(NonreflectiveMethodDefinition methodDef)
	{
		int assumedArgumentSize = 1;  // for collections
		if (getAccessedAttribute().canHaveMapKeyAndValueTypes())
			assumedArgumentSize = 2;
		
		if (getMethod().methodParametersSize() != assumedArgumentSize)
			super.insertArguments(methodDef);
		
		if (getAccessedAttribute().canHaveMapKeyAndValueTypes())
			methodDef.addArgument(getMethod().getMethodParameter(0).declaration(), "key");
		
		addArgument(methodDef, getMethod().getMethodParameter(assumedArgumentSize -1));
	}
	
	/**
	 * return 
	 * 	"this.<attribute name>.add(<argument name>);" 
	 * 		-or-
	 * 	"<value get method>.add(<argument name>);
	 * 
	 * if back pointer, add
	 * 	"<argument name>.<set method name>(this);"
	 * 
	 * e.g.
	 * "addBar(Bar bar) {
	 * 		getBars().add(bar);
	 * 		bar.setFoo(this);
	 *  }"
	 */ 
	protected void insertCollectionMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		if (methodDef.argumentNamesSize() != 1)
			super.insertMethodBody(methodDef);
		
		String argumentName = methodDef.getArgumentName(0);
		methodDef.addLine(attributeValueCode()
					  	  + ".add(" + argumentName + ");" );
		
		if (getBackPointerSetMethod() != null)
			methodDef.addLine(argumentName + "."
							  + getBackPointerSetMethod().getName() + "(this);" );
	}
	
	/**
	 * return 
	 * 	"this.<attribute name>.put(<argument name 1>, <argument name 2>);"
	 * 		-or-
	 * 	"<value get method>.add(<argument name>);
	 * 
	 * if back pointer, adds
	 * 	"<argument name>.<set method name>(this);"
	 * 
	 * e.g.
	 * "addBar(Key key, Bar bar) {
	 * 		getBars().put(key, bar);
	 * 		bar.setFoo(this);
	 *  }"
	 */
	protected void insertMapMethodBody(NonreflectiveMethodDefinition methodDef)
	{	
		if (methodDef.argumentNamesSize() != 2)
			super.insertMethodBody(methodDef);
		
		String keyArgumentName = methodDef.getArgumentName(0);
		String valueArgumentName = methodDef.getArgumentName(1);
		methodDef.addLine(attributeValueCode()
						  + ".put(" + keyArgumentName
						  + ", " + valueArgumentName
						  + ");" );
		
		if (getBackPointerSetMethod() != null)
			methodDef.addLine(valueArgumentName + "."
							  + getBackPointerSetMethod().getName() + "(this);");
	}
}
