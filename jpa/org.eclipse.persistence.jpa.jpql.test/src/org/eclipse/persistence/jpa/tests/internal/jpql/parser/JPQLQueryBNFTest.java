/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectItemBNF;

import org.junit.Test;

public final class JPQLQueryBNFTest {

	@Test
	public void test_SelectItemIdentifiers() {

		JPQLQueryBNF queryBNF = JPQLExpression.queryBNF(SelectItemBNF.ID);

		for (String identifier : queryBNF.identifiers()) {
			System.out.println(identifier);
		}
	}
}