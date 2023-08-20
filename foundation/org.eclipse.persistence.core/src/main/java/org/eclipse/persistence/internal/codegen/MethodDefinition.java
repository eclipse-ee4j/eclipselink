/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a method for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public abstract class MethodDefinition extends CodeDefinition {
    protected boolean isAbstract;
    protected boolean isConstructor;
    protected String returnType;
    protected List<String> argumentNames;
    protected List<String> lines;
    protected List<String> exceptions;
    protected StringBuilder storedBuffer;

    protected MethodDefinition() {
        this.isConstructor = false;
        this.returnType = "void";
        this.lines = new ArrayList<>();
        this.exceptions = new ArrayList<>();
        this.storedBuffer = new StringBuilder();
    }

    public void addException(String exceptionTypeName) {
        this.exceptions.add(exceptionTypeName);
    }

    public void addLine(String line) {
        this.storedBuffer.append(line);
        getLines().add(this.storedBuffer.toString());
        this.storedBuffer = new StringBuilder();
    }

    /**
     * This method can be used to store a string that will be prepended to the very next line of code entered
     */
    public void addToBuffer(String partOfLine) {
        this.storedBuffer.append(partOfLine);
    }

    private void adjustExceptions(Map<String, Set<String>> typeNameMap) {
        for (String exceptionName : new ArrayList<>(getExceptions())) {
            String adjustedExceptionName = adjustTypeName(exceptionName, typeNameMap);

            if (!exceptionName.equals(adjustedExceptionName)) {
                replaceException(exceptionName, adjustedExceptionName);
            }
        }
    }

    /**
     * Parses the line, removing the package name for each type
     * (and adding the appropriate import) if the type is
     * unambiguous.
     */
    private void adjustLine(String line, Map<String, Set<String>> typeNameMap) {
        StringBuilder lineInProgress = new StringBuilder(line);
        Set<String> typeNames = parseForTypeNames(lineInProgress.toString());

        for (String typeName : typeNames) {
            String adjustedTypeName = adjustTypeName(typeName, typeNameMap);

            if (!typeName.equals(adjustedTypeName)) {
                int typeNameStartIndex = lineInProgress.toString().indexOf(typeName);

                while (typeNameStartIndex != -1) {
                    lineInProgress.replace(typeNameStartIndex, typeNameStartIndex + typeName.length(), adjustedTypeName);
                    typeNameStartIndex = lineInProgress.toString().indexOf(typeName);
                }
            }
        }

        replaceLine(line, lineInProgress.toString());
    }

    private void adjustLines(Map<String, Set<String>> typeNameMap) {
        for (String s : new ArrayList<>(getLines())) {
            adjustLine(s, typeNameMap);
        }
    }

    private void adjustReturnType(Map<String, Set<String>> typeNameMap) {
        String adjustedReturnType = adjustTypeName(getReturnType(), typeNameMap);

        if (!getReturnType().equals(adjustedReturnType)) {
            setReturnType(adjustedReturnType);
        }
    }

    protected void adjustTypeNames(Map<String, Set<String>> typeNameMap) {
        adjustReturnType(typeNameMap);
        adjustExceptions(typeNameMap);
        adjustLines(typeNameMap);
    }

    protected abstract boolean argumentsEqual(MethodDefinition methodDefinition);

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof MethodDefinition)) {
            return false;
        }

        MethodDefinition methodDefinition = (MethodDefinition)object;

        if ((this.name == null) && (methodDefinition.getName() != null)) {
            return false;
        }
        if ((this.name != null) && !this.name.equals(methodDefinition.getName())) {
            return false;
        }

        if (!this.accessLevel.equals(methodDefinition.getAccessLevel())) {
            return false;
        }

        if (!this.returnType.equals(methodDefinition.getReturnType())) {
            return false;
        }

        if (!argumentsEqual(methodDefinition)) {
            return false;
        }

        if (!exceptionsEqual(methodDefinition)) {
            return false;
        }

        return true;
    }

    protected boolean exceptionsEqual(MethodDefinition methodDefinition) {
        Object[] exceptions1 = this.getExceptions().toArray();
        Object[] exceptions2 = methodDefinition.getExceptions().toArray();
        if (exceptions1.length == exceptions2.length) {
            for (int i = 0; i < exceptions1.length; i++) {
                if (((exceptions1[i] == null) && (exceptions1[i] != exceptions2[i])) || (!exceptions1[i].equals(exceptions2[i]))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected List<String> getArgumentNames() {
        if (this.argumentNames == null) {
            this.argumentNames = new ArrayList<>(5);
        }
        return argumentNames;
    }

    public String getArgumentName(int index) {
        return getArgumentNames().get(index);
    }

    public Iterator<String> argumentNames() {
        return getArgumentNames().iterator();
    }

    public int argumentNamesSize() {
        return getArgumentNames().size();
    }

    protected abstract List<String> getArgumentTypeNames();

    protected abstract List<String> getArgumentTypes();

    public List<String> getLines() {
        return lines;
    }

    protected List<String> getExceptions() {
        return this.exceptions;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public int hashCode() {
        int hash = this.accessLevel.hashCode();
        hash ^= this.returnType.hashCode();
        hash ^= this.getArgumentTypes().hashCode();

        if (this.name != null) {
            hash ^= this.name.hashCode();
        }

        if (this.name != null) {
            hash ^= this.name.hashCode();
        }

        hash ^= this.getExceptions().hashCode();

        return hash;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    /**
     * Used for calculating imports.  @see org.eclipse.persistence.internal.codegen.ClassDefinition#calculateImports()
     */
    protected void putTypeNamesInMap(Map<String, Set<String>> typeNameMap) {
        putTypeNameInMap(getReturnType(), typeNameMap);

        for (String string : getExceptions()) {
            putTypeNameInMap(string, typeNameMap);
        }

        for (String s : getArgumentTypeNames()) {
            putTypeNameInMap(s, typeNameMap);
        }
    }

    protected void replaceException(String oldExceptionName, String newExceptionName) {
        int index = getExceptions().indexOf(oldExceptionName);
        getExceptions().remove(oldExceptionName);
        getExceptions().add(index, newExceptionName);
    }

    protected void replaceLine(String oldLine, String newLine) {
        int index = getLines().indexOf(oldLine);
        getLines().remove(oldLine);
        getLines().add(index, newLine);
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public void setIsConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Write the code out to the generator's stream.
     */
    @Override
    public void writeBody(CodeGenerator generator) {
        if (!isConstructor()) {
            generator.writeType(getReturnType());
            generator.write(" ");
        }
        generator.write(getName());
        generator.write("(");

        writeArguments(generator);

        generator.write(")");

        if (!this.exceptions.isEmpty()) {
            writeThrowsClause(generator);
        }

        if (isAbstract()) {
            generator.write(";");
        } else {
            generator.write(" {");

            generator.cr();

            for (String s : getLines()) {
                generator.tab();
                generator.writeln(s);
            }

            generator.write("}");
        }
    }

    protected abstract void writeArguments(CodeGenerator generator);

    protected void writeThrowsClause(CodeGenerator generator) {
        generator.write(" throws ");

        for (Iterator<String> exceptionIterator = this.exceptions.iterator(); exceptionIterator.hasNext();) {
            generator.write(exceptionIterator.next());

            if (exceptionIterator.hasNext()) {
                generator.write(", ");
            }
        }
    }
}
