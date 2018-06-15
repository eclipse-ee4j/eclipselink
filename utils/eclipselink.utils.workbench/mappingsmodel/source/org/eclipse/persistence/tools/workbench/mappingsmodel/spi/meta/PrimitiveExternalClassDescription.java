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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This is a hard-coded implementation of ExternalClassDescription that can be used for
 * primitive types (int, float, void, etc.).
 */
final class PrimitiveExternalClassDescription
    implements ExternalClassDescription
{
    private final Class primitiveClass;
    private final ExternalClass externalClass;


    PrimitiveExternalClassDescription(Class primitiveClass) {
        super();
        this.primitiveClass = primitiveClass;
        this.externalClass = new PrimitiveExternalClass(primitiveClass);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription#getAdditionalInfo()
     */
    public String getAdditionalInfo() {
        return "";
    }

    /**
     * "int", "float", "void", etc.
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription#getName()
     */
    public String getName() {
        return this.primitiveClass.getName();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getArrayDepth()
     */
    public int getArrayDepth() {
        return 0;
    }

    /**
     * "int", "float", "void", etc.
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getElementTypeName()
     */
    public String getElementTypeName() {
        return this.primitiveClass.getName();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getExternalClass()
     */
    public synchronized ExternalClass getExternalClass() throws ExternalClassNotFoundException {
        return this.externalClass;
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#isSynthetic()
     */
    public boolean isSynthetic() {
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return StringTools.buildToStringFor(this, this.getName());
    }

}
