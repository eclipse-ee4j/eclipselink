/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

/**
 * A LangName encapsulates the name of a type for a language.
 */
public abstract class LangName extends Name {

    protected String m_useClass;

    public LangName() {
    }

    public LangName(String context, String name) {
        super(context, name);
    }

    /**
     * Returns the package name of the declared class.
     */
    @Override
    public String getDeclPackage() {
        return m_context;
    }

    /**
     * Returns the name of the generated class within the package. This name is generated for the
     * declaration of the class, as opposed to uses of the class.
     */
    @Override
    public String getDeclClass() {
        return m_name;
    }

    /**
     * Convert any unicode character in a string into ASCII code
     */
    protected static String unicode2Ascii(String uni) {
        if (uni == null || uni.isEmpty()) {
            return uni;
        }
        uni = uni.trim();
        boolean anyUnicode = false;
        for (int i = 0; i < uni.length(); i++) {
            if (!isFilePathPart(uni.charAt(i))) {
                anyUnicode = true;
            }
        }
        if (!anyUnicode) {
            return uni;
        }
        StringBuilder asc = new StringBuilder();
        for (int i = 0; i < uni.length(); i++) {
            if (isFilePathPart(uni.charAt(i))) {
                asc.append(uni.charAt(i));
            }
            else {
                asc.append("_").append(Integer.toHexString(uni.charAt(i)));
            }
        }
        return asc.toString();
    }

    private static boolean isFilePathPart(char c) {
        return (c >= '\u0000' && c <= '\u00ff');
    }

    protected static String packageConcat(String package1, String package2) {
        if (package1 == null || package1.isEmpty()) {
            return package2;
        }
        if (package2 == null || package2.isEmpty()) {
            return package1;
        }
        return package1 + "." + package2;
    }

}
