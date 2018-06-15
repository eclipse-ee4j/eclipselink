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
package org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWDefaultMethodCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodCodeGenPolicy;


final class BasicClassCodeGenPolicy
    extends AbstractClassCodeGenPolicy
{
    private MWClass mwClass;

    BasicClassCodeGenPolicy(MWClass mwClass, ApplicationContext context) {
        super(context);
        this.mwClass = mwClass;
    }

    protected MWClass getMWClass() {
        return this.mwClass;
    }

    public MWMethodCodeGenPolicy getMethodCodeGenPolicy(MWMethod method) {
        return new MWDefaultMethodCodeGenPolicy(method, this);
    }

    public void addAccessorCodeGenPolicy(MWMethod method, MWMethodCodeGenPolicy methodCodeGenPolicy) {
        throw new UnsupportedOperationException();
    }

}
