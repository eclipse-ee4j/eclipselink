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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWColumnPairHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWColumnPairs.
 * Since a MWColumnPair is nested within the XML file
 * for a MWTable, we need to store a reference to a particular
 * column pair as a list of instance variables:
 *   - the name of the containing table
 *   - the name of the containing reference
 *   - the name of the column pair
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWColumnPairHandle extends MWHandle {

	/**
	 * This is the actual column pair.
	 * It is built from the table, reference, and column pair names, below.
	 */
	private volatile MWColumnPair columnPair;

	/**
	 * The table, reference, and column pair names are transient. They
	 * are used only to hold their values until #postProjectBuild()
	 * is called and we can resolve the actual column pair.
	 * We do not keep these in synch with the column pair itself because
	 * we cannot know when the column pair has been renamed etc.
	 */
	private volatile String tableName;
	private volatile String referenceName;
	private volatile String columnPairName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWColumnPairHandle() {
		super();
	}

	public MWColumnPairHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWColumnPairHandle(MWModel parent, MWColumnPair columnPair, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.columnPair = columnPair;
	}


	// ********** instance methods **********

	public MWColumnPair getColumnPair() {
		return this.columnPair;
	}

	public void setColumnPair(MWColumnPair columnPair) {
		this.columnPair = columnPair;
	}

	protected Node node() {
		return this.getColumnPair();
	}
	
	/**
	 * Override to delegate comparison to the column pair itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER column pair should be null.
	 */
	public int compareTo(Object o) {
		return this.columnPair.compareTo(((MWColumnPairHandle) o).columnPair);
	}

	public void toString(StringBuffer sb) {
		if (this.columnPair == null) {
			sb.append("null");
		} else {
			this.columnPair.toString(sb);
		}
	}

	public MWColumnPairHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		
		if (this.tableName != null && this.referenceName != null && this.columnPairName != null) {
			MWTable table = this.getDatabase().tableNamed(this.tableName);
			if (table != null) {
				MWReference reference = table.referenceNamed(this.referenceName);
				if (reference != null) {
					this.columnPair = reference.columnPairNamed(this.columnPairName);
				}
			}
		}

		// Ensure tableName, referenceName, and columnPairName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.tableName = null;
		this.referenceName = null;
		this.columnPairName = null;
	}
	

	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWColumnPairHandle.class);

		descriptor.addDirectMapping("tableName", "getTableNameForTopLink", "setTableNameForTopLink", "table-name/text()");
		descriptor.addDirectMapping("referenceName", "getReferenceNameForTopLink", "setReferenceNameForTopLink", "reference-name/text()");
		descriptor.addDirectMapping("columnPairName", "getColumnPairNameForTopLink", "setColumnPairNameForTopLink", "column-pair-name/text()");

		return descriptor;
	}

	private String getTableNameForTopLink() {
		return (this.columnPair == null) ? null : this.columnPair.sourceTable().getName();
	}
	private void setTableNameForTopLink(String tableName) {
		this.tableName = tableName;
	}

	private String getReferenceNameForTopLink(){
		return (this.columnPair == null) ? null : this.columnPair.getReference().getName();
	}
	private void setReferenceNameForTopLink(String referenceName){
		this.referenceName = referenceName;
	}

	private String getColumnPairNameForTopLink() {
		return (this.columnPair == null) ? null : this.columnPair.getName();
	}
	private void setColumnPairNameForTopLink(String columnPairName) {
		this.columnPairName = columnPairName;
	}
}
