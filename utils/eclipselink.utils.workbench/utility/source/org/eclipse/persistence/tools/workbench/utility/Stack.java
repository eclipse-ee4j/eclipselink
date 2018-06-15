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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Interface defining the classic stack behavior,
 * without the backdoors allowed by java.util.Stack.
 */
public interface Stack {

    /**
     * "Push" the specified item on to the top of the stack.
     */
    void push(Object o);

    /**
     * "Pop" an item from the top of the stack.
     */
    Object pop();

    /**
     * Return the item on the top of the stack
     * without removing it from the stack.
     */
    Object peek();

    /**
     * Return whether the stack is empty.
     */
    boolean isEmpty();

}
