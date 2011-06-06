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

import java.util.Iterator;

public interface MWSchemaContextComponent 
	extends MWNamedSchemaComponent 
{
	/** 
	 * Return true if this context component has a type 
	 * (Model group definitions being a notable exception)
	 */
	boolean hasType();
	
	/** 
	 * Return a non-null name of the type represented by this context component.
	 *  - if this context component *has* no type, return null.
	 *  - if this context has a type, and that type has a name, return its qname.
	 *  - if this context has a type, but that type has no name, return the 
	 *   contextTypeQname() of that type's base type.
	 */
	String contextTypeQname();
	
	/** Return true if this schema context may contain a text node */
	boolean containsText();
	
	/** Return true if this schema context may contain a wildcard node */
	boolean containsWildcard();
	
	/** 
	 * Return -1 if element1 comes before element2, +1 if it comes after,
	 * or 0 if there is no order dependence.
	 */
	int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2);
	
	/**
	 * Return an iterator of MWSimpleTypeDefinition objects that comprise the base data 
	 *  types of this context component.
	 * This includes detailing all simple types for unions or lists.
	 */
	Iterator baseBuiltInTypes();
}
