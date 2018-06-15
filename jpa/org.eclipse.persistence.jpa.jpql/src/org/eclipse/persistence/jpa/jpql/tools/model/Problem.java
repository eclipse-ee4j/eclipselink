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
 * A problem describes an issue found in a JPQL query because it is either grammatically or
 * semantically incorrect.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface Problem {

    /**
     * Returns the arguments associate with the problem's message.
     *
     * @return A non-<code>null</code> list of arguments that can be used to format the localized
     * message
     */
    String[] getMessageArguments();

    /**
     * Returns the key used to retrieve the localized message describing the problem found in the
     * {@link StateObject}.
     *
     * @return The key used to retrieve the localized message
     */
    String getMessageKey();

    /**
     * Returns the {@link StateObject} where the problem was found.
     *
     * @return The {@link StateObject} where the problem was found
     */
    StateObject getStateObject();
}
