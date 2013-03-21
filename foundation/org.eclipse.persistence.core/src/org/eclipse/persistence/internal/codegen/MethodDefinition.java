/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.codegen;

import java.util.*;

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
    protected Vector argumentNames;
    protected Vector lines;
    protected Vector exceptions;
    protected StringBuffer storedBuffer;

    public MethodDefinition() {
        this.isConstructor = false;
        this.returnType = "void";
        this.lines = new Vector();
        this.exceptions = new Vector();
        this.storedBuffer = new StringBuffer();
    }

    public void addException(String exceptionTypeName) {
        this.exceptions.add(exceptionTypeName);
    }

    public void addLine(String line) {
        this.storedBuffer.append(line);
        getLines().addElement(this.storedBuffer.toString());
        this.storedBuffer = new StringBuffer();
    }

    /**
     * This method can be used to store a string that will be prepended to the very next line of code entered
     */
    public void addToBuffer(String partOfLine) {
        this.storedBuffer.append(partOfLine);
    }

    private void adjustExceptions(Map typeNameMap) {
        for (Iterator i = new Vector(getExceptions()).iterator(); i.hasNext();) {
            String exceptionName = (String)i.next();
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
    private void adjustLine(String line, Map typeNameMap) {
        StringBuffer lineInProgress = new StringBuffer(line);
        Set typeNames = parseForTypeNames(lineInProgress.toString());

        for (Iterator i = typeNames.iterator(); i.hasNext();) {
            String typeName = (String)i.next();
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

    private void adjustLines(Map typeNameMap) {
        for (Iterator i = new Vector(getLines()).iterator(); i.hasNext();) {
            adjustLine((String)i.next(), typeNameMap);
        }
    }

    private void adjustReturnType(Map typeNameMap) {
        String adjustedReturnType = adjustTypeName(getReturnType(), typeNameMap);

        if (!getReturnType().equals(adjustedReturnType)) {
            setReturnType(adjustedReturnType);
        }
    }

    protected void adjustTypeNames(Map typeNameMap) {
        adjustReturnType(typeNameMap);
        adjustExceptions(typeNameMap);
        adjustLines(typeNameMap);
    }

    protected abstract boolean argumentsEqual(MethodDefinition methodDefinition);

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

    protected Vector getArgumentNames() {
        if (this.argumentNames == null) {
            this.argumentNames = new Vector(5);
        }
        return argumentNames;
    }

    public String getArgumentName(int index) {
        return (String)getArgumentNames().get(index);
    }

    public Iterator argumentNames() {
        return getArgumentNames().iterator();
    }

    public int argumentNamesSize() {
        return getArgumentNames().size();
    }

    protected abstract Vector getArgumentTypeNames();

    protected abstract Vector getArgumentTypes();

    public Vector getLines() {
        return lines;
    }

    protected Vector getExceptions() {
        return this.exceptions;
    }

    public String getReturnType() {
        return returnType;
    }

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
    protected void putTypeNamesInMap(Map typeNameMap) {
        putTypeNameInMap(getReturnType(), typeNameMap);

        for (Iterator i = getExceptions().iterator(); i.hasNext();) {
            putTypeNameInMap((String)i.next(), typeNameMap);
        }

        for (Iterator i = getArgumentTypeNames().iterator(); i.hasNext();) {
            putTypeNameInMap((String)i.next(), typeNameMap);
        }
    }

    protected void replaceException(String oldExceptionName, String newExceptionName) {
        int index = getExceptions().indexOf(oldExceptionName);
        getExceptions().remove(oldExceptionName);
        getExceptions().insertElementAt(newExceptionName, index);
    }

    protected void replaceLine(String oldLine, String newLine) {
        int index = getLines().indexOf(oldLine);
        getLines().remove(oldLine);
        getLines().insertElementAt(newLine, index);
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

            for (Enumeration linesEnum = getLines().elements(); linesEnum.hasMoreElements();) {
                generator.tab();
                generator.writeln(linesEnum.nextElement());
            }

            generator.write("}");
        }
    }

    protected abstract void writeArguments(CodeGenerator generator);

    protected void writeThrowsClause(CodeGenerator generator) {
        generator.write(" throws ");

        for (Iterator exceptionIterator = this.exceptions.iterator(); exceptionIterator.hasNext();) {
            generator.write(exceptionIterator.next());

            if (exceptionIterator.hasNext()) {
                generator.write(", ");
            }
        }
    }
}
