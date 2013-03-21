/*******************************************************************************
 * Copyright (c) 2012, 2013, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.ClassType;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.EmptyIterable;

/**
 * This extension can be used to provide additional support to JPQL content assist that is outside
 * the scope of providing proposals related to JPA metadata. It adds support for providing
 * suggestions related to class names, enum constants, table names, column names.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see AbstractJPQLQueryHelper#buildContentAssistProposals(int, ContentAssistExtension)
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public interface ContentAssistExtension {

	/**
	 * The <code>null</code> instance of <code>ContentAssistExtension</code>.
	 */
	ContentAssistExtension NULL_HELPER = new ContentAssistExtension() {
		public Iterable<String> classNames(String prefix, ClassType type) {
			return EmptyIterable.instance();
		}
		public Iterable<String> columnNames(String tableName, String prefix) {
			return EmptyIterable.instance();
		}
		public Iterable<String> tableNames(String tableNamePrefix) {
			return EmptyIterable.instance();
		}
	};

	/**
	 * Returns the fully qualified class names filtered by the given prefix and type.
	 *
	 * @param prefix The prefix is used to filter, it can be an empty string but never <code>null</code>
	 * @param type Determines how to filter the various types of classes
	 * @return The filtered fully qualified class names
	 */
	Iterable<String> classNames(String prefix, ClassType type);

	/**
	 * Returns the names of the given table's columns.
	 *
	 * @param tableName The name of the table to retrieve the name of its columns, which is never
	 * <code>null</code>
	 * @param prefix
	 * @return The column names
	 */
	Iterable<String> columnNames(String tableName, String prefix);

	/**
	 * Returns the names of the database tables filtered by the given prefix.
	 *
	 * @param prefix The prefix is used to filter, it can be an empty string but never <code>null</code>
	 * @return The filtered table names
	 */
	Iterable<String> tableNames(String prefix);
}