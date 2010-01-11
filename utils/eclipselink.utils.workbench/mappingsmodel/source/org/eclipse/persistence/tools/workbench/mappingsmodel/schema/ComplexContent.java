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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class ComplexContent
	extends Content
{
	private volatile boolean mixedFlag;
	
	private volatile MWParticle particle;
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected ComplexContent() {
		super();
	}
	
	ComplexContent(ExplicitComplexTypeDefinition parent, boolean mixed) {
		super(parent);
		this.initialize(mixed);
	}
	
	
	// **************** Initialization ****************************************
	
	protected /* private-protected */ void initialize(Node parent) {
		super.initialize(parent);
		this.particle = new NullParticle(this);
	}
	
	private void initialize(boolean mixed) {
		this.mixedFlag = mixed;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.particle);
	}
	
	
	// **************** Behavior **********************************************
	
	void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		if (this.particle instanceof MWElementDeclaration) {
			directlyOwnedComponents.add(this.particle);
		}
		else {
			this.particle.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
		}
	}
	
	boolean hasTextContent() {
		return this.mixedFlag == true;
	}
	
	boolean containsWildcard() {
		if (this.particle instanceof Wildcard) {
			return true;
		}
		else if (this.particle instanceof MWModelGroup) {
			return ((MWModelGroup) this.particle).containsWildcard();
		}
		else {
			return false;
		}
	}
	
	int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.particle.compareSchemaOrder(element1, element2);
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator structuralComponents() {
		return new SingleElementIterator(this.particle);
	}
	
	public Iterator descriptorContextComponents() {
		if (this.particle.isDescriptorContextComponent()) {
			return new SingleElementIterator(this.particle);
		}
		else {
			return this.particle.descriptorContextComponents();
		}
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	public Iterator xpathComponents() {
		if (this.particle instanceof MWElementDeclaration) {
			return new SingleElementIterator(this.particle);
		}
		else {
			return this.particle.xpathComponents();
		}
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		if (this.particle instanceof MWElementDeclaration) {
			MWElementDeclaration element = (MWElementDeclaration) this.particle;
			
			if (namespaceUrl.equals(element.getNamespaceUrl())
				&& elementName.equals(element.getName())) {
						return element;
			}
			else {
				return null;
			}
		}
		else {
			return this.particle.nestedElement(namespaceUrl, elementName);
		}
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	public int totalElementCount() {
		if (this.particle instanceof MWElementDeclaration) {
			return 1;
		}
		else {
			return this.particle.totalElementCount();
		}
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject schemaObject) {
		super.reloadInternal(schemaObject);
		XSParticleDecl particleNode = (XSParticleDecl)((XSComplexTypeDecl)schemaObject).getParticle();
		
		if (this.particle != null && this.particle.isEquivalentTo((XSParticleDecl) particleNode)) {
			this.particle.reload(particleNode);
		}
		else {
			MWParticle oldParticle = this.particle;
			this.particle = MWParticle.ParticleFactory.newParticle(this, particleNode);
			this.getProject().nodeRemoved(oldParticle);
		}	
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		this.particle.resolveReferences();
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ComplexContent.class);
		descriptor.getInheritancePolicy().setParentClass(Content.class);
		
		descriptor.addDirectMapping("mixedFlag", "mixed-flag/text()");
		
		XMLCompositeObjectMapping particleMapping = new XMLCompositeObjectMapping();
		particleMapping.setAttributeName("particle");
		particleMapping.setReferenceClass(AbstractSchemaComponent.class);
		particleMapping.setXPath("particle");
		descriptor.addMapping(particleMapping);
			
		return descriptor;
	}
}
