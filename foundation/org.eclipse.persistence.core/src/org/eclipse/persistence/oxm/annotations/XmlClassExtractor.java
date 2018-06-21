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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.persistence.descriptors.ClassExtractor;

/**
 * A ClassExtractor allows for a user defined class indicator in place of
 * using xsi:type. The class has the following restrictions:

 *  - It must extend the org.eclipse.persistence.descriptors.ClassExtractor
 *    class and implement the extractClassFromRow(Record, Session) method.
 *  - That method must take a database row (a Record/Map) as an argument and
 *    must return the class to use for that row.
 *
 * This method will be used to decide which class to instantiate when
 * unmarshalling an instance document.
 *
 * The ClassExtractor must only be set on the root of an entity class or
 * sub-hierarchy in which a different inheritance strategy is applied.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlClassExtractor {

    /**
     * (Required) Defines the name of the class extractor that should be
     * applied to this entity's descriptor.
     */
    Class<? extends ClassExtractor> value();

}
