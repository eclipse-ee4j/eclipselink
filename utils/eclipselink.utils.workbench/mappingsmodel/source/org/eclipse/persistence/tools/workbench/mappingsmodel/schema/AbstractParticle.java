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

import java.util.Collection;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public abstract class AbstractParticle
	extends AbstractSchemaComponent
	implements MWParticle
{
	/** The minimum number of times the particle occurs in a document */
	private volatile int minOccurs;
	
	/** The maximum number of times the particle occurs in a document */
	private volatile int maxOccurs;
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(AbstractParticle.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractSchemaComponent.class);
		
		((XMLDirectMapping) descriptor.addDirectMapping("minOccurs", "min-occurs/text()")).setNullValue(new Integer(1));
		((XMLDirectMapping) descriptor.addDirectMapping("maxOccurs", "max-occurs/text()")).setNullValue(new Integer(1));
		
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected AbstractParticle() {
		super();
	}
	
	protected AbstractParticle(AbstractSchemaModel parent) {
		super(parent);
	}
	
	
	// **************** Initialization ****************************************
	
	protected /* private-protected */ void initialize(Node parent) {
		super.initialize(parent);
		this.minOccurs = 1;
		this.maxOccurs = 1;
	}
	
	
	// **************** MWParticle contract ***********************************
	
	public int getMinOccurs() {
		return this.minOccurs;
	}
	
	public int getMaxOccurs() {
		return this.maxOccurs;
	}
	
	public boolean isDescriptorContextComponent() {
		return false;
	}
	
	/** No-op */
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {}
	
	/** Default implementation */
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return 0;
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject schemaObject) {
		super.reloadInternal(schemaObject);
		
		if (schemaObject != null) {
			this.minOccurs = ((XSParticleDecl)schemaObject).getMinOccurs();
			this.maxOccurs = ((XSParticleDecl)schemaObject).getMaxOccurs();
		}
		else {
			this.minOccurs = 0;
			this.maxOccurs = 0;
		}	

	}
}
