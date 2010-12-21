/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.ListIterator;

import org.apache.xerces.xs.XSSimpleTypeDefinition;


public interface MWSimpleTypeDefinition 
	extends MWSchemaTypeDefinition 
{
	/** Return the variety (atomic, list, or union) */
	String getVariety();
		public final static String ATOMIC 	= "atomic";
		public final static String LIST 	= "list";
		public final static String UNION	= "union";
		public final static String ABSENT	= "absent";
	
	/** Return the item type, if list, else return null */
	MWSimpleTypeDefinition getItemType();
	
	/** Return the member types, if union, else return empty iterator */
	ListIterator memberTypes();
	
	
	// **************** Static methods ****************************************
	
	class Reloader
	{
		static MWSimpleTypeDefinition reloadedSimpleType(
			AbstractSchemaModel parent,
			MWSimpleTypeDefinition oldSimpleType,
			XSSimpleTypeDefinition itemType
		) {
			MWSimpleTypeDefinition newSimpleType = oldSimpleType;
			
			if (itemType.getName() != null) {
				// if type has a name, it's a reference
				
				String typeName = itemType.getName();
				String typeNamespace = itemType.getNamespace();
				
				if (! (oldSimpleType instanceof ReferencedSimpleTypeDefinition)) {
					newSimpleType = new ReferencedSimpleTypeDefinition(parent, typeName, typeNamespace);
				}
				
				newSimpleType.reload(itemType);
			}
			else {
				if (itemType.getBaseType() == null) {
					// if type has no base type, it's the ur type
					newSimpleType = ReferencedSimpleTypeDefinition.simpleUrType(parent);
					// don't reload this type
				}
				else {
					// if type has a base type, it's a locally defined type
					if (! (newSimpleType instanceof ExplicitSimpleTypeDefinition)) {
						newSimpleType = new ExplicitSimpleTypeDefinition(parent, null);
					}
					
					newSimpleType.reload(itemType);
				}
			}
			
			return newSimpleType;
		}
	}
}
