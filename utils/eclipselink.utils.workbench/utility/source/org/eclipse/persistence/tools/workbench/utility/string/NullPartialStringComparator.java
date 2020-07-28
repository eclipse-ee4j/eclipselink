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
package org.eclipse.persistence.tools.workbench.utility.string;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This implementation of PartialStringComparator will always return zero when
 * comparing two strings.
 */
public final class NullPartialStringComparator
    implements PartialStringComparator
{

    // singleton
    private static NullPartialStringComparator INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized PartialStringComparator instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullPartialStringComparator();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullPartialStringComparator() {
        super();
    }

    /**
     * @see PartialStringComparator#compare(String, String)
     */
    public double compare(String s1, String s2) {
        return 0;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
