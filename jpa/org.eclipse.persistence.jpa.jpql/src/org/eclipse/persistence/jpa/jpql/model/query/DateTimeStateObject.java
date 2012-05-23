/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link DateTimeStateObject} represents a date or time. It supports the following identifiers:
 * <p>
 * <b>CURRENT_DATE</b>: This function returns the value of current date on the database server.
 * <p>
 * <b>CURRENT_TIME</b>: This function returns the value of current time on the database server.
 * <p>
 * <b>CURRENT_TIMESTAMP</b>: This function returns the value of current timestamp on the database
 * server.
 * <p>
 * <div nowrap><b>BNF:</b> <code>functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP</code>
 * <p>
 * The JDBC escape syntax may be used for the specification of date, time, and timestamp literals.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= {d 'yyyy-mm-dd'} | {t 'hh:mm:ss'} | {ts 'yyyy-mm-dd hh:mm:ss.f...'}</code><p>
 *
 * @see DateTime
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("unused") // unused used for the import statement: see bug 330740
public class DateTimeStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>DateTimeStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DateTimeStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>DateTimeStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param date Either <code><b>DATE</b></code>, <code><b>TIME</b></code>, <code><b>TIMESTAMP</b></code>
	 * or a JDBC date
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DateTimeStateObject(StateObject parent, String date) {
		super(parent, date);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTime getExpression() {
		return (DateTime) super.getExpression();
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_DATE CURRENT_DATE}.
	 *
	 * @return <code>true</code> if this {@link org.eclipse.persistence.jpa.jpql.parser.Expression
	 * Expression} represents {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_DATE
	 * CURRENT_DATE}; <code>false</code> otherwise
	 */
	public boolean isCurrentDate() {
		return CURRENT_DATE.equalsIgnoreCase(getText());
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_TIME CURRENT_TIME}.
	 *
	 * @return <code>true</code> if this {@link org.eclipse.persistence.jpa.jpql.parser.Expression
	 * Expression} represents {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_TIME
	 * CURRENT_TIME}; <code>false</code> otherwise
	 */
	public boolean isCurrentTime() {
		return CURRENT_TIME.equalsIgnoreCase(getText());
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_TIMESTAMP CURRENT_TIMESTAMP}.
	 *
	 * @return <code>true</code> if this {@link org.eclipse.persistence.jpa.jpql.parser.Expression
	 * Expression} represents {@link org.eclipse.persistence.jpa.jpql.parser.Expression#CURRENT_TIMESTAMP
	 * CURRENT_TIMESTAMP}; <code>false</code> otherwise
	 */
	public boolean isCurrentTimestamp() {
		return CURRENT_TIMESTAMP.equalsIgnoreCase(getText());
	}

	/**
	 * Determines whether this {@link DateTime} represents the JDBC escape syntax for date, time,
	 * timestamp formats.
	 *
	 * @return <code>true</code> if this {@link org.eclipse.persistence.jpa.jpql.parser.Expression
	 * Expression} represents a JDBC escape syntax; <code>false</code> otherwise
	 */
	public boolean isJDBCDate() {
		return hasText() ? (getText().charAt(0) == LEFT_CURLY_BRACKET) : false;
	}

	/**
	 * Keeps a reference of the {@link DateTime parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link DateTime parsed object} representing a date literal
	 */
	public void setExpression(DateTime expression) {
		super.setExpression(expression);
	}
}