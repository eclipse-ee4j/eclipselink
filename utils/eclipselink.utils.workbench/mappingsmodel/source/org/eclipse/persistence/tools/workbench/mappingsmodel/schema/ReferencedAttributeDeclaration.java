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
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ReferencedAttributeDeclaration
	extends SchemaComponentReference
	implements MWAttributeDeclaration 
{
	// **************** Variables *********************************************
	
	private volatile ExplicitAttributeDeclaration attribute;
	
	
	// **************** Constructors ******************************************
	
	/** For Toplink Use Only */
	protected ReferencedAttributeDeclaration() {
		super();
	}
	
	ReferencedAttributeDeclaration(AbstractSchemaModel parent, String attributeName, String attributeNamespace) {
		super(parent, attributeName, attributeNamespace);
	}
		
	
	// **************** SchemaComponentReference contract *********************
	
	protected MWNamedSchemaComponent getReferencedComponent() {
		return this.attribute;
	}
	
	protected void resolveReference(String attributeNamespace, String attributeName) {
		this.attribute = (ExplicitAttributeDeclaration) this.getSchema().attribute(attributeNamespace, attributeName);
	}
	
	
	// **************** MWAttributeDeclaration contract ***********************
	
	public MWSimpleTypeDefinition getType() {
		return this.attribute.getType();
	}
	
	public String getDefaultValue() {
		return this.attribute.getDefaultValue();
	}
	
	public String getFixedValue() {
		return this.attribute.getFixedValue();
	}
	
	public String getUse() {
		return this.attribute.getUse();
	}
	
	
	// **************** MWXpathableSchemaComponent contract *******************
	
	public int getMaxOccurs() {
		return this.attribute.getMaxOccurs();
	}
	
	public Iterator baseBuiltInTypes() {
		return this.attribute.baseBuiltInTypes();
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean hasType() {
		return true;
	}
	
	public String contextTypeQname() {
		return this.attribute.contextTypeQname();
	}
	
	public boolean containsText() {
		// should always be false, but consistently asking the attribute seems a good thing
		return this.attribute.containsText();
	}
	
	public boolean containsWildcard() {
		// should always be false, but consistently asking the attribute seems a good thing
		return this.attribute.containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.attribute.compareSchemaOrder(element1, element2);
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ReferencedAttributeDeclaration.class);
		descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);	
		return descriptor;
	}
}
