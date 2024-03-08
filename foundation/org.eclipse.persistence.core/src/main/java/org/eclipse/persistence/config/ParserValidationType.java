/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.config;

/**
 * Parser type property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.JPQL_VALIDATION, ParserValidationType.JPA21);</code>
 * <p>Property values are case-insensitive.
 *
 * @see org.eclipse.persistence.queries.JPAQueryBuilder JPAQueryBuilder
 */
public class ParserValidationType {
    public static final String EclipseLink = "EclipseLink";
    public static final String JPA10 = "JPA 1.0";
    public static final String JPA20 = "JPA 2.0";
    public static final String JPA21 = "JPA 2.1";
    public static final String JPA22 = "JPA 2.2";
    public static final String JPA30 = "JPA 3.0";
    public static final String JPA31 = "JPA 3.1";
    public static final String None = "None";

    public static final String DEFAULT = EclipseLink;

    /**
     * @deprecated This constructor will be marked private and the class final. It is not designed for extensibility.
     */
    @Deprecated(since = "4.0.3", forRemoval = true)
    public ParserValidationType() {
        // no instance please
    }
}
