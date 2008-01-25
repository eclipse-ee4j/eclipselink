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

public interface MWAttributeDeclaration
	extends MWXpathableSchemaComponent, MWNamedSchemaComponent
{
	/** Return the type of the attribute */
	MWSimpleTypeDefinition getType();
	
	/** Return the default value of the attribute */
	String getDefaultValue();
	
	/** Return the fixed value of the attribute */
	String getFixedValue();
	
	/** Return OPTIONAL, REQUIRED, or PROHIBITED */
	String getUse();
		final static String OPTIONAL 	= "optional";
		final static String REQUIRED 	= "required";
		final static String PROHIBITED 	= "prohibited";
}
