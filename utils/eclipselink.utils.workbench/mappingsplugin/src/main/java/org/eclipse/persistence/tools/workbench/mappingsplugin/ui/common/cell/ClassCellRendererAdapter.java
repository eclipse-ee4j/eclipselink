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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;


public class ClassCellRendererAdapter
    extends NoneSelectedCellRendererAdapter
{
    public ClassCellRendererAdapter(ResourceRepository repository) {
        super(repository);
    }

    protected String buildNonNullValueText(Object value) {
        return ((MWClass) value).displayStringWithPackage();
    }

    public boolean showDetailedIcon() {
        return true;
    }

    protected Icon buildNonNullValueIcon(Object value) {
        MWClass mwClass = (MWClass) value;

        if (this.showDetailedIcon()) {
            if (mwClass.isInterface()) {
                return resourceRepository().getIcon("class.interface");
            }
            else if (mwClass.isNonReferenceType()) {
                return null;
            }
            else if (mwClass.getModifier().isPackage()) {
                return resourceRepository().getIcon("class.default");
            }
            else if (mwClass.getModifier().isProtected()) {
                return resourceRepository().getIcon("class.protected");
            }
            else if (mwClass.getModifier().isPrivate()) {
                return resourceRepository().getIcon("class.private");
            }
        }

        return resourceRepository().getIcon("class.public");
    }
}
