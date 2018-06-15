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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsDirectCollectionAction;


/**
 * @version 10.1.3
 */
final class MapAsRelationalDirectCollectionAction
    extends MapAsDirectCollectionAction
{
    MapAsRelationalDirectCollectionAction(WorkbenchContext context) {
        super(context);
    }
    protected void initialize() {
        super.initialize();
        this.initializeIcon("mapping.directCollection");
        this.initializeText("MAP_AS_DIRECT_COLLECTION_ACTION");
        this.initializeMnemonic("MAP_AS_DIRECT_COLLECTION_ACTION");
        this.initializeToolTipText("MAP_AS_DIRECT_COLLECTION_ACTION.toolTipText");
    }

    protected Class mappingClass() {
        return MWRelationalDirectCollectionMapping.class;
    }
}
