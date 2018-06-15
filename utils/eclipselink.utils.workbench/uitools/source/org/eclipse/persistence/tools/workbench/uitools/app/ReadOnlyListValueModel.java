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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.List;

/**
 * Implementation of ListValueModel that can be used for
 * returning a list iterator on a static list, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 */
public class ReadOnlyListValueModel
    extends AbstractReadOnlyListValueModel
{
    /** The value. */
    protected List value;

    private static final long serialVersionUID = 1L;


    /**
     * Construct a ListValueModel for the specified value.
     */
    public ReadOnlyListValueModel(List value) {
        super();
        if (value == null) {
            throw new NullPointerException();
        }
        this.value = value;
    }


    // ********** ListValueModel implementation **********

    /**
     * @see ListValueModel#size()
     */
    public int size() {
        return this.value.size();
    }


    // ********** ValueModel implementation **********

    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return this.value.listIterator();
    }

}
