/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.node;

/**
 * Define an interface describing the problems associated with a node.
 */
public interface Problem {

    /**
     * Return the node most closely associated with the problem.
     */
    Node getSource();

    /**
     * Return a key that can be used to uniquely identify the problem's message.
     */
    String getMessageKey();

    /**
     * Return the arguments associate with the problem's message.
     */
    Object[] getMessageArguments();

    /**
     * Return whether the problem is equal to the specified object.
     * It is equal if the specified object is a implementation of the
     * Problem interface and its source, message key, and message
     * arguments are all equal to this problem's.
     */
    boolean equals(Object o);

    /**
     * Return the problem's hash code, which should calculated as an
     * XOR of the source's hash code and the message key's hash code.
     */
    int hashCode();

}
