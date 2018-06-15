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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel.QuickViewItem;




public abstract class QueryQuickViewItem implements QuickViewItem {

    private final MWQueryItem queryItem;

    public QueryQuickViewItem(MWQueryItem queryItem) {
        super();
        this.queryItem = queryItem;
    }

    public String accessibleName() {
        return null; // TODO: Might need it
    }

    public String displayString() {
        return this.queryItem.displayString();
    }

    public Object getValue() {
        return this.queryItem;
    }

    public Icon icon() {
        return null; // TODO
    }

    public boolean isRemovable() {
        return true;
    }

    public void remove() {
        this.queryItem.removeSelfFromParent();
    }
}
