/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import java.util.Stack;

/**
 * Unmarshaller context.
 *
 * Contains metadata about currently processed item.
 *
 * @author Roman Grigoriadi
 */
class Context {

    /**
     * Items are pushed to this stack as JSON parser encounters new object start.
     */
    private final Stack<CurrentItem<?>> workStack;

    private CurrentItem<?> CurrentItem;

    public Context() {
        workStack = new Stack<>();
    }

    /**
     * Push current item to stack, till new item is finished.
     * @param newItem new item to work on
     */
    void pushCurrent(CurrentItem<?> newItem) {
        workStack.push(CurrentItem);
        CurrentItem = newItem;
    }

    CurrentItem<?> popCurrent() {
        if (workStack.size() == 0) {
            return null;
        }
        CurrentItem = workStack.pop();
        return CurrentItem;
    }

    boolean stackEmpty() {
        return workStack.isEmpty();
    }


    CurrentItem<?> getCurrentItem() {
        return CurrentItem;
    }

    void setCurrentItem(CurrentItem CurrentItem) {
        this.CurrentItem = CurrentItem;
    }

}
