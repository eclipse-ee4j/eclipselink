/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Lukas Jungmann - initial API and implementation (bug 518155)
 ******************************************************************************/
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A UuidGenerators annotation allows the definition of multiple UuidGenerator.
 *
 * @see UuidGenerator
 * @author Lukas Jungmann
 * @since EclipseLink 2.7
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface UuidGenerators {

    /**
     * (Required) An array of UuidGenerator.
     */
    UuidGenerator[] value();

}
