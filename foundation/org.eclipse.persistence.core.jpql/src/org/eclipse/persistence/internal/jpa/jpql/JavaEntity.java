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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * The concrete implementation of {@link IEntity} that is wrapping the runtime representation of a
 * JPA entity.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class JavaEntity extends JavaManagedType
                       implements IEntity {

	/**
	 * Creates a new <code>JavaEntity</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param descriptor The EclipseLink descriptor wrapping the actual managed type
	 */
	JavaEntity(JavaManagedTypeProvider provider, ClassDescriptor descriptor) {
		super(provider, descriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IManagedTypeVisitor visitor) {
		visitor.visit(this);
	}

	private IQuery buildQuery(DatabaseQuery query) {
		return new JavaQuery(getProvider(), query.getEJBQLString());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		String name = getDescriptor().getAlias();
		if (ExpressionTools.stringIsEmpty(name)) {
			name = getDescriptor().getJavaClass().getSimpleName();
		}
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getNamedQuery(String queryName) {
		DatabaseQuery query = getProvider().getSession().getQuery(queryName);
		return (query == null) ? null : buildQuery(query);
	}
}