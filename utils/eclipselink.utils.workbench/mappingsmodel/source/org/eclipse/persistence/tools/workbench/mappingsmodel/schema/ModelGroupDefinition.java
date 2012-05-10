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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
public final class ModelGroupDefinition
	extends AbstractNamedSchemaComponent
	implements MWModelGroupDefinition
{
	private volatile ExplicitModelGroup modelGroup;
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ModelGroupDefinition.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);
				
		XMLCompositeObjectMapping modelGroupMapping = new XMLCompositeObjectMapping();
		modelGroupMapping.setAttributeName("modelGroup");
		modelGroupMapping.setReferenceClass(ExplicitModelGroup.class);
		modelGroupMapping.setXPath("model-group");
		descriptor.addMapping(modelGroupMapping);
			
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Toplink persistence use only */
	private ModelGroupDefinition() {
		super();
	}

	ModelGroupDefinition(MWModel parent, String name) {
		super(parent, name);
	}
	
	
	// **************** Inititalization ***************************************
	
	protected /* private-protected */ void initialize(Node parent) {
		super.initialize(parent);
		this.modelGroup = new ExplicitModelGroup(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.modelGroup);
	}
	
	
	// **************** MWModelGroupDefinition contract ***********************
	
	public MWModelGroup getModelGroup() {
		return this.modelGroup;
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean hasType() {
		return false;
	}
	
	public String contextTypeQname() {
		return null;
	}
	
	public boolean containsText() {
		return false;
	}
	
	public boolean containsWildcard() {
		return this.modelGroup.containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.modelGroup.compareSchemaOrder(element1, element2);
	}
	
	public Iterator baseBuiltInTypes() {
		return NullIterator.instance();
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String componentTypeName() {
		return "group";
	}
	
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		this.modelGroup.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator structuralComponents() {
		return this.modelGroup.structuralComponents();
	}
	
	public Iterator descriptorContextComponents() {
		return this.modelGroup.descriptorContextComponents();
	}
	
	public Iterator xpathComponents() {
		return this.modelGroup.xpathComponents();
	}
	
	public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
		return this.modelGroup.nestedNamedComponent(qName);
	}
	
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		return this.modelGroup.nestedElement(namespaceUrl, elementName);
	}
	
	public int totalElementCount() {
		return this.modelGroup.totalElementCount();
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject xsModelGroupDef) {
		super.reloadInternal(xsModelGroupDef);
		this.modelGroup.reload(((XSGroupDecl)xsModelGroupDef).getModelGroup());		
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		this.modelGroup.resolveReferences();
	}
}
