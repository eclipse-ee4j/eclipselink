/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence API 2.0 Public Draft
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *     
 * IMPORTANT: The Criteria API is defined as per the public draft specification
 * but is not implemented in the EclipseLink's early access.
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence;

import java.util.Calendar;
import java.util.Date;

/**
 * Interface for the construction of case expressions
 * 
 * @since Java Persistence API 2.0
 */
public interface CaseExpression {
	/**
	 * Add a when predicate clause to a general case expression. The when
	 * predicate must be followed by the corresponding then case expression that
	 * specifies the result of the specific case. Clauses are evaluated in the
	 * order added.
	 * 
	 * @param pred
	 *            - corresponds to the evaluation condition for the specific
	 *            case
	 * @return CaseExpression corresponding to the case with the added when
	 *         clause
	 */
	CaseExpression when(Predicate pred);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Expression when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Number when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(String when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Date when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Calendar when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Class when);

	/**
	 * Add a when clause to a simple case expression. The when case expression
	 * must be followed by the corresponding then case expression that specifies
	 * the result of the specific case. Clauses are evaluated in the order added
	 * 
	 * @param when
	 *            - corresponds to the value against which the case operand of
	 *            the simple case is tested
	 * @return CaseExpression corresponding to the case with the added clause
	 */
	CaseExpression when(Enum<?> when);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Expression then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Number then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(String then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Date then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Calendar then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Class then);

	/**
	 * Add a then clause to a general or simple case expression. The then clause
	 * specifies the result corresponding to the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * 
	 * @param then
	 *            - corresponds to the result of the case expression if the when
	 *            is satisfied
	 * @return CaseExpression corresponding to the case with the added then
	 *         clause
	 */
	CaseExpression then(Enum<?> then);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Expression arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(String arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Number arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Date arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Calendar arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Class arg);

	/**
	 * Add else to a case expression. A case expression must have an else
	 * clause.
	 * 
	 * @param arg
	 *            - corresponds to the result of the case expression if the when
	 *            condition is not satisfied
	 * @return Expression corresponding to the case expression with the added
	 *         clause
	 */
	Expression elseCase(Enum<?> arg);
}
