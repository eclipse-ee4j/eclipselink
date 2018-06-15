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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Implement some of the methods in Association that can
 * be defined in terms of the other methods.
 */
public abstract class AbstractAssociation
    implements Association
{

    /**
     * Default constructor.
     */
    protected AbstractAssociation() {
        super();
    }

    /**
     * @see Association#equals(Object)
     */
    public synchronized boolean equals(Object o) {
        if ( ! (o instanceof Association)) {
            return false;
        }
        Association other = (Association) o;
        return (this.getKey() == null ?
                    other.getKey() == null : this.getKey().equals(other.getKey()))
            && (this.getValue() == null ?
                    other.getValue() == null : this.getValue().equals(other.getValue()));
    }

    /**
     * @see Association#hashCode()
     */
    public synchronized int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode())
            ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }

    /**
     * @see Object#toString()
     */
    public synchronized String toString() {
        return this.getKey() + " => " + this.getValue();
    }

}
