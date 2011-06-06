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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public abstract class MWAbstractCompositeMapping 
	extends MWMapping
	implements MWReferenceObjectMapping, MWXmlMapping, MWXpathedMapping, MWXpathContext, MWXmlElementTypeableMapping
{
	// **************** Variables *********************************************
	
	private MWDescriptorHandle referenceDescriptorHandle;
		public final static String REFERENCE_DESCRIPTOR_PROPERTY = "referenceDescriptor";
	
	private MWXmlField xmlField;
	
	private MWNamedSchemaComponentHandle elementTypeHandle;
		public final static String ELEMENT_TYPE_PROPERTY = "elementType";
		
	private MWContainerAccessor containerAccessor;
		public final static String CONTAINER_ACCESSOR_PROPERTY = "containerAccessor";
		
	private Boolean usesContainerAccessor;
		public final static String USES_CONTAINER_ACCESSOR_PROPERTY = "usesContainerAccessor";

	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWAbstractCompositeMapping() {
		super();
	}
	
	protected MWAbstractCompositeMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.referenceDescriptorHandle = new MWDescriptorHandle(this, this.buildReferenceDescriptorScrubber());
		this.xmlField = new MWXmlField(this);
		this.elementTypeHandle = new MWNamedSchemaComponentHandle(this, this.buildElementTypeScrubber());
		this.containerAccessor = new MWNullContainerAccessor(this);
		this.usesContainerAccessor = new Boolean(false);
	}
	
	private NodeReferenceScrubber buildElementTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAbstractCompositeMapping.this.setElementType(null);
			}
			public String toString() {
				return "MWAbstractCompositeMapping.buildElementTypeScrubber()";
			}
		};
	}
	
	// **************** Reference descriptor **********************************
	
	public MWDescriptor getReferenceDescriptor() {
		return this.referenceDescriptorHandle.getDescriptor();
	}
	
	public void setReferenceDescriptor(MWDescriptor newReferenceDescriptor) {
		MWDescriptor oldReferenceDescriptor = this.referenceDescriptorHandle.getDescriptor();
		this.referenceDescriptorHandle.setDescriptor(newReferenceDescriptor);
		this.firePropertyChanged(REFERENCE_DESCRIPTOR_PROPERTY, oldReferenceDescriptor, newReferenceDescriptor);
	}
	
	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		// can have pretty much any reference descriptor
		return true;
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

	// **************** container accessor ************************************
	
	public Boolean usesContainerAccessor() {
		return this.usesContainerAccessor;
	}
	
	public void setUsesContainerAccessor(Boolean use) {
		boolean oldValue = this.usesContainerAccessor().booleanValue();
		this.usesContainerAccessor = use;
		this.firePropertyChanged(USES_CONTAINER_ACCESSOR_PROPERTY, oldValue, use.booleanValue());
	}
	
	public MWContainerAccessor getContainerAccessor() {
		return this.containerAccessor;
	}
	
	public void setContainerAccessor(MWContainerAccessor newAccessor) {
		MWContainerAccessor oldValue = this.getContainerAccessor();
		this.containerAccessor = newAccessor;
		this.firePropertyChanged(CONTAINER_ACCESSOR_PROPERTY, oldValue, newAccessor);
	}
	
	public void setContainerAccessorGetMethod(MWMethod getMethod) {
		if(getContainerAccessor().isNull() || getContainerAccessor().isAttribute()) {
			this.setContainerAccessor(new MWMethodContainerAccessor(this, getMethod, null));
		} else {
			((MWMethodContainerAccessor)this.getContainerAccessor()).setAccessorGetMethod(getMethod);
		}
	}
	
	public void setContainerAccessorSetMethod(MWMethod setMethod) {
		if(getContainerAccessor().isNull() || getContainerAccessor().isAttribute()) {
			this.setContainerAccessor(new MWMethodContainerAccessor(this, null, setMethod));
		} else {
			((MWMethodContainerAccessor)this.getContainerAccessor()).setAccessorSetMethod(setMethod);
		}
	}
	
	public void setContainerAccessorAttribute(MWClassAttribute attribute) {
		this.setContainerAccessor(new MWAttributeContainerAccessor(this, attribute));
	}
	
	public Iterator candidateAccessorMethods() {
		if (this.getReferenceDescriptor() != null) {
			return this.getReferenceDescriptor().getMWClass().allInstanceMethods();
		}
		return NullIterator.instance();
	}

	public Iterator candidateAccessorAttributes() {
		if (this.getReferenceDescriptor() != null) {
			return this.getReferenceDescriptor().getMWClass().allInstanceVariables();
		}
		return NullIterator.instance();
	}

	// **************** MWXpathedMapping implementation  **********************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		return this.getXmlField().isResolved() ? this.getXmlField() : null;
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		if (! this.isReadOnly() && ! this.getXmlField().getXpath().equals("")) {
			writtenFields.add(this.getXmlField());
		}
	}
	
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	protected MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return MWAbstractCompositeMapping.this.mayUseCollectionData();
			}
			
			public boolean mayUseComplexData() {
				return true;
			}
			
			public boolean mayUseSimpleData() {
				return false;
			}
		};
	}
	
	protected abstract boolean mayUseCollectionData();
	
	
	// **************** Convenience *******************************************
	
	protected MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getParent();
	}
	
	
	// **************** Morphing **********************************************
	
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		super.initializeFromMWXpathedMapping(oldMapping);
		
		this.getXmlField().setXpath(oldMapping.getXmlField().getXpath());
		// can't use typed, so don't set that
	}
	
	protected void initializeFromMWReferenceObjectMapping(MWReferenceObjectMapping oldMapping) {
		super.initializeFromMWReferenceObjectMapping(oldMapping);
		
		this.setReferenceDescriptor(oldMapping.getReferenceDescriptor());
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List newProblems) {
		// would like to add xpath and reference descriptor problems first
		this.addXmlFieldProblemsTo(newProblems);
		this.addReferenceDescriptorNotSpecifiedProblemTo(newProblems);
		// TODO: reference descriptor validation ??
		this.addReferenceDescriptorInactiveProblemTo(newProblems);
		this.addContainerAccessorNotSpecifiedProblemTo(newProblems);
		super.addProblemsTo(newProblems);
	}
	
	protected void addContainerAccessorNotSpecifiedProblemTo(List newProblems) {
		if (this.usesContainerAccessor() && this.containerAccessor.isNull()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_ACCESSOR_NOT_CONFIGURED));
		}
	}
	
	protected void addXmlFieldProblemsTo(List newProblems) {
		this.addXpathNotSpecifiedProblemTo(newProblems);
	}
	
	private void addXpathNotSpecifiedProblemTo(List newProblems) {
		if (! this.getXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SPECIFIED));
		}
	}
	
	private void addReferenceDescriptorNotSpecifiedProblemTo(List newProblems) {
		if (this.xmlDescriptor().isEisDescriptor() && this.getReferenceDescriptor() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED));
		}
	}
	
	private void addReferenceDescriptorInactiveProblemTo(List newProblems) {
		if (this.getReferenceDescriptor() != null && ! this.getReferenceDescriptor().isActive()) {
			newProblems.add(
				this.buildProblem(
					ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, 
					this.getInstanceVariable().getName(),
					this.getReferenceDescriptor().getMWClass().shortName()
				)
			);
		}
	}
	
	
	// **************** containment hierarchy *********************************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.referenceDescriptorHandle);
		children.add(this.xmlField);
		children.add(this.elementTypeHandle);
		children.add(this.containerAccessor);
	}
	
	private NodeReferenceScrubber buildReferenceDescriptorScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAbstractCompositeMapping.this.setReferenceDescriptor(null);
			}
			public String toString() {
				return "MWAbstractCompositeMapping.buildReferenceDescriptorScrubber()";
			}
		};
	}

	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		super.descriptorReplaced(oldDescriptor, newDescriptor);
		// we don't need this until we support multiple descriptor types in XML/EIS projects
//		if (this.getReferenceDescriptor() == oldDescriptor) {
//			this.setReferenceDescriptor(newDescriptor);
//		}
	}

	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		this.xmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.xmlField.schemaChanged(change);
	}
	
	
	// **************** Runtime conversion ************************************
	
	public DatabaseMapping runtimeMapping() {
		AggregateMapping runtimeMapping = 
			(AggregateMapping) super.runtimeMapping();
		
		if (this.getReferenceDescriptor() != null) {
			runtimeMapping.setReferenceClassName(this.getReferenceDescriptor().getMWClass().getName());
		}
		
		if (this.getElementType() != null && runtimeMapping.getField() != null) {
			((XMLField)runtimeMapping.getField()).setLeafElementType(new QName(getElementType().qName()));
		}
		
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractCompositeMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		descriptor.addDirectMapping("usesContainerAccessor", "getUsesContainerAccessorForTopLink", "setUsesContainerAccessorForTopLink", "uses-container-accessor");
		
		XMLCompositeObjectMapping referenceDescriptorMapping = new XMLCompositeObjectMapping();
		referenceDescriptorMapping.setAttributeName("referenceDescriptorHandle");
		referenceDescriptorMapping.setGetMethodName("getReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setSetMethodName("setReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setXPath("reference-descriptor-handle");
		referenceDescriptorMapping.setReferenceClass(MWDescriptorHandle.class);
		descriptor.addMapping(referenceDescriptorMapping);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xpath");
		descriptor.addMapping(xmlFieldMapping);
		
		XMLCompositeObjectMapping elementTypeHandleMapping = new XMLCompositeObjectMapping();
		elementTypeHandleMapping.setAttributeName("elementTypeHandle");
		elementTypeHandleMapping.setGetMethodName("getElementTypeHandleForTopLink");
		elementTypeHandleMapping.setSetMethodName("setElementTypeHandleForTopLink");
		elementTypeHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		elementTypeHandleMapping.setXPath("element-type-handle");
		descriptor.addMapping(elementTypeHandleMapping);
		
		XMLCompositeObjectMapping containerAccessorMapping = new XMLCompositeObjectMapping();
		containerAccessorMapping.setAttributeName("containerAccessor");
		containerAccessorMapping.setGetMethodName("getContainerAccessorForTopLink");
		containerAccessorMapping.setSetMethodName("setContainerAccessorForTopLink");
		containerAccessorMapping.setReferenceClass(MWContainerAccessor.class);
		containerAccessorMapping.setXPath("container-accessor");
		descriptor.addMapping(containerAccessorMapping);

		return descriptor;
	}
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
		descriptor.setJavaClass(MWAbstractCompositeMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		XMLCompositeObjectMapping referenceDescriptorMapping = new XMLCompositeObjectMapping();
		referenceDescriptorMapping.setAttributeName("referenceDescriptorHandle");
		referenceDescriptorMapping.setGetMethodName("getReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setSetMethodName("setReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setXPath("reference-descriptor-handle");
		referenceDescriptorMapping.setReferenceClass(MWDescriptorHandle.class);
		descriptor.addMapping(referenceDescriptorMapping);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xpath");
		descriptor.addMapping(xmlFieldMapping);
		
		XMLCompositeObjectMapping elementTypeHandleMapping = new XMLCompositeObjectMapping();
		elementTypeHandleMapping.setAttributeName("elementTypeHandle");
		elementTypeHandleMapping.setGetMethodName("getElementTypeHandleForTopLink");
		elementTypeHandleMapping.setSetMethodName("setElementTypeHandleForTopLink");
		elementTypeHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		elementTypeHandleMapping.setXPath("element-type-handle");
		descriptor.addMapping(elementTypeHandleMapping);
		
		return descriptor;
	}
	
	private Boolean getUsesContainerAccessorForTopLink() {
		return this.usesContainerAccessor;
	}
	
	private void setUsesContainerAccessorForTopLink(Boolean value) {
		if (value == null) {
			this.usesContainerAccessor = Boolean.FALSE;
		} else {
			this.usesContainerAccessor = value;
		}
	}
	
	private MWContainerAccessor getContainerAccessorForTopLink() {
		return this.containerAccessor.valueForTopLink();
	}
	
	private void setContainerAccessorForTopLink(MWContainerAccessor newAccessor) {
		this.containerAccessor = MWContainerAccessor.buildAccessorForTopLink(newAccessor);
	}
	
	/**
	 * check for null
	 */
	private MWDescriptorHandle getReferenceDescriptorHandleForTopLink() {
		return (this.referenceDescriptorHandle.getDescriptor() == null) ? null : this.referenceDescriptorHandle;
	}
	private void setReferenceDescriptorHandleForTopLink(MWDescriptorHandle referenceDescriptorHandle) {
		NodeReferenceScrubber scrubber = this.buildReferenceDescriptorScrubber();
		this.referenceDescriptorHandle = ((referenceDescriptorHandle == null) ? new MWDescriptorHandle(this, scrubber) : referenceDescriptorHandle.setScrubber(scrubber));
	}

	private MWXmlField getXmlFieldForTopLink() {
		return (this.xmlField.isSpecified()) ? this.xmlField: null;
	}
	
	private void setXmlFieldForTopLink(MWXmlField xmlField) {
		this.xmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
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
