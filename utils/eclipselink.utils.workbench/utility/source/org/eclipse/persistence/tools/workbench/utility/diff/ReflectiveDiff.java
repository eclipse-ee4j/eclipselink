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
 * This diff records the differences between two objects compared reflectively.
 */
public class ReflectiveDiff extends CompositeDiff {
    private final Class javaClass;

    public ReflectiveDiff(Class javaClass, Object object1, Object object2, ReflectiveFieldDiff[] diffs, Differentiator differentiator) {
        super(object1, object2, diffs, differentiator);
        this.javaClass = javaClass;
    }

    /**
     * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
     */
    public void appendDescription(IndentingPrintWriter pw) {
        if (this.different()) {
            pw.print("Reflective comparison (");
            pw.print(this.javaClass.getName());
            pw.print(") - ");
            this.appendHeader(pw);
        }
        super.appendDescription(pw);
    }

}
