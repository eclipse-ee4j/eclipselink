/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.Collections;
import java.util.List;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries using the default (generic) JPA support.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public final class DefaultContentAssistTest extends AbstractContentAssistTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> acceptableType(String identifier) {
		return defaultAcceptableType(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> classNames() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> columnNames(String tableName) {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> enumConstants() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> enumTypes() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<String> tableNames() {
		return Collections.emptyList();
	}
}