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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class AnyCollectionMappingNode
    extends MappingNode
{
    // **************** Constructors ******************************************

    public AnyCollectionMappingNode(MWAnyCollectionMapping mapping, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
        super(mapping, mappingNodeTypePolicy, parent);
    }


    // **************** MappingNode contract **********************************

    protected String buildIconKey() {
        return "mapping.anyCollection";
    }


    // ************** AbstractApplicationNode overrides *************

    protected String accessibleNameKey() {
        return "ACCESSIBLE_ANY_COLLECTION_MAPPING_NODE";
    }


    // **************** ApplicationNode contract ******************************

    public String helpTopicID() {
//        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".anyCollection";
        return "mapping.anyCollection"; // For 10.1.3
    }


    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return AnyCollectionMappingPropertiesPage.class;
    }
}
