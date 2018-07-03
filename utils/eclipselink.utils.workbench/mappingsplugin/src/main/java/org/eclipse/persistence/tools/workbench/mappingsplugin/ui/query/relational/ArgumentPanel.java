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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



/**
 * Abstract panel used by the ExpressionBuilderDialog
 * A specific panel is selected depending on what type of argument the user selects for their expression
 * (literal, queryable, parameter)
 */
abstract class ArgumentPanel
    extends AbstractPanel
{

    private PropertyValueModel argumentHolder;
    private PropertyValueModel queryHolder;

    protected ArgumentPanel(PropertyValueModel argumentHolder, WorkbenchContextHolder contextHolder) {
        super(contextHolder);
        this.argumentHolder = buildQueryArgumentHolder(argumentHolder);
        this.queryHolder = buildQueryHolder();
    }

    protected PropertyValueModel buildQueryArgumentHolder(PropertyValueModel argumentHolder) {
        return argumentHolder;
    }

    private PropertyValueModel buildQueryHolder() {
        return new PropertyAspectAdapter(this.argumentHolder) {
            protected Object getValueFromSubject() {
                return ((MWArgument) this.subject).getParentQuery();
            }
        };
    }

    protected PropertyValueModel getArgumentHolder() {
        return this.argumentHolder;
    }

    protected MWArgument getArgument() {
        return (MWArgument) this.argumentHolder.getValue();
    }

    protected PropertyValueModel getQueryHolder() {
        return this.queryHolder;
    }

}
