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
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWColumnHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWColumns.
 * Since a MWColumn is nested within the XML file
 * for a MWTable, we need to store a reference to a particular
 * column as a pair of instance variables:
 *   - the name of the containing table
 *   - the name of the column
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWColumnHandle extends MWHandle {

	/**
	 * This is the actual column.
	 * It is built from the table and column names, below.
	 */
	private volatile MWColumn column;

	/**
	 * The table and column names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual column.
	 * We do not keep these in synch with the column itself because
	 * we cannot know when the column has been renamed etc.
	 */
	private volatile String columnTableName;
	private volatile String columnName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWColumnHandle() {
		super();
	}

	public MWColumnHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWColumnHandle(MWModel parent, MWColumn column, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.column = column;
	}


	// ********** instance methods **********

	public MWColumn getColumn() {
		return this.column;
	}

	public void setColumn(MWColumn column) {
		this.column = column;
	}

	protected Node node() {
		return this.getColumn();
	}
	
	/**
	 * Override to delegate comparison to the column itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER column should be null.
	 */
	public int compareTo(Object o) {
		return this.column.compareTo(((MWColumnHandle) o).column);
	}

	public void toString(StringBuffer sb) {
		if (this.column == null) {
			sb.append("null");
		} else {
			this.column.toString(sb);
		}
	}

	public MWColumnHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveColumnHandles() {
		super.resolveColumnHandles();
		
		if (this.columnTableName != null && this.columnName != null) {
			MWTable table = this.getDatabase().tableNamed(this.columnTableName);
			if (table != null) {
				this.column = table.columnNamed(this.columnName);
			}
		}
		
		// Ensure columnTableName and columnName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.columnTableName = null;
		this.columnName = null;
	}
	

	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWColumnHandle.class);

		descriptor.addDirectMapping("columnTableName", "getColumnTableNameForTopLink", "setColumnTableNameForTopLink", "column-table-name/text()");
		descriptor.addDirectMapping("columnName", "getColumnNameForTopLink", "setColumnNameForTopLink", "column-name/text()");

		return descriptor;
	}
	
	private String getColumnTableNameForTopLink(){
		return (this.column == null) ? null : this.column.getTable().getName();
	}
	private void setColumnTableNameForTopLink(String columnTableName){
		this.columnTableName = columnTableName;
	}

	private String getColumnNameForTopLink() {
		return (this.column == null) ? null : this.column.getName();
	}
	private void setColumnNameForTopLink(String columnName) {
		this.columnName = columnName;
	}

}
