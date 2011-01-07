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

public interface MWComplexTypeDefinition 
	extends MWSchemaTypeDefinition 
{
	/** Return either RESTRICTION or EXTENSION */
	String getDerivationMethod();
		final static String RESTRICTION = "restriction";
		final static String EXTENSION = "extension";
	
	/** Return whether this type is abstract */
	boolean isAbstract();
	
	/** 
	 * Return the amount of unique elements that this type can have.
	 * 
	 * (NOTE: This does not indicate the number of TOTAL elements that this type 
	 *  can have, as some of this elements will probably be mutually exclusive.)
	 */
	int totalElementCount();
	
	/** Return the amount of different attributes that this type can have. */
	int attributeCount();
}
