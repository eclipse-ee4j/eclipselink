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
import java.util.ListIterator;
import java.util.Vector;

import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public final class ExplicitModelGroup 
	extends AbstractParticle
	implements MWModelGroup
{
	private volatile String compositor;
	
	private List particles;
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ExplicitModelGroup.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractParticle.class);
				
		descriptor.addDirectMapping("compositor", "@compositor");
		
		XMLCompositeCollectionMapping particlesMapping = new XMLCompositeCollectionMapping();
		particlesMapping.setAttributeName("particles");
		particlesMapping.setReferenceClass(AbstractSchemaComponent.class);
		particlesMapping.setXPath("particles/particle");
		descriptor.addMapping(particlesMapping);
			
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	private ExplicitModelGroup() {
		super();
	}
	
	ExplicitModelGroup(AbstractSchemaModel parent) {
		super(parent);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.particles = new Vector();
	}
	
	protected /* private-protected */ void initialize(Node parent) {
		super.initialize(parent);
		this.compositor = MWModelGroup.SEQUENCE;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.particles) { children.addAll(this.particles); }
	}
	
	
	// **************** Internal ************************************************
	
	ListIterator particles() {
		return this.particles.listIterator();
	}
	
	
	// **************** MWModelGroup contract ***********************************
	
	public String getCompositor() {
		return this.compositor;
	}
	
	public boolean containsWildcard() {
		boolean containsWildcard = false;
		
		for (Iterator stream = this.particles(); stream.hasNext() && containsWildcard == false; ) {
			MWParticle particle = (MWParticle) stream.next();
			
			if (particle instanceof Wildcard) {
				containsWildcard = true;
			}
			else if (particle instanceof MWModelGroup) {
				containsWildcard |= ((MWModelGroup) particle).containsWildcard();
			}
		}
		
		return containsWildcard;
	}
	
	
	// **************** MWParticle contract ***********************************
	
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		for (Iterator stream = this.particles(); stream.hasNext(); ) {
			MWParticle particle = (MWParticle) stream.next();
			
			if (particle instanceof MWElementDeclaration) {
				directlyOwnedComponents.add(particle);
			}
			else {
				particle.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
			}
		}
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		MWParticle particle1 = null, particle2 = null;
		
		for (Iterator stream = this.particles(); stream.hasNext(); ) {
			MWParticle particle = (MWParticle) stream.next();
			
			if (element1.isDescendantOf(particle)) {
				particle1 = particle;
			}
			
			if (element2.isDescendantOf(particle)) {
				particle2 = particle;
			}
		}
		
		if (particle1 == null || particle2 == null) {
			return 0;
		}
		else if (particle1 == particle2) {
			return particle1.compareSchemaOrder(element1, element2);
		}
		else if (this.particles.indexOf(particle1) < this.particles.indexOf(particle2)) {
			return -1;
		}
		else if (this.particles.indexOf(particle1) > this.particles.indexOf(particle2)) {
			return +1;
		}
		else {
			return 0;
		}
	}
	
	public boolean isEquivalentTo(XSParticleDecl xsParticle) {
		return xsParticle.getTerm() instanceof XSModelGroup;
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator structuralComponents() {
		return this.particles();
	}
	
	public Iterator descriptorContextComponents() {
		return new CompositeIterator(this.descriptorContextComponentIterators());
	}
	
	private Iterator descriptorContextComponentIterators() {
		return new TransformationIterator(this.particles()) {
			protected Object transform(Object next) {
				MWParticle particle = (MWParticle) next;
				
				if (particle.isDescriptorContextComponent()) {
					return new SingleElementIterator(particle);
				}
				else {
					return particle.descriptorContextComponents();
				}
			}
		};
	}
	
	public Iterator xpathComponents() {
		return new CompositeIterator(this.xpathComponentIterators());
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	private ListIterator xpathComponentIterators() {
		return new TransformationListIterator(this.particles()) {
			protected Object transform(Object next) {
				MWParticle particle = (MWParticle) next;
				
				if (particle instanceof MWElementDeclaration) {
					return new SingleElementIterator(particle);
				}
				else {
					return ((MWParticle) next).xpathComponents();
				}
			}
		};
	}
	
	public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
		for (Iterator stream = this.particles(); stream.hasNext(); ) {
			MWAttributeDeclaration attribute = ((MWParticle) stream.next()).nestedAttribute(namespaceUrl, attributeName);
		
			if (attribute != null) {
				return attribute;
			}
		}
		
		return null;
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		for (Iterator stream = this.particles(); stream.hasNext(); ) {
			MWParticle particle = (MWParticle) stream.next();
			MWElementDeclaration element;
			
			if (particle instanceof MWElementDeclaration) {
				element = (MWElementDeclaration) particle;
				
				if (elementName.equals(element.getName())) {
							return element;
				}
			}
			else {
				element = particle.nestedElement(namespaceUrl, elementName);
				
				if (element != null) {
					return element;
				}
			}
		}
		
		return null;
	}
	
	// NOTE: If anyone is upset by use of "instanceof" here, please give me suggestions - pwf
	public int totalElementCount() {
		int totalElementCount = 0;
		
		for (Iterator stream = this.particles(); stream.hasNext(); ) {
			MWParticle particle = (MWParticle) stream.next();
			
			if (particle instanceof MWElementDeclaration) {
				totalElementCount ++;
			}
			else {
				totalElementCount += particle.totalElementCount();
			}
		}
		
		return totalElementCount;
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject xsObject) {
		XSModelGroupImpl groupNode = null;
		if (xsObject instanceof XSParticleDecl) {
			super.reloadInternal(xsObject);
			groupNode = (XSModelGroupImpl) ((XSParticleDecl)xsObject).getTerm();
		} else {
			groupNode = (XSModelGroupImpl) xsObject;
		}
		this.reloadCompositor(groupNode);
		this.reloadParticles(groupNode);
	}
	
	private void reloadCompositor(XSModelGroup xsGroup) {
		if (xsGroup.getCompositor() == XSModelGroup.COMPOSITOR_CHOICE) {
			this.compositor = MWModelGroup.CHOICE;
		}
		else if (xsGroup.getCompositor() == XSModelGroup.COMPOSITOR_SEQUENCE) {
			this.compositor = MWModelGroup.SEQUENCE;
		}
		else /** xsGroup.getCompositor() == XSModelGroup.COMPOSITOR_ALL) */ {
			this.compositor = MWModelGroup.ALL;
		}	
	}
	
	private void reloadParticles(XSModelGroup xsGroup) {
		List newParticles = new Vector();
		ListIterator oldParticles = this.particles();

		while (oldParticles.hasNext()) {
			MWParticle oldParticle =(MWParticle)oldParticles.next();
			if (!containsEquivalentNode(oldParticle, XercesTools.listIteratorFromXSObjectList(xsGroup.getParticles()))) {
				oldParticles.remove();
				this.getProject().nodeRemoved(oldParticle);
			}
		}
		
		ListIterator particleNodes = XercesTools.listIteratorFromXSObjectList(xsGroup.getParticles());
		
		while (particleNodes.hasNext()) {
			XSParticleDecl particleNode = (XSParticleDecl)particleNodes.next();
			MWParticle oldParticle = containsEquivalentParticle(particleNode, this.particles());
			if (oldParticle != null) {
				oldParticle.reload(particleNode);
				newParticles.add(oldParticle);
			} else {
				MWParticle newParticle = MWParticle.ParticleFactory.newParticle(this, particleNode);
				newParticles.add(newParticle);
			}
		}
		
		this.particles = newParticles;

	}
	
	private MWParticle containsEquivalentParticle(XSParticleDecl particleNode, ListIterator particles) {
		while (particles.hasNext()) {
			MWParticle particle = (MWParticle)particles.next();
			if (particle.isEquivalentTo((XSParticleDecl)particleNode)) {
				return particle;
			}
		}
		return null;
	}
	
	private boolean containsEquivalentNode(MWParticle particle, Iterator particleNodes) {
		while (particleNodes.hasNext()) {
			XSParticleDecl particleNode = (XSParticleDecl)particleNodes.next();
			if (particle.isEquivalentTo(particleNode)) {
				return true;
 			}
		}
		return false;
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		
		for (Iterator stream = this.particles.iterator(); stream.hasNext(); ) {
			((MWParticle) stream.next()).resolveReferences();
		}
	}
}
