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
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWQueryKeyHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWQueryKeys.
 * Since a MWQueryKey is nested within the XML file
 * for a MWDescriptor, we need to store a reference to a particular
 * query key as a pair of instance variables:
 *   - the name of the containing descriptor
 *   - the name of the query key
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWQueryKeyHandle extends MWHandle {

	/**
	 * This is the actual query key.
	 * It is built from the descriptor and query key names, below.
	 */
	private volatile MWQueryKey queryKey;

	/**
	 * The descriptor and query key names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual query key.
	 * We do not keep these in synch with the query key itself because
	 * we cannot know when the query key has been renamed etc.
	 */
	private volatile String descriptorName;
	private volatile String queryKeyName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWQueryKeyHandle() {
		super();
	}

	public MWQueryKeyHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWQueryKeyHandle(MWModel parent, MWQueryKey queryKey, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.queryKey = queryKey;
	}


	// ********** instance methods **********

	public MWQueryKey getQueryKey() {
		return this.queryKey;
	}

	public void setQueryKey(MWQueryKey queryKey) {
		this.queryKey = queryKey;
	}
	
	protected Node node() {
		return getQueryKey();
	}
	
	public MWQueryKeyHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveColumnHandles() {
		super.resolveColumnHandles();
		
		if (this.descriptorName != null && this.queryKeyName != null) {
			MWRelationalDescriptor descriptor = (MWRelationalDescriptor) this.getProject().descriptorNamed(this.descriptorName);
			if (descriptor != null) {
				this.queryKey = descriptor.queryKeyNamed(this.queryKeyName);
			}
		}
		
		// Ensure descriptorName and queryKeyName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.descriptorName = null;
		this.queryKeyName = null;
	}
	
	/**
	 * Override to delegate comparison to the queryKey itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER query key should be null.
	 */
	public int compareTo(Object o) {
		return this.queryKey.compareTo(((MWQueryKeyHandle) o).queryKey);
	}

	public void toString(StringBuffer sb) {
		if (this.queryKey == null) {
			sb.append("null");
		} else {
			sb.append(this.queryKey.getName());
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWQueryKeyHandle.class);

		descriptor.addDirectMapping("descriptorName", "getDescriptorNameForTopLink", "setDescriptorNameForTopLink", "descriptor-name/text()");
		descriptor.addDirectMapping("queryKeyName", "getQueryKeyNameForTopLink", "setQueryKeyNameForTopLink", "query-key-name/text()");

		return descriptor;
	}

	private String getDescriptorNameForTopLink(){
		return (this.queryKey == null) ? null : this.queryKey.getDescriptor().getName();
	}

	private void setDescriptorNameForTopLink(String descriptorName){
		this.descriptorName = descriptorName;
	}

	private String getQueryKeyNameForTopLink() {
		return (this.queryKey == null) ? null : this.queryKey.getName();
	}

	private void setQueryKeyNameForTopLink(String queryKeyName) {
		this.queryKeyName = queryKeyName;
	}

}
