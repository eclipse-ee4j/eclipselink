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

import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * The default implementation of {@link IPropertyChangeEvent} where the generics is the type of the
 * old and new values.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class PropertyChangeEvent<T> implements IPropertyChangeEvent<T> {

	/**
	 * The new value of the property that changed.
	 */
	private T newValue;

	/**
	 * The old value of the property that changed.
	 */
	private T oldValue;

	/**
	 * The name of the property associated with the property change.
	 */
	private String propertyName;

	/**
	 * The source where the modification occurred and that fired the event.
	 */
	private StateObject source;

	/**
	 * Creates a new <code>PropertyChangeEvent</code>.
	 *
	 * @param source The source where the modification occurred and that fired the event
	 * @param propertyName The name of the property associated with the property change
	 * @param oldValue The old value of the property that changed
	 * @param newValue The new value of the property that changed
	 */
	public PropertyChangeEvent(StateObject source, String propertyName, T oldValue, T newValue) {
		super();
		this.source       = source;
		this.propertyName = propertyName;
		this.oldValue     = oldValue;
		this.newValue     = newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getNewValue() {
		return newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getOldValue() {
		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <S extends StateObject> S getSource() {
		return (S) source;
	}
}