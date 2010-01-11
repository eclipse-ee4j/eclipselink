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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWTableHandle is used to handle a reference to a MWTable
 * via a table name.
 */
public final class MWTableHandle extends MWHandle {

	/**
	 * This is the actual table.
	 * It is built from the table name, below.
	 */
	private volatile MWTable table;

	/**
	 * The table name is transient. It is used only to hold its value
	 * until postProjectBuild() is called and we can resolve
	 * the actual table. We do not keep it in synch with the table
	 * itself because we cannot know when the table has been renamed etc.
	 */
	private volatile String tableName;


	// ********** constructors **********

	/**
 	 * default constructor - for TopLink use only
 	 */
	private MWTableHandle() {
		super();
	}

	public MWTableHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWTableHandle(MWModel parent, MWTable table, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.table = table;
	}


	// ********** instance methods **********

	public MWTable getTable() {
		return this.table;
	}

	public void setTable(MWTable table) {
		this.table = table;
	}
	
	protected Node node() {
		return getTable();
	}

	public MWTableHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveMetadataHandles() {
		super.resolveMetadataHandles();
		
		if (this.tableName != null) {
			this.table = this.getDatabase().tableNamed(this.tableName);
		}
		// Ensure tableName is not used by setting it to null....
		this.tableName = null;
	}

	/**
	 * Override to delegate comparison to the table itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER table should be null.
	 */
	public int compareTo(Object o) {
		return this.table.compareTo(((MWTableHandle) o).table);
	}

	public void toString(StringBuffer sb) {
		if (this.table == null) {
			sb.append("null");
		} else {
			this.table.toString(sb);
		}
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWTableHandle.class);

		descriptor.addDirectMapping("tableName", "getTableNameForToplink", "setTableNameForToplink", "table-name/text()");

		return descriptor;
	}
	
	private String getTableNameForToplink() {
		return (this.table == null) ? null : this.table.getName();
	}

	private void setTableNameForToplink(String tableName) {
		this.tableName = tableName;
	}

}
