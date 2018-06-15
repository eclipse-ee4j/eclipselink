/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.config;

import org.eclipse.persistence.queries.JPAQueryBuilder;

/**
 * Parser type property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.JPQL_PARSER, ParserType.Hermes);</code>
 * <p>Property values are case-insensitive.
 *
 * @see JPAQueryBuilder
 */
public class ParserType {
    public static final String Hermes = "Hermes";
    @Deprecated
    public static final String ANTLR = "ANTLR";

    public static final String DEFAULT = Hermes;
}

