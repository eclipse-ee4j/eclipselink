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

import org.eclipse.persistence.jpa.jpql.DefaultLiteralVisitor;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;

/**
 * The default implementation of {@link BasicStateObjectBuilder}, which provides support for
 * creating a {@link StateObject} representation of any
 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultStateObjectBuilder extends BasicStateObjectBuilder {

	/**
	 * Creates a new <code>DefaultStateObjectBuilder</code>.
	 */
	public DefaultStateObjectBuilder() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LiteralVisitor buildLiteralVisitor() {
		return new DefaultLiteralVisitor();
	}
}