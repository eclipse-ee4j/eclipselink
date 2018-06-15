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

import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * Reasonably straightforward implementation of the Diff interface
 * for two objects that are different.
 */
public class SimpleDiff implements Diff {
    /** the compared objects */
    private final Object object1;
    private final Object object2;

    /** printed at the top of the description */
    private final String descriptionTitle;

    /** original differentiator that performed the comparison */
    private final Differentiator differentiator;


    public SimpleDiff(Object object1, Object object2, String descriptionTitle, Differentiator differentiator) {
        super();
        this.object1 = object1;
        this.object2 = object2;
        this.descriptionTitle = descriptionTitle;
        this.differentiator = differentiator;
    }


    /**
     * @see Diff#getObject1()
     */
    public Object getObject1() {
        return this.object1;
    }

    /**
     * @see Diff#getObject2()
     */
    public Object getObject2() {
        return this.object2;
    }

    /**
     * @see Diff#identical()
     */
    public boolean identical() {
        return false;
    }

    /**
     * @see Diff#different()
     */
    public boolean different() {
        return true;
    }

    /**
     * @see Diff#getDifferentiator()
     */
    public Differentiator getDifferentiator() {
        return this.differentiator;
    }

    /**
     * @see Diff#getDescription()
     */
    public String getDescription() {
        StringWriter sw = new StringWriter();
        IndentingPrintWriter pw = new IndentingPrintWriter(sw);
        this.appendDescription(pw);
        return sw.toString();
    }

    /**
     * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
     */
    public void appendDescription(IndentingPrintWriter pw) {
        pw.print(this.descriptionTitle);
        pw.println(':');
        pw.print("object 1: ");
        pw.println(this.object1);
        pw.print("object 2: ");
        pw.println(this.object2);
    }

    public String getDescriptionTitle() {
        return this.descriptionTitle;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return this.getDescription();
    }

}
