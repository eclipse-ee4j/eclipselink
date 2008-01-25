/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
import deprecated.sdk.SDKFieldValue;
import org.eclipse.persistence.sessions.Record;

public final class MWDescriptorMultiTableInfoPolicy extends MWAbstractDescriptorPolicy 
{
	private Collection secondaryTableHolders;
		public static final String SECONDARY_TABLE_HOLDERS_COLLECTION = "secondaryTableHolders";
	
	//used only for legacy projects
	private Collection legacySecondaryReferenceHandles;
	
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
		// these legacy handles are only briefly children
		if (this.legacySecondaryReferenceHandles != null) {
			synchronized (this.legacySecondaryReferenceHandles) { children.addAll(this.legacySecondaryReferenceHandles); }
		}
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


	public static ClassDescriptor legacy50BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.setJavaClass(MWDescriptorMultiTableInfoPolicy.class);
		descriptor.descriptorIsAggregate();

		descriptor.setTableName("multi-table-info-policy");

		TransformationMapping secondaryReferenceHandlesMapping = new TransformationMapping();
		secondaryReferenceHandlesMapping.setAttributeName("legacySecondaryReferenceHandles");
		secondaryReferenceHandlesMapping.setAttributeTransformation("legacy50GetSecondaryReferenceHandlesFromRecordForTopLink");
		descriptor.addMapping(secondaryReferenceHandlesMapping);

		return descriptor;
	}

	private Collection legacy50GetSecondaryReferenceHandlesFromRecordForTopLink(Record record) {
		Collection refHandles = new Vector();
		SDKFieldValue refsValue = (SDKFieldValue) record.get("secondary-table-associations");
		
		if (refsValue == null) {
			return refHandles;
		}
		
		for (Iterator stream = refsValue.getElements().iterator(); stream.hasNext(); ) {
			Object next = stream.next();
			
			if (next instanceof Record) {
				Record refRecord = (Record) next;
				// these handles are garbage-collected the project is read in
				MWReferenceHandle handle = new MWReferenceHandle(this, NodeReferenceScrubber.NULL_INSTANCE);
				handle.legacySetReferenceTableName((String) refRecord.get("source-table"));
				handle.legacySetReferenceName((String) refRecord.get("name"));
				refHandles.add(handle);
			}
		}
		
		return refHandles;
	}
	
	protected void legacy50PostBuild(DescriptorEvent event) {
		super.legacy50PostBuild(event);
		this.secondaryTableHolders = new Vector();
	}
	
	/**
	 * called by MWTableDescriptor.legacyXXPostPostProjectBuild()
	 */
	protected void legacyResolveReferences() {
		for (Iterator stream = this.legacySecondaryReferenceHandles.iterator(); stream.hasNext(); ) {
			MWReference reference = ((MWReferenceHandle) stream.next()).getReference();
			MWSecondaryTableHolder holder = this.secondaryTableHolderFor(reference.getSourceTable());
			if (holder == null) {
				holder = this.secondaryTableHolderFor(reference.getTargetTable());
			}
			if (holder != null) {
				holder.setPrimaryKeysHaveSameName(false);
				holder.setReference(reference);
			}
		}
		this.legacySecondaryReferenceHandles = null;
	}

	
	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.setJavaClass(MWDescriptorMultiTableInfoPolicy.class);
		descriptor.descriptorIsAggregate();

		descriptor.setTableName("MultiTableInfoPolicy");

		TransformationMapping secondaryReferenceHandlesMapping = new TransformationMapping();
		secondaryReferenceHandlesMapping.setAttributeName("legacySecondaryReferenceHandles");
		secondaryReferenceHandlesMapping.setAttributeTransformation("legacy45GetSecondaryReferenceHandlesFromRecordForTopLink");
		descriptor.addMapping(secondaryReferenceHandlesMapping);

		return descriptor;
	}
	
	protected void legacy45PostBuild(DescriptorEvent event) {
		super.legacy45PostBuild(event);
		this.secondaryTableHolders = new Vector();
	}
	
	private Collection legacy45GetSecondaryReferenceHandlesFromRecordForTopLink(Record record) {
		Collection refHandles = new Vector();
		SDKFieldValue refsValue = (SDKFieldValue) record.get("secondaryTableAssociations");
		
		if (refsValue == null) {
			return refHandles;
		}
		
		for (Iterator stream = refsValue.getElements().iterator(); stream.hasNext(); ) {
			Object next = stream.next();
			
			if (next instanceof Record) {
				Record refRecord = (Record) next;
				// these handles are garbage-collected the project is read in
				MWReferenceHandle handle = new MWReferenceHandle(this, NodeReferenceScrubber.NULL_INSTANCE);
				handle.legacySetReferenceTableName((String) refRecord.get("sourceTableId"));
				handle.legacySetReferenceName((String) refRecord.get("name"));
				refHandles.add(handle);
			}
		}
		
		return refHandles;
	}

}
