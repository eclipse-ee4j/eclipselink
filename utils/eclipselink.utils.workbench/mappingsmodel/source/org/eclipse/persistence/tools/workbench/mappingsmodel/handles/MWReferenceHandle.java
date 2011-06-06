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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWReferenceHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWReferences.
 * Since a MWReference is nested within the XML file
 * for a MWTable, we need to store a reference to a particular
 * reference as a pair of instance variables:
 *   - the name of the containing table
 *   - the name of the reference
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWReferenceHandle extends MWHandle {

	/**
	 * This is the actual reference.
	 * It is built from the table and reference names, below.
	 */
	private volatile MWReference reference;

	/**
	 * The table and reference names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual reference.
	 * We do not keep these in synch with the reference itself because
	 * we cannot know when the reference has been renamed etc.
	 */
	private volatile String referenceTableName;
	private volatile String referenceName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWReferenceHandle() {
		super();
	}

	public MWReferenceHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWReferenceHandle(MWModel parent, MWReference reference, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.reference = reference;
	}


	// ********** instance methods **********

	public MWReference getReference() {
		return this.reference;
	}
	
	public void setReference(MWReference reference) {
		this.reference = reference;
	}
	
	protected Node node() {
		return getReference();
	}

	public MWReferenceHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveReferenceHandles() {
		super.resolveReferenceHandles();
		if ((this.referenceTableName != null) && (this.referenceName != null)) {
			MWTable table = this.getDatabase().tableNamed(this.referenceTableName);
			if (table != null) {
				this.reference = table.referenceNamed(this.referenceName);
			}
		}
		// Ensure referenceTableName and referenceName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.referenceTableName = null;
		this.referenceName = null;
	}

	/**
	 * Override to delegate comparison to the reference itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER reference should be null.
	 */
	public int compareTo(Object o) {
		return this.reference.compareTo(((MWReferenceHandle) o).reference);
	}

	public void toString(StringBuffer sb) {
		if (this.reference == null) {
			sb.append("null");
		} else {
			this.reference.toString(sb);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWReferenceHandle.class);

		descriptor.addDirectMapping("referenceTableName", "getReferenceTableNameForTopLink", "setReferenceTableNameForTopLink", "reference-table-name/text()");
		descriptor.addDirectMapping("referenceName", "getReferenceNameForTopLink", "setReferenceNameForTopLink", "reference-name/text()");

		return descriptor;
	}

	private String getReferenceTableNameForTopLink() {
		return (this.reference == null) ? null : this.reference.getSourceTable().getName();
	}
	private void setReferenceTableNameForTopLink(String referenceTableName) {
		this.referenceTableName = referenceTableName;
	}

	private String getReferenceNameForTopLink() {
		return (this.reference == null) ? null : this.reference.getName();
	}
	private void setReferenceNameForTopLink(String referenceName) {
		this.referenceName = referenceName;
	}

}
