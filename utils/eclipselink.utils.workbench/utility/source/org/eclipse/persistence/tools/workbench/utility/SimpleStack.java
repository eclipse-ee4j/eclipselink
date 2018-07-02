/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Straightforward implementation of the Stack interface.
 */
public class SimpleStack
    implements Stack, Cloneable, Serializable
{
    private LinkedList elements;

    private static final long serialVersionUID = 1L;


    // ********** constructors **********

    /**
     * Construct an empty stack.
     */
    public SimpleStack() {
        super();
        this.elements = new LinkedList();
    }


    // ********** Stack implementation **********

    /**
     * @see Stack#push(Object)
     */
    public void push(Object o) {
        this.elements.addLast(o);
    }

    /**
     * @see Stack#pop()
     */
    public Object pop() {
        try {
            return this.elements.removeLast();
        } catch (NoSuchElementException ex) {
            throw new EmptyStackException();
        }
    }

    /**
     * @see Stack#peek()
     */
    public Object peek() {
        try {
            return this.elements.getLast();
        } catch (NoSuchElementException ex) {
            throw new EmptyStackException();
        }
    }

    /**
     * @see Stack#isEmpty()
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }


    // ********** Cloneable implementation **********

    /**
     * @see Object#clone()
     */
    public Object clone() {
        SimpleStack clone;
        try {
            clone = (SimpleStack) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
        clone.elements = (LinkedList) this.elements.clone();
        return clone;
    }

}
