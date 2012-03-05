/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.EclipseLinkLiteralVisitor;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ColumnExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FuncExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OperatorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SQLExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TreatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.parser.ColumnExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.OperatorExpression;
import org.eclipse.persistence.jpa.jpql.parser.SQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

/**
 * The default implementation of {@link StateObjectBuilder}, which provides support based on the
 * JPQL grammar defined in the Java Persistence functional specification and for the additional
 * support provided by EclipseLink.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkStateObjectBuilder extends BasicStateObjectBuilder
                                           implements EclipseLinkExpressionVisitor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IBuilder<JoinStateObject, AbstractIdentificationVariableDeclarationStateObject> buildJoinBuilder() {
		return new EclipseLinkJoinBuilder();
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
	protected IBuilder<StateObject, SelectClauseStateObject> buildSelectItemBuilder() {
		return new EclipseLinkSelectItemBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {

		FuncExpressionStateObject stateObject = new FuncExpressionStateObject(
			parent,
			expression.getUnquotedFunctionName(),
			buildChildren(expression.getExpression())
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

        /**
         * {@inheritDoc}
         */
        public void visit(ColumnExpression expression) {

                ColumnExpressionStateObject stateObject = new ColumnExpressionStateObject(
                        parent,
                        expression.getUnquotedColumn(),
                        buildChildren(expression.getExpression())
                );

                stateObject.setExpression(expression);
                this.stateObject = stateObject;
        }

        /**
         * {@inheritDoc}
         */
        public void visit(SQLExpression expression) {

                SQLExpressionStateObject stateObject = new SQLExpressionStateObject (
                        parent,
                        expression.getUnquotedSQL(),
                        buildChildren(expression.getExpression())
                );

                stateObject.setExpression(expression);
                this.stateObject = stateObject;
        }

        /**
         * {@inheritDoc}
         */
        public void visit(OperatorExpression expression) {

                OperatorExpressionStateObject stateObject = new OperatorExpressionStateObject (
                        parent,
                        expression.getUnquotedOperator(),
                        buildChildren(expression.getExpression())
                );

                stateObject.setExpression(expression);
                this.stateObject = stateObject;
        }

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		// Nothing to do here
	}

	/**
	 * This builder adds support for the <code><b>TREAT</b></code> expression used in a
	 * <code><b>JOIN</b></code> expression.
	 */
	protected class EclipseLinkJoinBuilder extends JoinBuilder
	                                       implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpression expression) {
			// Nothing to do
		}
		
                /**
                 * {@inheritDoc}
                 */
                public void visit(SQLExpression expression) {
                        // Nothing to do
                }
                
                /**
                 * {@inheritDoc}
                 */
                public void visit(ColumnExpression expression) {
                        // Nothing to do
                }
                
                /**
                 * {@inheritDoc}
                 */
                public void visit(OperatorExpression expression) {
                        // Nothing to do
                }

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpression expression) {

			TreatExpressionStateObject treatStateObject = new TreatExpressionStateObject(
				stateObject,
				expression.hasAs(),
				literal(expression.getEntityType(), LiteralType.ENTITY_TYPE)
			);

			treatStateObject.setExpression(expression);
			stateObject.getJoinAssociationPathStateObject().decorate(treatStateObject);
			expression.getCollectionValuedPathExpression().accept(this);
		}
	}

	/**
	 * This builder adds support for the <code><b>FUNC</b></code> expression used as a select item
	 * expression.
	 */
	protected class EclipseLinkSelectItemBuilder extends SelectItemBuilder
	                                             implements EclipseLinkExpressionVisitor {


		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpression expression) {
			this.stateObject = buildStateObjectImp(expression);
		}
		
                /**
                 * {@inheritDoc}
                 */
                public void visit(SQLExpression expression) {
                        this.stateObject = buildStateObjectImp(expression);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public void visit(ColumnExpression expression) {
                        this.stateObject = buildStateObjectImp(expression);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public void visit(OperatorExpression expression) {
                        this.stateObject = buildStateObjectImp(expression);
                }

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpression expression) {
			// Nothing to do here
		}
	}
}