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
package org.eclipse.persistence.tools.workbench.utility.diff;

/**
 * An interface implemented by objects that can be "diffed"
 * directly, as opposed to using a differentiator.
 */
public interface Diffable {

    /**
     * Return the "diff" between this object and the specified object.
     */
    Diff diff(Object o);

    /**
     * Return the "key diff" between this object and the specified object.
     */
    Diff keyDiff(Object o);

}
