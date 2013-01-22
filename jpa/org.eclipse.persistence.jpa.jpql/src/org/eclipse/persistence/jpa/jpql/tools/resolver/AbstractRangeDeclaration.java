/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.Join;

/**
 * The abstract definition of a range declaration, which is used to navigate to a "root" object.
 *
 * @see DerivedDeclaration
 * @see RangeDeclaration
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public abstract class AbstractRangeDeclaration extends Declaration {

	/**
	 * The list of <b>JOIN</b> expressions that are declared in the same declaration than the range
	 * variable declaration represented by this declaration.
	 */
	protected List<Join> joins;

	/**
	 * Creates a new <code>AbstractRangeDeclaration</code>.
	 */
	public AbstractRangeDeclaration() {
		super();
	}

	/**
	 * Adds the given {@link Join}.
	 *
	 * @param join The {@link Join} that is declared in the range variable declaration
	 */
	protected void addJoin(Join join) {
		if (joins == null) {
			joins = new LinkedList<Join>();
		}
		joins.add(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Join> getJoins() {
		if (joins == null) {
			return Collections.emptyList();
		}
		return joins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasJoins() {
		return (joins != null);
	}
}