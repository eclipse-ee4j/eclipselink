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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This diff records the differences between two ordered containers,
 * where matching elements must occupy the same positions in their
 * respective containers.
 */
public class OrderedContainerDiff extends CompositeDiff {
    private final Class javaClass;


    public OrderedContainerDiff(Class javaClass, Object object1, Object object2, Diff[] diffs, Differentiator differentiator) {
        super(object1, object2, diffs, differentiator);
        this.javaClass = javaClass;
    }

    /**
     * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
     */
    public void appendDescription(IndentingPrintWriter pw) {
        if (this.different()) {
            pw.print("The ");
            pw.print(ClassTools.shortNameFor(this.javaClass));
            pw.print("s are different:");
            pw.println();
        }
        super.appendDescription(pw);
    }

}
