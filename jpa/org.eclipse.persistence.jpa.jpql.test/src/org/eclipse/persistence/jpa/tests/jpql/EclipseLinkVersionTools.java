/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class EclipseLinkVersionTools {

	/**
	 * Creates a new <code>EclipseLinkVersionTools</code>.
	 */
	private EclipseLinkVersionTools() {
		super();
	}

	public static boolean isNewerThan2_4(JPQLGrammar grammar) {
		return isNewerThan2_4(grammar.getProviderVersion());
	}

	public static boolean isNewerThan2_4(String value) {
		return EclipseLinkVersion.value(value).isNewerThan(EclipseLinkVersion.VERSION_2_4);
	}

	public static boolean isNewerThan2_5(JPQLGrammar grammar) {
		return isNewerThan2_5(grammar.getProviderVersion());
	}

	public static boolean isNewerThan2_5(String value) {
		return EclipseLinkVersion.value(value).isNewerThan(EclipseLinkVersion.VERSION_2_5);
	}

	public static boolean isOlderThan2_4(JPQLGrammar grammar) {
		return isOlderThan2_4(grammar.getProviderVersion());
	}

	public static boolean isOlderThan2_4(String value) {
		return EclipseLinkVersion.value(value).isOlderThan(EclipseLinkVersion.VERSION_2_4);
	}

	public static boolean isOlderThan2_5(JPQLGrammar grammar) {
		return isOlderThan2_5(grammar.getProviderVersion());
	}

	public static boolean isOlderThan2_5(String value) {
		return EclipseLinkVersion.value(value).isOlderThan(EclipseLinkVersion.VERSION_2_5);
	}
}