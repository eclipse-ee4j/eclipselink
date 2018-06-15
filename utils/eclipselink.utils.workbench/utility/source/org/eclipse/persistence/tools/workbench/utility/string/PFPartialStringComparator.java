/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.utility.string;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This implementation of the PartialStringComparator interface uses the
 * the weighted sum of percentage of matched characters from each string.
 *
 * @author lddavis
 *
 */
public class PFPartialStringComparator implements PartialStringComparator {

    // singleton
    private static PFPartialStringComparator INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized PartialStringComparator instance() {
        if (INSTANCE == null) {
            INSTANCE = new PFPartialStringComparator();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private PFPartialStringComparator() {
        super();
    }

    public double compare(String s1, String s2) {
        return StringTools.calculateHighestMatchWeight(s1, s2);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
