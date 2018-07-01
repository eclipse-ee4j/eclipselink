/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class AggregateDescriptorNode extends RelationalClassDescriptorNode {


    // ********** constructors/initialization **********

    public AggregateDescriptorNode(MWAggregateDescriptor descriptor, DescriptorPackageNode parentNode) {
        super(descriptor, parentNode);
    }


    // ********** ApplicationNode implementation **********

    public String helpTopicID() {
        return "descriptor.aggregate";
    }

    public String buildIconKey() {
        return "descriptor.aggregate";
    }

    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return AggregateDescriptorTabbedPropertiesPage.class;
    }


    // ********** DescriptorNode implementation **********

    protected String accessibleNameKey() {
        return "ACCESSIBLE_AGGREGATE_DESCRIPTOR_NODE";
    }


    // ********** MWRelationalClassDescriptorNode overrides **********

    public boolean isAggregateDescriptor() {
        return true;
    }

    public void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context) {
        ((AggregateDescriptorTabbedPropertiesPage) context.getPropertiesPage()).selectQueryKey(queryKey);
    }

}
