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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsDirectCollectionAction;


final class MapAsEisDirectCollectionAction
    extends MapAsDirectCollectionAction
{
    MapAsEisDirectCollectionAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeIcon("mapping.xmlDirectCollection");
        this.initializeText("MAP_AS_XML_DIRECT_COLLECTION_ACTION");
        this.initializeMnemonic("MAP_AS_XML_DIRECT_COLLECTION_ACTION");
        this.initializeToolTipText("MAP_AS_XML_DIRECT_COLLECTION_ACTION.toolTipText");
    }


    protected Class mappingClass() {
        return MWXmlDirectCollectionMapping.class;
    }

}
