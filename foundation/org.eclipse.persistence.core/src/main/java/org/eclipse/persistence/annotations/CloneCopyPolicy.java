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
 * A CloneCopyPolicy is used to set an {@linkplain org.eclipse.persistence.descriptors.copying.CloneCopyPolicy}
 * on an Entity. A CloneCopyPolicy must specify at least one or both of the {@linkplain #method()} or {@linkplain #workingCopyMethod()}.
 * <ul>
 * <li>{@linkplain #workingCopyMethod()} is used to clone objects that will be returned to the user
 *      as they are registered in EclipseLink's transactional mechanism, the {@linkplain org.eclipse.persistence.sessions.UnitOfWork}.</li>
 * <li>{@linkplain #method()} is used for the clone that is used for comparison in conjunction with
 *      EclipseLink's {@linkplain org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy}.</li>
 * </ul>
 * <p>
 * A CloneCopyPolicy can be specified on an Entity, MappedSuperclass or Embeddable.
 * <p><b>Example:</b>
 * {@snippet :
 *  @Entity
 *  @CloneCopyPolicy(method="myCloneMethod")
 * }
 * or:
 * {@snippet :
 *  @Entity
 *  @CloneCopyPolicy(method="myCloneMethod", workingCopyMethod="myWorkingCopyCloneMethod")
 * }
 * {@snippet :
 *  @Entity
 *  @CloneCopyPolicy(workingCopyMethodName="myWorkingCopyClone")
 * }
 *
 * @see org.eclipse.persistence.descriptors.copying.CloneCopyPolicy
 * @see CloneCopyPolicy
 * @see CopyPolicy
 *
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CloneCopyPolicy {

    /**
     * Defines a method that will be used to create a clone that will be
     * used for comparison by EclipseLink's {@linkplain org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy}.
     * <p>
     * Either method or {@linkplain #workingCopyMethod()} must be specified
     */
    String method();

    /**
     * Defines a method that will be used to create a clone that will be
     * used to create the object returned when registering an Object in an
     * EclipseLink {@linkplain org.eclipse.persistence.sessions.UnitOfWork}.
     * <p>
     * Either {@linkplain #method()} or workingCopyMethod must be specified
     */
    String workingCopyMethod();
}
