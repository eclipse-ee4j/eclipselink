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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.ColumnStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyAdditionException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyRemovalException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInterfaceAliasDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWRefreshCachePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MappingStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine.StringHolderPair;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.Record;

public final class MWTableDescriptor 
	extends MWRelationalClassDescriptor
	implements MWTransactionalDescriptor, MWInterfaceAliasDescriptor
{
	private MWTableHandle primaryTableHandle;
		public final static String PRIMARY_TABLE_PROPERTY = "primaryTable";

	//TODO This needs to be made into a policy ~kfm
	private volatile boolean usesSequencing;
		public final static String USES_SEQUENCING_PROPERTY = "usesSequencing";

	private volatile String sequenceNumberName;
		public final static String SEQUENCE_NUMBER_NAME_PROPERTY = "sequenceNumberName";

	private MWTableHandle sequenceNumberTableHandle;
		public final static String SEQUENCE_NUMBER_TABLE_PROPERTY = "sequenceNumberTable";

	private MWColumnHandle sequenceNumberColumnHandle;
		public final static String SEQUENCE_NUMBER_COLUMN_PROPERTY = "sequenceNumberColumn";

	private volatile MWDescriptorPolicy multiTableInfoPolicy;
		public final static String MULTI_TABLE_INFO_POLICY_PROPERTY = "multiTableInfoPolicy";

	private volatile MWDescriptorPolicy interfaceAliasPolicy;
		public final static String INTERFACE_ALIAS_POLICY_PROPERTY = "interfaceAliasPolicy";

	private volatile MWDescriptorPolicy returningPolicy;

	//This is purposefully not in the UI. you only get this functionality
	//if you migrate from cmp and you cannot change it once you have it.
	//Don't ask me why, ask Doug or Mike Keith :) - kfm
	private volatile String changeTrackingType;
		public final static String CHANGE_TRACKING_TYPE_PROPERTY = "changeTrackingType";
		public final static String OBJECT_LEVEL_CHANGE_TRACKING = "objectLevelChangeTracking";
		public final static String ATTRIBUTE_LEVEL_CHANGE_TRACKING = "attributeLevelChangeTracking";

	// ********** Constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWTableDescriptor() {
		super();
	}

	public MWTableDescriptor(MWRelationalProject parent, MWClass type, String name) {
		super(parent, type, name);
	}


	// ********** Initialization **********


	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.primaryTableHandle = new MWTableHandle(this, this.buildPrimaryTableScrubber());
		this.sequenceNumberColumnHandle = new MWColumnHandle(this, this.buildSequenceNumberColumnScrubber());
		this.sequenceNumberTableHandle = new MWTableHandle(this, this.buildSequenceNumberTableScrubber());
		this.sequenceNumberName = "";
		this.interfaceAliasPolicy = new MWNullDescriptorPolicy(this);
		this.multiTableInfoPolicy = new MWNullDescriptorPolicy(this);
		this.returningPolicy = new MWNullDescriptorPolicy(this);
	}


	// ********** Accessors **********

	public MWTable getPrimaryTable() {
		return this.primaryTableHandle.getTable();
	}

	public void setPrimaryTable(MWTable newValue) {
		MWTable oldValue = getPrimaryTable();
		this.primaryTableHandle.setTable(newValue);
		this.firePropertyChanged(PRIMARY_TABLE_PROPERTY, oldValue, newValue);

		this.primaryKeyPolicy().descriptorPrimaryTableChanged(newValue);
	}

	public void notifyExpressionsToRecalculateQueryables() {
		getRelationalQueryManager().notifyExpressionsToRecalculateQueryables();
	}

	// ********** Transactional Policy **********

	/** override default behavior */
	protected MWTransactionalPolicy buildDefaultTransactionalPolicy() {
		return new MWRelationalTransactionalPolicy(this);
	}

	public MWRelationalTransactionalPolicy getRelationalTransactionalPolicy() {
		return (MWRelationalTransactionalPolicy) getTransactionalPolicy();
	}


	// **************** Primary key policy ************************************

	public MWRelationalPrimaryKeyPolicy primaryKeyPolicy() {
		return this.getRelationalTransactionalPolicy().getPrimaryKeyPolicy();
	}

	public Iterator primaryKeyChoices() {
		return this.primaryKeyPolicy().primaryKeyChoices();
	}


	// - returns a collection of the attributes mapped by all mappings obtained by
	//   getPrimaryKeyMappings()
	public Iterator primaryKeyAttributes()
	{
		Collection pkAttributes = new Vector();

		for (Iterator it = this.primaryKeyMappings(); it.hasNext(); )
			pkAttributes.add(((MWMapping) it.next()).getInstanceVariable());

		return pkAttributes.iterator();
	}


	// - returns an iterator on all mappings in this descriptor that map to primary key fields,
	//   plus all mappings in this descriptor's superdescriptors that do the same
	public Iterator primaryKeyMappings() {
		Collection pkMappings = new Vector();
		Iterator pkFieldIt = primaryKeyPolicy().primaryKeys();
		while (pkFieldIt.hasNext()) {
			pkMappings.addAll(allWritableMappingsForField((MWColumn) pkFieldIt.next()));
		}

		return pkMappings.iterator();
	}

	public Iterator primaryKeyNames() {
		return new TransformationIterator(primaryKeys()) {
			protected Object transform(Object next) {
				return ((MWColumn) next).getName();
			}
		};
	}

	public	Iterator primaryKeys() {
		return primaryKeyPolicy().primaryKeys();
	}

	public int primaryKeysSize() {
		return primaryKeyPolicy().primaryKeysSize();
	}

	// ********** Sequencing **********

	public boolean usesSequencing() {
		return this.usesSequencing;
	}

	public void setUsesSequencing(boolean usesSequencing) {
		boolean old = this.usesSequencing;
		this.usesSequencing = usesSequencing;
		if (old != usesSequencing) {
			if ( ! usesSequencing) {
				setSequenceNumberName("");
				setSequenceNumberTable(null);
			}
			else {
				initializeDefaultSequenceTableInfo();
			}
			this.firePropertyChanged(USES_SEQUENCING_PROPERTY, old, usesSequencing);
		}
	}

	private void initializeDefaultSequenceTableInfo() {
		setSequenceNumberName("SEQ");
		if(getPrimaryTable() != null) {
			setSequenceNumberTable(getPrimaryTable());
			if (getPrimaryTable().primaryKeyColumnsSize() > 0) {
				setSequenceNumberColumn((MWColumn) getPrimaryTable().primaryKeyColumns().next());
			}
		}
	}

	/** 
	 * Return true if this descriptor, or any descriptor in the 
	 * inheritance hierarchy uses sequencing, as descriptors
	 * inherit sequencing information
	 */
	public boolean usesSequencingInDescriptorHierarchy() {
		if (this.usesSequencing()) {
			return true;
		}
		MWDescriptor parent = this.getInheritancePolicy().getParentDescriptor();
		return (parent == null) ? false : parent.usesSequencingInDescriptorHierarchy();
	}

	public String getSequenceNumberName() {
		return this.sequenceNumberName;
	}

	public void setSequenceNumberName(String sequenceNumberName) {
		if (!usesSequencing() && !sequenceNumberName.equals("")) {
			throw new IllegalStateException("Must have useSequencing set to true to set the sequence number name");
		}
		String old = this.sequenceNumberName;
		this.sequenceNumberName = sequenceNumberName;
		this.firePropertyChanged(SEQUENCE_NUMBER_NAME_PROPERTY, old, sequenceNumberName);
	}

	public MWColumn getSequenceNumberColumn() {
		return this.sequenceNumberColumnHandle.getColumn();
	}

	public void setSequenceNumberColumn(MWColumn sequenceNumberColumn) {
		if ( ! this.usesSequencing() && sequenceNumberColumn != null) {
			throw new IllegalStateException("Must have useSequencing set to true to set the sequence column");
		}
		if (this.getSequenceNumberTable() == null && sequenceNumberColumn != null) {
			throw new IllegalStateException("Must have the sequence table set to set the sequence column");
		}
		if (sequenceNumberColumn != null && this.getSequenceNumberTable() != sequenceNumberColumn.getTable()) {
			throw new IllegalArgumentException("The column must be in the current sequence number table");
		}

		Object old = this.sequenceNumberColumnHandle.getColumn();
		this.sequenceNumberColumnHandle.setColumn(sequenceNumberColumn);
		this.firePropertyChanged(SEQUENCE_NUMBER_COLUMN_PROPERTY, old, sequenceNumberColumn);
	}

	public MWTable getSequenceNumberTable() {
		return this.sequenceNumberTableHandle.getTable();
	}

	public void setSequenceNumberTable(MWTable sequenceNumberTable) {
		if (!usesSequencing() && sequenceNumberTable != null) {
			throw new IllegalStateException("Must have useSequencing set to true to set the sequence table");
		}
		MWTable old = getSequenceNumberTable();
		this.sequenceNumberTableHandle.setTable(sequenceNumberTable);
		setSequenceNumberColumn(null);
		firePropertyChanged(SEQUENCE_NUMBER_TABLE_PROPERTY, old, sequenceNumberTable);
	}


	// ********** RefreshCachePolicy **********

	public MWRefreshCachePolicy getRefreshCachePolicy() {
		return getRelationalTransactionalPolicy().getRefreshCachePolicy();
	}


	// *************** Queries ******************

	public MWRelationalQueryManager getRelationalQueryManager() {
		return (MWRelationalQueryManager)getRelationalTransactionalPolicy().getQueryManager();
	}


	public MWQueryable firstQueryable() {
		for (Iterator i = getQueryables(Filter.NULL_INSTANCE).iterator(); i.hasNext(); ) {
			MWQueryable queryable = (MWQueryable) i.next();
			if (queryable.isLeaf(Filter.NULL_INSTANCE)) {
				return queryable;
			}
		}
		return null;
	}


	//	 ********** MultiTableInfoPolicy API**********

	private MWDescriptorMultiTableInfoPolicy getMultiTableInfoPolicyForTopLink()
	{
		return (MWDescriptorMultiTableInfoPolicy) this.multiTableInfoPolicy.getPersistedPolicy();
	}

	private void setMultiTableInfoPolicyForTopLink(MWDescriptorMultiTableInfoPolicy multiTableInfoPolicy)
	{
		if (multiTableInfoPolicy == null)
		{
			this.multiTableInfoPolicy = new MWNullDescriptorPolicy(this);
		}
		else
		{
			this.multiTableInfoPolicy = multiTableInfoPolicy;
		}
	}

	private void setMultiTableInfoPolicy(MWDescriptorPolicy multiTableInfoPolicy)
	{
		Object old = this.multiTableInfoPolicy;
		this.multiTableInfoPolicy = multiTableInfoPolicy;
		firePropertyChanged(MULTI_TABLE_INFO_POLICY_PROPERTY, old, this.multiTableInfoPolicy);
	}

	public MWDescriptorPolicy getMultiTableInfoPolicy()
	{
		return this.multiTableInfoPolicy;
	}


	public void addMultiTableInfoPolicy() throws MWAdvancedPropertyAdditionException
	{
		if (this.multiTableInfoPolicy.isActive())
		{
			throw new MWAdvancedPropertyAdditionException(MULTI_TABLE_INFO_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setMultiTableInfoPolicy(new MWDescriptorMultiTableInfoPolicy(this));
	}

	public void removeMultiTableInfoPolicy()
	{
		if (this.multiTableInfoPolicy.isActive())
		{
			getMultiTableInfoPolicy().dispose();
			setMultiTableInfoPolicy(new MWNullDescriptorPolicy(this));
		}
		else
		{
			throw new MWAdvancedPropertyRemovalException(MULTI_TABLE_INFO_POLICY_PROPERTY, "policy does not exist on the descriptor");
		}
	}

	//	 ********** IntefaceAliasPolicy API**********


	private void setInterfaceAliasPolicyForTopLink(MWDescriptorInterfaceAliasPolicy interfaceAliasPolicy)
	{
		if (interfaceAliasPolicy == null)
		{
			this.interfaceAliasPolicy = new MWNullDescriptorPolicy(this);
		}
		else
		{
			this.interfaceAliasPolicy = interfaceAliasPolicy;
		}
	}

	private MWDescriptorInterfaceAliasPolicy getInterfaceAliasPolicyForTopLink()
	{
		return (MWDescriptorInterfaceAliasPolicy) this.interfaceAliasPolicy.getPersistedPolicy();
	}

	protected void setInterfaceAliasPolicy(MWDescriptorPolicy interfaceAliasPolicy)	{
		Object old = this.interfaceAliasPolicy;
		this.interfaceAliasPolicy = interfaceAliasPolicy;
		firePropertyChanged(INTERFACE_ALIAS_POLICY_PROPERTY, old, this.interfaceAliasPolicy);
	}

	public MWDescriptorPolicy getInterfaceAliasPolicy()
	{
		return this.interfaceAliasPolicy;
	}

	public void removeInterfaceAliasPolicy()
	{
		if (this.interfaceAliasPolicy.isActive())
		{
			setInterfaceAliasPolicy(new MWNullDescriptorPolicy(this));
		}
		else
		{
			throw new MWAdvancedPropertyRemovalException(INTERFACE_ALIAS_POLICY_PROPERTY, "policy does not exist on the descriptor");
		}
	}

	public void addInterfaceAliasPolicy() throws MWAdvancedPropertyAdditionException
	{
		if (this.interfaceAliasPolicy.isActive())
		{
			throw new MWAdvancedPropertyAdditionException(INTERFACE_ALIAS_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setInterfaceAliasPolicy(new MWDescriptorInterfaceAliasPolicy(this));
	}

	public boolean supportsInterfaceAliasPolicy() {
		return true;
	}

	public boolean supportsMultitablePolicy() {
		return true;
	}
	// ********* Associated Tables API ************

	public void addAssociatedTable(MWTable table) {
		if (!getMultiTableInfoPolicy().isActive()) {
			throw new IllegalStateException("Cannot add associated tables unless the descriptor has the Multitable info policy");
		}
		((MWDescriptorMultiTableInfoPolicy) getMultiTableInfoPolicy()).addSecondaryTable(table);
	}

	public void removeAssociatedTable(MWTable table) {
		((MWDescriptorMultiTableInfoPolicy) getMultiTableInfoPolicy()).removeSecondaryTable(table);
	}

	public MWTable getAssociatedTableNamed(String tableName)
	{
		for (Iterator stream = associatedTables(); stream.hasNext(); ) {
			MWTable table = (MWTable) stream.next();
			if (table.getName().equals(tableName)) {
				return table;
			}
		}
		return null;
	}

	public boolean hasAssociatedTable(MWTable table)
	{
		for (Iterator stream = associatedTables(); stream.hasNext(); ) {
			MWTable currTable = (MWTable) stream.next();
			if (currTable == table) {
				return true;
			}
		}
		return false;
	}

	public Iterator associatedTables() {
		if (getPrimaryTable() != null && !CollectionTools.contains(secondaryTables(), getPrimaryTable())) {
			return new CompositeIterator(getPrimaryTable(), secondaryTables());
		}
		return this.secondaryTables();
	}

	public int associatedTablesSize() {
		return CollectionTools.size(associatedTables());
	}


	public Iterator secondaryTables() {
		if (getMultiTableInfoPolicy().isActive()) {
			return ((MWDescriptorMultiTableInfoPolicy) getMultiTableInfoPolicy()).secondaryTables();
		}
		return NullIterator.instance();
	}


	//	 ********** Change Tracking Type API**********

	/**
	 * @return Returns the changeTrackingType.
	 */
	public String getChangeTrackingType()
	{
		return this.changeTrackingType;
	}

	/**
	 * @param changeTrackingType The changeTrackingType to set.
	 */
	public void setChangeTrackingType(String changeTrackingType)
	{
		String oldChangeTrackingType = this.changeTrackingType;
		this.changeTrackingType = changeTrackingType;
		firePropertyChanged(CHANGE_TRACKING_TYPE_PROPERTY, oldChangeTrackingType, changeTrackingType);
	}


	//	 ********** MWReturningPolicy API**********

	public MWDescriptorPolicy getReturningPolicy() {
		return this.returningPolicy;
	}

	private MWReturningPolicy getReturningPolicyForTopLink()
	{
		return (MWReturningPolicy) this.returningPolicy.getPersistedPolicy();
	}

	private void setReturningPolicyForTopLink(MWReturningPolicy returningPolicy)
	{
		if (returningPolicy == null)
		{
			this.returningPolicy = new MWNullDescriptorPolicy(this);
		}
		else
		{
			this.returningPolicy = returningPolicy;
		}
	}

	private void setReturningPolicy(MWDescriptorPolicy returningPolicy) {
		Object old = this.returningPolicy;
		this.returningPolicy = returningPolicy;
		firePropertyChanged( RETURNING_POLICY_PROPERTY, old, returningPolicy);
	}

	public void addReturningPolicy() throws MWAdvancedPropertyAdditionException {
		if( this.returningPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException( RETURNING_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		setReturningPolicy( new MWRelationalReturningPolicy( this));
	}

	public void removeReturningPolicy() {
		if( this.returningPolicy.isActive()) {
			getReturningPolicy().dispose();
			setReturningPolicy(new MWNullDescriptorPolicy(this));
		}
		else {
			throw new MWAdvancedPropertyRemovalException( RETURNING_POLICY_PROPERTY, "policy does not exist on the descriptor");
		}
	}

	public boolean supportsReturningPolicy() {
		return true;
	}

	public boolean supportsCachingPolicy() {
		return true;
	}

	// *************** morphing support **************

	public MWTableDescriptor asMWTableDescriptor() {
		return this;
	}

	public void initializeOn(MWDescriptor newDescriptor) {
		((MWRelationalDescriptor) newDescriptor).initializeFromMWTableDescriptor(this);
	}

	@Override
	public boolean isRootDescriptor() {
		return true;
	}

	public boolean isTableDescriptor() {
		return true;
	}

	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		defaultsPolicy.applyAdvancedPolicyDefaults(this);
	}

	// ************** containment hierarchy **************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.sequenceNumberColumnHandle);
		children.add(this.sequenceNumberTableHandle);
		children.add(this.primaryTableHandle);
		children.add(this.interfaceAliasPolicy);
		children.add(this.multiTableInfoPolicy);
		children.add(this.returningPolicy);
	}

	private NodeReferenceScrubber buildSequenceNumberColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTableDescriptor.this.setSequenceNumberColumn(null);
			}
			public String toString() {
				return "MWTableDescriptor.buildSequenceNumberColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildSequenceNumberTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTableDescriptor.this.setSequenceNumberTable(null);
			}
			public String toString() {
				return "MWTableDescriptor.buildSequenceNumberTableScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPrimaryTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTableDescriptor.this.setPrimaryTable(null);
			}
			public String toString() {
				return "MWTableDescriptor.buildPrimaryTableScrubber()";
			}
		};
	}


	// ************* Automap Support *************

	/**
	 * use class inheritance to find a parent descriptor;
	 * if a parent descriptor is found, convert the descriptor to
	 * an aggregate descriptor if necessary
	 */
	protected void automapInheritanceHierarchyInternal(Collection automapDescriptors) {
		super.automapInheritanceHierarchyInternal(automapDescriptors);
		MWRelationalClassDescriptor parentDescriptor = this.automapFindParentDescriptor();
		if (parentDescriptor == null) {
			return;		// nothing to do
		}

		if (automapDescriptors.contains(parentDescriptor)) {
			parentDescriptor.automapSuperDescriptorInheritance(automapDescriptors);
		}
		this.automapDescriptorInheritance(parentDescriptor);
		if (parentDescriptor.isAggregateDescriptor()) {
			this.asMWAggregateDescriptor();
		}
	}

	/**
	 * climb up the descriptor class hierarchy looking for a class with an
	 * active descriptor
	 */
	private MWRelationalClassDescriptor automapFindParentDescriptor() {
		MWClass javaClass = this.getMWClass().getSuperclass();
		while (javaClass != null) {
			if (javaClass.isInterface()) {
				 // ???? something must be whacked: we have an interface as a superclass
				 return null;
			}
			MWDescriptor parentDescriptor = this.getProject().descriptorForType(javaClass);
			if ((parentDescriptor != null) && parentDescriptor.isActive()) {
				return (MWRelationalClassDescriptor) parentDescriptor;
			}
			javaClass = javaClass.getSuperclass();
		}
		return null;
	}

	private void automapDescriptorInheritance(MWRelationalClassDescriptor parentDescriptor) {
		MWInheritancePolicy ip = this.getInheritancePolicy();
		if ( ! ip.isActive()) {
			this.addInheritancePolicy();
			ip = this.getInheritancePolicy();
		}

		((MWDescriptorInheritancePolicy) ip).setIsRoot(false);
		((MWDescriptorInheritancePolicy) ip).setParentDescriptor(parentDescriptor);

		if ( ! this.getMWClass().isAbstract()) {
			MWInheritancePolicy rootIP = ip.getRootDescriptor().getInheritancePolicy();

			if (rootIP.isActive()) {
				MWClassIndicatorFieldPolicy fieldPolicy = (MWClassIndicatorFieldPolicy) rootIP.getClassIndicatorPolicy();
				MWClassIndicatorValue indicatorValue = fieldPolicy.getClassIndicatorValueForDescriptor(this);
				if (indicatorValue == null) {
					indicatorValue = fieldPolicy.addIndicator(this.getName(), this);
				} else {
					indicatorValue.setIndicatorValue(this.getName());
				}
				indicatorValue.setInclude(true);
			}
		}
	}

	protected boolean autoMapRequiresMetaDataInternal() {
		return (this.getPrimaryTable() == null);
	}

	protected void automapDirectMappingColumns() {
		MappingStringHolder[] mappingStringHolders = this.buildColumnlessDirectMappingStringHolders();
		ColumnStringHolder[] columnStringHolders = this.buildAllUnmappedColumnStringHolders();
		StringHolderPair[] pairs = PARTIAL_STRING_COMPARATOR_ENGINE.match(mappingStringHolders, columnStringHolders);
		for (int i = pairs.length; i-- > 0; ) {
			StringHolderPair pair = pairs[i];
			MappingStringHolder mappingHolder = (MappingStringHolder) pair.getStringHolder1();
			ColumnStringHolder columnHolder = (ColumnStringHolder) pair.getStringHolder2();
			if ((mappingHolder == null) || (columnHolder == null)) {
				continue;
			}
			if (pair.getScore() > 0.50) {		// ???
				MWRelationalDirectMapping mapping = (MWRelationalDirectMapping) mappingHolder.getMapping();
				mapping.setColumn(columnHolder.getColumn());
			}
		}
	}

	private MappingStringHolder[] buildColumnlessDirectMappingStringHolders() {
		Collection columnlessDirectMappings = new ArrayList();
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			((MWMapping) stream.next()).addColumnlessDirectMappingTo(columnlessDirectMappings);
		}
		return MappingStringHolder.buildHolders(columnlessDirectMappings);
	}

	private ColumnStringHolder[] buildAllUnmappedColumnStringHolders() {
		Collection unmappedColumns = new ArrayList();
		for (Iterator stream = this.associatedTables(); stream.hasNext(); ) {
			CollectionTools.addAll(unmappedColumns, ((MWTable) stream.next()).columns());
		}

		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			Collection writtenFields = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFields);
			unmappedColumns.removeAll(writtenFields);
		}

		return ColumnStringHolder.buildHolders(unmappedColumns);
	}



	//************* Problem Handling *************

	/** Check for any problems and add them to the specified collection. */
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);

		// primary table and primary keys
		this.checkPrimaryTable(newProblems);
		this.checkPrimaryKeys(newProblems);
		this.checkPrimaryKeysMapped(newProblems);
		this.checkPrimaryKeysMatchParent(newProblems);

		this.checkPrimaryKeyMappingsNotReadOnly(newProblems);

		// sequencing
		this.checkSequencing(newProblems);

		this.checkClassIndicatorFieldMapping(newProblems);

		// queries
		this.checkQueries(newProblems);
	}

	private void checkPrimaryTable(List newProblems) {
		if (this.getPrimaryTable() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_PRIMARY_TABLE_SPECIFIED));
		}
	}

	private void checkPrimaryKeys(List newProblems) {
		boolean hasParent = this.getInheritancePolicy().getParentDescriptor() != null;

		if (! (hasParent || this.getPrimaryTable() == null || this.primaryKeyPolicy().primaryKeysSize() != 0)) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED));
		}
	}

	private void checkPrimaryKeysMapped(List newProblems) {
		boolean hasParent = (this.getInheritancePolicy().getParentDescriptor() != null);

		boolean noTable = this.getPrimaryTable() == null;

		if (hasParent || noTable) {
			return;
		}

		for (Iterator stream = this.primaryKeyPolicy().primaryKeys(); stream.hasNext(); ) {
			MWColumn primaryKey = (MWColumn) stream.next();
			Collection mappingsForField = this.allWritableMappingsForField(primaryKey);
			if (mappingsForField.size() == 0) {
			 	newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_PRIMARY_KEY_FIELD_UNMAPPED, 
			 								 primaryKey.getName()));
			}
		}
	}

	private void checkPrimaryKeysMatchParent(List newProblems) {
		boolean noParent = ! this.getInheritancePolicy().isActive() || this.getInheritancePolicy().getParentDescriptor() == null;
		boolean noPrimaryTable = this.getPrimaryTable() == null;
		if (noParent || noPrimaryTable) {
			return;
		}
		// if a multi table policy is defined, the primary keys don't
		// have to be the same as the parent if they define a reference
		// associating the differently named primary keys
		if (this.getMultiTableInfoPolicy().isActive() && ((MWDescriptorMultiTableInfoPolicy) this.getMultiTableInfoPolicy()).pksAcrossMultipleTablesTest(true)) {
			return;
		}
		if (this.getInheritancePolicy().getParentDescriptor() instanceof MWTableDescriptor) {
			Collection parentKeyNamesCollection = CollectionTools.collection(((MWTableDescriptor) this.getInheritancePolicy().getParentDescriptor()).primaryKeyNames());
			// parent and child must at least have the same number of
			// keys.
			if (this.primaryKeysSize() != parentKeyNamesCollection.size()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_PK_SIZE_DONT_MATCH));
			}
			for (Iterator stream = this.primaryKeys(); stream.hasNext(); ) {
				MWColumn column = (MWColumn) stream.next();
				if ( ! parentKeyNamesCollection.contains(column.getName())) {
					newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_PKS_DONT_MATCH_PARENT));
				}
			}
			//bug #6619070 must check for duplicate pk mappings as well.
			MWTableDescriptor parentDescriptor = (MWTableDescriptor)this.getInheritancePolicy().getParentDescriptor();
			for (Iterator stream = parentDescriptor.primaryKeyMappings(); stream.hasNext(); ) {
				MWMapping parentPkMapping = (MWMapping)stream.next();
				MWMapping childMapping = this.mappingNamed(parentPkMapping.getInstanceVariable().getName());
				if (childMapping != null) {
					if (CollectionTools.collection(this.primaryKeyMappings()).contains(childMapping)) {
						if (((MWRelationalDirectMapping)parentPkMapping).getColumn() == ((MWRelationalDirectMapping)childMapping).getColumn()) {
							newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_PRIMARY_KEY_MAPPING_DUPLICATED_IN_HIERARCHY, childMapping));
						}
					}
				}
			}
		}
	}


	private void checkSequencing(List newProblems) {
		if ( ! this.usesSequencing()) {
			return;
		}
		if (StringTools.stringIsEmpty(this.getSequenceNumberName())) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NAME_SPECIFIED));
		}

		if (this.getSequenceNumberTable() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_SEQUENCE_TABLE_NOT_SPECIFIED));
			return;
		}
		if (this.getSequenceNumberColumn() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NUMBER_FIELD_SPECIFIED));
		}
		if ( ! CollectionTools.contains(this.associatedTables(), this.getSequenceNumberTable())) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_SEQUENCE_TABLE_NOT_VALID));
		}
	}

	private void checkQueries(List newProblems) {
		for (Iterator queriesOuter = this.getRelationalQueryManager().queries(); queriesOuter.hasNext(); ) {
			String queryOneSignature = ((MWQuery)queriesOuter.next()).signature();
			boolean firstMatch = true;
			for (Iterator queriesInner = this.getRelationalQueryManager().queries(); queriesInner.hasNext(); ) {
				MWQuery query = (MWQuery) queriesInner.next();
				if (query.signature().equals(queryOneSignature)) {
					if(!firstMatch) {
						String queryName = query.getName();
						newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_MULTIPLE_QUERIES_WITH_SAME_SIGNATURE, queryName));
					} else {
						firstMatch = false;
					}
				}
			}
		}
	}

//TODO this test applies to root eis descriptors as well
	private void checkPrimaryKeyMappingsNotReadOnly(List newProblems) {
		// Test to see if there is at least one mapping for each field in the primary key that is writable."
		MWInheritancePolicy policy = getInheritancePolicy();
		boolean hasParent = policy.getParentDescriptor() != null;
		boolean noTable = getPrimaryTable() == null;
		boolean noPrimaryKeyFields = getRelationalTransactionalPolicy().getPrimaryKeyPolicy().primaryKeysSize() == 0;
		if (hasParent || noTable || noPrimaryKeyFields) {
			return;
		}
		// Loop through all of the primary key fields to make sure 
		// that each one has at least one writable mapping.
		for (Iterator stream = getRelationalTransactionalPolicy().getPrimaryKeyPolicy().primaryKeys(); stream.hasNext(); ) {
			MWColumn primaryKey = (MWColumn) stream.next();
			if (allWritableMappingsForField(primaryKey).size() == 0) {
				newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_PRIMARY_KEY_MAPPING_READ_ONLY, primaryKey.getName()));
			}
		}
	}

	protected String multipleMappingsWriteFieldProblemResourceStringKey() {
		return ProblemConstants.MULTIPLE_MAPPINGS_WRITE_TO_COLUMN;
	}

	private void checkClassIndicatorFieldMapping(List newProblems) {
		MWInheritancePolicy inheritPolicy = getInheritancePolicy();
		if (!inheritPolicy.isRoot()) {
			return;
		}
		MWClassIndicatorPolicy indicatorPolicy = inheritPolicy.getClassIndicatorPolicy();
		if (indicatorPolicy.getType() == MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE
			|| indicatorPolicy.getType() == MWClassIndicatorPolicy.NULL_TYPE) {
				return;
		}
		MWColumn indicatorField = ((MWRelationalClassIndicatorFieldPolicy)indicatorPolicy).getColumn();
		if (indicatorField == null || indicatorField.isPrimaryKey()) {
			return;
		}
		if(allWritableMappingsForField(indicatorField).size() != 0) {
			newProblems.add(buildProblem(ProblemConstants.WRITABLE_MAPPING_FOR_CLASS_INDICATOR_FIELD, indicatorField.getName()));
		}
	}


	//************** runtime conversion **************/

	protected ClassDescriptor buildBasicRuntimeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(getMWClass().getName());
		return descriptor;
	}

	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = super.buildRuntimeDescriptor();

		if (getPrimaryTable() != null) {
			runtimeDescriptor.setTableName(getPrimaryTable().getName());
		}

		if (usesSequencing() && getSequenceNumberColumn() != null) {	   
			runtimeDescriptor.setSequenceNumberField(new DatabaseField(getSequenceNumberColumn().qualifiedName()));
			runtimeDescriptor.setSequenceNumberName(getSequenceNumberName());
		}

		// Change tracking type
		String ctt = getChangeTrackingType();
		if (ATTRIBUTE_LEVEL_CHANGE_TRACKING.equals(ctt))
		{
			// set attr. level change tracking
			runtimeDescriptor.setObjectChangePolicy(new AttributeChangeTrackingPolicy());
		}
		else if (OBJECT_LEVEL_CHANGE_TRACKING.equals(ctt))
		{
			runtimeDescriptor.setObjectChangePolicy(new ObjectChangeTrackingPolicy());
		}
		// else deferred.  do not set this, it's implicit.  We don't want to create an entry for this
		// case, as this element is only for the conversion case.

		this.interfaceAliasPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.multiTableInfoPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.returningPolicy.adjustRuntimeDescriptor(runtimeDescriptor);

		return runtimeDescriptor;
	}

	protected void adjustUserDefinedQueryKeys(ClassDescriptor runtimeDescriptor) {
		for (Iterator queryKeys = userDefinedQueryKeys(); queryKeys.hasNext();) {
			MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey) queryKeys.next();
			if (queryKey.getColumn() != null){
				runtimeDescriptor.addDirectQueryKey(queryKey.getName(), queryKey.getColumn().qualifiedName());
			}
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWTableDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalClassDescriptor.class);

		XMLCompositeObjectMapping primaryTableMapping = new XMLCompositeObjectMapping();
		primaryTableMapping.setAttributeName("primaryTableHandle");
		primaryTableMapping.setGetMethodName("getPrimaryTableHandleForTopLink");
		primaryTableMapping.setSetMethodName("setPrimaryTableHandleForTopLink");
		primaryTableMapping.setReferenceClass(MWTableHandle.class);
		primaryTableMapping.setXPath("primary-table-handle");
		descriptor.addMapping(primaryTableMapping);

		XMLDirectMapping usesSequencingMapping = (XMLDirectMapping) descriptor.addDirectMapping("usesSequencing", "uses-sequencing/text()");
		usesSequencingMapping.setNullValue(Boolean.FALSE);

		XMLDirectMapping sequenceNumberNameMapping = (XMLDirectMapping) descriptor.addDirectMapping("sequenceNumberName", "sequence-number-name/text()");
		sequenceNumberNameMapping.setNullValue("");

		XMLCompositeObjectMapping sequenceNumberTableHandleMapping = new XMLCompositeObjectMapping();
		sequenceNumberTableHandleMapping.setAttributeName("sequenceNumberTableHandle");
		sequenceNumberTableHandleMapping.setGetMethodName("getSequenceNumberTableHandleForTopLink");
		sequenceNumberTableHandleMapping.setSetMethodName("setSequenceNumberTableHandleForTopLink");
		sequenceNumberTableHandleMapping.setReferenceClass(MWTableHandle.class);
		sequenceNumberTableHandleMapping.setXPath("sequence-number-table-handle");
		descriptor.addMapping(sequenceNumberTableHandleMapping);

		XMLCompositeObjectMapping sequenceNumberColumnHandleMapping = new XMLCompositeObjectMapping();
		sequenceNumberColumnHandleMapping.setAttributeName("sequenceNumberColumnHandle");
		sequenceNumberColumnHandleMapping.setGetMethodName("getSequenceNumberColumnHandleForTopLink");
		sequenceNumberColumnHandleMapping.setSetMethodName("setSequenceNumberColumnHandleForTopLink");
		sequenceNumberColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		sequenceNumberColumnHandleMapping.setXPath("sequence-number-column-handle");
		descriptor.addMapping(sequenceNumberColumnHandleMapping);

		descriptor.addDirectMapping("changeTrackingType", "change-tracking-type/text()");

		XMLCompositeObjectMapping interfaceAliasPolicyMapping = new XMLCompositeObjectMapping();
		interfaceAliasPolicyMapping.setAttributeName("interfaceAliasPolicy");
		interfaceAliasPolicyMapping.setReferenceClass(MWDescriptorInterfaceAliasPolicy.class);
		interfaceAliasPolicyMapping.setSetMethodName("setInterfaceAliasPolicyForTopLink");
		interfaceAliasPolicyMapping.setGetMethodName("getInterfaceAliasPolicyForTopLink");
		interfaceAliasPolicyMapping.setXPath("interface-alias-policy");
		descriptor.addMapping(interfaceAliasPolicyMapping);

		XMLCompositeObjectMapping multiTableInfoPolicyMapping = new XMLCompositeObjectMapping();
		multiTableInfoPolicyMapping.setAttributeName("multiTableInfoPolicy");
		multiTableInfoPolicyMapping.setReferenceClass(MWDescriptorMultiTableInfoPolicy.class);
		multiTableInfoPolicyMapping.setSetMethodName("setMultiTableInfoPolicyForTopLink");
		multiTableInfoPolicyMapping.setGetMethodName("getMultiTableInfoPolicyForTopLink");
		multiTableInfoPolicyMapping.setXPath("multi-table-info-policy");
		descriptor.addMapping(multiTableInfoPolicyMapping);

		XMLCompositeObjectMapping returningPolicyMapping = new XMLCompositeObjectMapping();
		returningPolicyMapping.setAttributeName("returningPolicy");
		returningPolicyMapping.setReferenceClass(MWRelationalReturningPolicy.class);
		returningPolicyMapping.setSetMethodName("setReturningPolicyForTopLink");
		returningPolicyMapping.setGetMethodName("getReturningPolicyForTopLink");
		returningPolicyMapping.setXPath("returning-policy");
		descriptor.addMapping(returningPolicyMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWTableHandle getPrimaryTableHandleForTopLink() {
		return (this.primaryTableHandle.getTable() == null) ? null : this.primaryTableHandle;
	}
	private void setPrimaryTableHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildPrimaryTableScrubber();
		this.primaryTableHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWTableHandle getSequenceNumberTableHandleForTopLink() {
		return (this.sequenceNumberTableHandle.getTable() == null) ? null : this.sequenceNumberTableHandle;
	}
	private void setSequenceNumberTableHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildSequenceNumberTableScrubber();
		this.sequenceNumberTableHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getSequenceNumberColumnHandleForTopLink() {
		return (this.sequenceNumberColumnHandle.getColumn() == null) ? null : this.sequenceNumberColumnHandle;
	}
	private void setSequenceNumberColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildSequenceNumberColumnScrubber();
		this.sequenceNumberColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
