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

import javax.xml.XMLConstants;

import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class ReferencedSchemaTypeDefinition 
	extends SchemaComponentReference
	implements MWSchemaTypeDefinition
{
	// **************** Static methods ****************************************
	
	static ReferencedSchemaTypeDefinition reloadedReferencedType(AbstractSchemaModel parent,
																 MWSchemaTypeDefinition oldType,
																 String typeName,
																 String typeNamespace,
																 XSTypeDefinition typeDef) {
		ReferencedSchemaTypeDefinition newReferencedType;
		
		try {
			newReferencedType = (ReferencedSchemaTypeDefinition) oldType;
		}
		catch (ClassCastException cce) {
			newReferencedType = null;
		}
		
		if (typeName != null) {
			if (typeDef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
				if (! (newReferencedType instanceof ReferencedComplexTypeDefinition)) {
					newReferencedType = new ReferencedComplexTypeDefinition(parent, typeName, typeNamespace);
				}
			}
			else {
				// (elementNode.getType().getNodeType() == XSDElement.DATATYPE)
				if (! (newReferencedType instanceof ReferencedSimpleTypeDefinition)) {
					newReferencedType = new ReferencedSimpleTypeDefinition(parent, typeName, typeNamespace);
				}
			}
		}
		else {
			newReferencedType = urType(parent);
		}
		
		newReferencedType.reload(typeDef);
		
		return newReferencedType;
	}
	
	static ReferencedSchemaTypeDefinition urType(AbstractSchemaModel parent) {
		return new ReferencedComplexTypeDefinition(parent, "anyType", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
		
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ReferencedSchemaTypeDefinition.class);
		descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);				
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected ReferencedSchemaTypeDefinition() {
		super();
	}
	
	protected /* private-protected */ ReferencedSchemaTypeDefinition(AbstractSchemaModel parent, String typeName, String typeNamespace) {
		super(parent, typeName, typeNamespace);
	}
	
	
	// **************** Internal **********************************************
	
	private MWSchemaTypeDefinition getReferencedType() {
		return (MWSchemaTypeDefinition) this.getReferencedComponent();
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean hasType() {
		return true;
	}
	
	public String contextTypeQname() {
		return (this.getReferencedType() == null) ? 
			this.qName() : this.getReferencedType().contextTypeQname();
	}
	
	public boolean containsText() {
		return (this.getReferencedType() == null) ?
			false : this.getReferencedType().containsText();
	}
	
	public boolean containsWildcard() {
		return (this.getReferencedType() == null) ?
			false : this.getReferencedType().containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return (this.getReferencedType() == null) ? 
			0 : this.getReferencedType().compareSchemaOrder(element1, element2);
	}
}
