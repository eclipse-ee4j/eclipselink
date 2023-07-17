/*
 * Copyright (c) 2006, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in the
 * latest Jakarta Persistence functional specification.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see DefaultJPQLGrammar
 * @see DefaultEclipseLinkJPQLGrammar
 *
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultJPQLGrammar implements JPQLGrammar {

    /**
     * The generic persistence provider name: JPA.
     */
    public static final String PROVIDER_NAME = "JPA";

    /**
     * Creates a new <code>DefaultJPQLGrammar</code>.
     */
    private DefaultJPQLGrammar() {
        super();
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance of this {@link DefaultJPQLGrammar}
     */
    public static JPQLGrammar instance() {
        return JPQLGrammar3_2.instance();
    }

    @Override
    public ExpressionRegistry getExpressionRegistry() {
        return instance().getExpressionRegistry();
    }

    @Override
    public JPAVersion getJPAVersion() {
        return instance().getJPAVersion();
    }

    @Override
    public String getProvider() {
        return instance().getProvider();
    }

    @Override
    public String getProviderVersion() {
        return ExpressionTools.EMPTY_STRING;
    }

    @Override
    public String toString() {
        return instance().toString();
    }
}
