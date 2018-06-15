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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


final class MapAsTypeConversionAction extends MapAsRelationalDirectMapping {

    public MapAsTypeConversionAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        initializeIcon("mapping.typeConversion");
        initializeText("MAP_AS_TYPE_CONVERSION_ACTION");
        initializeMnemonic("MAP_AS_TYPE_CONVERSION_ACTION");
        initializeToolTipText("MAP_AS_TYPE_CONVERSION_ACTION.toolTipText");
    }

    protected MappingNode morphNode(MappingNode selectedNode) {
        MappingNode mappingNode = super.morphNode(selectedNode);
        ((MWDirectMapping) mappingNode.getMapping()).setTypeConversionConverter();
        return mappingNode;
    }

    protected String converterType() {
        return MWConverter.TYPE_CONVERSION_CONVERTER;
    }

}
