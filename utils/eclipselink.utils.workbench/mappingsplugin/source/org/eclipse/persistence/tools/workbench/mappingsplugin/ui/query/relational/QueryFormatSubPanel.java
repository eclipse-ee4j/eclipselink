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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



/**
 *  An abstract class used on the QueryFormat panel
 *
 * If the user chooses expression then the subclass expressionQueryFormatSubPanel is shown
 * If the user chooses SQL or EJBQL then the subclass stringQueryFormatSubPanel is shown
 */
abstract class QueryFormatSubPanel
    extends AbstractPanel
{
    private PropertyValueModel queryFormatHolder;
    private PropertyValueModel queryHolder;


    protected QueryFormatSubPanel(PropertyValueModel queryHolder, PropertyValueModel queryFormatHolder, WorkbenchContextHolder contextHolder) {
        super(contextHolder);
        this.queryHolder = queryHolder;
        this.queryFormatHolder = queryFormatHolder;
        initialize();
    }

    protected abstract void initialize();

    protected MWRelationalQuery getQuery() {
        return (MWRelationalQuery) this.queryHolder.getValue();
    }

    protected PropertyValueModel getQueryHolder() {
        return this.queryHolder;
    }

    protected PropertyValueModel getQueryFormatHolder() {
        return this.queryFormatHolder;
    }

}
