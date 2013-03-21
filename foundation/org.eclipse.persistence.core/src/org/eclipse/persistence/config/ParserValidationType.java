/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.config;

/**
 * Parser type property values.
 *
 * <p>JPA persistence property Usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.JPQL_VALIDATION, ParserValidationType.JPA21);</code>
 * <p>Property values are case-insensitive.
 *
 * @see JPAQueryBuilder
 */
public class ParserValidationType {
    public static final String EclipseLink = "EclipseLink";
    public static final String JPA10 = "JPA 1.0";
    public static final String JPA20 = "JPA 2.0";
    public static final String JPA21 = "JPA 2.1";
    public static final String None = "None";

    public static final String DEFAULT = EclipseLink;
}