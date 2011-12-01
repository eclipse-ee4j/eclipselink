/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model;

import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

import static org.eclipse.persistence.jpa.jpql.model.BaseJPQLQueryFormatter.*;

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
				sb.append(token.substring(1).toLowerCase());
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
				case LOWERCASE:            return identifier.toLowerCase();
				default:                   return identifier.toUpperCase();
			}
		}
	}
}