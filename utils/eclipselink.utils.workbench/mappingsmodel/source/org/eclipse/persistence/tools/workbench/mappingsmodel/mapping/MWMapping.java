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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractAnyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractCompositeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWOXMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXpathedMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * Discussion of morphing mappings:<ol>
 *
 * <li> Any subclass can be sent one of the asMW___Mapping() methods.
 * The default implementation is defined here. They are overridden where
 * appropriate - when no transmogrification is needed and the mapping simply
 * returns itself.
 * 
 * <li> Each concrete subclass must override the method initializeOn(MWMapping newMapping)
 * and call the appropriate initializeFromMW___Mapping(MW___Mapping oldMapping).
 * [This is call double-dispatching.]
 * We could have overloaded the same method name (e.g. initializeFrom(MW___Mapping oldMapping))
 * but the resulting confusion is not worth it. "Upcasting" just makes this really fuzzy....
 *
 * <il> If necessary, each subclass (concrete and abstract) should override
 * the initializeFromMW___Mapping(MW___Mapping oldMapping) method. This override
 * should first call super.initializeFromMW___Mapping(MW___Mapping oldMapping); then
 * it should initialize only the properties that are defined by it that have
 * corresponding properties in the oldMapping.
 *
 * </ol>
 */
public abstract class MWMapping extends MWModel implements MWQueryable {
	
		
	private MWAttributeHandle attributeHandle;
		public final static String ATTRIBUTE_PROPERTY = "attribute";
	
	private volatile String name;
		public final static String NAME_PROPERTY = "name";

	private volatile boolean inherited;
		public final static String INHERITED_PROPERTY= "inherited";

	private volatile boolean readOnly;
		public final static String READ_ONLY_PROPERTY= "readOnly";

	private volatile boolean usesMethodAccessing;	
		public final static String USES_METHOD_ACCESSING_PROPERTY = "usesMethodAccessing";
	
	private MWMethodHandle getMethodHandle;
		public final static String GET_METHOD_PROPERTY = "getMethod";
		
	private MWMethodHandle setMethodHandle;
		public final static String SET_METHOD_PROPERTY = "setMethod";
	

	// used for backward compatibility
	protected Map legacyValuesMap;
	

	// ********** constructors **********

	/** Default constructor - for TopLink use only */
	protected MWMapping() {
		super();
	}

	protected MWMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent);
		initialize(attribute, name);
	}


	// **************** Initialization ***************
	
	/**
	 * initialize transient state
	 */
	protected void initialize() {
		super.initialize();
		this.legacyValuesMap = new HashMap();
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.attributeHandle = new MWAttributeHandle(this, this.buildAttributeScrubber());
		this.getMethodHandle = new MWMethodHandle(this, this.buildGetMethodScrubber());
		this.setMethodHandle = new MWMethodHandle(this, this.buildSetMethodScrubber());
	}
	
	protected void initialize(MWClassAttribute attribute, String mappingName) {
		this.attributeHandle.setAttribute(attribute);
		if (getProject().getDefaultsPolicy().isMethodAccessing()) {
			this.setUsesMethodAccessing(true);
		}
		this.name = mappingName;
	}

	
	// **************** Accessors ***************

	public MWClassAttribute getInstanceVariable() {
		return this.attributeHandle.getAttribute();
	}
	
	public void setInstanceVariable(MWClassAttribute newValue) {
		Object oldValue = getInstanceVariable();
		this.attributeHandle.setAttribute(newValue);
		firePropertyChanged(ATTRIBUTE_PROPERTY, oldValue, newValue);
	}

	public String getName(){	
		return this.name;
	}
	
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}

	public boolean isInherited() {
		return this.inherited;
	}	
	
	public void setInherited(boolean inherited) {
		boolean old = this.inherited;
		this.inherited = inherited;
		this.firePropertyChanged(INHERITED_PROPERTY, old, inherited);
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		boolean old = this.readOnly;
		this.readOnly = readOnly;
		this.firePropertyChanged(READ_ONLY_PROPERTY, old, readOnly);
	}
	
	public boolean usesMethodAccessing() {
		return this.usesMethodAccessing;
	}
	
	public void setUsesMethodAccessing(boolean usesMethodAccessing) {
		boolean old = this.usesMethodAccessing;
		this.usesMethodAccessing = usesMethodAccessing;
		
        if (old != usesMethodAccessing) {
			this.firePropertyChanged(USES_METHOD_ACCESSING_PROPERTY, old, usesMethodAccessing);
    		if (usesMethodAccessing) {
    			if (this.getGetMethod() == null) {
    				this.setGetMethod((this.getInstanceVariable().getGetMethod() != null) ?
    							this.getInstanceVariable().getGetMethod()
    						:
    							this.getInstanceVariable().guessGetMethod()
    						);
    			}
    			if (this.getSetMethod() == null) {
    				this.setSetMethod((this.getInstanceVariable().getSetMethod() != null) ?
    							this.getInstanceVariable().getSetMethod()
    						:
    							this.getInstanceVariable().guessSetMethod()
    						);
    			}
    		} else {
    			this.setGetMethod(null);
    			this.setSetMethod(null);
    		}
        }
	}
	
	public MWMethod getGetMethod() {
		return this.getMethodHandle.getMethod();
	}
	
	public void setGetMethod(MWMethod getMethod) {
		MWMethod old = this.getGetMethod();
		this.getMethodHandle.setMethod(getMethod);
		this.firePropertyChanged(GET_METHOD_PROPERTY, old, getMethod);
	}
	
	public Iterator candidateGetMethods() {
		return new CompositeIterator(this.candidateGetMethodIterators());
	}
	
	private Iterator candidateGetMethodIterators() {
		return new TransformationIterator(this.declaringClassesForCandidateAccessorMethods()) {
			protected Object transform(Object next) {
				return ((MWClass) next).candidateTopLinkGetMethods();
			}
		};
	}
	
	public MWMethod getSetMethod() {
		return this.setMethodHandle.getMethod();
	}
	
	public void setSetMethod(MWMethod setMethod) {
		MWMethod old = this.getSetMethod();
		this.setMethodHandle.setMethod(setMethod);
		this.firePropertyChanged(SET_METHOD_PROPERTY, old, setMethod);
	}
	
	public Iterator candidateSetMethods() {
		return new CompositeIterator(this.candidateSetMethodIterators());
	}
	
	private Iterator candidateSetMethodIterators() {
		return new TransformationIterator(this.declaringClassesForCandidateAccessorMethods()) {
			protected Object transform(Object next) {
				return ((MWClass) next).candidateTopLinkSetMethods();
			}
		};
	}
	
	private Iterator declaringClassesForCandidateAccessorMethods() {
		return this.getParentDescriptor().getMWClass().lineageTo(this.getInstanceVariable().getDeclaringType());
	}
	
	/**
	 * Some mappings automatically have query keys associated with them.
	 * For example, a direct-to-field mapping called "firstName" has a
	 * query key named "firstName" that maps to the direct field
	 * in that mapping.
	 * 
	 * If the mapping doesn't have an auto-generated query key, or if it
	 * is irrelevant for that type of mapping, this method returns null.
	 */
	public MWQueryKey getAutoGeneratedQueryKey() {
		return null;
	}


	public boolean maintainsBidirectionalRelationship() {
		return false;
	}
	
	public MWMapping getRelationshipPartnerMapping() {
		return null;
	}

	public boolean isValidRelationshipPartner() {
		return false;
	}

	public boolean isCollectionMapping(){
		return false;
	}

	public boolean isOneToOneMapping(){
		return false;
	}

	public boolean isReferenceMapping(){
		return false;
	}

	public boolean isTableReferenceMapping(){
		return false;
	}
	
	public void implementorsChangedFor(MWInterfaceDescriptor descriptor) {
		//this is overridden by MWVariableOneToOneMapping
	}


	// ************ aggregate support ************
	
	public final Iterator aggregateFieldNameGenerators() {
		return buildAggregateFieldNameGenerators().iterator();		
	}
	
	protected Collection buildAggregateFieldNameGenerators() {
		return new ArrayList();
	}

	public void parentDescriptorMorphedToAggregate() {
		// Method to be implemented by subclasses that need to do stuff like null out fields
		// when they are morphed to aggregate.   jbb
	}


	// **************** Containment hierarchy ***************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.attributeHandle);
		children.add(this.getMethodHandle);
		children.add(this.setMethodHandle);
	}

	private NodeReferenceScrubber buildAttributeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMapping.this.attributeRemoved();
			}
			public String toString() {
				return "MWMapping.buildAttributeScrubber()";
			}
		};
	}

	void attributeRemoved() {
		// we don't really need to clear the attribute;
		// and some listeners would really appreciate it if we kept it around
		// this.attributeHandle.setAttribute(null);
		this.getParentDescriptor().removeMapping(this);
	}

	private NodeReferenceScrubber buildGetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMapping.this.setGetMethod(null);
			}
			public String toString() {
				return "MWMapping.buildGetMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildSetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMapping.this.setSetMethod(null);
			}
			public String toString() {
				return "MWMapping.buildSetMethodScrubber()";
			}
		};
	}


	// **************** Convenience methods ***************

	public MWMappingDescriptor getParentDescriptor() {
		return (MWMappingDescriptor) this.getParent();
	}

		
	// ***************** Morphing ***************
	
	private MWMappingFactory mappingFactory() {
		return getParentDescriptor().mappingFactory();
	}
	
	/**
	 * IMPORTANT:
	 * See MWRMapping class comment concerning asBldr___Mapping() methods.
	 */
	public MWDirectMapping asMWDirectMapping() {
		MWDirectMapping newMapping = 
			mappingFactory().createDirectMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}

	public MWXmlFragmentMapping asMWXmlFragmentMapping() {
		MWXmlFragmentMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlFragmentMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}

	public MWXmlFragmentCollectionMapping asMWXmlFragmentCollectionMapping() {
		MWXmlFragmentCollectionMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlFragmentCollectionMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}

	public MWXmlObjectReferenceMapping asMWXmlObjectReferenceMapping() {
		MWXmlObjectReferenceMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlObjectReferenceMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}

	public MWXmlCollectionReferenceMapping asMWXmlCollectionReferenceMapping() {
		MWXmlCollectionReferenceMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createXmlCollectionReferenceMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}

	public MWDirectMapping asMWObjectTypeMapping() {
		MWDirectMapping newMapping = asMWDirectMapping();
		newMapping.setObjectTypeConverter();
		return newMapping;
	}

	public MWDirectMapping asMWSerializedMapping() {		
		MWDirectMapping newMapping = asMWDirectMapping();
		newMapping.setSerializedObjectConverter();
		return newMapping;
	}
	
	public MWDirectMapping asMWTypeConversionMapping() {
		MWDirectMapping newMapping = asMWDirectMapping();
		newMapping.setTypeConversionConverter();
		return newMapping;
	}

	public MWDirectToXmlTypeMapping asMWDirectToXmlTypeMapping() {
		MWDirectToXmlTypeMapping newMapping = 
			((MWRelationalMappingFactory) mappingFactory()).createDirectToXmlTypeMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWDirectCollectionMapping asMWDirectCollectionMapping() {
		MWDirectCollectionMapping newMapping = 
			mappingFactory().createDirectCollectionMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn((MWMapping) newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith((MWMapping) newMapping);		// this will set the descriptor
		return newMapping;
	}

	public MWDirectMapMapping asMWDirectMapMapping() {
		MWDirectMapMapping newMapping =
			mappingFactory().createDirectMapMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn((MWMapping) newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith((MWMapping) newMapping);		// this will set the descriptor
		return newMapping;
	}

	public MWCompositeObjectMapping asMWCompositeObjectMapping() {
		MWCompositeObjectMapping newMapping = 
			((MWXmlMappingFactory) mappingFactory()).createCompositeObjectMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}
	
	public MWCompositeCollectionMapping asMWCompositeCollectionMapping() {
		MWCompositeCollectionMapping newMapping = 
			((MWXmlMappingFactory) mappingFactory()).createCompositeCollectionMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}
	
	public MWAnyObjectMapping asMWAnyObjectMapping() {
		MWAnyObjectMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyObjectMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}
	
	public MWAnyCollectionMapping asMWAnyCollectionMapping() {
		MWAnyCollectionMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyCollectionMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}
	
	public MWAnyAttributeMapping asMWAnyAttributeMapping() {
		MWAnyAttributeMapping newMapping = 
			((MWOXMappingFactory) this.mappingFactory()).createAnyAttributeMapping(this.getParentDescriptor(), this.getInstanceVariable(), this.getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);
		return newMapping;
	}

	public MWVariableOneToOneMapping asMWVariableOneToOneMapping() {
		MWVariableOneToOneMapping newMapping = 
			((MWRelationalMappingFactory) mappingFactory()).createVariableOneToOneMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWTransformationMapping asMWTransformationMapping() {
		MWTransformationMapping newMapping = 
			mappingFactory().createTransformationMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWAggregateMapping asMWAggregateMapping() {
		MWAggregateMapping newMapping =
			((MWRelationalMappingFactory) mappingFactory()).createAggregateMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWOneToOneMapping asMWOneToOneMapping() {
		MWOneToOneMapping newMapping = 
			((MWRelationalMappingFactory) mappingFactory()).createOneToOneMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWOneToManyMapping asMWOneToManyMapping() {
		MWOneToManyMapping newMapping =
			((MWRelationalMappingFactory) mappingFactory()).createOneToManyMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWManyToManyMapping asMWManyToManyMapping() {
		MWManyToManyMapping newMapping = 
			((MWRelationalMappingFactory) mappingFactory()).createManyToManyMapping(getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}
	
	public MWEisOneToManyMapping asMWEisOneToManyMapping() {
		MWEisOneToManyMapping newMapping = 
			((MWEisMappingFactory) mappingFactory()).createEisOneToManyMapping((MWEisDescriptor) getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}

	public MWEisOneToOneMapping asMWEisOneToOneMapping() {
		MWEisOneToOneMapping newMapping = 
			((MWEisMappingFactory) mappingFactory()).createEisOneToOneMapping((MWEisDescriptor) getParentDescriptor(), getInstanceVariable(), getName());
		this.initializeOn(newMapping);
		newMapping.setChildBackpointers();
		this.replaceWith(newMapping);		// this will set the descriptor
		return newMapping;
	}

	/**
	 * IMPORTANT:  See MWRMapping class comment.
	 * Subclasses should override this method to call the
	 * appropriate initializeFromMW___Mapping() method.
	 */
	protected abstract void initializeOn(MWMapping newMapping);
	
	
	protected void replaceWith(MWMapping newMapping) {
		this.getParentDescriptor().replaceMapping(this, newMapping);
	}
	
	/**
	 * IMPORTANT: See MWRMapping class comment concerning
	 * initializeFromMW___Mapping() methods.
	 * 
	 * Note that these methods climb up the hierarchy tree.
	 */
	protected void initializeFromMWMapping(MWMapping oldMapping)  {
		this.setUsesMethodAccessing(oldMapping.usesMethodAccessing());		
		this.setGetMethod(oldMapping.getGetMethod());
		this.setSetMethod(oldMapping.getSetMethod());
		this.setInherited(oldMapping.isInherited());
		this.setReadOnly(oldMapping.isReadOnly());
	}

	protected void initializeFromMWDirectMapping(MWDirectMapping oldMapping) {
		this.initializeFromMWConverterMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	protected void initializeFromMWRelationalDirectMapping(MWRelationalDirectMapping oldMapping) {
		this.initializeFromMWMapping(oldMapping);
	}
	
	public void initializeFromMWDirectToFieldMapping(MWDirectToFieldMapping oldMapping) {
		this.initializeFromMWRelationalDirectMapping(oldMapping);
	}

	public void initializeFromMWDirectToXmlTypeMapping(MWDirectToXmlTypeMapping oldMapping) {
		this.initializeFromMWDirectMapping(oldMapping);
	}
	
	public void initializeFromMWXmlDirectMapping(MWXmlDirectMapping oldMapping) {
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWDirectMapping(oldMapping);
	}
	
	public void initializeFromMWXmlFragmentMapping(MWXmlFragmentMapping oldMapping) {
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWDirectMapping(oldMapping);
	}
	
	public void initializeFromMWXmlFragmentCollectionMapping(MWXmlFragmentCollectionMapping oldMapping) {
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWDirectCollectionMapping(oldMapping);
	}

	protected void initializeFromMWDirectCollectionMapping(MWDirectCollectionMapping oldMapping) {
		this.initializeFromMWDirectContainerMapping((MWDirectContainerMapping) oldMapping);
	}
	
	public void initializeFromMWRelationalDirectMapMapping(MWRelationalDirectMapMapping oldMapping) {
		this.initializeFromMWRelationalDirectContainerMapping(oldMapping);
		this.initializeFromMWDirectMapMapping(oldMapping);
	}
	
	protected void initializeFromMWDirectMapMapping(MWDirectMapMapping oldMapping) {
		this.initializeFromMWDirectContainerMapping((MWDirectContainerMapping) oldMapping);
	}
	
	protected void initializeFromMWDirectContainerMapping(MWDirectContainerMapping oldMapping) {
		this.initializeFromMWConverterMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	public void initializeFromMWRelationalDirectCollectionMapping(MWRelationalDirectCollectionMapping oldMapping) {
		this.initializeFromMWRelationalDirectContainerMapping(oldMapping);
		this.initializeFromMWDirectCollectionMapping(oldMapping);
	}
	
	public void initializeFromMWRelationalDirectContainerMapping(MWRelationalDirectContainerMapping oldMapping) {
		this.initializeFromMWIndirectableContainerMapping(oldMapping);
	}
	
	public void initializeFromMWXmlDirectCollectionMapping(MWXmlDirectCollectionMapping oldMapping) {
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWDirectCollectionMapping(oldMapping);
	}
	
	protected void initializeFromMWAbstractAnyMapping(MWAbstractAnyMapping oldMapping) {
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	public void initializeFromMWAnyObjectMapping(MWAnyObjectMapping oldMapping) {
		this.initializeFromMWAbstractAnyMapping(oldMapping);
	}
	
	public void initializeFromMWAnyCollectionMapping(MWAnyCollectionMapping oldMapping) {
		this.initializeFromMWAbstractAnyMapping(oldMapping);
	}
	
	public void initializeFromMWAnyAttributeMapping(MWAnyAttributeMapping oldMapping) {
		this.initializeFromMWAbstractAnyMapping(oldMapping);
	}

	protected void initializeFromMWAbstractCompositeMapping(MWAbstractCompositeMapping oldMapping) {
		this.initializeFromMWReferenceObjectMapping(oldMapping);
		this.initializeFromMWXpathedMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	public void initializeFromMWCompositeObjectMapping(MWCompositeObjectMapping oldMapping) {
		this.initializeFromMWAbstractCompositeMapping(oldMapping);
	}
	
	public void initializeFromMWCompositeCollectionMapping(MWCompositeCollectionMapping oldMapping) {
		this.initializeFromMWAbstractCompositeMapping(oldMapping);
	}
	
	public void initializeFromMWVariableOneToOneMapping(MWVariableOneToOneMapping oldMapping) {
		this.initializeFromMWAbstractReferenceMapping(oldMapping);
	}
	
	protected void initializeFromMWAbstractReferenceMapping(MWAbstractReferenceMapping oldMapping) {
		this.initializeFromMWIndirectableMapping(oldMapping);
		this.initializeFromMWReferenceMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	protected void initializeFromMWAbstractTableReferenceMapping(MWAbstractTableReferenceMapping oldMapping) {
		this.initializeFromMWAbstractReferenceMapping(oldMapping);
	}
	
	protected void initializeFromMWCollectionMapping(MWCollectionMapping oldMapping) {
		this.initializeFromMWIndirectableContainerMapping(oldMapping);
		this.initializeFromMWAbstractTableReferenceMapping(oldMapping);
	}
	
	public void initializeFromMWManyToManyMapping(MWManyToManyMapping oldMapping) {
		this.initializeFromMWCollectionMapping(oldMapping);
	}
	
	public void initializeFromMWOneToManyMapping(MWOneToManyMapping oldMapping) {
		this.initializeFromMWCollectionMapping(oldMapping);
	}
	
	public void initializeFromMWOneToOneMapping(MWOneToOneMapping oldMapping) {
		this.initializeFromMWAbstractTableReferenceMapping(oldMapping);
	}
	
	protected void initializeFromMWEisReferenceMapping(MWEisReferenceMapping oldMapping) {
		this.initializeFromMWAbstractReferenceMapping(oldMapping);
	}
	
	public void initializeFromMWEisOneToManyMapping(MWEisOneToManyMapping oldMapping) {
		this.initializeFromMWIndirectableContainerMapping(oldMapping);
		this.initializeFromMWEisReferenceMapping(oldMapping);
	}
	
	public void initializeFromMWEisOneToOneMapping(MWEisOneToOneMapping oldMapping) {
		this.initializeFromMWEisReferenceMapping(oldMapping);
	}
	
	public void initializeFromMWAggregateMapping(MWAggregateMapping oldMapping) {
		this.initializeFromMWReferenceObjectMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	public void initializeFromMWTransformationMapping(MWTransformationMapping oldMapping) {
		this.initializeFromMWIndirectableMapping(oldMapping);
		this.initializeFromMWMapping(oldMapping);
	}
	
	protected void initializeFromMWConverterMapping(MWConverterMapping oldMapping) {
		// do nothing
	}
	
	protected void initializeFromMWIndirectableMapping(MWIndirectableMapping oldMapping) {
		// do nothing
	}
	
	protected void initializeFromMWIndirectableContainerMapping(MWIndirectableContainerMapping oldMapping) {
		this.initializeFromMWIndirectableMapping(oldMapping);
	}
	
	protected void initializeFromMWReferenceObjectMapping(MWReferenceObjectMapping oldMapping) {
		// do nothing
	}
	
	protected void initializeFromMWReferenceMapping(MWReferenceMapping oldMapping) {
		this.initializeFromMWReferenceObjectMapping(oldMapping);
	}
	
	public void initializeFromMWXmlCollectionReferenceMapping(MWXmlCollectionReferenceMapping oldMapping) {
		
	}
	
	public void initializeFromMWXmlObjectReferenceMapping(MWXmlObjectReferenceMapping oldMapping) {
		
	}
	
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		// do nothing
	}
	
    
    // ********** MWQueryable implementation **********
    
    public boolean allowsChildren() {
        return false;
    }
    
    public boolean allowsOuterJoin() {
        return false;
    }
    
    public boolean isTraversableForBatchReadAttribute() {
        return false;
    }
    public boolean isValidForBatchReadAttribute() {
        return false;
    }
    
    public boolean isLeaf(Filter queryableFilter) {
        return true;
    }

    public MWQueryable subQueryableElementAt(int index, Filter queryableFilter) {
        throw new UnsupportedOperationException();
    }

    public List subQueryableElements(Filter queryableFilter) {
        return Collections.EMPTY_LIST;
    }
    
    public boolean usesAnyOf() {
        return false;
    }
    
    public boolean isTraversableForJoinedAttribute() {
        return false;
    }
    public boolean isValidForJoinedAttribute() {
        return false;
    }
    
    public boolean isTraversableForReadAllQueryOrderable() {
        return false;
    }
    public boolean isValidForReadAllQueryOrderable() {
        return false;
    }
    
    public boolean isTraversableForReportQueryAttribute() {
        return false;
    }
    public boolean isValidForReportQueryAttribute() {
        return false;
    }
    
    public boolean isTraversableForQueryExpression() {
        return false;
    }    
    public boolean isValidForQueryExpression() {
        return false;
    }
    
    public String iconKey() {
        throw new UnsupportedOperationException();
    }

    
	// **************** Source Code Generation ***************

	/**
	 * Used for code gen.
	 * Default behavior is to defer to the instance variable,
	 * but some mappings (see MWOneToManyMapping)
	 * have particular additional information to impart.
	 */
	public MWMethodCodeGenPolicy accessorCodeGenPolicy(MWMethod accessor, MWClassCodeGenPolicy classCodeGenPolicy) {
		return getInstanceVariable().accessorCodeGenPolicy(accessor, classCodeGenPolicy);
	}

	/**
	 * Used for code gen.
	 * Default behavior is to defer to the instance variable,
	 * but some mappings (see MWAggregateMapping, MWOneToOneMapping)
	 * have particular initial value requirements.
	 */
	public String initialValue(MWClassCodeGenPolicy classCodeGenPolicy) {
		return getInstanceVariable().initialValueSourceCode(classCodeGenPolicy);
	}


	//************** Problem Handling ***************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkGetMethod(currentProblems);	
		this.checkSetMethod(currentProblems);	
		this.checkMethods(currentProblems);	
		this.checkWriteLockField(currentProblems);
		this.checkMappableAttribute(currentProblems);
	}

	private void checkMappableAttribute(List currentProblems) {
		if (this.getInstanceVariable() == null) {
			return;
		}
		if (!this.getInstanceVariable().isMappable()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_NO_LONGER_MAPPABLE));
		}
	}
	
	private void checkGetMethod(List currentProblems) {
		if (this.getGetMethod() == null){
			return;
		}
		if (this.getGetMethod().methodParametersSize() != 0) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_INVALID_GET_METHOD));
		}
	}

	private void checkSetMethod(List currentProblems) {
		if (this.getSetMethod() == null){
			return;
		}
		if (this.getSetMethod().methodParametersSize() != 1) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_INVALID_SET_METHOD));
		}
	}

	private void checkMethods(List currentProblems) {
		if ( ! this.usesMethodAccessing()) {
			return;
		}
		if ((this.getGetMethod() == null) || (this.getSetMethod() == null)) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_METHOD_ACCESSORS_NOT_SPECIFIED));
		}
	}

	private void checkWriteLockField(List currentProblems) {
		// Pass if my referenced fields do not interfere with the write 
		// lock field (must be read only if referenced).
		MWLockingPolicy policy = this.getParentDescriptor().getLockingPolicy();

		Collection writtenFields = new ArrayList();
		this.addWrittenFieldsTo(writtenFields);
		for (Iterator stream = writtenFields.iterator(); stream.hasNext(); ) {
			MWDataField field = (MWDataField) stream.next();
			//CR #2098  If not stored in cache, read only is not required.
			if ((field == policy.getVersionLockField()) && policy.shouldStoreVersionInCache()) {
				if ( ! this.isReadOnly()) {
					currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_WRITE_LOCK_FIELD_NOT_READ_ONLY));
				}
			}
		}
	}
	
	/**
	 * Add all fields that are written by this mapping in this descriptor
	 */
	public abstract void addWrittenFieldsTo(Collection writtenFields);

	
	// ************* Automap Support ****************
	
	/**
	 * attempt to automap the mapping
	 */
	public void automap() {
		// the default is to do nothing
	}

	public void addColumnlessDirectMappingTo(Collection columnlessDirectMappings) {
		// the default is to do nothing
	}


	// ********** Runtime Conversion **********
	
	public DatabaseMapping runtimeMapping() {
		DatabaseMapping mapping = buildRuntimeMapping();
		mapping.setAttributeName(this.getInstanceVariable().getName());
    	
		if (this.usesMethodAccessing()) {
			if (getGetMethod() != null) {
				mapping.setGetMethodName(this.getGetMethod().getName());
			}		
			if (getSetMethod() != null) {
				mapping.setSetMethodName(this.getSetMethod().getName());
			}
		}
    	
		mapping.setIsReadOnly(this.isReadOnly()); 
		
		return mapping;  	
	}
		
	protected abstract DatabaseMapping buildRuntimeMapping();
		

	// ********** display methods **********

	/**
	 * add the attribute type, making it easier to map in the UI, e.g.
	 *     attributeName : foo.bar.Type
	 */
	public String nameWithType() {
		return (this.getInstanceVariable() != null) ?
			this.getInstanceVariable().nameWithType()
		:
			this.getName();
	}
	
	/**
	 * add the attribute type, making it easier to map in the UI, e.g.
	 *     attributeName : Type
	 */
	public String nameWithShortType() {
		return (this.getInstanceVariable() != null) ?
			this.getInstanceVariable().nameWithShortType()
		:
			this.getName();
	}
	
	public String displayString() {
		return this.getName();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.getName());
	}

	
 	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWMapping.class);
	
		org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		// *** relational mappings ***
		ip.addClassIndicator(MWDirectToFieldMapping.class, "direct-to-field");
		ip.addClassIndicator(MWDirectToXmlTypeMapping.class, "direct-to-xml-type");
		ip.addClassIndicator(MWRelationalDirectCollectionMapping.class, "relational-direct-collection");
		ip.addClassIndicator(MWManyToManyMapping.class, "many-to-many");
		ip.addClassIndicator(MWOneToManyMapping.class, "one-to-many");
		ip.addClassIndicator(MWOneToOneMapping.class, "one-to-one");
		ip.addClassIndicator(MWVariableOneToOneMapping.class, "variable-one-to-one");
		ip.addClassIndicator(MWRelationalTransformationMapping.class, "relational-transformation");
		ip.addClassIndicator(MWAggregateMapping.class, "aggregate");
		ip.addClassIndicator(MWRelationalDirectMapMapping.class, "relational-direct-map");
		// *** xml mappings ***
		ip.addClassIndicator(MWXmlDirectMapping.class, "xml-direct");
		ip.addClassIndicator(MWXmlDirectCollectionMapping.class, "xml-direct-collection");
		ip.addClassIndicator(MWAnyAttributeMapping.class, "any-attribute");
		ip.addClassIndicator(MWAnyObjectMapping.class, "any-object");
		ip.addClassIndicator(MWAnyCollectionMapping.class, "any-collection");
		ip.addClassIndicator(MWCompositeObjectMapping.class, "composite-object");
		ip.addClassIndicator(MWCompositeCollectionMapping.class, "composite-collection");
		ip.addClassIndicator(MWXmlTransformationMapping.class, "xml-transformation");
		ip.addClassIndicator(MWEisOneToOneMapping.class, "eis-one-to-one");
		ip.addClassIndicator(MWEisOneToManyMapping.class, "eis-one-to-many");
		ip.addClassIndicator(MWXmlFragmentMapping.class, "xml-fragment");
		ip.addClassIndicator(MWXmlFragmentCollectionMapping.class, "xml-fragment-collection");
		ip.addClassIndicator(MWXmlObjectReferenceMapping.class, "xml-object-reference");
		ip.addClassIndicator(MWXmlCollectionReferenceMapping.class, "xml-collection-reference");
						
		descriptor.addDirectMapping("name", "name/text()");

		((XMLDirectMapping) descriptor.addDirectMapping("comment", "comment/text()")).setNullValue("");

		((XMLDirectMapping) descriptor.addDirectMapping("inherited", "inherited/text()")).setNullValue(Boolean.FALSE);
		
		((XMLDirectMapping) descriptor.addDirectMapping("readOnly", "read-only/text()")).setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping attributeHandleMapping = new XMLCompositeObjectMapping();
		attributeHandleMapping.setAttributeName("attributeHandle");
		attributeHandleMapping.setReferenceClass(MWAttributeHandle.class);
		attributeHandleMapping.setSetMethodName("setAttributeHandleForTopLink");
		attributeHandleMapping.setGetMethodName("getAttributeHandleForTopLink");
		attributeHandleMapping.setXPath("attribute-handle");
		descriptor.addMapping(attributeHandleMapping);

		((XMLDirectMapping) descriptor.addDirectMapping("usesMethodAccessing", "uses-method-accessing/text()")).setNullValue(Boolean.FALSE);
	
		XMLCompositeObjectMapping getMethodHandleMapping = new XMLCompositeObjectMapping();
		getMethodHandleMapping.setAttributeName("getMethodHandle");
		getMethodHandleMapping.setSetMethodName("setGetMethodHandleForTopLink");
		getMethodHandleMapping.setGetMethodName("getGetMethodHandleForTopLink");
		getMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		getMethodHandleMapping.setXPath("get-method-handle");
		descriptor.addMapping(getMethodHandleMapping);
	
		XMLCompositeObjectMapping setMethodHandleMapping = new XMLCompositeObjectMapping();
		setMethodHandleMapping.setAttributeName("setMethodHandle");
		setMethodHandleMapping.setSetMethodName("setSetMethodHandleForTopLink");
		setMethodHandleMapping.setGetMethodName("getSetMethodHandleForTopLink");
		setMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		setMethodHandleMapping.setXPath("set-method-handle");
		descriptor.addMapping(setMethodHandleMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWAttributeHandle getAttributeHandleForTopLink() {
		return (this.attributeHandle.getAttribute() == null) ? null : this.attributeHandle;
	}
	private void setAttributeHandleForTopLink(MWAttributeHandle attributeHandle) {
		NodeReferenceScrubber scrubber = this.buildAttributeScrubber();
		this.attributeHandle = ((attributeHandle == null) ? new MWAttributeHandle(this, scrubber) : attributeHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getGetMethodHandleForTopLink() {
		return (this.getMethodHandle.getMethod() == null) ? null : this.getMethodHandle;
	}
	private void setGetMethodHandleForTopLink(MWMethodHandle getMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildGetMethodScrubber();
		this.getMethodHandle = ((getMethodHandle == null) ? new MWMethodHandle(this, scrubber) : getMethodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getSetMethodHandleForTopLink() {
		return (this.setMethodHandle.getMethod() == null) ? null : this.setMethodHandle;
	}
	private void setSetMethodHandleForTopLink(MWMethodHandle setMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildSetMethodScrubber();
		this.setMethodHandle = ((setMethodHandle == null) ? new MWMethodHandle(this, scrubber) : setMethodHandle.setScrubber(scrubber));
	}

}
