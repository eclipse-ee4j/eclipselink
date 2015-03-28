/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *         Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation annotation allows the definition of multiple
 * RestPageableQuery.
 *
 * @author Dmitry Kornilov
 */
@Target(value= ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface RestPageableQueries {

    /**
     * (Required) An array of REST pageable queries.
     */
    RestPageableQuery[] value();
}
