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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;

import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

public final class MWNullNullValuePolicy
    extends MWModel
    implements MWNullValuePolicy {


    // ************* Constructors **************

    public MWNullNullValuePolicy(MWModel parent) {
        super(parent);
    }

    public boolean usesNullValue() {
        return false;
    }

    public String getNullValue() {
        return null;
    }

    public void setNullValue(String newValue) {
        throw new UnsupportedOperationException();
    }

    public MWTypeDeclaration getNullValueType() {
        return null;
    }

    public void setNullValueType(MWTypeDeclaration newNullType) {
        throw new UnsupportedOperationException();
    }

    public void adjustRuntimeMapping(AbstractDirectMapping mapping) {
        // nothing to adjust
    }

    public MWNullValuePolicy getValueForTopLink() {
        return null;
    }
}
