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
package org.eclipse.persistence.jpa.jpql;

import java.lang.reflect.Method;
import javax.persistence.Entity;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The concrete implementation of {@link IEntity} that is wrapping the runtime representation of a
 * JPA entity.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaEntity extends JavaManagedType
                       implements IEntity {

	/**
	 * Creates a new <code>JavaEntity</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param type The {@link IType} wrapping the Java type
	 */
	JavaEntity(IManagedTypeProvider provider, JavaType type) {
		super(provider, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IManagedTypeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {

		Class<?> type = getType().getType();
		Entity entity = type.getAnnotation(Entity.class);
		String name = null;

		try {
			Method nameMethod = entity.getClass().getMethod("name");
			name = (String) nameMethod.invoke(entity);
		}
		catch (Exception e) {
			// Ignore
		}
		finally {
			if (ExpressionTools.stringIsEmpty(name)) {
				name = type.getSimpleName();
			}
		}

		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getNamedQuery(String queryName) {
		return null;
	}
}