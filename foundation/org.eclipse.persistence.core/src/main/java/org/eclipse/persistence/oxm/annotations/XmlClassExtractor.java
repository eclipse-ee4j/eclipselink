/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A ClassExtractor allows for a user defined class indicator in place of
 * using xsi:type. The class has the following restrictions:
 * <ul>
 *  <li>It must extend the {@linkplain org.eclipse.persistence.descriptors.ClassExtractor} class and implement the
 *    {@linkplain org.eclipse.persistence.descriptors.ClassExtractor#extractClassFromRow(org.eclipse.persistence.sessions.DataRecord, org.eclipse.persistence.sessions.Session)}
 * method.</li>
 *  <li>That method must take a database row (a {@linkplain org.eclipse.persistence.sessions.DataRecord}/Map) as an argument and
 *    must return the class to use for that row.</li>
 * </ul>
 * <p>
 * This method will be used to decide which class to instantiate when
 * unmarshalling an instance document.
 * <p>
 * The ClassExtractor must only be set on the root of an entity class or
 * sub-hierarchy in which a different inheritance strategy is applied.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlClassExtractor {
    /**
     * Defines the name of the class extractor that should be
     * applied to this entity's descriptor.
     */
    Class<?> value();

}
