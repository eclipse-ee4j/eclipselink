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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;



class CreateSelectedTablesOnDatabaseAction extends AbstractCreateTablesOnDatabaseAction {

    CreateSelectedTablesOnDatabaseAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("SELECTED_TABLES_LABEL");
    }

    protected Collection buildTables() {
        return this.selectedMWTables();
    }

}
