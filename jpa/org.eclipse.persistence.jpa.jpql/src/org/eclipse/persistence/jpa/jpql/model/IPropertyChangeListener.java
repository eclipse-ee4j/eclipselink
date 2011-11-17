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

/**
 * A <code>IPropertyChangeListener</code> can be registered with a {@link
 * org.eclipse.persistence.jpa.jpql.model.query.StateObject StateObject} in order to be notified
 * when the value of a property changes.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IPropertyChangeListener<T> {

	/**
	 * Notifies this listener that a certain property has changed.
	 *
	 * @param e The {@link IPropertyChangeEvent} object containing the information of the change
	 */
	void propertyChanged(IPropertyChangeEvent<T> e);
}