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

import org.eclipse.persistence.jpa.jpql.model.query.EclipseLinkStateObjectVisitor;

/**
 * The default implementation of a {@link IJPQLQueryFormatter} that adds support for formatting
 * EclipseLink specific JPQL identifiers. It supports creating a string representation of a JPQL
 * query written for JPA 1.0 and 2.1 and with EclipseLink 1.x and 2.x.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkJPQLQueryFormatter extends AbstractJPQLQueryFormatter
                                           implements EclipseLinkStateObjectVisitor {

	/**
	 * Creates a new <code>EclipseLinkJPQLQueryFormatter</code>.
	 *
	 * @param style Determines how the JPQL identifiers are written out
	 * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
	 */
	public EclipseLinkJPQLQueryFormatter(IdentifierStyle style) {
		super(style);
	}
}