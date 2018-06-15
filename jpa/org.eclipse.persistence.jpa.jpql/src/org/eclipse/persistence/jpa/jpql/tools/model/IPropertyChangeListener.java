/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * A <code>IPropertyChangeListener</code> can be registered with a {@link
 * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} in order to be notified
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
