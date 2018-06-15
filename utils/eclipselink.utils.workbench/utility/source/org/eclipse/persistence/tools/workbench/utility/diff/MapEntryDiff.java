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
import java.io.Writer;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * Combine a map entry's key and value diffs.
 */
public class MapEntryDiff implements Diff {
    /** the compared entries */
    private final Map.Entry entry1;
    private final Map.Entry entry2;

    /** the diffs */
    private final Diff keyDiff;
    private final Diff valueDiff;

    /** the differentiator */
    private final Differentiator differentiator;


    public MapEntryDiff(Map.Entry entry1, Map.Entry entry2, Diff keyDiff, Diff valueDiff, Differentiator differentiator) {
        super();
        this.entry1 = entry1;
        this.entry2 = entry2;
        this.keyDiff = keyDiff;
        this.valueDiff = valueDiff;
        this.differentiator = differentiator;
    }

    /**
     * @see Diff#getObject1()
     */
    public Object getObject1() {
        return this.entry1;
    }

    /**
     * @see Diff#getObject2()
     */
    public Object getObject2() {
        return this.entry2;
    }

    /**
     * @see Diff#identical()
     */
    public boolean identical() {
        return ! this.different();
    }

    /**
     * @see Diff#different()
     */
    public boolean different() {
        return this.keyDiff.different() || this.valueDiff.different();
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
        if (this.identical()) {
            return "";
        }
        Writer sw = new StringWriter();
        IndentingPrintWriter pw = new IndentingPrintWriter(sw, "    ");
        this.appendHeader(pw);
        pw.indent();
            this.appendDescription(pw);
        pw.undent();
        return sw.toString();
    }

    protected void appendHeader(IndentingPrintWriter pw) {
        pw.print("Map Entries are different:");
        pw.println();
        pw.print("entry 1: ");
        pw.print(this.entry1);
        pw.println();
        pw.print("entry 2: ");
        pw.print(this.entry2);
        pw.println();
    }

    /**
     * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
     */
    public void appendDescription(IndentingPrintWriter pw) {
        if (this.keyDiff.different()) {
            pw.print("The entries' keys are different");
            pw.println();
            pw.indent();
                this.keyDiff.appendDescription(pw);
            pw.undent();
        }
        if (this.valueDiff.different()) {
            pw.print("The entries' values are different");
            pw.println();
            pw.indent();
                this.valueDiff.appendDescription(pw);
            pw.undent();
        }
    }

    public Map.Entry getEntry1() {
        return this.entry1;
    }

    public Map.Entry getEntry2() {
        return this.entry2;
    }

    public Diff getKeyDiff() {
        return this.keyDiff;
    }

    public Diff getValueDiff() {
        return this.valueDiff;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return (this.identical()) ? NO_DIFFERENCE_DESCRIPTION : this.getDescription();
    }

}
