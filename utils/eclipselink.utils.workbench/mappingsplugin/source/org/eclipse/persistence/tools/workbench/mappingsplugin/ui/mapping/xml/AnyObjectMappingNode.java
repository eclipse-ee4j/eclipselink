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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class AnyObjectMappingNode
    extends MappingNode
{
    // **************** Constructors ******************************************

    public AnyObjectMappingNode(MWAnyObjectMapping mapping, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
        super(mapping, mappingNodeTypePolicy, parent);
    }


    // **************** MappingNode contract **********************************

    protected String buildIconKey() {
        return "mapping.anyObject";
    }


    // ************** AbstractApplicationNode overrides *************

    protected String accessibleNameKey() {
        return "ACCESSIBLE_ANY_OBJECT_MAPPING_NODE";
    }


    // **************** ApplicationNode contract ******************************

    public String helpTopicID() {
//        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".anyObject";
        return "mapping.anyObject"; // For 10.1.3
    }


    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return AnyObjectMappingPropertiesPage.class;
    }
}
