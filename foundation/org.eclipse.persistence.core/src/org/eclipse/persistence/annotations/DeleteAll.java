/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - part of fix for bug 351186
package org.eclipse.persistence.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A DeleteAll annotation is specified to indicate that when an relationship
 * is deleted, it should use a delete all query.  This typically happens if the
 * relationship is PrivateOwned and its owner is deleted.  In that case, the members
 * of the relationship will be deleted without reading them in.
 *
 * Use this annotation with caution.  EclipseLink will not validate for you whether the
 * target entity is mapped in such a way as to allow the delete all to work.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface DeleteAll {
}
