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
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class SchemaComponentReference 
	extends AbstractNamedSchemaComponent 
{	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(SchemaComponentReference.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);
				
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** For Toplink Use Only */
	protected SchemaComponentReference() {
		super();
	}
	
	SchemaComponentReference(AbstractSchemaModel parent, String name, String namespace) {
		super(parent, name, namespace);
	}
	
	
	// **************** SchemaComponentReference contract *********************
	
	protected abstract MWNamedSchemaComponent getReferencedComponent();
	
	
	// **************** MWNamedSchemaComponent contract **********************
	
	public MWNamespace getTargetNamespace() {
		return this.getReferencedComponent().getParentNamespace();
	}
	
	public String componentTypeName() {
		return this.getReferencedComponent().componentTypeName();
	}
	
	public boolean directlyOwns(MWNamedSchemaComponent nestedComponent) {
		return this.getReferencedComponent().directlyOwns(nestedComponent);
	}
	
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		this.getReferencedComponent().addDirectlyOwnedComponentsTo(directlyOwnedComponents);
	}
	
	
	// **************** MWSchemaComponent contract ****************************
	
	public boolean isReference() {
		return true;
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator xpathComponents() {
		return this.getReferencedComponent().xpathComponents();
	}
	
	/** Overridden from AbstractSchemaModel */
	public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
		return this.getReferencedComponent().nestedAttribute(namespaceUrl, attributeName);
	}
	
	/** Overridden from AbstractSchemaModel */
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		return this.getReferencedComponent().nestedElement(namespaceUrl, elementName);
	}
	
	
	// **************** SchemaModel contract **********************************
	
	public void resolveReferences() {
		super.resolveReferences();
		this.resolveReference(this.getNamespaceUrl(), this.getName());
	}
	
	
	// **************** SchemaComponentReference contract *********************
	
	protected /* private-protected */ abstract void resolveReference(String componentNamespace, String componentName);
}
