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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IMappingType;

/**
 * This {@link PathExpressionResolver} is responsible to create the property chain of {@link
 * Expression expressions} for a collection-valued path expression or state field path expression.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractPathPathExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * The external form of the mapping representing this path.
	 */
	private final IMapping mapping;

	/**
	 * A single path that is part of a state field path expression or a collection-valued path
	 * expression.
	 */
	private final String path;

	/**
	 * Creates a new <code>AbstractPathPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the givne path
	 * @param mapping The external form of the mapping representing this path
	 * @param path A single path that is part of a state field path expression or a collection-valued
	 * path expression
	 */
	AbstractPathPathExpressionResolver(PathExpressionResolver parent, IMapping mapping, String path) {
		super(parent);
		this.path    = path;
		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract AbstractPathPathExpressionResolver clone();

	/**
	 * {@inheritDoc}
	 */
	public final Expression getExpression() {
		return getExpression(path);
	}

	/**
	 * Returns the external form of the mapping representing this path.
	 *
	 * @return The external form of the mapping represented by this path, which can be <code>null</code>
	 */
	final IMapping getMapping() {
		return mapping;
	}

	/**
	 * Returns the type of the mapping.
	 *
	 * @return The {@link IMappingType} of the {@link IMapping mapping} represented by this path or
	 * {@link IMappingType#TRANSIENT} if there is no {@link IMapping mapping} associated with this
	 * {@link PathExpressionResolver}
	 */
	final IMappingType getMappingType() {
		return MappingTypeHelper.mappingType(mapping);
	}

	/**
	 * Returns the single path handled by this {@link PathExpressionResolver}.
	 *
	 * @return The single path that is part of a path expression
	 */
	final String getPath() {
		return path;
	}
}