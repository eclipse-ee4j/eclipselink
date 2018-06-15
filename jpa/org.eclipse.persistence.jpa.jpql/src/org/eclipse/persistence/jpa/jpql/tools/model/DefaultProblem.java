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

import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * The default implementation of {@link Problem}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultProblem implements Problem {

    /**
     * A list of arguments that can be used to complete the message or an empty list if no additional
     * information is necessary.
     */
    private String[] arguments;

    /**
     * The key used to retrieve the localized message describing the problem found with the current
     * state of this {@link StateObject}.
     */
    private String messageKey;

    /**
     * The location where the problem was found.
     */
    private StateObject stateObject;

    /**
     * Creates a new <code>DefaultProblem</code>.
     *
     * @param stateObject The {@link StateObject} where the problem was found
     * @param messageKey The key used to retrieve the localized message describing the problem found
     * with the current state of this {@link StateObject}
     * @param arguments A list of arguments that can be used to complete the message or an empty list
     * if no additional information is necessary
     */
    public DefaultProblem(StateObject stateObject, String messageKey, String[] arguments) {
        super();
        this.stateObject = stateObject;
        this.messageKey  = messageKey;
        this.arguments   = arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMessageArguments() {
        return arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateObject getStateObject() {
        return stateObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StateObject=");
        sb.append(stateObject);
        sb.append(", messageKey=");
        sb.append(messageKey);
        return sb.toString();
    }
}
