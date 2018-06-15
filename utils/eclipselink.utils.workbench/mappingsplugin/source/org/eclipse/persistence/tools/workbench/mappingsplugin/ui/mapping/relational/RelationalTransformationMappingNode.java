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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class RelationalTransformationMappingNode
    extends MappingNode
{

    public RelationalTransformationMappingNode(MWRelationalTransformationMapping value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
        super(value, mappingNodeTypePolicy, parent);
    }


    // ************** AbstractApplicationNode overrides *************

    protected String accessibleNameKey() {
        return "ACCESSIBLE_TRANSFORMATION_MAPPING_NODE";
    }


    // ************** ApplicationNode implementation *************

    public String helpTopicID() {
//        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".transformation";
        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".transformation.relational"; // For 10.1.3
    }

    protected String buildIconKey() {
        return getMapping().iconKey();
    }


    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return RelationalTransformationMappingPropertiesPage.class;
    }

}
