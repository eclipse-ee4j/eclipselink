/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.Transformer;


/**
 * A <code>null</code> implementation of a <code>Transformer</code>. The
 * singleton instance can be typed cast properly when using generics.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class NullTransformer implements Transformer
{
    /**
     * The singleton instance of this <code>NullTransformer</code>.
     */
    private static Transformer INSTANCE;

    /**
     * Creates a new <code>NullTransformer</code>.
     */
    private NullTransformer()
    {
        super();
    }

    /*
     * (non-Javadoc)
     */
    public Object transform(Object object)
    {
        return object;
    }

    /**
     * Returns the singleton instance of this <code>NullTransformer</code>.
     *
     * @return The singleton instance of this <code>NullTransformer</code>
     */
    @SuppressWarnings("unchecked")
    public static synchronized Transformer instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new NullTransformer();
        }

        return (Transformer) INSTANCE;
    }
}
