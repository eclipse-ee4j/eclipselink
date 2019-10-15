/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * This {@link IJPQLQueryFormatter} is used to generate a string representation of a {@link
 * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} based on how it was parsed,
 * which means this formatter can only be used when the
 * {@link org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} was created by parsing a JPQL
 * query because it needs to retrieve parsing information from the corresponding
 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}.
 * <p>
 * It is possible to partially match the JPQL query that was parsed, the value of the <em>exactMatch</em>
 * will determine whether the string representation of any given
 * {@link org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} should reflect the exact string
 * that was parsed. <code>true</code> will use every bit of information contained in the corresponding {@link
 * org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to perfectly match what was parsed;
 * <code>false</code> will only match the case sensitivity of the JPQL identifiers.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultActualJPQLQueryFormatter extends AbstractActualJPQLQueryFormatter {

    /**
     * Creates a new <code>DefaultActualJPQLQueryFormatter</code>.
     *
     * @param exactMatch Determines whether the string representation of any given {@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} should reflect the exact string that
     * was parsed: <code>true</code> will use every bit of information contained in the corresponding
     * {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to perfectly match what
     * was parsed (case of JPQL identifiers and the presence of whitespace); <code>false</code> will
     * only match the case sensitivity of the JPQL identifiers
     */
    public DefaultActualJPQLQueryFormatter(boolean exactMatch) {
        super(exactMatch);
    }

    /**
     * Creates a new <code>DefaultActualJPQLQueryFormatter</code>.
     *
     * @param exactMatch Determines whether the string representation of any given {@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} should reflect the exact string that
     * was parsed: <code>true</code> will use every bit of information contained in the corresponding
     * {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to perfectly match what
     * was parsed (case of JPQL identifiers and the presence of whitespace); <code>false</code> will
     * only match the case sensitivity of the JPQL identifiers
     * @param style Determines how the JPQL identifiers are written out, which is used if the {@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} was modified after its
     * creation
     * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
     */
    public DefaultActualJPQLQueryFormatter(boolean exactMatch, IdentifierStyle style) {
        super(exactMatch, style);
    }
}
