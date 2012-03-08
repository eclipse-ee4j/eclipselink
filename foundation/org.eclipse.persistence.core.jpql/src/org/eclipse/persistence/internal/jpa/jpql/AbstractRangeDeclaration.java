/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;

/**
 * The abstract definition of a range declaration, which is used to navigate to a "root" object.
 *
 * @see DerivedDeclaration
 * @see RangeDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
abstract class AbstractRangeDeclaration extends Declaration {

	/**
	 * The list of <b>JOIN</b> expressions that are declared in the same declaration than the
	 * range variable declaration.
	 */
	List<Join> joins;

	/**
	 * Creates a new <code>AbstractRangeDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	AbstractRangeDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * Adds the given {@link Join} with its identification variable, which can be <code>null</code>.
	 *
	 * @param join The {@link Join} that was found in the list of joins
	 */
	void addJoin(Join join) {
		if (joins == null) {
			joins = new LinkedList<Join>();
		}
		joins.add(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RangeVariableDeclaration getBaseExpression() {
		return (RangeVariableDeclaration) super.getBaseExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableDeclaration getDeclarationExpression() {
		return (IdentificationVariableDeclaration) super.getDeclarationExpression();
	}

	/**
	 * Returns the <b>JOIN</b> expressions that were part of the range variable declaration in the
	 * ordered they were parsed.
	 *
	 * @return The ordered list of <b>JOIN</b> expressions or an empty collection if none was
	 * present
	 */
	public List<Join> getJoins() {
		return (joins == null) ? Collections.<Join>emptyList() : joins;
	}

	/**
	 * Determines whether the declaration contains <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if at least one <b>JOIN</b> expression was parsed; otherwise
	 * <code>false</code>
	 */
	public boolean hasJoins() {
		return joins != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isCollection() {
		return false;
	}
}