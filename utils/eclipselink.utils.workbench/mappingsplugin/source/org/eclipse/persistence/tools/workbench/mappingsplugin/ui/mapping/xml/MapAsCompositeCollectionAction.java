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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;


final class MapAsCompositeCollectionAction extends ChangeMappingTypeAction
{
    MapAsCompositeCollectionAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeIcon("mapping.compositeCollection");
        this.initializeText("MAP_AS_COMPOSITE_COLLECTION_ACTION");
        this.initializeMnemonic("MAP_AS_COMPOSITE_COLLECTION_ACTION");
        this.initializeToolTipText("MAP_AS_COMPOSITE_COLLECTION_ACTION.toolTipText");
    }


    // ************ ChangeMappingTypeAction implementation ***********

    protected MWMapping morphMapping(MWMapping mapping) {
        return mapping.asMWCompositeCollectionMapping();
    }

    protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
        return ((MWXmlDescriptor) descriptor).addCompositeCollectionMapping(attribute);
    }

    protected Class mappingClass() {
        return MWCompositeCollectionMapping.class;
    }

}
