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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroupDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSimpleTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;


public abstract class MWXmlDescriptor 
	extends MWMappingDescriptor
	implements MWXmlNode
{
	private MWNamedSchemaComponentHandle schemaContextHandle;
		public final static String SCHEMA_CONTEXT_PROPERTY = "schemaContext";
		
	//TODO this only applies to root MWOxDescriptors and MWRootEisDescriptors
	private MWNamedSchemaComponentHandle defaultRootElementHandle;
		public final static String DEFAULT_ROOT_ELEMENT_PROPERTY = "defaultRootElement";
	
	
	// **************** Constructors ******************************************

	/** Default constructor - for TopLink use only. */
	protected MWXmlDescriptor() {
		super();
	}
	
	public MWXmlDescriptor(MWXmlProject project, MWClass type, String name) {
		super(project, type, name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.schemaContextHandle = new MWNamedSchemaComponentHandle(this, this.buildSchemaContextScrubber());
		this.defaultRootElementHandle = new MWNamedSchemaComponentHandle(this, this.buildDefaultRootElementScrubber());
	}
	
	protected void initializeFromMWXmlDescriptor(MWXmlDescriptor oldDescriptor) {
		super.initializeFromMWMappingDescriptor(oldDescriptor);
		this.setSchemaContext(oldDescriptor.getSchemaContext());
		this.setDefaultRootElement(oldDescriptor.getDefaultRootElement());
	}

	
	// **************** Schema context ****************************************
	
	public MWSchemaContextComponent getSchemaContext() {
		return (MWSchemaContextComponent) this.schemaContextHandle.getComponent();
	}
	
	public void setSchemaContext(MWSchemaContextComponent newContext) {
		MWSchemaContextComponent oldContext = this.getSchemaContext();
		this.schemaContextHandle.setComponent(newContext);
		this.firePropertyChanged(SCHEMA_CONTEXT_PROPERTY, oldContext, newContext);
		
		this.setInitialDefaultRootElement();
		this.resolveXpaths();
	}
	
	
	// **************** Default root element **********************************
	
	public abstract boolean isRootDescriptor();
	
	public abstract boolean isEisDescriptor();
	
	public MWElementDeclaration getDefaultRootElement() {
		return (MWElementDeclaration) this.defaultRootElementHandle.getComponent();
	}
	
	public void setDefaultRootElement(MWElementDeclaration newDefaultRootElement) {
		MWElementDeclaration oldDefaultRootElement = this.getDefaultRootElement();
		this.defaultRootElementHandle.setComponent(newDefaultRootElement);
		this.firePropertyChanged(DEFAULT_ROOT_ELEMENT_PROPERTY, oldDefaultRootElement, newDefaultRootElement);
	}
	
	/** set to an initial value, if it exists */
	protected void setInitialDefaultRootElement() {
		if (this.getSchemaContext() instanceof MWElementDeclaration) {
			this.setDefaultRootElement((MWElementDeclaration) this.getSchemaContext());
		}
	}
	
	
	//************** Mapping Creation *****************************************
	
	public abstract MWMappingFactory mappingFactory();
	
	public MWCompositeObjectMapping addCompositeObjectMapping(MWClassAttribute attribute) {
		MWCompositeObjectMapping mapping = 
			((MWXmlMappingFactory) mappingFactory()).createCompositeObjectMapping(this, attribute, attribute.getName());
		addMapping(mapping);
		return mapping;
	}
	
	public MWCompositeCollectionMapping addCompositeCollectionMapping(MWClassAttribute attribute) {
		MWCompositeCollectionMapping mapping = 
			((MWXmlMappingFactory) mappingFactory()).createCompositeCollectionMapping(this, attribute, attribute.getName());
		addMapping(mapping);
		return mapping;
	}
	
	
	//***************** Model synchronization *********************************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.schemaContextHandle);
		children.add(this.defaultRootElementHandle);
	}
	
	private NodeReferenceScrubber buildSchemaContextScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWXmlDescriptor.this.setSchemaContext(null);
			}
			public String toString() {
				return "MWXmlDescriptor.buildSchemaContextScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildDefaultRootElementScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWXmlDescriptor.this.setDefaultRootElement(null);
			}
			public String toString() {
				return "MWXmlDescriptor.buildDefaultRootElementtScrubber()";
			}
		};
	}

	protected void refreshClass(MWClassRefreshPolicy refreshPolicy)
		throws ExternalClassNotFoundException, InterfaceDescriptorCreationException
	{
		super.refreshClass(refreshPolicy);

		if (this.getMWClass().isInterface()) {
			throw new InterfaceDescriptorCreationException(getMWClass());
		}
	}
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		((MWXmlNode) this.getInheritancePolicy()).resolveXpaths();
		((MWXmlNode) this.getLockingPolicy()).resolveXpaths();
		
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).resolveXpaths();
		}
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		if (this.getSchemaContext() == null || this.getSchemaContext().getSchema() != change.getSchema()) {
			return;
		}
		
		// The following policies may have xpath information ...
		((MWXmlNode) this.getInheritancePolicy()).schemaChanged(change);
		((MWXmlNode) this.getLockingPolicy()).schemaChanged(change);
		
		// ... and mappings always do.
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).schemaChanged(change);
		}
	}
	

	//***************** Problem Handling **************************************

	/** Check for any problems and add them to the specified collection. */
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.addSchemaContextNotSpecifiedProblemTo(newProblems);
		this.addDefaultRootElementNotSpecifiedProblemTo(newProblems);
	}

	private void addSchemaContextNotSpecifiedProblemTo(List newProblems) {
		if (this.schemaContextIsRequired() && this.getSchemaContext() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_SCHEMA_CONTEXT_SPECIFIED));
		}
	}
	
	public Collection allWritableMappingsForField(MWDataField field) {
		// Answer all mappings that map to the xml field with an xpath that maps to this same element/attribute
		Collection mappingsForField = new ArrayList();
		for (Iterator mappings = this.mappings(); mappings.hasNext(); )  {
			MWMapping mapping = (MWMapping) mappings.next();
			if (! mapping.isReadOnly()) {			
				Collection writtenFields = new ArrayList();
				mapping.addWrittenFieldsTo(writtenFields);
				for (Iterator writtenFieldIter = writtenFields.iterator(); writtenFieldIter.hasNext(); ) {
					MWXmlField writtenField = (MWXmlField) writtenFieldIter.next();
					if (writtenField.getXpath().equals(((MWXmlField)field).getXpath())) {
						mappingsForField.add(mapping);
					}
				}
			}
		}
		
		if (! writableMappingsForField(field).isEmpty() 
			&& ! getInheritancePolicy().isRoot() 
			&& getInheritancePolicy().getParentDescriptor() != null)
		{
			for (Iterator stream = getInheritancePolicy().getParentDescriptor().writableMappingsForField(field).iterator(); stream.hasNext(); ) {
				// do not add mappings that are overwritten in this descriptor
				MWMapping nextInheritedMapping = (MWMapping) stream.next();
				MWMapping inheritedMappingInThisDescriptor = mappingNamed(nextInheritedMapping.getName());
				if (inheritedMappingInThisDescriptor == null) {
					mappingsForField.add(nextInheritedMapping);
				}
			}
		}
		
		return mappingsForField;
	}

	public Collection writableMappingsForField( MWDataField field ) {
		// Answer all mappings that map to the xml field

		Collection mappingsForField = new ArrayList();
		for (Iterator mappings = mappings(); mappings.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappings.next();
			Collection writtenFields = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFields);
			for (Iterator writtenFieldIter = writtenFields.iterator(); writtenFieldIter.hasNext(); ) {
				MWXmlField writtenField = (MWXmlField)writtenFieldIter.next();
				if (writtenField.getXpath().equals(((MWXmlField)field).getXpath())) {
					mappingsForField.add(mapping);
				}
			}
		}
		return mappingsForField;
	}

	/** default behavior - overridden in MWOXDescriptor */
	protected boolean schemaContextIsRequired() {
		return true;
	}
	
	private void addDefaultRootElementNotSpecifiedProblemTo(List newProblems) {
		if (this.isRootDescriptor()) {
			if (this.getDefaultRootElement() == null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_DEFAULT_ROOT_ELEMENT_SPECIFIED));
			}
		}
	}
	
	protected String multipleMappingsWriteFieldProblemResourceStringKey() {
		return ProblemConstants.DESCRIPTOR_MULTIPLE_MAPPINGS_WRITE_TO_XPATH;
	}
	
	
	// **************** Runtime conversion ************************************
	
	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = super.buildRuntimeDescriptor();
		
		this.adjustRuntimeDescriptorRootProperties(runtimeDescriptor);
	
		return runtimeDescriptor;	
	}
	
	protected void adjustRuntimeDescriptorRootProperties(ClassDescriptor runtimeDescriptor) {
		if (! this.isRootDescriptor()) {
			runtimeDescriptor.descriptorIsAggregate();
		}
	}
	
	protected Comparator orderedMappingComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				MWXmlField xmlField1 = ((MWXmlMapping) o1).firstMappedXmlField();
				MWXmlField xmlField2 = ((MWXmlMapping) o2).firstMappedXmlField();
				
				return MWXmlField.compareSchemaOrder(xmlField1, xmlField2);
			}
		};
	}
	
	protected XMLSchemaReference runtimeSchemaReference() {
		MWSchemaContextComponent context = this.getSchemaContext();
			
		if (context != null) {
			XMLSchemaReference runtimeSchemaRef = context.getSchema().runtimeSchemaReference();
			
			String runtimeSchemaContext = "";
			
			for (Iterator stream = context.namedComponentChain(); stream.hasNext(); ) {
				runtimeSchemaContext = "/" + ((MWNamedSchemaComponent) stream.next()).qName() + runtimeSchemaContext;
			}
			
			runtimeSchemaRef.setSchemaContext(runtimeSchemaContext);
			
			if (context instanceof MWComplexTypeDefinition) {
				runtimeSchemaRef.setType(XMLSchemaReference.COMPLEX_TYPE);
			}
			else if (context instanceof MWSimpleTypeDefinition) {
				runtimeSchemaRef.setType(XMLSchemaReference.SIMPLE_TYPE);
			}
			else if (context instanceof MWElementDeclaration) {
				runtimeSchemaRef.setType(XMLSchemaReference.ELEMENT);
			}
			else if (context instanceof MWModelGroupDefinition) {
				runtimeSchemaRef.setType(XMLSchemaReference.GROUP);
			}
			
			return runtimeSchemaRef;
		}
		else {
			return null;
		}
	}
	
	protected NamespaceResolver runtimeNamespaceResolver() {
		if (this.getSchemaContext() != null) {
			return this.getSchemaContext().getSchema().runtimeNamespaceResolver();
		}
		else {
			return null;
		}
	}
	
	public abstract AbstractDirectMapping buildDefaultRuntimeDirectMapping();
	
	public abstract AbstractCompositeDirectCollectionMapping buildDefaultRuntimeDirectCollectionMapping();
	
	public abstract AbstractCompositeObjectMapping buildDefaultRuntimeCompositeObjectMapping();
	
	public abstract AbstractCompositeCollectionMapping buildDefaultRuntimeCompositeCollectionMapping();
	
	public abstract AbstractTransformationMapping buildDefaultRuntimeTransformationMapping();
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWXmlDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWMappingDescriptor.class);

		XMLCompositeObjectMapping schemaContextHandleMapping = new XMLCompositeObjectMapping();
		schemaContextHandleMapping.setAttributeName("schemaContextHandle");
		schemaContextHandleMapping.setGetMethodName("getSchemaContextHandleForTopLink");
		schemaContextHandleMapping.setSetMethodName("setSchemaContextHandleForTopLink");
		schemaContextHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		schemaContextHandleMapping.setXPath("schema-context-handle");
		descriptor.addMapping(schemaContextHandleMapping);
				
		XMLCompositeObjectMapping defaultRootElementHandleMapping = new XMLCompositeObjectMapping();
		defaultRootElementHandleMapping.setAttributeName("defaultRootElementHandle");
		defaultRootElementHandleMapping.setGetMethodName("getDefaultRootElementHandleForTopLink");
		defaultRootElementHandleMapping.setSetMethodName("setDefaultRootElementHandleForTopLink");
		defaultRootElementHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		defaultRootElementHandleMapping.setXPath("default-root-element-handle");
		descriptor.addMapping(defaultRootElementHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWNamedSchemaComponentHandle getSchemaContextHandleForTopLink() {
		return (this.schemaContextHandle.getComponent() == null) ? null : this.schemaContextHandle;
	}
	private void setSchemaContextHandleForTopLink(MWNamedSchemaComponentHandle handle) {
		NodeReferenceScrubber scrubber = this.buildSchemaContextScrubber();
		this.schemaContextHandle = ((handle == null) ? new MWNamedSchemaComponentHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWNamedSchemaComponentHandle getDefaultRootElementHandleForTopLink() {
		return (this.defaultRootElementHandle.getComponent() == null) ? null : this.defaultRootElementHandle;
	}
	private void setDefaultRootElementHandleForTopLink(MWNamedSchemaComponentHandle handle) {
		NodeReferenceScrubber scrubber = this.buildDefaultRootElementScrubber();
		this.defaultRootElementHandle = ((handle == null) ? new MWNamedSchemaComponentHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	public abstract MWXmlPrimaryKeyPolicy primaryKeyPolicy();

}
