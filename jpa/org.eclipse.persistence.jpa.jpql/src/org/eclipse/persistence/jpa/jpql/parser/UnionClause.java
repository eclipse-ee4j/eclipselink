/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>UNION</b> clause allows the results of two queries to be combined.
 * <p>
 * <div nowrap><b>BNF:</b> <code>union_clause ::= <b>UNION|INTERSECT|EXCEPT</b> [ALL] query</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class UnionClause extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after <b>UNION</b>.
	 */
	private boolean hasSpaceAfterUnion;

        /**
         * Determines whether a whitespace was parsed after <b>ALL</b>.
         */
        private boolean hasSpaceAfterAll;

	/**
	 * The actual <b></b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

        /**
         * The actual <b></b> identifier found in the string representation of the JPQL query.
         */
        private String actualIdentifier;

        /**
         * Determines if ALL keyword is used.
         */
        private boolean hasAll;

        /**
         * The actual <b></b> identifier found in the string representation of the JPQL query.
         */
        private String allIdentifier;

	/**
	 * The unioned query.
	 */
	private AbstractExpression query;

	/**
	 * Creates a new <code>UnionClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public UnionClause(AbstractExpression parent) {
		super(parent, UNION);
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
	public void acceptChildren(ExpressionVisitor visitor) {
		getQuery().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		children.add(buildStringExpression(identifier));

		if (hasSpaceAfterUnion) {
			children.add(buildStringExpression(SPACE));
		}
		
                if (hasAll) {
                    children.add(buildStringExpression(ALL));
                    if (hasSpaceAfterAll) {
                        children.add(buildStringExpression(SPACE));
                    }
                }

		if (query != null) {
			children.add(query);
		}
	}

        /**
         * Returns the union identifier.
         * This is one of "UNION", "INTERSECT", "EXCEPT".
         */
        public String getIdentifier() {
                return identifier;
        }

	/**
	 * Returns the actual union found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The union identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return actualIdentifier;
	}

	/**
	 * Returns the {@link Expression} representing the unioned query.
	 *
	 * @return The expression representing the query
	 */
	public Expression getQuery() {
		if (query == null) {
		    query = buildNullExpression();
		}
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(UnionClauseBNF.ID);
	}

	/**
	 * Determines whether the query  was parsed.
	 *
	 * @return <code>true</code> the query was parsed; <code>false</code> otherwise
	 */
	public boolean hasQuery() {
		return query != null &&
		      !query.isNull();
	}

        /**
         * Determines whether ALL was parsed.
         *
         * @return <code>true</code> if ALL was parsed; <code>false</code> otherwise
         */
        public boolean hasAll() {
                return hasAll;
        }

	/**
	 * Determines whether a whitespace was parsed after <b>UNION</b>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after <b>UNION</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterUnion() {
		return hasSpaceAfterUnion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		if (wordParser.startsWithIdentifier(UNION)) {
		    identifier = UNION;
		    actualIdentifier = wordParser.moveForward(UNION);
		} else if (wordParser.startsWithIdentifier(INTERSECT)) {
                    identifier = INTERSECT;
                    actualIdentifier = wordParser.moveForward(INTERSECT);
                } else if (wordParser.startsWithIdentifier(EXCEPT)) {
                    identifier = EXCEPT;
                    actualIdentifier = wordParser.moveForward(EXCEPT);
                }

		hasSpaceAfterUnion = wordParser.skipLeadingWhitespace() > 0;
		
		hasAll = wordParser.startsWithIdentifier(ALL);
		if (hasAll) {
	                allIdentifier = wordParser.moveForward(ALL);
	                hasSpaceAfterAll = wordParser.skipLeadingWhitespace() > 0;		    
		}

		// Query
		query = parse(wordParser, SubqueryBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'UNION'
		writer.append(actual ? actualIdentifier : identifier);

		if (hasSpaceAfterUnion) {
			writer.append(SPACE);
		}
		
		if (hasAll) {
	            writer.append(actual ? allIdentifier : ALL);
                    if (hasSpaceAfterAll) {
                            writer.append(SPACE);
                    }
                }

		// Query
		if (query != null) {
			query.toParsedText(writer, actual);
		}
	}
	
	public boolean isUnion() {
	    return identifier == UNION;
	}
        
        public boolean isIntersect() {
            return identifier == INTERSECT;
        }
        
        public boolean isExcept() {
            return identifier == EXCEPT;
        }
}