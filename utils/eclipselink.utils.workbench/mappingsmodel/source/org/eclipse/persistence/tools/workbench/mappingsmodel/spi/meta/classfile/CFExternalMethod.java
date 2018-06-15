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
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.classfile.Method;


/**
 * Wrap a java.lang.reflect.Method.
 */
final class CFExternalMethod
    extends CFExternalMember
    implements ExternalMethod
{

    /**
     * Constructor.
     */
    CFExternalMethod(Method method, CFExternalClass declaringExternalClass) {
        super(method, declaringExternalClass);
    }


    // ********** ExternalMethod implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getExceptionTypes()
     */
    public ExternalClassDescription[] getExceptionTypes() {
        return this.buildClassDescriptionArray(this.getMethod().exceptionClassNames());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getParameterTypes()
     */
    public ExternalClassDescription[] getParameterTypes() {
        return this.buildClassDescriptionArray(this.getMethod().javaParameterTypeNames());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getReturnType()
     */
    public ExternalClassDescription getReturnType() {
        return this.classDescriptionNamed(this.getMethod().javaReturnTypeName());
    }


    // ********** internal methods **********

    /**
     * Convenience method.
     */
    private Method getMethod() {
        return (Method) this.member;
    }

}
