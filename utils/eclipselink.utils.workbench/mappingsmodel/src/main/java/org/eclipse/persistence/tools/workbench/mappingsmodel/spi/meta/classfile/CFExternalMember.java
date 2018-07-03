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
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember;
import org.eclipse.persistence.tools.workbench.utility.classfile.Member;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Common behavior for CFExternalField, CFExternalConstructor,
 * and CFExternalMethod.
 */
abstract class CFExternalMember
    implements ExternalMember
{

    /** The wrapped member. */
    final Member member;    // private-protected

    /** The external class that declares the member. */
    final CFExternalClass declaringClass;    // private-protected


    // ********** Constructors **********

    /**
     * Useful constructor.
     */
    CFExternalMember(Member member, CFExternalClass declaringClass) {    // private-protected
        super();
        this.member = member;
        this.declaringClass = declaringClass;
    }


    // ********** ExternalMember implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getDeclaringClass()
     */
    public ExternalClassDescription getDeclaringClass() {
        return this.classDescriptionNamed(this.member.declaringClassName());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getModifiers()
     */
    public int getModifiers() {
        return this.member.standardAccessFlags();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getName()
     */
    public String getName() {
        return this.member.name();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#isSynthetic()
     */
    public boolean isSynthetic() {
        return this.member.isSynthetic();
    }


    // ********** standard methods **********

    public String toString() {
        return StringTools.buildToStringFor(this, this.getName());
    }


    // ********** private-protected methods **********

    ExternalClassDescription classDescriptionNamed(String className) {    // private-protected
        return this.declaringClass.classDescriptionNamed(className);
    }

    ExternalClassDescription[] buildClassDescriptionArray(String[] classNames) {    // private-protected
        ExternalClassDescription[] externalClassDescriptions = new ExternalClassDescription[classNames.length];
        for (int i = classNames.length; i-- > 0; ) {
            externalClassDescriptions[i] = this.classDescriptionNamed(classNames[i]);
        }
        return externalClassDescriptions;
    }

}
