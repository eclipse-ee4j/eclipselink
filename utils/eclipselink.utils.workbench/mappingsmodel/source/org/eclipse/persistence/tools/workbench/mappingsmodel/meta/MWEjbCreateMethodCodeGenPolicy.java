/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
