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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Record;

public final class MWDescriptorMultiTableInfoPolicy extends MWAbstractDescriptorPolicy 
{
	private Collection secondaryTableHolders;
		public static final String SECONDARY_TABLE_HOLDERS_COLLECTION = "secondaryTableHolders";
	
	/** Default constructor - for TopLink use only */
	private MWDescriptorMultiTableInfoPolicy() {
		super();
	}
	
	public MWDescriptorMultiTableInfoPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}

	
	// **************** Initialization ***************
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.secondaryTableHolders = new Vector();
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.secondaryTableHolders) { children.addAll(this.secondaryTableHolders); }
	}

	
	// **************** Accessors ***************
	
	public MWSecondaryTableHolder addSecondaryTable(MWTable secondaryTable) {
		MWSecondaryTableHolder associationHolder = new MWSecondaryTableHolder(this, secondaryTable);
		addItemToCollection(associationHolder, this.secondaryTableHolders, SECONDARY_TABLE_HOLDERS_COLLECTION);
		return associationHolder;
	}

	public void removeSecondaryTable(MWTable table) {
		for (Iterator stream = secondaryTableHolders(); stream.hasNext();) {
			MWSecondaryTableHolder tableAssociationHolder = (MWSecondaryTableHolder) stream.next();
			if (tableAssociationHolder.getTable() == table) {
				removeSecondaryTableHolder(tableAssociationHolder);
				return;
			}
		}
	}

	void removeSecondaryTableHolder(MWSecondaryTableHolder holder) {
		removeItemFromCollection(holder, this.secondaryTableHolders, SECONDARY_TABLE_HOLDERS_COLLECTION);
	}
	
	public MWSecondaryTableHolder secondaryTableHolderFor(MWTable table) {
		for (Iterator stream = secondaryTableHolders(); stream.hasNext();) {
			MWSecondaryTableHolder tableHolder = (MWSecondaryTableHolder) stream.next();
			if (tableHolder.getTable() == table) {
				return tableHolder;
			}
		}
		return null;
	}
	
	public Iterator secondaryTableHolders() {
		return new CloneIterator(this.secondaryTableHolders);
	}

	private MWReference secondaryReferenceFor(MWTable table) {
		for (Iterator stream = secondaryReferences(); stream.hasNext(); ) {
			MWReference reference = (MWReference) stream.next();
			if (reference.getSourceTable() == table || reference.getTargetTable() == table) {
				return reference;
			}
		}
		return null;
	}
	
	private Iterator secondaryReferences() {
		Collection references = new ArrayList();
		for (Iterator stream = secondaryTableHolders(); stream.hasNext(); ) {
			MWSecondaryTableHolder holder = (MWSecondaryTableHolder) stream.next();
			if (!holder.primaryKeysHaveSameName()) {
				if (holder.getReference() != null) {
					references.add(holder.getReference());
				}
			}
		}
		return references.iterator();
	}
	
	public int secondaryTableHoldersSize() {
		return this.secondaryTableHolders.size();
	}

	public Iterator secondaryTables() {
		return new TransformationIterator(secondaryTableHolders()) {
			protected Object transform(Object next) {
				return ((MWSecondaryTableHolder) next).getTable();
			}
		};
	}
	
	
	// ************** containment hierarchy *************
	
	private MWTableDescriptor getTableDescriptor() {
		return (MWTableDescriptor) getOwningDescriptor();
	}
	
	
	// ************* MWAbstractDescriptorPolicy overrides ***************
	
	public boolean isActive() {
		return true;
	}
	
	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}
	

	// ************* Problems overrides ***************

	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		checkPrimaryKeysAcrossMultipleTables(problems);
	}
	
	private void checkPrimaryKeysAcrossMultipleTables(List newProblems) {
		if(!pksAcrossMultipleTablesTest(false)) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_MULTI_TABLE_PKS_DONT_MATCH));
		}
	}

	public boolean pksAcrossMultipleTablesTest(boolean moreThanOneTableMustBeDefined) {
		int associatedTablesSize = getTableDescriptor().associatedTablesSize();
		if (associatedTablesSize < 2) {
			return !moreThanOneTableMustBeDefined;
		}
		MWTable primaryTable = getTableDescriptor().getPrimaryTable();
		if (primaryTable == null) {
			return false;
		}
		for (Iterator tables = getTableDescriptor().associatedTables(); tables.hasNext(); ) {
			MWTable secondaryTable = (MWTable) tables.next();
			// Are they related by FK's?
			MWSecondaryTableHolder tableAssociationHolder = secondaryTableHolderFor(secondaryTable);
			if (tableAssociationHolder == null) {
				continue;
			}
			if (tableAssociationHolder.primaryKeysHaveSameName()) {
				// Do the PK's match?
				for (Iterator columns = primaryTable.primaryKeyColumns(); columns.hasNext(); ) {
					MWColumn pk = (MWColumn) columns.next();
					MWColumn matchingPk = secondaryTable.columnNamed(pk.getName());
					if (matchingPk == null || ! matchingPk.isPrimaryKey()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// ************** runtime conversion *************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		runtimeDescriptor.getAdditionalTablePrimaryKeyFields();  // lazy initialization
		Iterator secondaryTables = getTableDescriptor().secondaryTables();
		// add all secondary tables
		while (secondaryTables.hasNext()) 
		{
			MWTable secondaryTable = (MWTable) secondaryTables.next(); 
			((RelationalDescriptor) runtimeDescriptor).addTableName(secondaryTable.getName());
		}
		// add all associations
		for (Iterator tableAssocs = secondaryReferences(); tableAssocs.hasNext(); ) {
			MWReference ref = (MWReference) tableAssocs.next();
			for (Iterator stream = ref.columnPairs(); stream.hasNext(); ) {
				MWColumnPair columnPair = (MWColumnPair) stream.next();
				if (columnPair.getSourceColumn() != null && columnPair.getTargetColumn() != null)
				{
				   String sourceColumnName = columnPair.getSourceColumn().qualifiedName();
				   String targetColumnName = columnPair.getTargetColumn().qualifiedName();
				   runtimeDescriptor.addForeignKeyFieldNameForMultipleTable(sourceColumnName, targetColumnName);
				}
			}
		}
	}
	

	
	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorMultiTableInfoPolicy.class);
	
		XMLCompositeCollectionMapping secondaryTableAssociationHoldersMapping = new XMLCompositeCollectionMapping();
		secondaryTableAssociationHoldersMapping.setAttributeName("secondaryTableHolders");
		secondaryTableAssociationHoldersMapping.setReferenceClass(MWSecondaryTableHolder.class);
		secondaryTableAssociationHoldersMapping.setXPath("secondary-table-holders/table-holder");
		descriptor.addMapping(secondaryTableAssociationHoldersMapping);

		return descriptor;
	}
	
}
