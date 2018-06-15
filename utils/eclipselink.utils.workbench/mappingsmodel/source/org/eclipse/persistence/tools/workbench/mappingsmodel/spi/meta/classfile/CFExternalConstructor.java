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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.utility.classfile.Method;


/**
 * Wrap a ClassFileMethod.
 */
final class CFExternalConstructor
    extends CFExternalMember
    implements ExternalConstructor
{

    /**
     * Constructor.
     */
    CFExternalConstructor(Method constructor, CFExternalClass declaringExternalClass) {
        super(constructor, declaringExternalClass);
    }


    // ********** ExternalConstructor implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getName()
     */
    public String getName() {
        return this.getConstructor().constructorName();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor#getExceptionTypes()
     */
    public ExternalClassDescription[] getExceptionTypes() {
        return this.buildClassDescriptionArray(this.getConstructor().exceptionClassNames());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor#getParameterTypes()
     */
    public ExternalClassDescription[] getParameterTypes() {
        return this.buildClassDescriptionArray(this.getConstructor().javaParameterTypeNames());
    }


    // ********** internal methods **********

    /**
     * Convenience method.
     */
    private Method getConstructor() {
        return (Method) this.member;
    }

}
