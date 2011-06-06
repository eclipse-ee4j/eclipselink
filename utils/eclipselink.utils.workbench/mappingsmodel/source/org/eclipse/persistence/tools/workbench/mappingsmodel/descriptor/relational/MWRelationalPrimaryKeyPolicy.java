/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public final class MWRelationalPrimaryKeyPolicy extends MWModel {

	protected Collection primaryKeyHandles;
		public final static String PRIMARY_KEYS_COLLECTION = "primaryKeys";
		private NodeReferenceScrubber primaryKeyScrubber;


	// **************** Constructors ******************************************

	/** Default constructor - for TopLink use only. */
	private MWRelationalPrimaryKeyPolicy() {
		super();
	}

	MWRelationalPrimaryKeyPolicy(MWRelationalTransactionalPolicy parent) {
		super(parent);
	}

	// **************** Initialization ****************************************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.primaryKeyHandles = new Vector();
	}


	// **************** Primary keys ******************************************

	private Iterator primaryKeyHandles() {
		return new CloneIterator(this.primaryKeyHandles) {
			protected void remove(Object current) {
				MWRelationalPrimaryKeyPolicy.this.removePrimaryKeyHandle((MWColumnHandle) current);
			}
		};
	}

	void removePrimaryKeyHandle(MWColumnHandle handle) {
		this.primaryKeyHandles.remove(handle);
		this.fireItemRemoved(PRIMARY_KEYS_COLLECTION, handle.getColumn());
	}

	public Iterator primaryKeys() {
		return new TransformationIterator(this.primaryKeyHandles()) {
			protected Object transform(Object next) {
				return ((MWColumnHandle) next).getColumn();
			}
		};
	}

	public int primaryKeysSize() {
		return this.primaryKeyHandles.size();
	}

	public void addPrimaryKey(MWColumn primaryKey) {
		if (this.containsPrimaryKey(primaryKey)) {
			throw new IllegalArgumentException(primaryKey.toString());
		}
		this.primaryKeyHandles.add(new MWColumnHandle(this, primaryKey, this.primaryKeyScrubber()));
		this.fireItemAdded(PRIMARY_KEYS_COLLECTION, primaryKey);
	}

	public void removePrimaryKey(MWColumn primaryKey) {
		for (Iterator stream = this.primaryKeys(); stream.hasNext(); ) {
			if (stream.next() == primaryKey) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(primaryKey.toString());
	}

	public boolean containsPrimaryKey(MWColumn primaryKey) {
		return CollectionTools.contains(this.primaryKeys(), primaryKey);
	}

	public void clearPrimaryKeys() {
		for (Iterator stream = this.primaryKeyHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}


	// **************** Model Synchronization ************************************************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.primaryKeyHandles) { children.addAll(this.primaryKeyHandles); }
	}

	private NodeReferenceScrubber primaryKeyScrubber() {
		if (this.primaryKeyScrubber == null) {
			this.primaryKeyScrubber = this.buildPrimaryKeyScrubber();
		}
		return this.primaryKeyScrubber;
	}

	private NodeReferenceScrubber buildPrimaryKeyScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalPrimaryKeyPolicy.this.removePrimaryKeyHandle((MWColumnHandle) handle);
			}
			public String toString() {
				return "MWRelationalPrimaryKeyPolicy.buildPrimaryKeyScrubber()";
			}
		};
	}


	// **************** "Public" **********************************************

	/**
	 * The relational descriptor's primary table changed.
	 * Clear out old primary keys and initialize new primary keys.
	 */
	void descriptorPrimaryTableChanged(MWTable primaryTable) {
		this.clearPrimaryKeys();

		if (primaryTable == null) {
			return;
		}

		for (Iterator stream = primaryTable.primaryKeyColumns(); stream.hasNext(); ) {
			this.addPrimaryKey((MWColumn) stream.next());
		}
	}

	public MWTableDescriptor getTableDescriptor() {
		return (MWTableDescriptor) ((MWRelationalTransactionalPolicy) getParent()).getParent();
	}

	public Iterator primaryKeyChoices() {
		MWTable primaryTable = getTableDescriptor().getPrimaryTable();
		if (primaryTable == null) {
			return NullIterator.instance();
		}

		return new FilteringIterator(primaryTable.columns()) {
			protected boolean accept(Object next) {
				return ! MWRelationalPrimaryKeyPolicy.this.containsPrimaryKey((MWColumn) next);
			}
		};
	}

	// **************** Runtime Conversion ************************************

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		for (Iterator primaryKeys = primaryKeys(); primaryKeys.hasNext(); ) {
			runtimeDescriptor.addPrimaryKeyField(((MWDataField) primaryKeys.next()).runtimeField());
		}
	}


	// **************** TopLink methods ***************************************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalPrimaryKeyPolicy.class);

		XMLCompositeCollectionMapping primaryKeyHandlesMapping = new XMLCompositeCollectionMapping();
		primaryKeyHandlesMapping.setReferenceClass(MWColumnHandle.class);
		primaryKeyHandlesMapping.setAttributeName("primaryKeyHandles");
		primaryKeyHandlesMapping.setGetMethodName("getPrimaryKeyHandlesForTopLink");
		primaryKeyHandlesMapping.setSetMethodName("setPrimaryKeyHandlesForTopLink");
		primaryKeyHandlesMapping.setXPath("primary-key-handles/column-handle");
		descriptor.addMapping(primaryKeyHandlesMapping);

		return descriptor;
	}

	private Collection getPrimaryKeyHandlesForTopLink() {
		synchronized (this.primaryKeyHandles) {
			return new TreeSet(this.primaryKeyHandles);
		}
	}
	private void setPrimaryKeyHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWColumnHandle) stream.next()).setScrubber(this.primaryKeyScrubber());
		}
		this.primaryKeyHandles = handles;
	}

	/**
	 * this is called by MWTableDescriptor.legacyXXPrePostProjectBuild()
	 */
	protected void legacySetPrimaryKeys(Collection handles) {
		this.setPrimaryKeyHandlesForTopLink(handles);
	}

}
