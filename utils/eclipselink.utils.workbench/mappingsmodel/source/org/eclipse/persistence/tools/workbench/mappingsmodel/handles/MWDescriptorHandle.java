/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWDescriptorHandle is used to handle a reference to a MWDescriptor
 * via a descriptor name.
 */
public final class MWDescriptorHandle extends MWHandle {

    /**
     * This is the actual descriptor.
     * It is built from the descriptor name, below.
     */
    private volatile MWDescriptor descriptor;

    /**
     * The descriptor name is transient. It is used only to hold its value
     * until postProjectBuild() is called and we can resolve
     * the actual descriptor. We do not keep it in synch with the descriptor
     * itself because we cannot know when the descriptor has been renamed etc.
     */
    private volatile String descriptorName;


    // ********** constructors **********

    /**
    * default constructor - for TopLink use only
    */
    private MWDescriptorHandle() {
        super();
    }

    public MWDescriptorHandle(MWModel parent, NodeReferenceScrubber scrubber) {
        super(parent, scrubber);
    }

    public MWDescriptorHandle(MWModel parent, MWDescriptor descriptor, NodeReferenceScrubber scrubber) {
        super(parent, scrubber);
        this.descriptor = descriptor;
    }


    // ********** instance methods **********

    public MWDescriptor getDescriptor() {
        return this.descriptor;
    }

    public void setDescriptor(MWDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    protected Node node() {
        return getDescriptor();
    }

    public MWDescriptorHandle setScrubber(NodeReferenceScrubber scrubber) {
        this.setScrubberInternal(scrubber);
        return this;
    }

    public void resolveDescriptorHandles() {
        super.resolveDescriptorHandles();
        if (this.descriptorName != null) {
            this.descriptor = this.getProject().descriptorNamed(this.descriptorName);
        }
        // Ensure descriptor name is not used by setting them to null....
        this.descriptorName = null;
    }

    /**
     * Override to delegate comparison to the descriptor itself.
     * If the handles being compared are in a collection that is being sorted,
     * NEITHER descriptor should be null.
     */
    public int compareTo(Object o) {
        return this.descriptor.compareTo(((MWDescriptorHandle) o).descriptor);
    }

    public void toString(StringBuffer sb) {
        sb.append((this.descriptor == null) ? "null" : this.descriptor.getName());
    }


    // ********** TopLink methods **********

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWDescriptorHandle.class);

        descriptor.addDirectMapping("descriptorName", "getDescriptorNameForToplink", "setDescriptorNameForToplink", "descriptor-name/text()");

        return descriptor;
    }

    private String getDescriptorNameForToplink() {
        return (this.descriptor == null) ? null : this.descriptor.getName();
    }

    private void setDescriptorNameForToplink(String descriptorName) {
        this.descriptorName = descriptorName;
    }

}
