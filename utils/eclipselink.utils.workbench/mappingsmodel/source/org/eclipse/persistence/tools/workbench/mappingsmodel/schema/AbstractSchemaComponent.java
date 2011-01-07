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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class AbstractSchemaComponent 
	extends AbstractSchemaModel
	implements MWSchemaComponent
{
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(AbstractSchemaComponent.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(ExplicitAttributeDeclaration.class, "attribute");
		ip.addClassIndicator(ReferencedAttributeDeclaration.class, "attribute-ref");
		ip.addClassIndicator(ExplicitElementDeclaration.class, "element");
		ip.addClassIndicator(ReferencedElementDeclaration.class, "element-ref");
		ip.addClassIndicator(ExplicitComplexTypeDefinition.class, "complex-type");
		ip.addClassIndicator(ReferencedComplexTypeDefinition.class, "complex-type-ref");
		ip.addClassIndicator(ExplicitSimpleTypeDefinition.class, "simple-type");
		ip.addClassIndicator(ReferencedSimpleTypeDefinition.class, "simple-type-ref");
		ip.addClassIndicator(ModelGroupDefinition.class, "model-group-def");
		ip.addClassIndicator(ExplicitModelGroup.class, "model-group");
		ip.addClassIndicator(ReferencedModelGroup.class, "model-group-ref");
		ip.addClassIndicator(NullParticle.class, "null-particle");
		ip.addClassIndicator(Wildcard.class, "wildcard");
		
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** For Toplink Use Only */
	protected AbstractSchemaComponent() {
		super();
	}
	
	protected AbstractSchemaComponent(MWModel parent) {
		super(parent);
	}
	
	/** default implementation */
	public boolean isReference() {
		return false;
	}
}
