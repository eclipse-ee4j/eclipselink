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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;


/**
 * Define the node methods needed by a problems view.
 */
public interface ApplicationProblemContainer {

    /**
     * Return the node's "exclusive" problems.
     */
    ListIterator applicationProblems();
        String APPLICATION_PROBLEMS_LIST = "applicationProblems";

    int applicationProblemsSize();

    /**
     * Add the node's "exclusive" problems to the specified list.
     */
    void addApplicationProblemsTo(List list);

    /**
     * Return the node's branch problems.
     */
    ListIterator branchApplicationProblems();
        String BRANCH_APPLICATION_PROBLEMS_LIST = "branchApplicationProblems";

    int branchApplicationProblemsSize();

    /**
     * Add the node's branch problems to the specified list.
     */
    void addBranchApplicationProblemsTo(List list);

    /**
     * Return whether the node or one of its descendants contains
     * the application problem corresponding to the specified model problem.
     */
    boolean containsBranchApplicationProblemFor(Problem problem);

    /**
     * Print the node's branch problems on the specified stream.
     */
    void printBranchApplicationProblemsOn(IndentingPrintWriter writer);

}
