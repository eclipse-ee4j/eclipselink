/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.spi;

/**
 * This enumeration lists the mapping types defined in the Java Persistence functional specification.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface IMappingType {

    /**
     * The constant for a basic mapping.
     */
    int BASIC = 1;

    /**
     * The constant for an element collection mapping.
     */
    int ELEMENT_COLLECTION = 2;

    /**
     * The constant for an embedded mapping.
     */
    int EMBEDDED = 3;

    /**
     * The constant for an embedded ID mapping.
     */
    int EMBEDDED_ID = 4;

    /**
     * The constant for an ID mapping.
     */
    int ID = 5;

    /**
     * The constant for a many to many mapping.
     */
    int MANY_TO_MANY = 6;

    /**
     * The constant for a many to one mapping.
     */
    int MANY_TO_ONE = 7;

    /**
     * The constant for a one to many mapping.
     */
    int ONE_TO_MANY = 8;

    /**
     * The constant for a one to one mapping.
     */
    int ONE_TO_ONE = 9;

    /**
     * The constant for an attribute that is not persistent.
     */
    int TRANSIENT = 10;

    /**
     * The constant for a version mapping.
     */
    int VERSION = 11;
}
