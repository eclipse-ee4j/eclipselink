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


/**
 * This implementation of PartialStringComparator will convert the strings to
 * upper case before using the nested comparator to compare them.
 */
public class UpperCasePartialStringComparator
    implements PartialStringComparator
{
    private final PartialStringComparator partialStringComparator;

    public UpperCasePartialStringComparator(PartialStringComparator partialStringComparator) {
        super();
        this.partialStringComparator = partialStringComparator;
    }

    /**
     * @see PartialStringComparator#compare(String, String)
     */
    public double compare(String s1, String s2) {
        return this.partialStringComparator.compare(s1.toUpperCase(), s2.toUpperCase());
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return StringTools.buildToStringFor(this, this.partialStringComparator);
    }

}
