/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.spi;

/**
 * The interface is used to visit a {@link IManagedType}. It follows the Visitor pattern.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public interface IManagedTypeVisitor
{
	/**
	 * Visits the given {@link IEmbeddable} object.
	 *
	 * @param embeddable The embeddable object to visit
	 */
	void visit(IEmbeddable embeddable);

	/**
	 * Visits the given {@link IEntity} object.
	 *
	 * @param entity The entity object to visit
	 */
	void visit(IEntity entity);

	/**
	 * Visits the given {@link IMappedSuperclass} object.
	 *
	 * @param mappedSuperclass The mapped superclass object to visit
	 */
	void visit(IMappedSuperclass mappedSuperclass);
}