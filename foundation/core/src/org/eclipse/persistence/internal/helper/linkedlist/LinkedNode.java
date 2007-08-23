/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper.linkedlist;


/**
 * INTERNAL:
 * A custom implementation of a linked list node for use in the ExposedNodeLinkedList.
 * @author Gordon Yorke
 * @since 10.0.3
 * @see ExposedNodeLinkedList
 */
public class LinkedNode {
    Object contents;
    LinkedNode next;
    LinkedNode previous;

    LinkedNode(Object object, LinkedNode next, LinkedNode previous) {
        this.contents = object;
        this.next = next;
        this.previous = previous;
    }

    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }
}