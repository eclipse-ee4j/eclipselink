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
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * The external representation of the managed type that is annotated with
 * {@link javax.persistence.Entity &#64;Entity}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IEntity extends IManagedType {

	/**
	 * Returns the name of this entity.
	 *
	 * @return The non-default name or the short class name of this entity
	 */
	String getName();

	/**
	 * Returns the external form of the given named query;
	 *
	 * @param queryName The name of the JPQL query to retrieve
	 * @return The {@link IQuery} representing the JPQL query named with the given name; or
	 * <code>null</code> if none could be found
	 */
	IQuery getNamedQuery(String queryName);
}