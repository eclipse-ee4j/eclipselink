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
package org.eclipse.persistence.jpa.tests.jpql.model;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class EclipseLinkJPQLStateObjectTest extends AbstractStateObjectTest {

	public static StateObjectTester stateObject_224() throws Exception {

		// SELECT FUNC('NVL', e.firstName, 'NoFirstName'),
		//        func('NVL', e.lastName,  'NoLastName')
		// FROM Employee e

		return selectStatement(
			select(
				function(FUNC, "NVL", path("e.firstName"), string("'NoFirstName'")),
				function(FUNC, "NVL", path("e.lastName"),  string("'NoLastName'"))
			),
			from("Employee", "e")
		);
	}
}