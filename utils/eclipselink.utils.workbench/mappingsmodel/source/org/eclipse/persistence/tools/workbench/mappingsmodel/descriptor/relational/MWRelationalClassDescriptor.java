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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.AffixStrippingPartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.CollectionStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.ExhaustivePartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public abstract class MWRelationalClassDescriptor 
	extends MWMappingDescriptor
	implements MWRelationalDescriptor
{
	// These are only the non-autogenerated query keys. The autogenerated query keys are always recalculated.
	private Collection userDefinedQueryKeys;
		public static final String QUERY_KEYS_COLLECTION = "userDefinedQueryKeys";

	protected static final PartialStringMatcher PARTIAL_STRING_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);

	protected static final float PARTIAL_STRING_AFFIX_THRESHOLD = 0.80f;		// ???
	protected static final PartialStringComparatorEngine PARTIAL_STRING_COMPARATOR_ENGINE =
				AffixStrippingPartialStringComparatorEngine.forPrefixStripping(
					AffixStrippingPartialStringComparatorEngine.forSuffixStripping(
						new ExhaustivePartialStringComparatorEngine(PartialStringComparator.DEFAULT_COMPARATOR),
						PARTIAL_STRING_AFFIX_THRESHOLD
					),
					PARTIAL_STRING_AFFIX_THRESHOLD
				);


	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWRelationalClassDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWMappingDescriptor.class);		

		XMLCompositeCollectionMapping userDefinedQueryKeysMapping = new XMLCompositeCollectionMapping();
		userDefinedQueryKeysMapping.setAttributeName("userDefinedQueryKeys");
		userDefinedQueryKeysMapping.setGetMethodName("getUserDefinedQueryKeysForTopLink");
		userDefinedQueryKeysMapping.setSetMethodName("setUserDefinedQueryKeysForTopLink");
		userDefinedQueryKeysMapping.setReferenceClass(MWUserDefinedQueryKey.class);
		userDefinedQueryKeysMapping.setXPath("user-defined-query-keys/user-defined-query-key");
		descriptor.addMapping(userDefinedQueryKeysMapping);

		return descriptor;
	}
	
	/** Default constructor - for TopLink use only */
	protected MWRelationalClassDescriptor() {
		super();
	}

	protected MWRelationalClassDescriptor(MWRelationalProject parent, MWClass type, String name) {
		super(parent, type, name);
	}


	// ********** Initialization **********
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.userDefinedQueryKeys = new Vector();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.userDefinedQueryKeys) { children.addAll(this.userDefinedQueryKeys); }
	}


	// ********** MWRelationalDescriptor implementation **********
	
	public boolean isTableDescriptor() {
		return false;
	}

	public boolean isAggregateDescriptor() {
		return false;
	}

	public boolean isInterfaceDescriptor() {
		return false;
	}

	public Iterator implementors() {
		return NullIterator.instance();
	}
	
	/**
	 * Returns an Iterator of all columns in the primary table and associated tables.
	 */
	public Iterator allAssociatedColumns() {
		return new CompositeIterator(
			new TransformationIterator(associatedTables()) {
				protected Object transform(Object next) {
					return ((MWTable) next).columns();
				}
			}
		);
	}
	
	public int allAssociatedColumnsSize() {
		int numColumns = 0;
		for (Iterator i = associatedTables(); i.hasNext(); ) {
			numColumns += ((MWTable) i.next()).columnsSize();
		}
		return numColumns;
	}
	
	/** 
	 * Return all tables associated with this descriptor and all descriptors
	 * in its inheritance hierarchy
	 */
	public Iterator associatedTablesIncludingInherited() {
		return new CompositeIterator(
			new TransformationIterator(this.inheritanceHierarchy()) {
				protected Object transform(Object next) {
					return ((MWRelationalDescriptor) next).associatedTables();
				}
			}
		);
	}
	
	public int associatedTablesIncludingInheritedSize() {
		return CollectionTools.size(this.associatedTablesIncludingInherited());
	}
	
	public Iterator candidateTables() {
		if (this.associatedTablesSize() == 0) {
			return this.getDatabase().tables();
		}
		return this.associatedTables();
	}

	public int candidateTablesSize() {
		return CollectionTools.size(this.candidateTables());
	}
	
	public Iterator candidateTablesIncludingInherited() {
		if (this.associatedTablesIncludingInheritedSize() == 0) {
			return this.getDatabase().tables();
		}
		return this.associatedTablesIncludingInherited();
	}
	
	public int candidateTablesIncludingInheritedSize() {
		return CollectionTools.size(this.candidateTablesIncludingInherited());
	}
	

	//************** Morphing **************
	
	public MWAggregateDescriptor asMWAggregateDescriptor() {
		MWAggregateDescriptor newDescriptor = ((MWRelationalProject) this.getProject()).addAggregateDescriptorForType(this.getMWClass());	
		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}
	
	public MWTableDescriptor asMWTableDescriptor() throws InterfaceDescriptorCreationException{
		MWTableDescriptor newDescriptor;
		try {
			newDescriptor = (MWTableDescriptor) this.getProject().addDescriptorForType(this.getMWClass());
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}
		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}

	/**
	 * This really should only happen as the result of a class refresh
	 */
	public MWInterfaceDescriptor asMWInterfaceDescriptor() throws InterfaceDescriptorCreationException {
		MWInterfaceDescriptor newDescriptor = (MWInterfaceDescriptor) this.getProject().addDescriptorForType(getMWClass());
		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}

	
	public void initializeFromMWAggregateDescriptor(MWAggregateDescriptor oldDescriptor) {
	//	super.initializeFromMWAggregateDescriptor(oldDescriptor);
		this.initializeFromMWRelationalClassDescriptor(oldDescriptor);
	}
	
	public void initializeFromMWRelationalClassDescriptor(MWRelationalClassDescriptor oldDescriptor) {
	//	super.initializeFromMWRelationalClassDescriptor(oldDescriptor);
		this.initializeFromMWMappingDescriptor(oldDescriptor);

		for (Iterator i = oldDescriptor.userDefinedQueryKeys(); i.hasNext(); ){
			addQueryKey((MWUserDefinedQueryKey) i.next());
		}
	}
	
	public void initializeFromMWTableDescriptor(MWTableDescriptor oldDescriptor) {
	//	super.initializeFromMWTableDescriptor(oldDescriptor);
		this.initializeFromMWRelationalClassDescriptor(oldDescriptor);
	}
	
	public void initializeFromMWInterfaceDescriptor(MWInterfaceDescriptor oldDescriptor) {
	//	super.initializeFromMWInterfaceDescriptor(oldDescriptor);
	   this.initializeFromMWDescriptor(oldDescriptor);
	}

	protected void refreshClass(MWClassRefreshPolicy refreshPolicy)
		throws ExternalClassNotFoundException, InterfaceDescriptorCreationException {
		super.refreshClass(refreshPolicy);
		
		if (this.getMWClass().isInterface()) {
			this.asMWInterfaceDescriptor();
		}
	}
	
	
	//************** Mapping Creation **************

	public MWMappingFactory mappingFactory() {
		return MWRelationalMappingFactory.instance();
	}
	
	public MWManyToManyMapping addManyToManyMapping(MWClassAttribute attribute) {
		MWManyToManyMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createManyToManyMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public MWOneToManyMapping addOneToManyMapping(MWClassAttribute attribute) {
		MWOneToManyMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createOneToManyMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWOneToOneMapping addOneToOneMapping(MWClassAttribute attribute) {
		MWOneToOneMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createOneToOneMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}

	public MWVariableOneToOneMapping addVariableOneToOneMapping(MWClassAttribute attribute) {
		MWVariableOneToOneMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createVariableOneToOneMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWDirectToXmlTypeMapping addDirectToXmlTypeMapping(MWClassAttribute attribute) {
		MWDirectToXmlTypeMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createDirectToXmlTypeMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	public MWAggregateMapping addAggregateMapping(MWClassAttribute attribute) {
		MWAggregateMapping mapping = ((MWRelationalMappingFactory) mappingFactory()).createAggregateMapping(this, attribute, attribute.getName());
		this.addMapping(mapping);
		return mapping;
	}
	
	
	// **************** Disorganized methods **********************************

	public MWUserDefinedQueryKey addQueryKey(String name, MWColumn column) {
		checkQueryKeyName(name);
		
		MWUserDefinedQueryKey queryKey = new MWUserDefinedQueryKey(name, this, column);
		addQueryKey(queryKey);
		return queryKey;
	}	

	/**
	 * disallow duplicate user defined query key names in a descriptor
	 */
	void checkQueryKeyName(String queryKeyName) {
		if (queryKeyName == null || queryKeyName == "") {
			throw new NullPointerException();
		}
		//TODO can there be 2 query keys with the same name?  One an auto-generated and the other user defined?
		if (queryKeyNamed(queryKeyName) != null) {
			throw new IllegalArgumentException(queryKeyName);
		}
	}

	private void addQueryKey(MWUserDefinedQueryKey newQueryKey) {
		this.userDefinedQueryKeys.add(newQueryKey);
		this.fireItemAdded(QUERY_KEYS_COLLECTION, newQueryKey);
		this.getProject().recalculateAggregatePathsToColumn(this);
	}
	
	public void removeQueryKey(MWUserDefinedQueryKey queryKeyToRemove) {
		this.removeNodeFromCollection(queryKeyToRemove, this.userDefinedQueryKeys, QUERY_KEYS_COLLECTION);
		this.getProject().recalculateAggregatePathsToColumn(this);
	}

	public Iterator allQueryKeys() {
		return this.getAllQueryKeys().iterator();
	}
	public Iterator allQueryKeysIncludingInherited() {
		return this.getAllQueryKeysIncludingInherited().iterator();
	}
	
	public Iterator allQueryKeyNames() {
		return new TransformationIterator(allQueryKeys()) {
			protected Object transform(Object next) {			
				return ((MWQueryKey) next).getName();
			}
		};
	}
	
	public Iterator userDefinedQueryKeys() {
		return new CloneIterator(this.userDefinedQueryKeys);
	}
	
	public SortedSet getAllQueryKeys() {
		SortedSet result =  new TreeSet();
		result.addAll(this.userDefinedQueryKeys);
		result.addAll(this.getAutoGeneratedQueryKeys());
		return result;
	}
		
	public Collection getAutoGeneratedQueryKeys() {
		Collection queryKeys = new ArrayList();
		Iterator i = mappings();
		while(i.hasNext()){
			MWMapping mapping = (MWMapping) i.next();
			MWQueryKey bldrQueryKey = mapping.getAutoGeneratedQueryKey();
			if(bldrQueryKey != null){
				queryKeys.add(bldrQueryKey);
			}
		}
		return queryKeys;
	}
	
	public Collection getAutoGeneratedQueryKeysIncludingInherited() {
		Collection autoQueryKeys = super.getAutoGeneratedQueryKeysIncludingInherited();
		
		if (getInheritancePolicy().isRoot()) {
			// i.e., this is the root descriptor, or there is no inheritance
			autoQueryKeys = getAutoGeneratedQueryKeys();
		} else if(getInheritancePolicy().getParentDescriptor() != null){
			//User might not have set a parent
			autoQueryKeys = getInheritancePolicy().getParentDescriptor().getAutoGeneratedQueryKeysIncludingInherited();
			autoQueryKeys.addAll(getAutoGeneratedQueryKeys());
		}
		return autoQueryKeys;
	}
	
	public Collection getAllQueryKeysIncludingInherited() {
		Collection allQueryKeys = super.getAllQueryKeysIncludingInherited();
		allQueryKeys.addAll(getAllQueryKeys());
		
		if(getInheritancePolicy().isActive() && getInheritancePolicy().getParentDescriptor() != null){
			//I am checking this because the user might not have set a parent
			allQueryKeys.addAll(getInheritancePolicy().getParentDescriptor().getAllQueryKeysIncludingInherited());
		}
		return allQueryKeys;
	}

	public MWQueryKey queryKeyNamed(String name) {
		for (Iterator stream = this.allQueryKeys(); stream.hasNext(); ) {
			MWQueryKey queryKey = (MWQueryKey) stream.next();
			if (queryKey.getName().equals(name)) {
				return queryKey;
			}
		}
		return null;
	}
	
	public MWQueryKey queryKeyNamedIncludingInherited(String name) {
		for (Iterator stream = this.allQueryKeysIncludingInherited(); stream.hasNext(); ) {
			MWQueryKey queryKey = (MWQueryKey) stream.next();
			if (queryKey.getName().equals(name)) {
				return queryKey;
			}
		}
		return null;
	}


	public MWTable getPrimaryTable() {
		return null;
	}


	public MWQueryable queryableNamed(String queryableName) {
		Iterator queryables = getQueryables(Filter.NULL_INSTANCE).iterator();
		while (queryables.hasNext()) {
			MWQueryable queryable = (MWQueryable) queryables.next();
			if (queryable.getName().equals(queryableName)) {
				return queryable;
			}
		}
		return null;
	}
	
	public List getQueryables(Filter queryableFilter) {
		List queryables = new ArrayList();
		for (Iterator mappings = mappingsIncludingInherited(); mappings.hasNext();) {
			MWMapping mapping = (MWMapping) mappings.next();
			if (queryableFilter.accept(mapping)) {
				queryables.add(mapping);
			}
		}
		for (Iterator i = userDefinedQueryKeys(); i.hasNext(); ) {
			MWUserDefinedQueryKey key = (MWUserDefinedQueryKey) i .next();
			if (queryableFilter.accept(key)) {
				queryables.add(key);
			}
		}
		return queryables;
	}
	
	
	// ********** Behavior **********
	
	protected MWDescriptorInheritancePolicy buildInheritancePolicy() {
		return new MWRelationalDescriptorInheritancePolicy(this);
	}
	
	public boolean supportsInterfaceAliasPolicy() {
		return false;
	}	
	
	public boolean supportsMultitablePolicy() {
		return false;
	}
	
	/**
	 * throw property change for the query key collection since the umapping changes the auto generated ones.
	 */
	public void unmap() {
		super.unmap();
		fireCollectionChanged(QUERY_KEYS_COLLECTION);
	}

	// *************** Automap Support **************

	/**
	 * this is called by another descriptor when it discovers this descriptor
	 * can be its parent and this descriptor is among the collection of
	 * descriptors to be automapped, meaning that this descriptor can
	 * be automapped also;
	 * this method is called *before* the normal #automap() method
	 */
	protected void automapSuperDescriptorInheritance(Collection automapDescriptors) {
		this.automapInheritanceHierarchy(automapDescriptors);

		MWInheritancePolicy ip = this.getInheritancePolicy();
		if ( ! ip.isActive()) {
			this.addInheritancePolicy();
			ip = this.getInheritancePolicy();
		}

		if (ip.getParentDescriptor() == null) {
			((MWDescriptorInheritancePolicy) ip).setIsRoot(true);
		}
		
		if ((ip.getParentDescriptor() == null) && ! this.getMWClass().isAbstract()) {
			MWClassIndicatorFieldPolicy fieldPolicy = (MWClassIndicatorFieldPolicy) ip.getClassIndicatorPolicy();
			MWClassIndicatorValue indicatorValue = fieldPolicy.getClassIndicatorValueForDescriptor(this);

			indicatorValue.setInclude(true);
			indicatorValue.setIndicatorValue(this.getName());
		}
	}

	protected void automapInternal() {
		// automap the unmapped attributes *before* looping
		// through the mappings and automapping them
		this.automapUnmappedAttributes();
		this.automapDirectMappingColumns();
		super.automapInternal();
	}

	private void automapUnmappedAttributes() {
		Set attributes = this.allAttributes();

		CollectionTools.removeAll(attributes, this.mappedAttributes());

		for (Iterator stream = attributes.iterator(); stream.hasNext(); ) {
			this.automapUnmappedAttribute((MWClassAttribute) stream.next());
		}
	}

	private Set allAttributes() {
		Set attributes = CollectionTools.set(this.getMWClass().attributes());
		CollectionTools.addAll(attributes, this.inheritedAttributes());
		CollectionTools.addAll(attributes, this.getMWClass().ejb20Attributes());
		return attributes;
	}

	private Iterator mappedAttributes() {
		return new TransformationIterator(this.mappings()) {
			protected Object transform(Object next) {
				return ((MWMapping) next).getInstanceVariable();
			}
		};
	}

	private void automapUnmappedAttribute(MWClassAttribute attribute) {
		if (attribute.getDimensionality() > 0) {
			// toplink doesn't support arrays
			return;
		}
		MWClass type = attribute.getType();
		if (type.isValueHolder()) {
			type = attribute.getValueType();
			if (type.isObject()) {
				// no 'valueType' is specified; use "get" method return type
				MWMethod getMethod = attribute.standardValueGetMethod();
				if (getMethod != null) {
					type = getMethod.getReturnType();
				}
			}
		}
		if (type.isContainer()) {
			MWDescriptor descriptor = this.findReferenceDescriptorForCollectionAttribute(attribute);
			if (descriptor == null) {
				this.automapAsDirectContainerMapping(attribute, type);
			} else {
				this.automapAsCollectionMapping(attribute, descriptor);
			}
		} else {
			MWDescriptor descriptor = this.findReferenceDescriptorForType(type);
			if (descriptor != null) {
				this.automapAsReferenceMapping(attribute, descriptor);
			} else {
				this.automapAsDirectMapping(attribute);
			}
		}
	}

	private MWDescriptor findReferenceDescriptorForCollectionAttribute(MWClassAttribute attribute) {
		CollectionStringHolder[] holders = this.buildMultiDescriptorStringHolders();
		StringHolderScore shs = this.match(attribute.getName().toLowerCase(), holders);
		if (shs.getScore() < 0.80) {	// ???
			return null;
		}
		// look for a descriptor in the same package as this descriptor
		String packageName = this.packageName();
		MWDescriptor descriptor = null;
		for (Iterator stream = ((CollectionStringHolder) shs.getStringHolder()).iterator(); stream.hasNext(); ) {
			descriptor = (MWDescriptor) stream.next();
			if (descriptor.packageName().equals(packageName)) {
				return descriptor;
			}
		}
		// if none of the descriptors is in the same package, take the last one
		return descriptor;
	}

	/**
	 * gather together all the descriptors that have the same "short" name
	 * but different packages
	 */
	private CollectionStringHolder[] buildMultiDescriptorStringHolders() {
		Map holders = new HashMap(this.getProject().descriptorsSize());
		for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			String shortName = descriptor.shortName().toLowerCase();
			CollectionStringHolder holder = (CollectionStringHolder) holders.get(shortName);
			if (holder == null) {
				holder = new CollectionStringHolder(shortName);
				holders.put(shortName, holder);
			}
			holder.add(descriptor);
		}
		return (CollectionStringHolder[]) holders.values().toArray(new CollectionStringHolder[holders.size()]);
	}

	private StringHolderScore match(String string, CollectionStringHolder[] multiDescriptorStringHolders) {
		return PARTIAL_STRING_MATCHER.match(string, multiDescriptorStringHolders);
	}

	private void automapAsDirectContainerMapping(MWClassAttribute attribute, MWClass type) {
		if (type.isAssignableToCollection()) {
			this.addDirectCollectionMapping(attribute);
		} else {
			this.addDirectMapMapping(attribute);
		}
	}

	private void automapAsCollectionMapping(MWClassAttribute attribute, MWDescriptor referenceDescriptor) {
		MWOneToManyMapping mapping = this.addOneToManyMapping(attribute);
		mapping.setReferenceDescriptor(referenceDescriptor);
	}

	private MWDescriptor findReferenceDescriptorForType(MWClass type) {
		MWDescriptor descriptor = this.getProject().descriptorForType(type);
		if (descriptor != null) {
			return descriptor;
		}
		if (type.isInterface()) {
			for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
				descriptor = (MWDescriptor) stream.next();
				if (descriptor.getMWClass().isAssignableTo(type)) {
					return descriptor;
				}
			}
		}
		return null;
	}

	private void automapAsReferenceMapping(MWClassAttribute attribute, MWDescriptor referenceDescriptor) {
		MWRelationalDescriptor relationalDescriptor = (MWRelationalDescriptor) referenceDescriptor;
		if (relationalDescriptor.isInterfaceDescriptor()) {
			MWClass referenceInterface = relationalDescriptor.getMWClass();
			int numImplementors = 0;
			MWDescriptor implementorDescriptor = null;
			for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
				MWDescriptor descriptor = (MWDescriptor) stream.next();
				if ((descriptor != referenceDescriptor) && descriptor.getMWClass().allInterfacesContains(referenceInterface)) {
					implementorDescriptor = descriptor;
					numImplementors++;
				}
			}
			MWReferenceMapping mapping;
			if (numImplementors == 1) {
				mapping = this.addOneToOneMapping(attribute);
				mapping.setReferenceDescriptor(implementorDescriptor);
			} else {
				mapping = this.addVariableOneToOneMapping(attribute);
				mapping.setReferenceDescriptor(referenceDescriptor);
			}
		}
		else if (relationalDescriptor.isAggregateDescriptor()) {
			MWAggregateMapping mapping = this.addAggregateMapping(attribute);
			mapping.setReferenceDescriptor(referenceDescriptor);
		}
		else if (relationalDescriptor.isTableDescriptor()) {
			MWOneToOneMapping mapping = this.addOneToOneMapping(attribute);
			mapping.setReferenceDescriptor(referenceDescriptor);
		}
	}

	private void automapAsDirectMapping(MWClassAttribute attribute) {
		// we will assign the columns for our direct mappings when they're all in place
		this.addDirectMapping(attribute);
	}

	/**
	 * match up any direct mapping that doesn't have a column
	 * with an unmapped column
	 */
	protected void automapDirectMappingColumns() {
		// by default do nothing - aggregate descriptors
		// don't match direct mapping columns
	}

	// *************** aggregate mappings **************

	public Collection buildAggregateFieldNameGenerators() {
		Collection generators = new ArrayList();
		if (getInheritancePolicy().isRoot()) {
			getInheritancePolicy().getClassIndicatorPolicy().addToAggregateFieldNameGenerators(generators);
		}
		else if (getInheritancePolicy().getParentDescriptor() != null) {//TODO add test case for null parentDescriptor case
			generators.addAll(((MWRelationalDescriptor) getInheritancePolicy().getParentDescriptor()).buildAggregateFieldNameGenerators());
		}

		for (Iterator i = userDefinedQueryKeys(); i.hasNext(); ) {
			MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey) i.next();
			generators.add(queryKey);
		}
		
		return generators;
	}


	//************** runtime conversion **************/

	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = super.buildRuntimeDescriptor();
					
		adjustUserDefinedQueryKeys(runtimeDescriptor);
		
		return runtimeDescriptor;	
	}
	
	protected abstract void adjustUserDefinedQueryKeys(ClassDescriptor runtimeDescriptor);
	
	
	// ********** TopLink methods **********
	
	/**
	 * sort the query keys for TopLink
	 */
	private Collection getUserDefinedQueryKeysForTopLink() {
		return CollectionTools.sort((List) this.userDefinedQueryKeys);
	}
	
	private void setUserDefinedQueryKeysForTopLink(Collection userDefinedQueryKeys) {
		this.userDefinedQueryKeys = userDefinedQueryKeys;
	}
		

}
