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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;


final class MapAsAggregateAction
    extends ChangeMappingTypeAction
{
    MapAsAggregateAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeIcon("mapping.aggregate");
        this.initializeText("MAP_AS_AGGREGATE_ACTION");
        this.initializeMnemonic("MAP_AS_AGGREGATE_ACTION");
        this.initializeToolTipText("MAP_AS_AGGREGATE_ACTION.toolTipText");
    }

    protected MWMapping morphMapping(MWMapping mapping) {
        return mapping.asMWAggregateMapping();
    }

    protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
        return ((MWRelationalClassDescriptor) descriptor).addAggregateMapping(attribute);
    }

    protected Class mappingClass() {
        return MWAggregateMapping.class;
    }

}
