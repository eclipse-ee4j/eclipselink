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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;


/**
 * An "ExternalClassLoadFailureListener" that simply gathers up the
 * failures and makes them available for later investigation.
 */

public class ExternalClassLoadFailureContainer
    implements ExternalClassLoadFailureListener
{
    /** failures, keyed by class name */
    private Map failures = new HashMap();

    public void externalClassLoadFailure(ExternalClassLoadFailureEvent e) {
        this.failures.put(e.getClassName(), e.getCause());
    }

    /** return the names of the classes that failed to load */
    public Iterator failureClassNames() {
        return new ReadOnlyIterator(this.failures.keySet());
    }

    /** return the failures */
    public Iterator failures() {
        return new ReadOnlyIterator(this.failures.values());
    }

    /** return the load failure cause for the specified class */
    public Throwable failureForClassNamed(String className) {
        return (Throwable) this.failures.get(className);
    }

    /** return whether the specified class failed to load */
    public boolean containsFailureForClassNamed(String className) {
        return this.failures.get(className) != null;
    }

    /** return whether any failures occurred */
    public boolean isEmpty() {
        return this.failures.isEmpty();
    }

    /** return whether any failures occurred */
    public boolean containsFailures() {
        return ! this.failures.isEmpty();
    }
}
