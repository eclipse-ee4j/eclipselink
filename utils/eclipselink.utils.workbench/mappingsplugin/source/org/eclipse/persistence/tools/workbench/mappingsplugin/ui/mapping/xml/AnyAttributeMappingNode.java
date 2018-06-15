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
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class AnyAttributeMappingNode extends MappingNode {

    public AnyAttributeMappingNode(MWAnyAttributeMapping value, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
        super(value, mappingNodeTypePolicy, parent);
    }

    // **************** MappingNode contract **********************************

    @Override
    protected String buildIconKey() {
        return "mapping.anyAttribute";
    }


    // ************** AbstractApplicationNode overrides *************

    @Override
    protected String accessibleNameKey() {
        return "ACCESSIBLE_ANY_ATTRIBUTE_MAPPING_NODE";
    }


    // **************** ApplicationNode contract ******************************

    @Override
    public String helpTopicID() {
        return "mapping.anyAttribute";
    }


    // ********** MWApplicationNode overrides **********

    @Override
    protected Class propertiesPageClass() {
        return AnyAttributeMappingPropertiesPage.class;
    }

}
