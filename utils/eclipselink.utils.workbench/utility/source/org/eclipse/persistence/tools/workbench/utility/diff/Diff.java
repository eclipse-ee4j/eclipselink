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

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * A Diff describes the result of comparing two objects with a Differentiator.
 *
 * @see Differentiator
 */
public interface Diff {

    /**
     * Return the first object.
     */
    Object getObject1();

    /**
     * Return the second object.
     */
    Object getObject2();

    /**
     * Return whether the two objects are "identical",
     * as specified by the differentiator that generated
     * the diff.
     */
    boolean identical();

    /**
     * Return whether the two objects are "different",
     * as specified by the differentiator that generated
     * the diff.
     */
    boolean different();

    /**
     * Return the differentiator that compared the two objects
     * and created the diff.
     */
    Differentiator getDifferentiator();

    /**
     * Return a description of the difference. Return an empty string if
     * there is no difference between the objects.
     */
    String getDescription();

    /**
     * Append a description of the difference to the specified
     * stream. Do not append anything to the stream if
     * there is no difference between the objects.
     */
    void appendDescription(IndentingPrintWriter pw);

    /**
     * Convenience constants.
     */
    String NO_DIFFERENCE_DESCRIPTION = "<no difference>";

}
