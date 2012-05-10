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

public final class MWGetMethodCodeGenPolicy
	extends MWAccessorCodeGenPolicy
{	
	MWGetMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, attribute, classCodeGenPolicy);
	}
	
	void insertArguments(NonreflectiveMethodDefinition methodDef)
	{
		// should have no arguments
	}
	
	/**
	 * Return "return this.<attribute name>;"
	 */ 
	void insertMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		methodDef.addLine("return this." + getAccessedAttribute().getName() + ";");
	}
}
