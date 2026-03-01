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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.codegen;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a element of code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public abstract class CodeDefinition {
    protected AccessLevel accessLevel;
    protected String name;
    protected String comment;
    protected static final String JAVA_LANG_PACKAGE_NAME = "java.lang";
    protected static final String JAVA_UTIL_PACKAGE_NAME = "java.util";
    protected static final String TOPLINK_INDIRECTION_PACKAGE_NAME = "org.eclipse.persistence.indirection";

    protected CodeDefinition() {
        this.accessLevel = new AccessLevel();
        this.name = "";
        this.comment = "";
    }

    private static boolean adjustmentNeededForType(String typeName, Map<String, Set<String>> typeNameMap) {
        if ((typeName == null) || typeName.isEmpty()) {
            return false;
        }

        if (packageName(typeName).isEmpty()) {
            return false;
        }

        Set<String> packages = typeNameMap.get(shortName(typeName));
        return (packages == null) || (packages.size() <= 1);
    }

    /**
     * Compares the typeName to those stored in the typeNameMap.
     * If the short name of the typeName is unambiguous (only one package for
     * that short name in the Map), removes the package name and returns the
     * short name, else returns the whole thing.
     * <p>
     * Assumes that typeName contains only a package name (optional) and a short name,
     * potentially with subtended brackets.
     * <p>
     * (e.g. int -&gt; int, java.util.Vector -&gt; Vector, java.lang.Boolean[] -&gt; Boolean[], etc.)
     */
    protected static String adjustTypeName(String typeName, Map<String, Set<String>> typeNameMap) {
        if (adjustmentNeededForType(typeName, typeNameMap)) {
            putTypeNameInMap(typeName, typeNameMap);
            return typeName.substring(packageName(typeName).length() + 1);
        } else {
            return typeName;
        }
    }

    /**
     * Returns a set of java.lang.String type names included in longString.
     * Will only look for ValueHolder, java.util collection types, and TopLink
     * indirect collection types.
     * All other searches too intractable at this point.
     */
    protected static Set<String> parseForTypeNames(String longString) {
        Set<String> typeNames = new HashSet<>();

        if (longString != null) {
            typeNames.addAll(parseForTypeNamesInPackage(longString, JAVA_LANG_PACKAGE_NAME));
            typeNames.addAll(parseForTypeNamesInPackage(longString, JAVA_UTIL_PACKAGE_NAME));
            typeNames.addAll(parseForTypeNamesInPackage(longString, TOPLINK_INDIRECTION_PACKAGE_NAME));
        }

        return typeNames;
    }

    private static Set<String> parseForTypeNamesInPackage(String longString, String packageName) {
        Set<String> typeNames = new HashSet<>();
        int packageStartIndex = longString.indexOf(packageName);

        while (packageStartIndex != -1) {
            boolean lookingForEndOfTypeName = true;
            int searchIndex = packageStartIndex + packageName.length() + 1;

            while (lookingForEndOfTypeName) {
                if (Character.isJavaIdentifierPart(longString.charAt(searchIndex))) {
                    searchIndex++;
                } else {
                    lookingForEndOfTypeName = false;
                }
            }

            typeNames.add(longString.substring(packageStartIndex, searchIndex));
            packageStartIndex = longString.indexOf(packageName, searchIndex);
        }

        return typeNames;
    }

    /**
     * Used for calculating imports.  @see org.eclipse.persistence.internal.codegen.ClassDefinition#calculateImports()
     */
    protected static void putTypeNameInMap(String typeName, Map<String, Set<String>> typeNameMap) {
        if ((typeName == null) || typeName.isEmpty()) {
            return;
        }

        String shortName = shortName(typeName);
        String packageName = packageName(typeName);

        if (!packageName.isEmpty()) {
            Set<String> packageNames;

            if (typeNameMap.get(shortName) == null) {
                packageNames = new HashSet<>();
                typeNameMap.put(shortName, packageNames);
            } else {
                packageNames = typeNameMap.get(shortName);
            }

            // There is no package name.  The package is the default package.
            // Do nothing, as neither an import is needed, nor does the class need to be unqualified.
            if (!packageNames.contains(packageName)) {
                packageNames.add(packageName);
            }
        }
    }

    private static String packageName(String typeName) {
        int lastPeriod = typeName.lastIndexOf('.');

        if (lastPeriod == -1) {
            return "";
        } else {
            return typeName.substring(0, lastPeriod);
        }
    }

    /**
     * Removes the package name, if there is one.  Also removes any trailing brackets.
     * <p>
     * Assumes that typeName contains only a package name (optional) and a short name,
     * potentially with subtended brackets.
     * <p>
     * (e.g. {@code int -> int}, {@code java.util.Vector -> Vector}, {@code java.lang.Boolean[] -> Boolean}, etc.)
     */
    private static String shortName(String typeName) {
        int shortNameStartIndex = typeName.lastIndexOf('.') + 1;
        int searchIndex = shortNameStartIndex;

        boolean stillLookingForEnd = true;
        while (stillLookingForEnd) {
            if (Character.isJavaIdentifierPart(typeName.charAt(searchIndex))) {
                searchIndex++;
                stillLookingForEnd = searchIndex < typeName.length();
            } else {
                stillLookingForEnd = false;
            }
        }

        return typeName.substring(shortNameStartIndex, searchIndex);
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        try {
            CodeGenerator generator = new CodeGenerator();
            write(generator);
            return generator.toString();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Write the code out to the generator's stream.
     */
    public void write(CodeGenerator generator) throws IOException {
        if (!getComment().isEmpty()) {
            generator.writeln("/**");
            String comment = getComment();
            String cr = System.lineSeparator();
            int lastLineIndex = 0;
            int nextLineIndex = comment.indexOf(cr);
            while (nextLineIndex != -1) {
                generator.write(" * ");
                generator.write(comment.substring(lastLineIndex, nextLineIndex + cr.length()));
                lastLineIndex = nextLineIndex + cr.length();
                nextLineIndex = comment.indexOf(cr, lastLineIndex);
            }
            generator.write(" * ");
            generator.writeln(comment.substring(lastLineIndex));
            generator.writeln(" */");
            generator.cr();
        }
        getAccessLevel().write(generator);
        generator.write(" ");
        writeBody(generator);
    }

    /**
     * Write the code out to the generator's stream.
     */
    public abstract void writeBody(CodeGenerator generator) throws IOException;
}
