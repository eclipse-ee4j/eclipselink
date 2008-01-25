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

import org.eclipse.persistence.internal.codegen.CodeDefinition;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public interface MWCodeGenPolicy 
{
	public void writeComment(CodeDefinition codeDef);
	public void writeMethodBody(MWMethod method, NonreflectiveMethodDefinition methodDef);
	public void writeMethodThrowsClause(MWMethod method, NonreflectiveMethodDefinition methodDef);
}