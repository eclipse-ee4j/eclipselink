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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;


public final class MWSecondaryTableHolder extends MWModel {

	private MWTableHandle tableHandle;
	
	private volatile boolean primaryKeysHaveSameName;
		public static final String PRIMARY_KEYS_HAVE_SAME_NAME_PROPERTY = "primaryKeysHaveSameName";
	
	private MWReferenceHandle referenceHandle;
		public static final String REFERENCE_PROPERTY = "reference";
		
		
	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	private MWSecondaryTableHolder() {
		super();
	}

	MWSecondaryTableHolder(MWDescriptorMultiTableInfoPolicy parent, MWTable mwTable) {
		super(parent);
		this.tableHandle.setTable(mwTable);
	}
	
	
	// **************** Initialization ***************
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node rent) {
		super.initialize(rent);
		this.primaryKeysHaveSameName = true;
		this.tableHandle = new MWTableHandle(this, this.buildTableScrubber());
		this.referenceHandle = new MWReferenceHandle(this, this.buildReferenceScrubber());
	}
	
	
	//*************** accessors ***************

	private MWDescriptorMultiTableInfoPolicy getPolicy() {
		return (MWDescriptorMultiTableInfoPolicy) this.getParent();
	}

	public MWTable getTable() {
		return this.tableHandle.getTable();
	}
	
	public boolean primaryKeysHaveSameName() {
		return this.primaryKeysHaveSameName;
	}
	
	public void setPrimaryKeysHaveSameName(boolean primaryKeysHaveSameName) {
		boolean old = this.primaryKeysHaveSameName;
		this.primaryKeysHaveSameName = primaryKeysHaveSameName;
		this.firePropertyChanged(PRIMARY_KEYS_HAVE_SAME_NAME_PROPERTY, old, primaryKeysHaveSameName);
		if ((old != primaryKeysHaveSameName) && primaryKeysHaveSameName) {
			this.setReference(null);
		}
	}
	
	public MWReference getReference() {
		if (this.primaryKeysHaveSameName) {
			throw new IllegalStateException("This secondary table association is based on primary keys");
		}
		return this.referenceHandle.getReference();
	}
	
	public void setReference(MWReference reference) {
		if ((reference != null) && this.primaryKeysHaveSameName) {
			throw new IllegalStateException("This secondary table association is based on primary keys");
		}
		Object old = this.referenceHandle.getReference();
		this.referenceHandle.setReference(reference);
		this.firePropertyChanged(REFERENCE_PROPERTY, old, reference);
	}
	
	
	// ********** model synchronization **********

	protected void addChildrenTo(List list) {
		super.addChildrenTo(list);
		list.add(this.tableHandle);
		list.add(this.referenceHandle);
	}
	
	private NodeReferenceScrubber buildTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWSecondaryTableHolder.this.tableRemoved();
			}
			public String toString() {
				return "MWSecondaryTableHolder.buildTableScrubber()";
			}
		};
	}

	void tableRemoved() {
		// we don't really need to clear the table;
		// and some listeners would really appreciate it if we kept it around
		// this.tableHandle.setTable(null);
		this.getPolicy().removeSecondaryTableHolder(this);
	}

	private NodeReferenceScrubber buildReferenceScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWSecondaryTableHolder.this.setReference(null);
			}
			public String toString() {
				return "MWSecondaryTableHolder.buildReferenceScrubber()";
			}
		};
	}


	// ********** printing **********

	public void toString(StringBuffer sb) {
		this.getTable().toString(sb);
	}

		
	// ********** TopLink methods **********
		
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWSecondaryTableHolder.class);
	
		XMLCompositeObjectMapping tableHandleMapping = new XMLCompositeObjectMapping();
		tableHandleMapping.setAttributeName("tableHandle");
		tableHandleMapping.setGetMethodName("getTableHandleForTopLink");
		tableHandleMapping.setSetMethodName("setTableHandleForTopLink");
		tableHandleMapping.setReferenceClass(MWTableHandle.class);
		tableHandleMapping.setXPath("table-handle");
		descriptor.addMapping(tableHandleMapping);

		descriptor.addDirectMapping("primaryKeysHaveSameName", "primary-keys-have-same-name/text()");

		XMLCompositeObjectMapping referenceHandleMapping = new XMLCompositeObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setXPath("reference-handle");
		descriptor.addMapping(referenceHandleMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWTableHandle getTableHandleForTopLink() {
		return (this.tableHandle.getTable() == null) ? null : this.tableHandle;
	}
	private void setTableHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTableScrubber();
		this.tableHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWReferenceHandle getReferenceHandleForTopLink() {
		return (this.referenceHandle.getReference() == null) ? null : this.referenceHandle;
	}
	private void setReferenceHandleForTopLink(MWReferenceHandle handle) {
		NodeReferenceScrubber scrubber = this.buildReferenceScrubber();
		this.referenceHandle = ((handle == null) ? new MWReferenceHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
