/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
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
    public String getDeclPackage() {
        return m_context;
    }

    /**
     * Returns the name of the generated class within the package. This name is generated for the
     * declaration of the class, as opposed to uses of the class.
     */
    public String getDeclClass() {
        return m_name;
    }

    /**
     * Convert any unicode character in a string into ASCII code
     */
    protected static String unicode2Ascii(String uni) {
        if (uni == null || uni.length() == 0) {
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
        String asc = "";
        for (int i = 0; i < uni.length(); i++) {
            if (isFilePathPart(uni.charAt(i))) {
                asc += uni.charAt(i);
            }
            else {
                asc += "_" + Integer.toHexString(uni.charAt(i));
            }
        }
        return asc;
    }

    private static boolean isFilePathPart(char c) {
        return (c >= '\u0000' && c <= '\u00ff');
    }

    protected static String packageConcat(String package1, String package2) {
        if (package1 == null || package1.length() == 0) {
            return package2;
        }
        if (package2 == null || package2.length() == 0) {
            return package1;
        }
        return package1 + "." + package2;
    }

}
