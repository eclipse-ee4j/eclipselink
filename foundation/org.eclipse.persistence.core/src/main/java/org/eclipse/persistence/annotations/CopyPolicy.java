/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     tware - March 25/2008 - 1.0M6 - Initial implementation

package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A CopyPolicy is used to set an
 * org.eclipse.persistence.descriptors.copying.CopyPolicy on an Entity.
 * It is required that a class that
 * implements org.eclipse.persistence.descriptors.copying.CopyPolicy
 * be specified as the argument.
 *
 * A CopyPolicy should be specified on an Entity, MappedSuperclass or
 * Embeddable.
 *
 * For instance:
 * {@literal @}Entity
 * {@literal @}CopyPolicy("example.MyCopyPolicy")
 *
 * @see org.eclipse.persistence.descriptors.copying.CopyPolicy
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @see org.eclipse.persistence.annotations.InstantiationCopyPolicy
 *
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CopyPolicy {

    /**
     * (Required)
     * This defines the class of the copy policy.  It must specify a class that
     * implements org.eclipse.persistence.descriptors.copying.CopyPolicy
     */
    Class value();
}
