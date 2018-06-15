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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.Expression;

/**
 * A problem describes an issue found in a JPQL query because it is either grammatically or
 * semantically incorrect.
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
public interface JPQLQueryProblem {

    /**
     * Returns the position from where the problem ends, inclusively.
     *
     * @return The position of the last character that was found as problematic within the JPQL query,
     * inclusively
     */
    int getEndPosition();

    /**
     * Returns the parsed tree representing the JPQL query.
     *
     * @return The parsed tree representing the JPQL query
     */
    Expression getExpression();

    /**
     * Returns the arguments associate with the problem's message.
     *
     * @return A non-<code>null</code> list of arguments that can be used to format the localized message
     */
    String[] getMessageArguments();

    /**
     * Returns the resource bundle key used to retrieve the localized message.
     *
     * @return The key used to retrieve the localized message
     */
    String getMessageKey();

    /**
     * Returns the position from where the problem starts.
     *
     * @return The position of the first character that was found as problematic within the JPQL
     * query, inclusively
     */
    int getStartPosition();
}
