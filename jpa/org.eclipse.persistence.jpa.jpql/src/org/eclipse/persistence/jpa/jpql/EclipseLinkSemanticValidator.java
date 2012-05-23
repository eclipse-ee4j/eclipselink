/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid for EclipseLink. The grammar is not validated by
 * this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 * <p>
 * <b>Note:</b> EclipseLink does not validate types, but leaves it to the database. This is because
 * some databases such as Oracle allow different types to different functions and perform implicit
 * type conversion. i.e. <code>CONCAT('test', 2)</code> returns <code>'test2'</code>. Also the
 * <b>FUNC</b> function has an unknown type, so should be allowed with any function.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkSemanticValidator extends AbstractSemanticValidator
                                          implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkSemanticValidator</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @exception NullPointerException The given {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkSemanticValidator(JPQLQueryContext queryContext) {
		super(new GenericSemanticValidatorHelper(queryContext));
	}

	/**
	 * Creates a new <code>EclipseLinkSemanticValidator</code>.
	 *
	 * @param helper The given helper allows this validator to access the JPA artifacts without using
	 * Hermes SPI
	 * @exception NullPointerException The given {@link SemanticValidatorHelper} cannot be <code>null</code>
	 */
	public EclipseLinkSemanticValidator(SemanticValidatorHelper helper) {
		super(helper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LiteralVisitor buildLiteralVisitor() {
		return new EclipseLinkLiteralVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OwningClauseVisitor buildOwningClauseVisitor() {
		return new EclipseLinkGrammarValidator.EclipseLinkOwningClauseVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TopLevelFirstDeclarationVisitor buildTopLevelFirstDeclarationVisitor() {
		return new TopLevelFirstDeclarationVisitor();
	}

	protected boolean isEclipseLink2_4OrLater() {
		return getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PathType selectClausePathExpressionPathType() {
		return PathType.ANY_FIELD_INCLUDING_COLLECTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateRangeVariableDeclarationRootObject(RangeVariableDeclaration expression) {

		Expression rootObject = expression.getRootObject();

		// Special case, the path expression could be a fully qualified class name,
		// make sure to not validate it as collection-valued path expression
		CollectionValuedPathExpression pathExpression = getCollectionValuedPathExpression(rootObject);

		if (pathExpression != null) {
			String path = pathExpression.toActualText();

			// The path expression is not a fully qualified class name
			if (helper.getType(path) == null) {
				pathExpression.accept(this);
			}
		}
		else {
			rootObject.accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CastExpression expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DatabaseType expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExtractExpression expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RegexpExpression expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableExpression expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableVariableDeclaration expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnionClause expression) {
		// Nothing to validate semantically
	}

	protected class TopLevelFirstDeclarationVisitor extends AbstractSemanticValidator.TopLevelFirstDeclarationVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			// Derived path is not allowed, this could although be a fully
			// qualified class name, which was added to EclipseLink 2.4
			valid = isEclipseLink2_4OrLater();

			if (valid) {
				Object type = helper.getType(expression.toActualText());
				valid = helper.isTypeResolvable(type);
			}
		}
	}
}