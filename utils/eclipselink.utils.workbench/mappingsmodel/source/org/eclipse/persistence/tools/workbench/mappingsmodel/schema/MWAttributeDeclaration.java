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
