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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWAnyObjectMapping 
	extends MWAbstractAnyMapping implements MWXmlElementTypeableMapping 
{
	
	private MWNamedSchemaComponentHandle elementTypeHandle;
		public final static String ELEMENT_TYPE_PROPERTY = "elementType";

	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWAnyObjectMapping() {
		super();
	}
	
	MWAnyObjectMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	@Override
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.elementTypeHandle = new MWNamedSchemaComponentHandle(this, this.buildElementTypeScrubber());
	}
	
	private NodeReferenceScrubber buildElementTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAnyObjectMapping.this.setElementType(null);
			}
			public String toString() {
				return "MWAbstractCompositeMapping.buildElementTypeScrubber()";
			}
		};
	}
	
	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.elementTypeHandle);
	}

	// **************** Element type ******************************************
	
	public MWComplexTypeDefinition getElementType() {
		return (MWComplexTypeDefinition) this.elementTypeHandle.getComponent();
	}
	
	public void setElementType(MWComplexTypeDefinition newElementType) {
		MWComplexTypeDefinition oldElementType = this.getElementType();
		this.elementTypeHandle.setComponent(newElementType);
		this.firePropertyChanged(ELEMENT_TYPE_PROPERTY, oldElementType, newElementType);
	}
	
	// **************** MWXpathContext implementation *************************
	
	protected boolean mayUseCollectionData() {
		return false;
	}
	
	
	// **************** Morphing **********************************************
	
	public MWAnyObjectMapping asAnyObjectMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWAnyObjectMapping(this);
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addXmlFieldProblemsTo(List newProblems) {
		super.addXmlFieldProblemsTo(newProblems);
		this.addXmlFieldNotSingularProblemTo(newProblems);
	}
	
	private void addXmlFieldNotSingularProblemTo(List newProblems) {
		if (this.getXmlField().isValid() && ! this.getXmlField().isSingular()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SINGULAR, this.getXmlField().getXpath()));
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new XMLAnyObjectMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		XMLAnyObjectMapping runtimeMapping = (XMLAnyObjectMapping) super.runtimeMapping();
		runtimeMapping.setField(this.getXmlField().runtimeField());
		if (this.getElementType() != null && runtimeMapping.getField() != null) {
			((XMLField)runtimeMapping.getField()).setLeafElementType(new QName(this.getElementType().qName()));
		}
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAnyObjectMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWAbstractAnyMapping.class);
		
		XMLCompositeObjectMapping elementTypeHandleMapping = new XMLCompositeObjectMapping();
		elementTypeHandleMapping.setAttributeName("elementTypeHandle");
		elementTypeHandleMapping.setGetMethodName("getElementTypeHandleForTopLink");
		elementTypeHandleMapping.setSetMethodName("setElementTypeHandleForTopLink");
		elementTypeHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		elementTypeHandleMapping.setXPath("element-type-handle");
		descriptor.addMapping(elementTypeHandleMapping);

		return descriptor;
	}
	/**
	 * check for null
	 */
	private MWNamedSchemaComponentHandle getElementTypeHandleForTopLink() {
		return (this.elementTypeHandle.getComponent() == null) ? null : this.elementTypeHandle;
	}
	private void setElementTypeHandleForTopLink(MWNamedSchemaComponentHandle handle) {
		NodeReferenceScrubber scrubber = this.buildElementTypeScrubber();
		this.elementTypeHandle = ((handle == null) ? new MWNamedSchemaComponentHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
