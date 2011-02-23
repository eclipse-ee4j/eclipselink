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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.queries.DeleteAllQuery;

/**
 * This builder/visitor is responsible to populate a {@link DeleteAllQuery} when the query is a
 * <b>DELETE</b> query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class DeleteQueryVisitor extends AbstractModifyAllQueryBuilder<DeleteAllQuery> {

	/**
	 * Creates a new <code>DeleteQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	DeleteQueryVisitor(QueryBuilderContext queryContext) {
		super(queryContext);
	}
}