/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This visitor is responsible to populate an {@link ObjectLevelReadQueryVisitor}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ObjectLevelReadQueryVisitor extends AbstractObjectLevelReadQueryVisitor {

	/**
	 * Creates a new <code>ObjectLevelReadQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param query The {@link ObjectLevelReadQuery} to populate by using this visitor to visit the
	 * parsed tree representation of the JPQL query
	 */
	ObjectLevelReadQueryVisitor(JPQLQueryContext queryContext, ObjectLevelReadQuery query) {
		super(queryContext, query);
	}
}