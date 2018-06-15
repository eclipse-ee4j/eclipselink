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
package org.eclipse.persistence.jpa.tests.jpql.parser;

/**
 * This formatter is used to format the real JPQL query that was parsed before it is compared to
 * the string representation of the parsed tree, which can be different because the parsed tree
 * does not keep track of multiple whitespace but only one.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface JPQLQueryStringFormatter {

    /**
     * The default implementation of {@link QueryStringFormatter}, which returns the JPQL query
     * without formatting it.
     */
    public static JPQLQueryStringFormatter DEFAULT = new JPQLQueryStringFormatter() {

        /**
         * {@inheritDoc}
         */
        public String format(String query) {
            return query;
        }
    };

    /**
     * Formats the given JPQL query by changing its information, which is usually changing the
     * number of whitespace between two non-whitespace characters.
     *
     * @param query The JPQL query to format
     * @return The formatted JPQL query
     */
    String format(String query);
}
