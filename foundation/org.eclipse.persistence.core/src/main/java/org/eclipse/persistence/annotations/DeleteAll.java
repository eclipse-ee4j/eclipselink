/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
