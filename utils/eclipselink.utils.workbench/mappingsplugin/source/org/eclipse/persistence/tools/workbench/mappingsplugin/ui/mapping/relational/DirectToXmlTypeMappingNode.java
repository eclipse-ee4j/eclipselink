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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class DirectToXmlTypeMappingNode extends MappingNode {


    public DirectToXmlTypeMappingNode(MWDirectToXmlTypeMapping value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
        super(value, mappingNodeTypePolicy, parent);
    }


    // ************** AbstractApplicationNode overrides *************

    protected String accessibleNameKey() {
        return "ACCESSIBLE_DIRECT_TO_XML_MAPPING_NODE";
    }

    // ************** ApplicationNode implementation *************

    public String helpTopicID() {
        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".directToXmlType";
    }

    protected String buildIconKey() {
        return ((MWDirectToXmlTypeMapping) getMapping()).iconKey();
    }


    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return DirectToXmlTypePropertiesPage.class;
    }

}
