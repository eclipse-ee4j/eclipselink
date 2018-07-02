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

/**
 * This simple container class simply puts a bit of semantics
 * around a pair of numbers.
 */
public class Range
    implements Cloneable, Serializable
{
    /** The starting index of the range. */
    public final int start;

    /** The ending index of the range. */
    public final int end;

    /**
     * The size can be negative if the ending index
     * is less than the starting index.
     */
    public final int size;

    private static final long serialVersionUID = 1L;


    /**
     * Construct with the specified start and end,
     * both of which are immutable.
     */
    public Range(int start, int end) {
        super();
        this.start = start;
        this.end = end;
        this.size = end - start + 1;
    }

    /**
     * Return whether the range includes the specified
     * index.
     */
    public boolean includes(int index) {
        return (this.start <= index) && (index <= this.end);
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ( ! (o instanceof Range)) {
            return false;
        }
        Range otherRange = (Range) o;
        return (this.start == otherRange.start)
            && (this.end == otherRange.end);
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return this.start ^ this.end;
    }

    /**
     * @see Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "[" + this.start + ", " + this.end + ']';
    }

}
