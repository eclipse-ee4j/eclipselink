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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

/**
 * A reference describes a foreign-key relationship from a "source" table
 * (the reference's "parent") to a "target" table. The foreign-key constraint
 * can either be real ("on database") or virtual (only implied by the joins
 * performed by TopLink).
 */
public final class MWReference extends MWModel {
	/** the name should never be null or empty */
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private MWTableHandle targetTableHandle;
		public static final String TARGET_TABLE_PROPERTY = "targetTable";

	private Collection columnPairs;
		public static final String COLUMN_PAIRS_COLLECTION = "columnPairs";

	/**
	 * indicate whether the reference is an actual database constraint or
	 * only used by TopLink for joining
	 */
	private volatile boolean onDatabase;
		public static final String ON_DATABASE_PROPERTY = "onDatabase";


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWReference() {
		super();
	}

	MWReference(MWTable parent, String name, MWTable targetTable) {
		super(parent);
		this.name = name;
		this.targetTableHandle.setTable(targetTable);
	}


	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.targetTableHandle = new MWTableHandle(this, this.buildTargetTableScrubber());
		this.columnPairs = new Vector();
		this.onDatabase = false;
	}


	// ********** accessors **********

	public MWTable getSourceTable() {
		return (MWTable) this.getParent();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.getSourceTable().checkReferenceName(name);
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}

	public MWTable getTargetTable() {
		return this.targetTableHandle.getTable();
	}

	/**
	 * if the target table changes, we clear out all the
	 * column pairs, because they are now invalid
	 */
	public void setTargetTable(MWTable targetTable) {
		Object old = this.targetTableHandle.getTable();
		this.targetTableHandle.setTable(targetTable);
		this.firePropertyChanged(TARGET_TABLE_PROPERTY, old, targetTable);
		if (this.attributeValueHasChanged(old, targetTable)) {
			this.clearColumnPairs();
		}
	}

	public boolean isOnDatabase() {
		return this.onDatabase;
	}

	public void setOnDatabase(boolean onDatabase) {
		boolean old = this.onDatabase;
		this.onDatabase = onDatabase;
		this.firePropertyChanged(ON_DATABASE_PROPERTY, old, onDatabase);
	}

	// ********** column pairs
	public Iterator columnPairs() {
		return new CloneIterator(this.columnPairs) {
			protected void remove(Object current) {
				MWReference.this.removeColumnPair((MWColumnPair) current);
			}
		};
	}

	public int columnPairsSize() {
		return this.columnPairs.size();
	}

	public MWColumnPair addColumnPair(MWColumn sourceColumn, MWColumn targetColumn) {
		if (sourceColumn.getTable() != this.getSourceTable()) {
			throw new IllegalArgumentException("invalid source column: " + sourceColumn);
		}
		if (targetColumn.getTable() != this.getTargetTable()) {
			throw new IllegalArgumentException("invalid target column: " + targetColumn);
		}
		return this.addColumnPair(new MWColumnPair(this, sourceColumn, targetColumn));
	}

	private MWColumnPair addColumnPair(MWColumnPair columnPair) {
		this.addItemToCollection(columnPair, this.columnPairs,  COLUMN_PAIRS_COLLECTION);
		return columnPair;
	}

	public void removeColumnPair(MWColumnPair columnPair) {
		this.removeItemFromCollection(columnPair, this.columnPairs, COLUMN_PAIRS_COLLECTION);
	}

	public void removeColumnPairs(Iterator pairs) {
		while (pairs.hasNext()) {
			this.removeColumnPair((MWColumnPair) pairs.next());
		}
	}

	public void removeColumnPairs(Collection pairs) {
		this.removeColumnPairs(pairs.iterator());
	}

	private void clearColumnPairs() {
		for (Iterator stream = this.columnPairs(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}

	/**
	 * used by MWColumnPairHandle
	 */
	public MWColumnPair columnPairNamed(String columnPairName) {
		synchronized (this.columnPairs) {
			for (Iterator stream = this.columnPairs.iterator(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				if (pair.getName().equals(columnPairName)) {
					return pair;
				}
			}
		}
		return null;
	}

	public MWColumnPair columnPairFor(MWColumn sourceColumn, MWColumn targetColumn) {
		synchronized (this.columnPairs) {
			for (Iterator stream = this.columnPairs.iterator(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				if (pair.pairs(sourceColumn, targetColumn)) {
					return pair;
				}
			}
		}
		return null;
	}


	// ********** queries **********

	/**
	 * this test is not entirely accurate, but it's close enough...
	 */
	public boolean isForeignKeyReference() {
		return ! this.isPrimaryKeyReference();
	}

	/**
	 * return whether the reference is a JOIN between the
	 * primary keys on the source table with the primary keys
	 * on the target table
	 */
	public boolean isPrimaryKeyReference() {
		Collection sourcePrimaryKeys = CollectionTools.collection(this.getSourceTable().primaryKeyColumns());
		if (sourcePrimaryKeys.size() != this.columnPairs.size()) {
			return false;
		}

		Collection targetPrimaryKeys = CollectionTools.collection(this.getTargetTable().primaryKeyColumns());
		if (targetPrimaryKeys.size() != this.columnPairs.size()) {
			return false;
		}

		synchronized (this.columnPairs) {
			for (Iterator stream = this.columnPairs.iterator(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				if ( ! sourcePrimaryKeys.remove(pair.getSourceColumn())) {
					return false;
				}
				if ( ! targetPrimaryKeys.remove(pair.getTargetColumn())) {
					return false;
				}
			}
		}
		return true;
	}


	// ********** model synchronization support **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.targetTableHandle);
		synchronized (this.columnPairs) { children.addAll(this.columnPairs); }
	}

	private NodeReferenceScrubber buildTargetTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWReference.this.setTargetTable(null);
			}
			public String toString() {
				return "MWReference.buildTargetTableScrubber()";
			}
		};
	}


	//************** problems ***************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if (this.columnPairs.isEmpty()) {
			currentProblems.add(this.buildProblem(ProblemConstants.REFERENCE_NO_COLUMN_PAIRS, this.getName()));
		}
		if (this.getTargetTable() == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.REFERENCE_NO_TARGET_TABLE, this.getName()));
		}
	}


	// ********** importing/refreshing **********

	/**
	 * the reference has the same name as the specified "external" foreign key,
	 * synchronize the reference's column pairs with the "external" foreign key's column pairs
	 */
	void refreshColumnPairs(ExternalForeignKey externalForeignKey) {
		// after we have looped through the "external" column pairs,
		// 'removedColumnPairs' will be left with the column pairs that need to be removed
		Collection removedColumnPairs;
		synchronized (this.columnPairs) {
			removedColumnPairs = new HashSet(this.columnPairs);
		}
		ExternalForeignKeyColumnPair[] externalPairs = externalForeignKey.getColumnPairs();
		for (int i = externalPairs.length; i-- > 0; ) {
			this.refreshColumnPair(externalPairs[i], removedColumnPairs);
		}
		this.removeColumnPairs(removedColumnPairs);
	}

	private void refreshColumnPair(ExternalForeignKeyColumnPair externalPair, Collection removedColumnPairs) {
		for (Iterator stream = removedColumnPairs.iterator(); stream.hasNext(); ) {
			if (((MWColumnPair) stream.next()).matches(externalPair)) {
				stream.remove();
				return;
			}
		}
		this.addColumnPair(this.sourceColumn(externalPair), this.targetColumn(externalPair));
	}

	/**
	 * return the column in the source table with the same name as the
	 * "external" column pair's source column
	 */
	MWColumn sourceColumn(ExternalForeignKeyColumnPair externalPair) {
		return this.getSourceTable().column(externalPair.getSourceColumn());
	}

	/**
	 * return the column in the target table with the same name as the
	 * "external" column pair's target column
	 */
	MWColumn targetColumn(ExternalForeignKeyColumnPair externalPair) {
		return this.getTargetTable().column(externalPair.getTargetColumn());
	}

	/**
	 * return whether the reference has the same column pairs as
	 * the specified "external" foreign key;
	 * we compare column pairs because some references have system-
	 * generated names that can change over the life of the reference
	 * and we don't want to remove a reference simply because its
	 * name changes - this would force the user to rebuild any objects
	 * that referenced the [mistakenly] removed reference, e.g. mappings
	 */
	boolean matchesColumnPairs(ExternalForeignKey externalForeignKey) {
		ExternalForeignKeyColumnPair[] externalPairs = externalForeignKey.getColumnPairs();
		int externalPairsLength = externalPairs.length;
		if (this.columnPairs.size() != externalPairsLength) {
			return false;
		}
		Collection columnPairsCopy;
		synchronized (this.columnPairs) {
			columnPairsCopy = new HashSet(this.columnPairs);
		}
		for (int i = externalPairsLength; i-- > 0; ) {
			ExternalForeignKeyColumnPair externalPair = externalPairs[i];
			boolean match = false;
			for (Iterator stream = columnPairsCopy.iterator(); stream.hasNext(); ) {
				if (((MWColumnPair) stream.next()).matches(externalPair)) {
					stream.remove();
					match = true;
					break;
				}
			}
			if ( ! match) {
				return false;
			}
		}
		return true;
	}


	// ********** runtime conversion **********

	ForeignKeyConstraint buildRuntimeConstraint() {
		ForeignKeyConstraint fkc = new ForeignKeyConstraint();
		fkc.setName(this.getName());
		if (this.getTargetTable() != null) {
			fkc.setTargetTable(this.getTargetTable().getName());
		}
		synchronized (this.columnPairs) {
			for (Iterator stream = this.columnPairs.iterator(); stream.hasNext(); ) {
				((MWColumnPair) stream.next()).configureRuntimeConstraint(fkc);
			}
		}
		return fkc;
	}


	// ********** displaying and printing **********

	public String displayString() {
		return this.name;
	}

	public void toString(StringBuffer sb) {
		sb.append(this.name);
		sb.append(" : ");
		this.printTableNameOn(this.getSourceTable(), sb);
		sb.append("=>");
		this.printTableNameOn(this.getTargetTable(), sb);
	}

	private void printTableNameOn(MWTable table, StringBuffer sb) {
		sb.append((table == null) ? "null" : table.getName());
	}


	 // ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWReference.class);

		descriptor.addDirectMapping("name", "name/text()");

		XMLCompositeObjectMapping targetTableHandleMapping = new XMLCompositeObjectMapping();
		targetTableHandleMapping.setAttributeName("targetTableHandle");
		targetTableHandleMapping.setGetMethodName("getTargetTableHandleForTopLink");
		targetTableHandleMapping.setSetMethodName("setTargetTableHandleForTopLink");
		targetTableHandleMapping.setReferenceClass(MWTableHandle.class);
		targetTableHandleMapping.setXPath("target-table-handle");
		descriptor.addMapping(targetTableHandleMapping);

		((XMLDirectMapping) descriptor.addDirectMapping("onDatabase", "on-database/text()")).setNullValue(Boolean.FALSE);

		XMLCompositeCollectionMapping columnPairsMapping = new XMLCompositeCollectionMapping();
		columnPairsMapping.setAttributeName("columnPairs");
		columnPairsMapping.setReferenceClass(MWColumnPair.class);
		columnPairsMapping.setXPath("column-pairs/column-pair");
		descriptor.addMapping(columnPairsMapping);
	
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWTableHandle getTargetTableHandleForTopLink() {
		return (this.targetTableHandle.getTable() == null) ? null : this.targetTableHandle;
	}
	private void setTargetTableHandleForTopLink(MWTableHandle targetTableHandle) {
		NodeReferenceScrubber scrubber = this.buildTargetTableScrubber();
		this.targetTableHandle = ((targetTableHandle == null) ? new MWTableHandle(this, scrubber) : targetTableHandle.setScrubber(scrubber));
	}

}
