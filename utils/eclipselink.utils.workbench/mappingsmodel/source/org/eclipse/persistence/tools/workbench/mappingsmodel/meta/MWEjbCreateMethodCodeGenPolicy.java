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

public final class MWEjbCreateMethodCodeGenPolicy 
	extends MWMethodCodeGenPolicy
{
	public MWEjbCreateMethodCodeGenPolicy(MWMethod method, MWClass primaryKeyClass, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, classCodeGenPolicy);
	}
	
	void insertArguments(NonreflectiveMethodDefinition methodDef)
	{
		super.insertArguments(methodDef);
	}
	
	void insertMethodBody(NonreflectiveMethodDefinition methodDef)
	{
		super.insertMethodBody(methodDef);
	}
}
