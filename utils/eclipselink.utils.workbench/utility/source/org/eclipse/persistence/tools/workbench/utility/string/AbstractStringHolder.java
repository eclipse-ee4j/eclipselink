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

import java.io.Serializable;

/**
 * Implement some behavior with calls to #getString().
 */
public abstract class AbstractStringHolder
    implements StringHolder, Serializable, Cloneable, Comparable
{

    protected AbstractStringHolder() {
        super();
    }

    public abstract String getString();

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    public boolean equals(Object o) {
        if ( ! (o instanceof StringHolder)) {
            return false;
        }
        StringHolder other = (StringHolder) o;
        String s = this.getString();
        return (s == null) ?
                (other.getString() == null)
            :
                s.equals(other.getString());
    }

    public int hashCode() {
        String s = this.getString();
        return (s == null) ? 0 : s.hashCode();
    }

    public int compareTo(Object o) {
        return this.compareTo((StringHolder) o);
    }

    public int compareTo(StringHolder sh) {
        return this.getString().compareTo(sh.getString());
    }

    public String toString() {
        return StringTools.buildToStringFor(this, this.getString());
    }

}
