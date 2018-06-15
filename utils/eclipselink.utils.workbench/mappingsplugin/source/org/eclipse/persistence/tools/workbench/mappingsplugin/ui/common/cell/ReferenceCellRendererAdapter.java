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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;


public class ReferenceCellRendererAdapter extends NoneSelectedCellRendererAdapter {

    public ReferenceCellRendererAdapter(ResourceRepository repository) {
        super(repository);
    }

    protected String buildNonNullValueText(Object value) {
        MWReference reference = (MWReference) value;
        String sourceTableName = reference.getSourceTable().getName();
        String targetTableName = reference.getTargetTable().getName();

        return resourceRepository().getString("REFERENCE_SIGNATURE", reference.getName(), sourceTableName, targetTableName);
    }

}
