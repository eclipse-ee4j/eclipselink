/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.util.iterable.EmptyIterable;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public interface ContentAssistProposalsHelper {

	/**
	 * The <code>null</code> instance of <code>ContentAssistProposalsHelper</code>.
	 */
	ContentAssistProposalsHelper NULL_HELPER = new ContentAssistProposalsHelper() {
		public Iterable<String> classNames(String prefix) {
			return EmptyIterable.instance();
		}
		public Iterable<String> columnNames(String tableName) {
			return EmptyIterable.instance();
		}
		public Iterable<String> tableNames(String tableNamePrefix) {
			return EmptyIterable.instance();
		}
	};

	/**
	 * Returns
	 *
	 * @param prefix
	 * @return
	 */
	Iterable<String> classNames(String prefix);

	/**
	 * Returns
	 *
	 * @param tableName
	 * @return
	 */
	Iterable<String> columnNames(String tableName);

	/**
	 * Returns
	 *
	 * @param tableNamePrefix
	 * @return
	 */
	Iterable<String> tableNames(String tableNamePrefix);
}