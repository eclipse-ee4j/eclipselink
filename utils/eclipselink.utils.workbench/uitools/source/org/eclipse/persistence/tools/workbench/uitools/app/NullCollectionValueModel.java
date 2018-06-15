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

import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;

/**
 * A read-only collection value model for when you
 * don't need to support a collection. In particular, this
 * is useful for the leaf nodes of a tree that never have
 * children.
 */
public final class NullCollectionValueModel
    extends AbstractReadOnlyCollectionValueModel
{
    private static final long serialVersionUID = 1L;

    // singleton
    private static NullCollectionValueModel INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized CollectionValueModel instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullCollectionValueModel();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullCollectionValueModel() {
        super();
    }


    // ********** CollectionValueModel implementation **********

    /**
     * @see CollectionValueModel#size()
     */
    public int size() {
        return 0;
    }

    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return NullIterator.instance();
    }


    // ********** Object overrides **********

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "NullCollectionValueModel";
    }

    /**
     * Serializable singleton support
     */
    private Object readResolve() {
        return instance();
    }

}
