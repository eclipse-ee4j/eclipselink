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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;


/**
 * Wrap a java.lang.reflect.Method.
 */
final class CLExternalMethod
    extends CLExternalMember
    implements ExternalMethod
{

    /**
     * Constructor.
     */
    CLExternalMethod(Method method, CLExternalClass declaringExternalClass) {
        super(method, declaringExternalClass);
    }


    // ********** ExternalMethod implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getExceptionTypes()
     */
    public ExternalClassDescription[] getExceptionTypes() {
        return this.buildClassDescriptionArray(this.getMethod().getExceptionTypes());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getParameterTypes()
     */
    public ExternalClassDescription[] getParameterTypes() {
        return this.buildClassDescriptionArray(this.getMethod().getParameterTypes());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getReturnType()
     */
    public ExternalClassDescription getReturnType() {
        return this.classDescriptionFor(this.getMethod().getReturnType());
    }


    // ********** internal methods **********

    /**
     * Convenience method.
     */
    private Method getMethod() {
        return (Method) this.member;
    }

}
