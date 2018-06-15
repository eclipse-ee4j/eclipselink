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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.Collection;

public final class DefaultMWClassRefreshPolicy
    extends AbstractMWClassRefreshPolicy
{
    // singleton
    private static MWClassRefreshPolicy INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized MWClassRefreshPolicy instance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultMWClassRefreshPolicy();
        }
        return INSTANCE;
    }

    private DefaultMWClassRefreshPolicy()
    {
        super();
    }

    protected void resolveMissingAttributes(MWClass mwClass, Collection missingAttributes)
    {
        mwClass.removeAttributes(missingAttributes);
    }

    public void finalizeRefresh(MWClass mwClass)
    {
        mwClass.clearEjb20Attributes();
    }
}
