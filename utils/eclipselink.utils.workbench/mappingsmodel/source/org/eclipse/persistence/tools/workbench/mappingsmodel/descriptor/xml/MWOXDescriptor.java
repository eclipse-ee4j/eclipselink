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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyAdditionException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWNullTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractAnyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWOXMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;

public final class MWOXDescriptor 
	extends MWXmlDescriptor implements MWTransactionalDescriptor
{
	private volatile boolean anyTypeDescriptor;
		public final static String ANY_TYPE_PROPERTY = "anyTypeDescriptor";
	
	private volatile boolean preserveDocument;
		public final static String PRESERVE_DOCUMENT_PROPERTY= "preserveDocument";		
	
	private volatile boolean rootDescriptor;
		public final static String ROOT_DESCRIPTOR_PROPERTY = "rootDescriptor";

	private MWNamedSchemaComponentHandle defaultRootElementTypeHandle;
		public final static String DEFAULT_ROOT_ELEMENT_TYPE_PROPERTY = "defaultRootElementType";

		
	// **************** Constructors ******************************************	

	private MWOXDescriptor() {
		super();
	}
	
	public MWOXDescriptor(MWXmlProject project, MWClass type, String name) {
		super(project, type, name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.rootDescriptor = false;
		this.defaultRootElementTypeHandle = new MWNamedSchemaComponentHandle(this, this.buildDefaultRootElementTypeScrubber());
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		if (getTransactionalPolicy() instanceof MWNullTransactionalPolicy) {
			this.transactionalPolicy = new MWOXTransactionalPolicy(this);
		}
	}
	
	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		defaultsPolicy.applyAdvancedPolicyDefaults(this);
	}
	
	private NodeReferenceScrubber buildDefaultRootElementTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWOXDescriptor.this.setDefaultRootElementType(null);
			}
			public String toString() {
				return "MWOXDescriptor.buildDefaultRootElementTypeScrubber()";
			}
		};
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.defaultRootElementTypeHandle);
	}
	
	// **************** Public ************************************************
	
	public MWOXProject getOXProject() {
		return (MWOXProject) this.getProject();
	}
	
	public MWMappingFactory mappingFactory() {
		return MWOXMappingFactory.instance();
	}
	
	// **************** Root descriptor ***************************************
	
	
	public boolean isRootDescriptor() {
		return this.rootDescriptor;
	}
	
	@Override
	public boolean isEisDescriptor() {
		return false;
	}
	
	public void setRootDescriptor(boolean descriptorIsRoot) {
		boolean oldValue = this.rootDescriptor;
		this.rootDescriptor = descriptorIsRoot;
		this.firePropertyChanged(ROOT_DESCRIPTOR_PROPERTY, oldValue, descriptorIsRoot);
		
		if (oldValue != descriptorIsRoot) {
			if (! descriptorIsRoot) {
				this.setDefaultRootElement(null);
			}
			else {
				this.setInitialDefaultRootElement();
			}
		}
	}

	//	**************** Any type descriptor **********************************
	
	public void setAnyTypeDescriptor(boolean newValue) {
		boolean oldValue = this.anyTypeDescriptor;
		this.anyTypeDescriptor = newValue;
		this.firePropertyChanged(ANY_TYPE_PROPERTY, oldValue, newValue);
	}
	
	public boolean isAnyTypeDescriptor(){
		return this.anyTypeDescriptor;
	}
	
	// **************** Default root element type ******************************
	
	public MWComplexTypeDefinition getDefaultRootElementType() {
		return (MWComplexTypeDefinition) this.defaultRootElementTypeHandle.getComponent();
	}
	
	public void setDefaultRootElementType(MWComplexTypeDefinition newDefaultRootElementType) {
		MWComplexTypeDefinition oldDefaultRootElementType = this.getDefaultRootElementType();
		this.defaultRootElementTypeHandle.setComponent(newDefaultRootElementType);
		this.firePropertyChanged(DEFAULT_ROOT_ELEMENT_TYPE_PROPERTY, oldDefaultRootElementType, newDefaultRootElementType);
	}
	
	//	**************** Preserve Docuement ***********************************
	
	public void setPreserveDocument(boolean newValue){
		boolean oldValue = this.preserveDocument;
		this.preserveDocument = newValue;
		this.firePropertyChanged(PRESERVE_DOCUMENT_PROPERTY, oldValue, newValue);
	}
	
	public boolean isPreserveDocument(){	
		return this.preserveDocument;
	}
	
	//	 **************** Inheritance policy ************************************
	@Override
	protected MWTransactionalPolicy buildDefaultTransactionalPolicy() {
		return new MWOXTransactionalPolicy(this);
	}
	
	// **************** Inheritance policy ************************************

	protected MWDescriptorInheritancePolicy buildInheritancePolicy() {
		return new MWOXDescriptorInheritancePolicy(this);
	}


	// **************** Mapping creation **************************************
	
	public MWAnyObjectMapping addAnyObjectMapping(MWClassAttribute attribute) {
		MWAnyObjectMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyObjectMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWAnyCollectionMapping addAnyCollectionMapping(MWClassAttribute attribute) {
		MWAnyCollectionMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyCollectionMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWAnyAttributeMapping addAnyAttributeMapping(MWClassAttribute attribute) {
		MWAnyAttributeMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyAttributeMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWXmlFragmentMapping addXmlFragmentMapping(MWClassAttribute attribute) {
		MWXmlFragmentMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlFragmentMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public MWXmlFragmentCollectionMapping addXmlFragmentCollectionMapping(MWClassAttribute attribute) {
		MWXmlFragmentCollectionMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlFragmentCollectionMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public MWXmlObjectReferenceMapping addXmlObjectReferenceMapping(MWClassAttribute attribute) {
		MWXmlObjectReferenceMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlObjectReferenceMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public MWXmlCollectionReferenceMapping addXmlCollectionReferenceMapping(MWClassAttribute attribute) {
		MWXmlCollectionReferenceMapping mapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlCollectionReferenceMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	//***************** Morphing support **************************************

	//currently there is no descriptor morhping for MWOXDescriptors
	public void initializeOn(MWDescriptor newDescriptor) {
		throw new UnsupportedOperationException();
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.addAnyTypeDescriptorWithInheritanceProblemTo(newProblems);
		this.addAnyTypeDescriptorWithNonAnyMappingsProblemTo(newProblems);
		this.addDefaultRootElementTypeProblemTo(newProblems);
	}
	
	/** overridden from MWXmlDescriptor */
	protected boolean schemaContextIsRequired() {
		// any type descriptors do not require a schema context
		return ! this.isAnyTypeDescriptor();
	}
	
	private void addAnyTypeDescriptorWithInheritanceProblemTo(List newProblems) {
		if (this.isAnyTypeDescriptor() && this.getInheritancePolicy().isActive()) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_ANY_TYPE_WITH_INHERITANCE));
		}
	}
	
	private void addAnyTypeDescriptorWithNonAnyMappingsProblemTo(List newProblems) {
		// anyType descriptors may only support a single any mapping
		if (this.isAnyTypeDescriptor()) {
			// no mappings -> obviously no worries
			if (this.mappingsSize() == 0) {
				return;
			}
			// more than one mapping or only mapping not an any mapping
			else if (
				this.mappingsSize() > 1 
				|| ! (this.mappings().next() instanceof MWAbstractAnyMapping)
			) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_ANY_TYPE_WITH_NON_ANY_MAPPINGS));	
			}
		}
	}
	
	private void addDefaultRootElementTypeProblemTo(List newProblems) {
		if (this.getDefaultRootElement() == null) {
			if (this.getDefaultRootElementType() != null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_DEFAULT_ROOT_ELEMENT_TYPE));
			}
		}
	}
	
	// **************** Runtime conversion ************************************
	
	protected ClassDescriptor buildBasicRuntimeDescriptor() {
		XMLDescriptor runtimeDescriptor = new XMLDescriptor();
		runtimeDescriptor.setJavaClassName(getMWClass().getName());
		return runtimeDescriptor;
	}
	
	public ClassDescriptor buildRuntimeDescriptor() {
		XMLDescriptor runtimeDescriptor = (XMLDescriptor) super.buildRuntimeDescriptor();
		
		runtimeDescriptor.setSchemaReference(this.runtimeSchemaReference());
		runtimeDescriptor.setNamespaceResolver(this.runtimeNamespaceResolver());
		
		return runtimeDescriptor;
	}
	
	protected void adjustRuntimeDescriptorRootProperties(ClassDescriptor runtimeDescriptor) {
		super.adjustRuntimeDescriptorRootProperties(runtimeDescriptor);
		
		if (this.isRootDescriptor() && this.getDefaultRootElement() != null) {
			((XMLDescriptor) runtimeDescriptor).setDefaultRootElement(this.getDefaultRootElement().qName());
			if (this.getDefaultRootElementType() != null) {
				((XMLDescriptor) runtimeDescriptor).setDefaultRootElementType(new QName(getDefaultRootElementType().qName()));
			}
		}
		
		((XMLDescriptor) runtimeDescriptor).setShouldPreserveDocument(this.preserveDocument);
		
	}
	
	public AbstractDirectMapping buildDefaultRuntimeDirectMapping() {
		return new XMLDirectMapping();
	}
	
	public AbstractCompositeDirectCollectionMapping buildDefaultRuntimeDirectCollectionMapping() {
		return new XMLCompositeDirectCollectionMapping();
	}
	
	public AbstractCompositeObjectMapping buildDefaultRuntimeCompositeObjectMapping() {
		return new XMLCompositeObjectMapping();
	}
	
	public AbstractCompositeCollectionMapping buildDefaultRuntimeCompositeCollectionMapping() {
		return new XMLCompositeCollectionMapping();
	}
	
	public AbstractTransformationMapping buildDefaultRuntimeTransformationMapping() {
		return new XMLTransformationMapping();
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWOXDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWXmlDescriptor.class);
		
		XMLDirectMapping anyTypeDescriptorMapping = (XMLDirectMapping) descriptor.addDirectMapping("anyTypeDescriptor", "any-type-descriptor/text()");
		anyTypeDescriptorMapping.setNullValue(Boolean.FALSE);
		
		descriptor.addDirectMapping("rootDescriptor", "root-descriptor/text()");
		
		XMLDirectMapping preserveDocumentMapping = (XMLDirectMapping) descriptor.addDirectMapping("preserveDocument", "preserve-document/text()");
		preserveDocumentMapping.setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping defaultRootElementTypeHandleMapping = new XMLCompositeObjectMapping();
		defaultRootElementTypeHandleMapping.setAttributeName("defaultRootElementTypeHandle");
		defaultRootElementTypeHandleMapping.setGetMethodName("getDefaultRootElementTypeHandleForTopLink");
		defaultRootElementTypeHandleMapping.setSetMethodName("setDefaultRootElementTypeHandleForTopLink");
		defaultRootElementTypeHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		defaultRootElementTypeHandleMapping.setXPath("default-root-element-type-handle");
		descriptor.addMapping(defaultRootElementTypeHandleMapping);

		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWNamedSchemaComponentHandle getDefaultRootElementTypeHandleForTopLink() {
		return (this.defaultRootElementTypeHandle.getComponent() == null) ? null : this.defaultRootElementTypeHandle;
	}
	private void setDefaultRootElementTypeHandleForTopLink(MWNamedSchemaComponentHandle handle) {
		NodeReferenceScrubber scrubber = this.buildDefaultRootElementTypeScrubber();
		this.defaultRootElementTypeHandle = ((handle == null) ? new MWNamedSchemaComponentHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	// **************** Primary key policy ************************************
	
	public MWXmlPrimaryKeyPolicy primaryKeyPolicy() {
		return ((MWOXTransactionalPolicy) this.getTransactionalPolicy()).getPrimaryKeyPolicy();
	}

	// - returns an iterator on the attributes mapped by all mappings obtained by
	//   getPrimaryKeyMappings()
	public Iterator primaryKeyAttributes() {
		return new TransformationIterator(this.primaryKeyMappings()) {
			protected Object transform(Object next) {
				return ((MWMapping) next).getInstanceVariable();
			}
		};
	}		
		
		
	// - returns an iterator on all mappings in this descriptor that map to primary key fields,
	//   plus all mappings in this descriptor's superdescriptors that do the same
	public Iterator primaryKeyMappings() {
		Collection pkMappings = new Vector();
		
		for (Iterator stream = primaryKeyPolicy().primaryKeyXpaths(); stream.hasNext(); ) {
			CollectionTools.addAll(pkMappings, mappingsForXpath((String) stream.next()));
		}
		
        MWDescriptor parentDescriptor = getInheritancePolicy().getParentDescriptor();
        //TODO remove the instanceof put the primaryKeyMapping() api on MWEisDescriptor and just return an empty collection
		if (parentDescriptor != null && parentDescriptor instanceof MWRootEisDescriptor) {
			CollectionTools.addAll(pkMappings, ((MWRootEisDescriptor) parentDescriptor).primaryKeyMappings());
		}
		
		return pkMappings.iterator();			
	}

	/** - returns an iterator on all mappings that map to the given xpath */
	private Iterator mappingsForXpath(String xpath) {
		Collection mappings = new ArrayList();
		
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			//TODO addWrittenFieldsTo(Collection) is not fully implemented in the mappings model
			Collection writtenFields = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFields);
			if (writtenFields.contains(xpath)) {
				mappings.add(mapping);
			}
		}
		
		return mappings.iterator();
	}
	
	//**************************** Returning Policy ***************************************************
	
	public MWDescriptorPolicy getReturningPolicy() {
		throw new UnsupportedOperationException("Returning Policy not supported on OX descriptor");
	}
	
	public void addReturningPolicy() throws MWAdvancedPropertyAdditionException {
		throw new UnsupportedOperationException("Returning Policy not supported on OX descriptor");
	}
	
	public void removeReturningPolicy() {
		throw new UnsupportedOperationException("Returning Policy not supported on OX descriptor");
	}
}
