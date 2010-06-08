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
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;


/**
 * MWQueryableHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWQueryables.
 * 
 * MWQueryables are some MWMappings and MWUserDefinedQueryKeys.
 * 
 * Since MWQueryables are nested within the XML files
 * for MWClassDescriptor, we need to store a reference to a particular
 * queryable as a pair of instance variables:
 *   - the name of the containing mapping descriptor
 *   - the name of the queryable
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */

public final class MWQueryableHandle extends MWHandle {

	/**
	 * This is the actual queryable.
	 * It is built from the mapping descriptor and queryable names, below.
	 */
	private volatile MWQueryable queryable;
	
	/**
	 * The mapping descriptor and queryable names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual queryable.
	 * We do not keep these in synch with the queryable itself because
	 * we cannot know when the queryable has been renamed etc.
	 */
	private volatile String mappingDescriptorName;
	private volatile String queryableName;

	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWQueryableHandle() {
		super();
	}
	
	public MWQueryableHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWQueryableHandle(MWModel parent, MWQueryable queryable, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.queryable = queryable;
	}


	// ********** instance methods **********

	public MWQueryable getQueryable() {
		return this.queryable;
	}

	public void setQueryable(MWQueryable queryable) {
		this.queryable = queryable;
	}

	protected Node node() {
		return getQueryable();
	}
	
	public MWQueryableHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.mappingDescriptorName != null && this.queryableName != null) {
			MWRelationalClassDescriptor mappingDescriptor = (MWRelationalClassDescriptor) this.getProject().descriptorNamed(this.mappingDescriptorName);
			if (mappingDescriptor != null) {
				this.queryable = mappingDescriptor.queryableNamed(this.queryableName);
			}
		}
		// Ensure mappingDescriptorName and queryable name are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.mappingDescriptorName = null;
		this.queryableName = null;
	}

	/**
	 * Override to delegate comparison to the queryable itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER queryable should be null.
	 */
	public int compareTo(Object o) {
		return this.queryable.compareTo(((MWQueryableHandle) o).queryable);
	}

	public void toString(StringBuffer sb) {
		sb.append((this.queryable == null) ? "null" : this.queryable.getName());
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWQueryableHandle.class);

		descriptor.addDirectMapping("mappingDescriptorName", "getMappingDescriptorNameForToplink", "setMappingDescriptorNameForToplink", "mapping-descriptor-name/text()");
		descriptor.addDirectMapping("queryableName", "getQueryableNameForToplink", "setQueryableNameForToplink", "queryable-name/text()");

		return descriptor;
	}

	private String getMappingDescriptorNameForToplink() {
		return (this.queryable == null) ? null : this.queryable.getParentDescriptor().getName();
	}

	private void setMappingDescriptorNameForToplink(String mappingDescriptorName) {
		this.mappingDescriptorName = mappingDescriptorName;
	}

	private String getQueryableNameForToplink() {
		return (this.queryable == null) ? null : this.queryable.getName();
	}

	private void setQueryableNameForToplink(String queryableName) {
		this.queryableName = queryableName;
	}

}
