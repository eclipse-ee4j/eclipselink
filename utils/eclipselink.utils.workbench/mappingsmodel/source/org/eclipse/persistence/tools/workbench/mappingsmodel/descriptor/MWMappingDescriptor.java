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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.DefaultMWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

public abstract class MWMappingDescriptor
	extends MWDescriptor
{
	private Collection mappings;
		public static final String MAPPINGS_COLLECTION = "mappings";

	private Collection inheritedAttributeHandles;
		public static final String INHERITED_ATTRIBUTES_COLLECTION = "inheritedAttributes";
		private NodeReferenceScrubber inheritedAttributeScrubber;

	private volatile MWInheritancePolicy inheritancePolicy;
		public static final String INHERITANCE_POLICY_PROPERTY = "inheritancePolicy";

	private volatile MWDescriptorPolicy copyPolicy;
		public static final String COPY_POLICY_PROPERTY = "copyPolicy"; 

	private volatile MWDescriptorPolicy eventsPolicy;
		public static final String EVENTS_POLICY_PROPERTY = "eventsPolicy";

	private volatile MWDescriptorPolicy afterLoadingPolicy;
		public static final String AFTER_LOADING_POLICY_PROPERTY = "afterLoadingPolicy";

	private volatile MWDescriptorPolicy instantiationPolicy;
		public static final String INSTANTIATION_POLICY_PROPERTY = "instantiationPolicy";


	// ********** static methods **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	protected MWMappingDescriptor() {
		super();
	}

	protected MWMappingDescriptor(MWProject parent, MWClass type, String name) {
		super(parent, type, name);
	}


	// ********** Initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.mappings = new Vector();
		this.inheritedAttributeHandles = new Vector();
		this.inheritancePolicy 		= new MWNullInheritancePolicy(this);
		this.copyPolicy 			= new MWNullDescriptorPolicy(this);
		this.eventsPolicy 			= new MWNullDescriptorPolicy(this);
		this.afterLoadingPolicy		= new MWNullDescriptorPolicy(this);
		this.instantiationPolicy 	= new MWNullDescriptorPolicy(this);
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.mappings) { children.addAll(this.mappings); }
		synchronized (this.inheritedAttributeHandles) { children.addAll(this.inheritedAttributeHandles); }
		children.add(this.inheritancePolicy);
		children.add(this.copyPolicy);
		children.add(this.eventsPolicy);
		children.add(this.afterLoadingPolicy);
		children.add(this.instantiationPolicy);
	}

	private NodeReferenceScrubber inheritedAttributeScrubber() {
		if (this.inheritedAttributeScrubber == null) {
			this.inheritedAttributeScrubber = this.buildInheritedAttributeScrubber();
		}
		return this.inheritedAttributeScrubber;
	}

	private NodeReferenceScrubber buildInheritedAttributeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMappingDescriptor.this.removeInheritedAttributeHandle((MWAttributeHandle) handle);
			}
			public String toString() {
				return "MWMappingDescriptor.buildInheritedAttributeScrubber()";
			}
		};
	}

	/**
	 * Overridden from MWRDescriptor
	 */
	public MWClassRefreshPolicy buildMWClassRefreshPolicy()
	{
		return DefaultMWClassRefreshPolicy.instance();
	}

	protected MWDescriptorPolicy buildLockingPolicy() {
		return new MWNullDescriptorPolicy(this);
	}

	public boolean isReferencedBy(Collection mappingDescriptors)
	{
		for (Iterator it = mappingDescriptors.iterator(); it.hasNext(); )
		{
			for (Iterator it2 = ((MWMappingDescriptor) it.next()).mappings(); it2.hasNext(); )
			{
				MWMapping mapping = (MWMapping) it2.next();

				if (mapping.isReferenceMapping() && ((MWReferenceMapping) mapping).getReferenceDescriptor() == this)
					return true;
			}
		}

		return false;
	}

	public void addUnknownPrimaryKeyMapping(MWClassAttribute unknownPkAttribute) {
		this.addDirectMapping(unknownPkAttribute).setUsesMethodAccessing(false);
	}

	public MWQueryManager getQueryManager() {
		return getTransactionalPolicy().getQueryManager();
	}

	public MWCachingPolicy getCachingPolicy() {
		return getTransactionalPolicy().getCachingPolicy();
	}


//	 ********** CopyPolicy API**********

	private void setCopyPolicy(MWDescriptorPolicy copyPolicy) {
		Object old = this.copyPolicy;
		this.copyPolicy = copyPolicy;
		firePropertyChanged(COPY_POLICY_PROPERTY, old, this.copyPolicy);
	}

	public MWDescriptorPolicy getCopyPolicy() {
		return this.copyPolicy;
	}

	public void addCopyPolicy() throws MWAdvancedPropertyAdditionException {
		if (this.copyPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException(COPY_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setCopyPolicy(new MWDescriptorCopyPolicy(this));
	}

	public void removeCopyPolicy() throws MWAdvancedPropertyRemovalException {
		if (this.copyPolicy.isActive()) {
			this.copyPolicy.dispose();
			setCopyPolicy(new MWNullDescriptorPolicy(this));
		} else {
			throw new MWAdvancedPropertyRemovalException(COPY_POLICY_PROPERTY, "policy does not exist on descriptor");
		}
	}

	// **************** LockingPolicy API *************************************

	public MWLockingPolicy getLockingPolicy() {
		return getTransactionalPolicy().getLockingPolicy();
	}


	// **************** EventsPolicy API **************************************

	public MWDescriptorPolicy getEventsPolicy() {
		return this.eventsPolicy;
	}

	protected void setEventsPolicy(MWDescriptorPolicy eventsPolicy) {
		Object old = this.eventsPolicy;
		this.eventsPolicy = eventsPolicy;
		firePropertyChanged(EVENTS_POLICY_PROPERTY, old, this.eventsPolicy);
	}

	public void addEventsPolicy() throws MWAdvancedPropertyAdditionException {
		if (this.eventsPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException(EVENTS_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setEventsPolicy(new MWDescriptorEventsPolicy(this));
	}

	public void removeEventsPolicy() throws MWAdvancedPropertyRemovalException {
		if (this.eventsPolicy.isActive()) {
			this.eventsPolicy.dispose();
			setEventsPolicy(new MWNullDescriptorPolicy(this));
		} else {
			throw new MWAdvancedPropertyRemovalException(EVENTS_POLICY_PROPERTY, "policy does not exist on descriptor");
		}
	}



// ********** AfterLoadingPolicy API**********

	private void setAfterLoadingPolicy(MWDescriptorPolicy afterLoadingPolicy) {
		Object old = this.afterLoadingPolicy;
		this.afterLoadingPolicy = afterLoadingPolicy;
		firePropertyChanged(AFTER_LOADING_POLICY_PROPERTY, old, this.afterLoadingPolicy);
	}

	public MWDescriptorPolicy getAfterLoadingPolicy() {
		return this.afterLoadingPolicy;
	}

	public void addAfterLoadingPolicy() throws MWAdvancedPropertyAdditionException {
		if (this.afterLoadingPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException(AFTER_LOADING_POLICY_PROPERTY, "Policy already exists on descriptor");
		}
		setAfterLoadingPolicy(new MWDescriptorAfterLoadingPolicy(this));
	}

	public void removeAfterLoadingPolicy() throws MWAdvancedPropertyRemovalException {
		if (this.afterLoadingPolicy.isActive()) {
			this.afterLoadingPolicy.dispose();
			setAfterLoadingPolicy(new MWNullDescriptorPolicy(this));
		} else {
			throw new MWAdvancedPropertyRemovalException(AFTER_LOADING_POLICY_PROPERTY, "policy does not exist on descriptor");
		}
	}

	// ********** InstantiationPolicy API**********

	protected void setInstantiationPolicy(MWDescriptorPolicy instantiationPolicy) {
		Object old = this.instantiationPolicy;
		this.instantiationPolicy = instantiationPolicy;
		firePropertyChanged(INSTANTIATION_POLICY_PROPERTY, old, this.instantiationPolicy);
	}

	public MWDescriptorPolicy getInstantiationPolicy() {
		return this.instantiationPolicy;
	}

	public void addInstantiationPolicy() throws MWAdvancedPropertyAdditionException {
		if (this.instantiationPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException(INSTANTIATION_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setInstantiationPolicy(new MWDescriptorInstantiationPolicy(this));
	}

	public void removeInstantiationPolicy() {
		if (this.instantiationPolicy.isActive()) {
			this.instantiationPolicy.dispose();
			setInstantiationPolicy(new MWNullDescriptorPolicy(this));
		} else {
			throw new MWAdvancedPropertyRemovalException(INSTANTIATION_POLICY_PROPERTY, "property does not exist on descriptor");
		}
	}


	public boolean supportsReturningPolicy() {
		return false;
	}

	// ********** Mapping API **********

	public MWDirectMapping addDirectMapping(MWClassAttribute attribute) {
		MWDirectMapping mapping = mappingFactory().createDirectMapping(this, attribute, attribute.getName());
		addMapping(mapping);
		return mapping;
	}

	public MWDirectCollectionMapping addDirectCollectionMapping(MWClassAttribute attribute) {
		MWDirectCollectionMapping mapping = mappingFactory().createDirectCollectionMapping(this, attribute, attribute.getName());
		this.addMapping((MWMapping) mapping);
		return mapping;
	}

	public MWDirectMapMapping addDirectMapMapping(MWClassAttribute attribute) {
		MWDirectMapMapping mapping = mappingFactory().createDirectMapMapping(this, attribute, attribute.getName());
		this.addMapping((MWMapping)mapping);
		return mapping;
	}

	public MWTransformationMapping addTransformationMapping(MWClassAttribute attribute) {
		MWTransformationMapping mapping = mappingFactory().createTransformationMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public abstract MWMappingFactory mappingFactory();

	public Iterator mappings() {
		return new CloneIterator(this.mappings);
	}

	public int mappingsSize() {
		return this.mappings.size();
	}


	public Iterator tableReferenceMappings()
	{
		return new FilteringIterator(this.mappings()) 
		{
			protected boolean accept(Object next) 
			{
				return ((MWMapping) next).isTableReferenceMapping();
			}
		};
	}

	public void removeMappingForAttribute(MWClassAttribute attribute) {
		MWMapping mapping = mappingForAttribute(attribute);

		if (mapping != null) {
			removeMapping(mapping);
		}
		else {
			removeInheritedAttribute(attribute);
		}
	}

	protected void addMapping(MWMapping mapping) {
		if (CollectionTools.contains(this.inheritedAttributes(), mapping.getInstanceVariable())) {
			mapping.setInherited(true);
		}
		addItemToCollection(mapping, this.mappings, MAPPINGS_COLLECTION);
	}

	public void removeMapping(MWMapping mapping) {
		this.removeNodeFromCollection(mapping, this.mappings, MAPPINGS_COLLECTION);
	}

	private void removeMappings(Collection c) {
		for (Iterator stream = new CloneIterator(c); stream.hasNext(); ) {
			removeMapping((MWMapping) stream.next());
		}
	}

	public MWMapping mappingForAttribute(MWClassAttribute attribute) {
		for (Iterator stream = mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (mapping.getInstanceVariable() == attribute) {
				return mapping;
			}
		}
		return null;
	}

	public MWMapping mappingNamed(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		for (Iterator stream = mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if(name.equals(mapping.getName())) {
				return mapping;
			}
		}
		return null;
	}


	//TODO add this in to the addWrittenFieldsTo() method???
	public Collection allWritableMappingsForField(MWDataField field) {
		// Answer all mappings that map to the database field whose name is aString
		Collection mappingsForField = new ArrayList();
		for (Iterator stream = mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (! mapping.isReadOnly()) {
				Collection writtenFields = new ArrayList(); 
				mapping.addWrittenFieldsTo(writtenFields);
				if (writtenFields.contains(field))
				{
					mappingsForField.add(mapping);
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
				if (inheritedMappingInThisDescriptor == null)
				{
					mappingsForField.add(nextInheritedMapping);
				}
			}
		}

		return mappingsForField;
	}


	public Collection writableMappingsForField( MWDataField field ) {
		// Answer all mappings that map to the database field

		Collection mappingsForField = new ArrayList();
		for (Iterator stream = mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			Collection writtenFields = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFields);
			if (writtenFields.contains(field)) {
				mappingsForField.add( mapping );
			}
		}
		return mappingsForField;
	}

	public Iterator attributes() {
		return new TransformationIterator(mappings()) {
			protected Object transform(Object next) {
				return ((MWMapping) next).getInstanceVariable();
			}
		};
	}


	//TODO should this remove advanced properties as well?
	//what about regular descriptor settings like sequencingPolicy?
	//should we prompt the user and make it a preference whether to remove the advanced props?
	public void unmap() {
		super.unmap();
		Collection mappingsCopy = new ArrayList(this.mappings);
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = ((MWMapping) stream.next());
			this.removeItemFromCollection(mapping, this.mappings, MAPPINGS_COLLECTION);
		}
		// performance tuning: instead of calling #nodeRemoved(Node) for every mapping that is 
		// unmapped, we just finish removing all the mappings and then call #descriptorUnmapped(Collection)
		this.getProject().descriptorUnmapped(mappingsCopy);
	}

	/**
	 * Note that these methods cascade up the hierarchy tree.
	 */
	protected void initializeFromMWMappingDescriptor(MWMappingDescriptor oldDescriptor) {
		super.initializeFromMWMappingDescriptor(oldDescriptor);

		for (Iterator i = oldDescriptor.inheritedAttributes(); i.hasNext(); ) {
			addInheritedAttribute((MWClassAttribute) i.next());
		}
		for (Iterator i = oldDescriptor.mappings(); i.hasNext(); ) {
			addMapping((MWMapping) i.next());
		}

		this.setAfterLoadingPolicy(oldDescriptor.getAfterLoadingPolicy());
		this.setCopyPolicy(oldDescriptor.getCopyPolicy());
		this.setEventsPolicy(oldDescriptor.getEventsPolicy());
		this.setInheritancePolicy(oldDescriptor.getInheritancePolicy());
		this.setInstantiationPolicy(oldDescriptor.getInstantiationPolicy());

	}


		// assumes mwClass is in hierarchy
	// mwClass can not be for java.lang.Object
	public void mapInheritedAttributesToClass(MWClass mwClass) throws ClassNotFoundException {
		Collection inheritedAttributes = new ArrayList();
		MWClass hierarchyClass = getMWClass();

		while ((hierarchyClass != mwClass) && (hierarchyClass.getSuperclass() != typeFor(java.lang.Object.class))) {
			hierarchyClass = hierarchyClass.getSuperclass();
			CollectionTools.addAll(inheritedAttributes, hierarchyClass.attributes());
		}

		for (Iterator it = inheritedAttributes.iterator(); it.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) it.next();
			MWMapping existingMapping = mappingForAttribute(attribute);
			if (existingMapping == null && !CollectionTools.contains(this.inheritedAttributes(), attribute)) {
				addInheritedAttribute(attribute);
			}
		}
	}

	public void mapInheritedAttributesToRootMinusOne() throws ClassNotFoundException {
		MWClass bldrClass = getMWClass();
		while (bldrClass.getSuperclass() != typeFor(Object.class)) {
			bldrClass = bldrClass.getSuperclass();
		}
		mapInheritedAttributesToClass(bldrClass);
	}

	public void mapInheritedAttributesToSuperclass() throws ClassNotFoundException {
		mapInheritedAttributesToClass(getMWClass().getSuperclass());
	}

	public void removeInheritedAttributes() {
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (mapping.isInherited()) {
				this.removeMapping(mapping);
			}
		}
		for (Iterator stream = this.inheritedAttributes(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}

	private Iterator inheritedAttributeHandles() {
		return new CloneIterator(this.inheritedAttributeHandles) {
			protected void remove(Object current) {
				MWMappingDescriptor.this.removeInheritedAttributeHandle((MWAttributeHandle) current);
			}
		};
	}

	void removeInheritedAttributeHandle(MWAttributeHandle handle) {
		this.inheritedAttributeHandles.remove(handle);
		this.fireItemRemoved(INHERITED_ATTRIBUTES_COLLECTION, handle.getAttribute());
	}

	public Iterator inheritedAttributes() {
		return new TransformationIterator(this.inheritedAttributeHandles()) {
			protected Object transform(Object next) {
				return ((MWAttributeHandle) next).getAttribute();
			}
		};
	}

	public int inheritedAttributesSize() {
		return this.inheritedAttributeHandles.size();
	}

	public void addInheritedAttribute(MWClassAttribute inheritedAttribute) {
		this.inheritedAttributeHandles.add(new MWAttributeHandle(this, inheritedAttribute, this.inheritedAttributeScrubber()));
		this.fireItemAdded(INHERITED_ATTRIBUTES_COLLECTION, inheritedAttribute);
	}

	public void removeInheritedAttribute(MWClassAttribute inheritedAttribute) {
		for (Iterator stream = this.inheritedAttributes(); stream.hasNext(); ) {
			if (stream.next() == inheritedAttribute) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(inheritedAttribute.toString());
	}

	public void removeInheritedAttributes(Collection attributes) {
		this.removeInheritedAttributes(attributes.iterator());
	}

	public void removeInheritedAttributes(Iterator attributes) {
		while (attributes.hasNext()) {
			this.removeInheritedAttribute((MWClassAttribute) attributes.next());
		}
	}

	public MWClassAttribute inheritedAttributeNamed(String name) {
		for (Iterator stream = this.inheritedAttributes(); stream.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) stream.next();
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

	public boolean containsInheritedAttributeNamed(String name) {
		return this.inheritedAttributeNamed(name) != null;
	}

	//TODO this should probably be done differently
	//only the MappingDecriptors that are a part of the hierarchy
	//need to be notified. Need some good unit tests before changing this 
	//functionality
	public void hierarchyChanged(MWClass type) {
		Collection mappingsToRemove = new ArrayList();
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (mapping.isInherited() && 
				! this.getMWClass().isSubclassOf(mapping.getInstanceVariable().getDeclaringType())) {
					mappingsToRemove.add(mapping);
				}
		}
		this.removeMappings(mappingsToRemove);

		Collection attributesToRemove = new ArrayList();
		for (Iterator stream = this.inheritedAttributes(); stream.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) stream.next();
			if ( ! this.getMWClass().isSubclassOf(attribute.getDeclaringType())) {
				attributesToRemove.add(attribute);
			}
		}
		this.removeInheritedAttributes(attributesToRemove);
	}

	public MWMapping replaceMapping(MWMapping oldMapping, MWMapping newMapping) {
		if (oldMapping == newMapping) {
			return oldMapping;
		}
		if (oldMapping == null) {
			this.addMapping(newMapping);
			return newMapping;
		}
		else if (newMapping == null) {
			this.removeMapping(oldMapping);
			return null;
		}

		// don't want to call #nodeRemoved(Node), this will be taken care of by calling #mappingReplaced(...)
		this.removeItemFromCollection(oldMapping, this.mappings, MAPPINGS_COLLECTION);
		this.addMapping(newMapping);

		this.getProject().mappingReplaced(oldMapping, newMapping);

		return newMapping;
	}


	// ********** Inheritance *************************************************

	public MWInheritancePolicy getInheritancePolicy() {
		return this.inheritancePolicy;
	}

	public void addInheritancePolicy() {
		if (this.getInheritancePolicy().isActive()) {
			throw new MWAdvancedPropertyAdditionException(INHERITANCE_POLICY_PROPERTY, "Policy already exists on descriptor: ");
		}
		MWDescriptorInheritancePolicy ip = buildInheritancePolicy();
		setInheritancePolicy(ip);
		ip.initializeParentDescriptor();

		if (ip.getParentDescriptor() == null) {
			ip.setIsRoot(true);
		}

		getInheritancePolicy().getRootDescriptor().getInheritancePolicy().buildClassIndicatorValues();
	}

	protected abstract MWDescriptorInheritancePolicy buildInheritancePolicy();

	public void removeInheritancePolicy() throws MWAdvancedPropertyRemovalException {
		if (this.inheritancePolicy.isActive() == false) {
			throw new MWAdvancedPropertyRemovalException(INHERITANCE_POLICY_PROPERTY, "property does not exist on descriptor");
		}

		MWDescriptor oldRootDescriptor = this.getInheritancePolicy().getRootDescriptor();
		this.inheritancePolicy.dispose();
		this.setInheritancePolicy(new MWNullInheritancePolicy(this));

		oldRootDescriptor.getInheritancePolicy().buildClassIndicatorValues();

		// if this used to have a parent descriptor, its inheritance has now certainly changed
		this.inheritanceChanged();
	}

	protected void setInheritancePolicy(MWInheritancePolicy newInheritancePolicy) {
		Object oldInheritancePolicy = this.inheritancePolicy;
		this.inheritancePolicy = newInheritancePolicy;
		firePropertyChanged(INHERITANCE_POLICY_PROPERTY, oldInheritancePolicy, newInheritancePolicy);
	}

	public boolean canHaveInheritance() {
		return true;
	}

	public boolean hasDefinedInheritance() {
		MWInheritancePolicy ip = this.getInheritancePolicy();
		return ip.isRoot() || (ip.getParentDescriptor() != null);
	}

	public boolean hasActiveInstantiationPolicy() {
		return getInstantiationPolicy().isActive();
	}

	/** Use this method if other descriptor settings or policies depend upon inheritance */
	void inheritanceChanged() {
		super.inheritanceChanged();
		this.inheritancePolicy.descriptorInheritanceChanged();
	}

	public Iterator mappingsIncludingInherited() {
		return new CloneListIterator(getMappingsIncludingInherited());
	}

	protected List getMappingsIncludingInherited() {
		List allMappings = new Vector();

		if (this.getInheritancePolicy().getParentDescriptor() != null) {
			allMappings.addAll(this.getInheritancePolicy().getParentDescriptor().getMappingsIncludingInherited());
		}

		// add this descriptor's mappings
		allMappings.addAll(this.mappings);

		return allMappings;
	}

	public void implementorsChangedFor(MWInterfaceDescriptor descriptor) {
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			((MWMapping) stream.next()).implementorsChangedFor(descriptor);
		}
	}


	//*************** Aggregate Support *************

	public Collection buildAggregateFieldNameGenerators() {
		return new ArrayList();
	}


	//*************** Problem Handling *************

	/** Check for any problems and add them to the specified collection. */
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkMultipleMappingsWriteField(newProblems);
		this.checkForDuplicateMethods(newProblems);
	}

	protected void checkMultipleMappingsWriteField(List newProblems) {
		HashBag writtenFields = new HashBag();
		HashMap fieldToMappings = new HashMap();

		for (Iterator mappingStream = this.mappingsIncludingInherited(); mappingStream.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappingStream.next(); 
			if (mapping.getParentDescriptor().getTransactionalPolicy().isReadOnly()) {
				continue;  // skip to next mapping
			}
			Collection writtenFieldsForMapping = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFieldsForMapping);
			for (Iterator fieldStream = writtenFieldsForMapping.iterator(); fieldStream.hasNext(); ) {
				MWDataField field = (MWDataField) fieldStream.next();
				Collection fieldMappings = (Collection) fieldToMappings.get(field);
				if (fieldMappings == null) {
					fieldMappings = new ArrayList();
					fieldToMappings.put(field, fieldMappings);
				}
				fieldMappings.add(mapping);
			}
			writtenFields.addAll(writtenFieldsForMapping);
		}

		for (Iterator stream = writtenFields.uniqueIterator(); stream.hasNext(); ) {
			Object field = stream.next();
			if (writtenFields.count(field) > 1) {
				newProblems.add(this.buildMultipleMappingsWriteFieldProblem((Collection) fieldToMappings.get(field), (MWDataField) field));
			}
		}
	}

	private Problem buildMultipleMappingsWriteFieldProblem(Collection problemMappings, MWDataField field) {
		StringBuffer sb = new StringBuffer();
		for (Iterator stream = problemMappings.iterator(); stream.hasNext(); ) {
			sb.append('"');
			sb.append(((MWMapping) stream.next()).getName());
			sb.append('"');
			if (stream.hasNext()) {
				sb.append(", ");
			}
		}
		return this.buildProblem(multipleMappingsWriteFieldProblemResourceStringKey(), sb.toString(), field.fieldName());
	}

	protected abstract String multipleMappingsWriteFieldProblemResourceStringKey();

	private void checkForDuplicateMethods(List newProblems) {
		for (Iterator stream1 = this.getMWClass().methods(); stream1.hasNext(); ) {
			MWMethod method1 = (MWMethod) stream1.next();
			boolean firstMatch = true;
			for (Iterator stream2 = this.getMWClass().methods(); stream2.hasNext(); ) {
				MWMethod method2 = (MWMethod) stream2.next();
				if (method2.hasSameSignatureAs(method1)) {
					if (firstMatch) {
						firstMatch = false;
					} else {
						newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_CLASS_MULTIPLE_METHODS_WITH_SAME_SIGNATURE, 
										method1.signature()));
					}
				}
			}
		}
	}


	// ********** Automap Support **********

	protected void automapInternal() {
		super.automapInternal();
		this.getInheritancePolicy().automap();		// find a "type" column
		this.automapMappings();
	}

	private void automapMappings() {
		for (Iterator stream = this.mappings(); stream.hasNext();) {
			((MWMapping) stream.next()).automap();
		}
	}


	// ********** Runtime Conversion **********

	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = super.buildRuntimeDescriptor();

		this.inheritancePolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.copyPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.instantiationPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.afterLoadingPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.eventsPolicy.adjustRuntimeDescriptor(runtimeDescriptor);

		for (Iterator i = getProject().interfaceDescriptorsThatImplement(this); i.hasNext(); ) {
			MWInterfaceDescriptor implementor = (MWInterfaceDescriptor) i.next();
			runtimeDescriptor.getInterfacePolicy().addParentInterfaceName(implementor.getMWClass().getName());
		}

		for (Iterator mappings = this.orderedMappings(); mappings.hasNext(); ) {
			runtimeDescriptor.addMapping(((MWMapping) mappings.next()).runtimeMapping());
		}

		return runtimeDescriptor;
	}

	private Iterator orderedMappings() {
		return CollectionTools.sort(new Vector(this.mappings), this.orderedMappingComparator()).iterator();
	}

	/** The default comparator - no sorting is done */
	protected Comparator orderedMappingComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return 0;
			}
		};
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWMappingDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWDescriptor.class);

		XMLCompositeCollectionMapping mappingsMapping = new XMLCompositeCollectionMapping();
		mappingsMapping.setAttributeName("mappings");
		mappingsMapping.setGetMethodName("getMappingsForTopLink");
		mappingsMapping.setSetMethodName("setMappingsForTopLink");
		mappingsMapping.setReferenceClass(MWMapping.class);
		mappingsMapping.setXPath("mappings/mapping");
		descriptor.addMapping(mappingsMapping);

		XMLCompositeCollectionMapping inheritedAttributeHandlesMapping = new XMLCompositeCollectionMapping();
		inheritedAttributeHandlesMapping.setAttributeName("inheritedAttributeHandles");
		inheritedAttributeHandlesMapping.setGetMethodName("getInheritedAttributeHandlesForTopLink");
		inheritedAttributeHandlesMapping.setSetMethodName("setInheritedAttributeHandlesForTopLink");
		inheritedAttributeHandlesMapping.setReferenceClass(MWAttributeHandle.class);
		inheritedAttributeHandlesMapping.setXPath("inherited-attribute-handles/attribute-handle");
		descriptor.addMapping(inheritedAttributeHandlesMapping);

		XMLCompositeObjectMapping inheritancePolicyMapping = new XMLCompositeObjectMapping();
		inheritancePolicyMapping.setAttributeName("inheritancePolicy");
		inheritancePolicyMapping.setGetMethodName("getInheritancePolicyForTopLink");
		inheritancePolicyMapping.setSetMethodName("setInheritancePolicyForTopLink");
		inheritancePolicyMapping.setReferenceClass(MWDescriptorInheritancePolicy.class);
		inheritancePolicyMapping.setXPath("inheritance-policy");
		descriptor.addMapping(inheritancePolicyMapping);

		XMLCompositeObjectMapping copyPolicyMapping = new XMLCompositeObjectMapping();
		copyPolicyMapping.setAttributeName("copyPolicy");
		copyPolicyMapping.setSetMethodName("setCopyPolicyForTopLink");
		copyPolicyMapping.setGetMethodName("getCopyPolicyForTopLink");
		copyPolicyMapping.setReferenceClass(MWDescriptorCopyPolicy.class);
		copyPolicyMapping.setXPath("copy-policy");
		descriptor.addMapping(copyPolicyMapping);

		XMLCompositeObjectMapping eventsPolicyMapping = new XMLCompositeObjectMapping();
		eventsPolicyMapping.setAttributeName("eventsPolicy");
		eventsPolicyMapping.setReferenceClass(MWDescriptorEventsPolicy.class);
		eventsPolicyMapping.setXPath("events-policy");
		eventsPolicyMapping.setSetMethodName("setEventsPolicyForTopLink");
		eventsPolicyMapping.setGetMethodName("getEventsPolicyForTopLink");
		descriptor.addMapping(eventsPolicyMapping);

		XMLCompositeObjectMapping afterLoadPolicyMapping = new XMLCompositeObjectMapping();
		afterLoadPolicyMapping.setAttributeName("afterLoadingPolicy");
		afterLoadPolicyMapping.setSetMethodName("setAfterLoadingPolicyForTopLink");
		afterLoadPolicyMapping.setGetMethodName("getAfterLoadingPolicyForTopLink");
		afterLoadPolicyMapping.setReferenceClass(MWDescriptorAfterLoadingPolicy.class);
		afterLoadPolicyMapping.setXPath("after-loading-policy");
		descriptor.addMapping(afterLoadPolicyMapping);

		XMLCompositeObjectMapping instantiationPolicyMapping = new XMLCompositeObjectMapping();
		instantiationPolicyMapping.setAttributeName("instantiationPolicy");
		instantiationPolicyMapping.setReferenceClass(MWDescriptorInstantiationPolicy.class);
		instantiationPolicyMapping.setGetMethodName("getInstantiationPolicyForTopLink");
		instantiationPolicyMapping.setSetMethodName("setInstantiationPolicyForTopLink");
		instantiationPolicyMapping.setXPath("instantiation-policy");
		descriptor.addMapping(instantiationPolicyMapping);

		return descriptor;
	}

	/**
	 * sort the mappings for TopLink
	 */
	private Collection getMappingsForTopLink() {
		return CollectionTools.sort((List) mappings);
	}
	private void setMappingsForTopLink(Collection mappings) {
		this.mappings = mappings;
	}

	/**
	 * sort the inherited attributes for TopLink
	 */
	private Collection getInheritedAttributeHandlesForTopLink() {
		synchronized (this.inheritedAttributeHandles) {
			return new TreeSet(this.inheritedAttributeHandles);
		}
	}
	private void setInheritedAttributeHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWAttributeHandle) stream.next()).setScrubber(this.inheritedAttributeScrubber());
		}
		this.inheritedAttributeHandles = handles;
	}

	private MWDescriptorInheritancePolicy getInheritancePolicyForTopLink() {
		return (MWDescriptorInheritancePolicy) this.inheritancePolicy.getPersistedPolicy();
	}
	protected void setInheritancePolicyForTopLink(MWDescriptorInheritancePolicy policy) {
		if (policy == null) {
			this.inheritancePolicy = new MWNullInheritancePolicy(this);
		} else {
			this.inheritancePolicy = policy;
		}
	}

	private MWDescriptorCopyPolicy getCopyPolicyForTopLink() {
		return (MWDescriptorCopyPolicy) getCopyPolicy().getPersistedPolicy();
	}
	private void setCopyPolicyForTopLink(MWDescriptorCopyPolicy policy) {
		if (policy == null) {
			this.copyPolicy = new MWNullDescriptorPolicy(this);
		} else {
			this.copyPolicy = policy;
		}
	}

	private MWDescriptorEventsPolicy getEventsPolicyForTopLink() {
		return (MWDescriptorEventsPolicy) getEventsPolicy().getPersistedPolicy();
	}
	private void setEventsPolicyForTopLink(MWDescriptorEventsPolicy policy) {
		if (policy == null) {
			this.eventsPolicy = new MWNullDescriptorPolicy(this);
		} else {
			this.eventsPolicy = policy;
		}
	}

	private MWDescriptorAfterLoadingPolicy getAfterLoadingPolicyForTopLink() {
		return (MWDescriptorAfterLoadingPolicy) this.afterLoadingPolicy.getPersistedPolicy();
	}
	private void setAfterLoadingPolicyForTopLink(MWDescriptorAfterLoadingPolicy policy) {
		if (policy == null) {
			this.afterLoadingPolicy = new MWNullDescriptorPolicy(this);
		} else {
			this.afterLoadingPolicy = policy;
		}
	}

	private MWDescriptorInstantiationPolicy getInstantiationPolicyForTopLink() {
		return (MWDescriptorInstantiationPolicy) instantiationPolicy.getPersistedPolicy();
	}
	private void setInstantiationPolicyForTopLink(MWDescriptorInstantiationPolicy policy) {
		if (policy == null) {
			this.instantiationPolicy = new MWNullDescriptorPolicy(this);
		} else {
			this.instantiationPolicy = policy;
		}
	}

}
