/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSModelGroup;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;

public final class ReferencedModelGroup
	extends SchemaComponentReference
	implements MWModelGroup 
{
	// **************** Variables *********************************************
	
	private volatile ModelGroupDefinition modelGroupDef;
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	private ReferencedModelGroup() {
		super();
	}
	
	ReferencedModelGroup(AbstractSchemaModel parent, String groupName, String groupNamespace) {
		super(parent, groupName, groupNamespace);
	}
	
	
	// **************** SchemaComponentReference contract *********************
	
	protected MWNamedSchemaComponent getReferencedComponent() {
		return this.modelGroupDef;
	}
	
	protected void resolveReference(String modelGroupDefNamespace, String modelGroupDefName) {
		this.modelGroupDef = (ModelGroupDefinition) this.getSchema().modelGroupDefinition(modelGroupDefNamespace, modelGroupDefName);
	}
	
	
	// **************** MWModelGroup contract ***********************************
	
	public String getCompositor() {
		return this.modelGroupDef.getModelGroup().getCompositor();
	}
	
	public boolean containsWildcard() {
		return this.modelGroupDef.getModelGroup().containsWildcard();
	}
	
	
	// **************** MWParticle contract ***********************************
	
	public int getMinOccurs() {
		return this.modelGroupDef.getModelGroup().getMinOccurs();
	}
	
	public int getMaxOccurs() {
		return this.modelGroupDef.getModelGroup().getMaxOccurs();
	}
	
	public boolean isDescriptorContextComponent() {
		return false;
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.modelGroupDef.compareSchemaOrder(element1, element2);
	}
	
	public boolean isEquivalentTo(XSParticleDecl xsParticle) {
		return xsParticle.getTerm() instanceof XSModelGroup;
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
		return this.modelGroupDef.nestedNamedComponent(qName);
	}
	
	public int totalElementCount() {
		return this.modelGroupDef.totalElementCount();
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ReferencedModelGroup.class);
		descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);
							
		return descriptor;
	}
}
