/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     09/02/2019-3.0 Alexandre Jacob
//        - 527415: Fix code when locale is tr, az or lt
package org.eclipse.persistence.jpa.jpql.tools.model;

import java.util.Locale;
import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import static org.eclipse.persistence.jpa.jpql.tools.model.BaseJPQLQueryFormatter.*;

/**
 * A <code>IJPQLQueryFormatter</code> helps to write a string representation of a {@link StateObject}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IJPQLQueryFormatter {

    /**
     * Creates a string representation of the given {@link StateObject}.
     *
     * @param stateObject The {@link StateObject} that represents a complete or incomplete JPQL query
     * @return The string representation of the given {@link StateObject}
     */
    String toString(StateObject stateObject);

    /**
     * This enumeration determines how the JPQL identifiers are formatted when written out.
     */
    public enum IdentifierStyle {

        /**
         * The JPQL identifiers are written out the first letter being uppercase and the rest being
         * lower case.
         */
        CAPITALIZE_EACH_WORD,

        /**
         * The JPQL identifiers are written out with lowercase letters.
         */
        LOWERCASE,

        /**
         * The JPQL identifiers are written out with uppercase letters.
         */
        UPPERCASE;

        /**
         * Returns the given JPQL identifier with the first letter of each word capitalized and the rest
         * being lower case.
         *
         * @param identifier The JPQL identifier to format
         * @return The formatted JPQL identifier
         */
        public String capitalizeEachWord(String identifier) {

            StringBuilder sb = new StringBuilder();

            for (StringTokenizer tokenizer = new StringTokenizer(identifier, SPACE); tokenizer.hasMoreTokens(); ) {
                String token = tokenizer.nextToken();
                sb.append(Character.toUpperCase(token.charAt(0)));
                sb.append(token.substring(1).toLowerCase(Locale.ROOT));
                if (tokenizer.hasMoreTokens()) {
                    sb.append(SPACE);
                }
            }

            return sb.toString();
        }

        /**
         * Formats the given JPQL identifier, if it needs to be decorated with more information. Which
         * depends on how the string is created.
         *
         * @param identifier JPQL identifier to format
         * @return By default the given identifier is returned
         */
        public String formatIdentifier(String identifier) {
            switch (this) {
                case CAPITALIZE_EACH_WORD: return capitalizeEachWord(identifier);
                case LOWERCASE:            return identifier.toLowerCase(Locale.ROOT);
                default:                   return identifier.toUpperCase(Locale.ROOT);
            }
        }
    }
}
