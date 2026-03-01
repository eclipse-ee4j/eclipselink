/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     tware - March 25/2008 - 1.0M6 - Initial implementation

package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A CopyPolicy is used to set an {@linkplain org.eclipse.persistence.descriptors.copying.CopyPolicy} on an Entity.
 * It is required that a class that implements {@linkplain org.eclipse.persistence.descriptors.copying.CopyPolicy}
 * must be specified as the argument.
 * <p>
 * A CopyPolicy should be specified on an Entity, MappedSuperclass or Embeddable.
 * <p>
 * For instance:
 * {@snippet :
 *  @Entity
 *  @CopyPolicy("example.MyCopyPolicy")
 * }
 *
 * @see org.eclipse.persistence.descriptors.copying.CopyPolicy
 * @see CloneCopyPolicy
 * @see InstantiationCopyPolicy
 *
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CopyPolicy {

    /**
     * This defines the class of the copy policy. It must specify a class that
     * implements {@linkplain org.eclipse.persistence.descriptors.copying.CopyPolicy}
     */
    Class<?> value();
}
