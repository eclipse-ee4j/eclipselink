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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.utility.classfile.Field;


/**
 * Wrap a ClassFileField.
 */
final class CFExternalField
    extends CFExternalMember
    implements ExternalField
{

    /**
     * Constructor.
     */
    CFExternalField(Field field, CFExternalClass declaringExternalClass) {
        super(field, declaringExternalClass);
    }


    // ********** ExternalField implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField#getType()
     */
    public ExternalClassDescription getType() {
        return this.classDescriptionNamed(this.getField().javaTypeName());
    }


    // ********** internal methods **********

    /**
     * Convenience method.
     */
    private Field getField() {
        return (Field) this.member;
    }

}
