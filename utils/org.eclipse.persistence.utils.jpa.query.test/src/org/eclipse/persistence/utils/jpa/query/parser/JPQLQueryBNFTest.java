/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Iterator;
import org.junit.Test;

public final class JPQLQueryBNFTest
{
	@Test
	public void test_SelectItemIdentifiers()
	{
		JPQLQueryBNF queryBNF = JPQLExpression.queryBNF(SelectItemBNF.ID);

		for (Iterator<String> iter = queryBNF.identifiers(); iter.hasNext(); )
		{
			System.out.println(iter.next());
		}
	}
}