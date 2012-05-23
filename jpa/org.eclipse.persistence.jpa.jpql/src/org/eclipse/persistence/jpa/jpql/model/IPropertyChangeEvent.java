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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * This is used in conjunction with {@link IPropertyChangeListener}. It contains the information
 * regarding the property change.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IPropertyChangeEvent<T> {

	/**
	 * Returns the new value of the property that changed.
	 *
	 * @return The property's new value
	 */
	T getNewValue();

	/**
	 * Returns the old value of the property that changed.
	 *
	 * @return The property's old value
	 */
	T getOldValue();

	/**
	 * Returns the name of the property that changed.
	 *
	 * @return A unique identifier of the property that changed
	 */
	String getPropertyName();

	/**
	 * Returns the source where the modification occurred and that fired the event.
	 *
	 * @return The source of the event
	 */
	<S extends StateObject> S getSource();
}