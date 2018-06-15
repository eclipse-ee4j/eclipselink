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
package org.eclipse.persistence.utils.rename;

import java.util.Comparator;

public class RenameValue {
    private String sourceValue;

    private String replaceValue;

    public RenameValue(String source, String replace) {
        this.sourceValue = source;
        this.replaceValue = replace;
    }

    public String getSourceValue() {
        return this.sourceValue;
    }

    public String getReplaceValue() {
        return this.replaceValue;
    }

    public RenameFileData replace(RenameFileData data) {
        int srcLen = getSourceValue().length();
        int replaceLen = getReplaceValue().length();
        String newStr = data.getFileContentsString();

        int pos = newStr.indexOf(getSourceValue());
        int lastPos = pos;

        while (pos >= 0) {
            String firstPart;
            String lastPart;

            firstPart = newStr.substring(0, pos);
            lastPart = newStr.substring(pos + srcLen, newStr.length());
            newStr = firstPart + getReplaceValue() + lastPart;
            lastPos = pos + replaceLen;
            pos = newStr.indexOf(getSourceValue(), lastPos);
            data.setChanged(true);
        }
        data.setFileContentsString(newStr);
        return data;
    }

    public String toString() {
        return "REPLACE> " + getSourceValue() + " -> " + getReplaceValue();
    }

    protected static Comparator<RenameValue> renameValueComparator() {
        return new Comparator<RenameValue>() {

            public int compare(RenameValue rv1, RenameValue rv2) {
                return rv2.getSourceValue().length()
                        - rv1.getSourceValue().length();
            }

        };
    }
}
