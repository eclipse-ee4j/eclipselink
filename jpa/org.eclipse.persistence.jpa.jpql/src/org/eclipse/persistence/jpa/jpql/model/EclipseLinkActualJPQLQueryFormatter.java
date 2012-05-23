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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.EclipseLinkStateObjectVisitor;

/**
 * This {@link IJPQLQueryFormatter} is used to generate a string representation of a {@link
 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} based on how it was parsed,
 * which means this formatter can only be used when the {@link org.eclipse.persistence.jpa.jpql.
 * model.query.StateObject StateObject} was created by parsing a JPQL query because it needs to
 * retrieve parsing information from the corresponding {@link org.eclipse.persistence.jpa.jpql.
 * parser.Expression Expression}.
 * <p>
 * This version adds support for EclipseLink extension.
 * <p>
 * It is possible to partially match the JPQL query that was parsed, the value of the <em>exactMatch</em>
 * will determine whether the string representation of any given {@link org.eclipse.persistence.jpa.
 * jpql.model.query.StateObject StateObject} should reflect the exact string that was parsed.
 * <code>true</code> will use every bit of information contained in the corresponding {@link
 * org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to perfectly match what was parsed;
 * <code>false</code> will only match the case sensitivity of the JPQL identifiers.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkActualJPQLQueryFormatter extends AbstractActualJPQLQueryFormatter
                                                 implements EclipseLinkStateObjectVisitor {

	/**
	 * Creates a new <code>EclipseLinkActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link
	 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} should reflect the exact
	 * string that was parsed: <code>true</code> will use every bit of information contained in the
	 * corresponding {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * perfectly match what was parsed; <code>false</code> will only match the case sensitivity of
	 * the JPQL identifiers
	 */
	public EclipseLinkActualJPQLQueryFormatter(boolean exactMatch) {
		super(exactMatch);
	}

	/**
	 * Creates a new <code>EclipseLinkActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link
	 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} should reflect the exact
	 * string that was parsed: <code>true</code> will use every bit of information contained in the
	 * corresponding {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * perfectly match what was parsed (case of JPQL identifiers and the presence of whitespace);
	 * <code>false</code> will only match the case sensitivity of the JPQL identifiers
	 * @param style Determines how the JPQL identifiers are written out, which is used if the {@link
	 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} was modified after its
	 * creation
	 * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
	 */
	public EclipseLinkActualJPQLQueryFormatter(boolean exactMatch, IdentifierStyle style) {
		super(exactMatch, style);
	}
}