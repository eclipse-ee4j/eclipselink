/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;

public interface MWSchemaTypeDefinition
	extends MWSchemaContextComponent
{
	MWSchemaTypeDefinition getBaseType();
	
	boolean isComplex();
	
	/** 
	 * Return the type definitions of the built in type that this type is based on 
	 * (Used for runtime conversion) 
	 */
	Iterator baseBuiltInTypes();
}
