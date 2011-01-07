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

import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWMappingHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWMappings.
 * 
 * Since a MWMapping is nested within the XML file
 * for a MWMapping Descriptor, we need to store a reference to a particular
 * mapping as a pair of instance variables:
 *   - the name of the containing descriptor
 *   - the name of the mapping
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWMappingHandle extends MWHandle {

	/**
	 * This is the actual mapping.
	 * It is built from the descriptor and mapping names, below.
	 */
	private volatile MWMapping mapping;

	/**
	 * The descriptor and mapping names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual mapping.
	 * We do not keep these in synch with the mapping itself because
	 * we cannot know when the mapping has been renamed etc.
	 */
	private volatile String mappingDescriptorName;
	private volatile String mappingName;

	/**
	 * default constructor - for TopLink use only
	 */
	private MWMappingHandle() {
		super();
	}

	public MWMappingHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWMappingHandle(MWModel parent, MWMapping mapping, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.mapping = mapping;
	}


	// ********** instance methods **********

	public MWMapping getMapping() {
		return this.mapping;
	}

	public void setMapping(MWMapping mapping) {
		this.mapping = mapping;
	}

	protected Node node() {
		return getMapping();
	}
	
	public MWMappingHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.mappingDescriptorName != null && this.mappingName != null) {
			MWMappingDescriptor mappingDescriptor = (MWMappingDescriptor) this.getProject().descriptorNamed(mappingDescriptorName);
			if (mappingDescriptor != null) {
				this.mapping = mappingDescriptor.mappingNamed(this.mappingName);
			}
		}
		// Ensure mappingDescriptorName and mappingName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.mappingDescriptorName = null;
		this.mappingName = null;
	}

	/**
	 * Override to delegate comparison to the mapping itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER mapping should be null.
	 */
	public int compareTo(Object o) {
		return this.mapping.compareTo(((MWMappingHandle) o).mapping);
	}

	public void toString(StringBuffer sb) {
		if (this.mapping == null) {
			sb.append("null");
		} else {
			this.mapping.toString(sb);
		}
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWMappingHandle.class);

		descriptor.addDirectMapping("mappingDescriptorName", "getMappingDescriptorNameForToplink", "setMappingDescriptorNameForToplink", "mapping-descriptor-name/text()");
		descriptor.addDirectMapping("mappingName", "getMappingNameForToplink", "setMappingNameForToplink", "mapping-name/text()");

		return descriptor;
	}
	
	private String getMappingDescriptorNameForToplink() {
		return (this.mapping == null) ? null : this.mapping.getParentDescriptor().getName();
	}

	private void setMappingDescriptorNameForToplink(String mappingDescriptorName) {
		this.mappingDescriptorName = mappingDescriptorName;
	}

	private String getMappingNameForToplink() {
		return (this.mapping == null) ? null : this.mapping.getName();
	}

	private void setMappingNameForToplink(String mappingName) {
		this.mappingName = mappingName;
	}

}
