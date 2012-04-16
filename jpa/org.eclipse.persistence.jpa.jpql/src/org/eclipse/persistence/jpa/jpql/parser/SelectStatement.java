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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A select statement must always have a <b>SELECT</b> and a <b>FROM</b> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause]</code><p>
 *
 * @see SelectClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class SelectStatement extends AbstractSelectStatement {

	/**
	 * Determines whether there is a whitespace after the select statement parsed by the superclass
	 * and the <b>ORDER BY</b> identifier.
	 */
	private boolean hasSpaceBeforeOrderBy;

        /**
         * Determines whether there is a whitespace after the select statement parsed by the superclass
         * and the <b>UNION</b> identifier.
         */
        private boolean hasSpaceBeforeUnion;

	/**
	 * The <b>ORDER BY</b> expression.
	 */
	private AbstractExpression orderByClause;

        /**
         * The <b>UNION</b> expression.
         */
        private List<AbstractExpression> unionClauses;

	/**
	 * Creates a new <code>SelectStatement</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public SelectStatement(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void acceptChildren(ExpressionVisitor visitor) {
		super.acceptChildren(visitor);
		getOrderByClause().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {

		super.addChildrenTo(children);

		if (orderByClause != null) {
			children.add(orderByClause);
		}

                if (unionClauses != null) {
                        children.addAll(unionClauses);
                }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		super.addOrderedChildrenTo(children);

		if (hasSpaceBeforeOrderBy) {
			children.add(buildStringExpression(SPACE));
		}

		// 'ORDER BY' clause
		if (orderByClause != null) {
			children.add(orderByClause);
		}

                // 'UNION' clause
                if (unionClauses != null) {
                        children.addAll(unionClauses);
                }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FromClause buildFromClause() {
		return new FromClause(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SelectClause buildSelectClause() {
		return new SelectClause(this);
	}

	/**
	 * Returns the {@link Expression} representing the <b>ORDER BY</b> clause.
	 *
	 * @return The expression representing the <b>ORDER BY</b> clause
	 */
	public Expression getOrderByClause() {
		if (orderByClause == null) {
			orderByClause = buildNullExpression();
		}
		return orderByClause;
	}

        /**
         * Returns the list of {@link Expression} representing the <b>UNION</b> clauses.
         *
         * @return The list of expression representing the <b>UNION</b> clauses
         */
        public List<AbstractExpression> getUnionClauses() {
                if (unionClauses == null) {
                    unionClauses = new ArrayList<AbstractExpression>();
                }
                return unionClauses;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(SelectStatementBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectClause getSelectClause() {
		return (SelectClause) super.getSelectClause();
	}

	/**
	 * Determines whether the <b>ORDER BY</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>ORDER BY</b> clause
	 */
	public boolean hasOrderByClause() {
		return orderByClause != null &&
		      !orderByClause.isNull();
	}

        /**
         * Determines whether the <b>UNION</b> clause is defined.
         *
         * @return <code>true</code> if the query that got parsed had the <b>UNION</b> clause
         */
        public boolean hasUnionClause() {
                return unionClauses != null &&
                      !unionClauses.isEmpty();
        }

	/**
	 * Determines whether a whitespace was parsed before the <b>ORDER BY</b> clause. In some cases,
	 * the space could be owned by a child of the previous clause.
	 *
	 * @return <code>true</code> if there was a whitespace before the <b>ORDER BY</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceBeforeOrderBy() {
		return hasSpaceBeforeOrderBy;
	}

        /**
         * Determines whether a whitespace was parsed before the <b>UNION</b> clause. In some cases,
         * the space could be owned by a child of the previous clause.
         *
         * @return <code>true</code> if there was a whitespace before the <b>UNION</b>; <code>false</code>
         * otherwise
         */
        public boolean hasSpaceBeforeUnion() {
                return hasSpaceBeforeUnion;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		super.parse(wordParser, tolerant);

		hasSpaceBeforeOrderBy = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'ORDER BY'
		if (wordParser.startsWithIdentifier(ORDER_BY)) {
			orderByClause = new OrderByClause(this);
			orderByClause.parse(wordParser, tolerant);
		}

                hasSpaceBeforeUnion = wordParser.skipLeadingWhitespace() > 0;
                
                // Parse 'UNION'
                while (wordParser.startsWithIdentifier(UNION) || wordParser.startsWithIdentifier(INTERSECT)
                            || wordParser.startsWithIdentifier(EXCEPT)) {
                        UnionClause unionClause = new UnionClause(this);
                        unionClause.parse(wordParser, tolerant);
                        getUnionClauses().add(unionClause);
                        wordParser.skipLeadingWhitespace();
                }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		super.toParsedText(writer, actual);

		if (hasSpaceBeforeOrderBy) {
			writer.append(SPACE);
		}

		// 'ORDER BY' clause
		if (hasOrderByClause()) {
			orderByClause.toParsedText(writer, actual);
		}

                // 'UNION' clause
                if (hasUnionClause()) {
                        for (AbstractExpression union : this.unionClauses) {
                            union.toParsedText(writer, actual);
                        }
                }
	}
}