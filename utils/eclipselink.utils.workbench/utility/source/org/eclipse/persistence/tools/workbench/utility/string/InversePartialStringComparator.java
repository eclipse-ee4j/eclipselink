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
package org.eclipse.persistence.tools.workbench.utility.string;


/**
 * This implementation of PartialStringComparator will "invert" the results
 * of the nested comparator.
 */
public class InversePartialStringComparator
    implements PartialStringComparator
{
    private final PartialStringComparator partialStringComparator;

    public InversePartialStringComparator(PartialStringComparator partialStringComparator) {
        super();
        this.partialStringComparator = partialStringComparator;
    }

    /**
     * @see PartialStringComparator#compare(String, String)
     */
    public double compare(String s1, String s2) {
        return 1.0 - this.partialStringComparator.compare(s1, s2);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return StringTools.buildToStringFor(this, this.partialStringComparator);
    }

}
