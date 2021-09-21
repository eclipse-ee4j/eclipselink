/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a class for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public class ClassDefinition extends CodeDefinition {
    protected String packageName;
    protected Vector<String> imports;
    protected int type;
    public static final int CLASS_TYPE = 1;
    public static final int INTERFACE_TYPE = 2;
    protected String superClass;
    protected Vector<String> interfaces;
    protected Vector<AttributeDefinition> attributes;
    protected Vector<MethodDefinition> methods;
    protected Vector<ClassDefinition> innerClasses;

    public ClassDefinition() {
        this.packageName = "";
        this.imports = new Vector<>(3);
        this.type = CLASS_TYPE;
        this.interfaces = new Vector<>(3);
        this.attributes = new Vector<>();
        this.methods = new Vector<>();
        this.innerClasses = new Vector<>(3);
    }

    public void addAttribute(AttributeDefinition attribute) {
        getAttributes().addElement(attribute);
    }

    /**
     * The importStatement should be of the form
     * "{packageName}.{shortName or '*'}"
     */
    public void addImport(String importStatement) {
        if (!getImports().contains(importStatement)) {
            getImports().addElement(importStatement);
        }
    }

    private void addImports(Map<String, Set<String>> typeNameMap) {
        for (Map.Entry<String, Set<String>> entry : typeNameMap.entrySet()) {
            String shortName = entry.getKey();
            Set<String> packageNames = entry.getValue();

            if (packageNames.size() > 1) {
                continue;
            }

            for (String packageName : packageNames) {
                if (!packageName.equals(JAVA_LANG_PACKAGE_NAME) && !packageName.equals(getPackageName()) && !packageName.equals("")) {
                    addImport(packageName + "." + shortName);
                }
            }
        }

        sortImports();
    }

    public void addInnerClass(ClassDefinition classDefinition) {
        getInnerClasses().add(classDefinition);
    }

    public void addInterface(String interfaceClassName) {
        getInterfaces().addElement(interfaceClassName);
    }

    public void addMethod(MethodDefinition method) {
        getMethods().addElement(method);
    }

    private void addTypeNamesToMap(HashMap<String, Set<String>> typeNameMap) {
        putTypeNameInMap(getSuperClass(), typeNameMap);

        for (Iterator<String> i = getInterfaces().iterator(); i.hasNext();) {
            putTypeNameInMap(i.next(), typeNameMap);
        }

        for (Iterator<AttributeDefinition> i = getAttributes().iterator(); i.hasNext();) {
            (i.next()).putTypeNamesInMap(typeNameMap);
        }

        for (Iterator<MethodDefinition> i = getMethods().iterator(); i.hasNext();) {
            i.next().putTypeNamesInMap(typeNameMap);
        }
    }

    private void adjustTypeNames(HashMap<String, Set<String>> typeNameMap) {
        setSuperClass(adjustTypeName(getSuperClass(), typeNameMap));

        for (Iterator<String> i = new Vector<>(getInterfaces()).iterator(); i.hasNext();) {
            String interfaceName = i.next();
            replaceInterface(interfaceName, adjustTypeName(interfaceName, typeNameMap));
        }

        for (Iterator<AttributeDefinition> i = getAttributes().iterator(); i.hasNext();) {
            (i.next()).adjustTypeNames(typeNameMap);
        }

        for (Iterator<MethodDefinition> i = getMethods().iterator(); i.hasNext();) {
            i.next().adjustTypeNames(typeNameMap);
        }
    }

    /**
     * Parses the class definition, pulls out fully qualified class names,
     * adds imports for them, and un-fully qualifies the class names.
     * - Assumes that no imports have been previously added.
     * - Assumes that all types have been fully qualified to start.
     * - Will not unqualify ambiguous classes (java.util.Date and java.sql.Date).
     * - Will not add imports for java.lang.*
     * - Will not add imports for classes in the same package.
     * - Will not parse method bodies, but will unqualify types it finds.
     *
     * ?? - Should unqualification occur during writing?  That way, reflective definitions could take advantage.
     *
     */
    public void calculateImports() {
        // Calculate type name map for class definition.
        // Key - short type name, Value - Set of package names for that type name
        HashMap<String, Set<String>> typeNameMap = new HashMap<>();
        addTypeNamesToMap(typeNameMap);

        // Go back through class def, pulling out imports and removing package names from
        // non-repeated short type names.
        adjustTypeNames(typeNameMap);

        // Finally, add the imports
        addImports(typeNameMap);
    }

    public boolean containsMethod(MethodDefinition method) {
        return getMethods().contains(method);
    }

    protected Vector<AttributeDefinition> getAttributes() {
        return attributes;
    }

    protected Vector<String> getImports() {
        return imports;
    }

    protected Vector<ClassDefinition> getInnerClasses() {
        return innerClasses;
    }

    protected Vector<String> getInterfaces() {
        return interfaces;
    }

    protected Vector<MethodDefinition> getMethods() {
        return methods;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSuperClass() {
        return superClass;
    }

    public int getType() {
        return type;
    }

    public boolean isInterface() {
        return getType() == INTERFACE_TYPE;
    }

    protected void replaceInterface(String oldInterfaceName, String newInterfaceName) {
        // Don't bother sorting
        if (!oldInterfaceName.equals(newInterfaceName)) {
            this.interfaces.remove(oldInterfaceName);
            this.interfaces.add(newInterfaceName);
        }
    }

    private void setImports(Vector<String> imports) {
        this.imports = imports;
    }

    private void setMethods(Vector<MethodDefinition> methods) {
        this.methods = methods;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * If the class to be generated is an interface, do not use this method.
     * Instead, use addInterface(String) for each interface superclass.
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected void sortImports() {
        setImports(new Vector<>(new TreeSet<>(getImports())));
    }

    protected void sortMethods() {
        //Object methodArray[] = getMethods().toArray();
        MethodDefinition[] methodArray = getMethods().toArray(new MethodDefinition[0]);

        Comparator<MethodDefinition> comparison = new Comparator<MethodDefinition>() {
            @Override
            public int compare(MethodDefinition first, MethodDefinition second) {
                if (first.isConstructor()) {
                    return -1;
                } else if (second.isConstructor()) {
                    return 1;
                } else {
                    return first.getName().compareTo(second.getName());
                }
            }
        };

        Arrays.sort(methodArray, comparison);

        Vector<MethodDefinition> sortedMethods = new Vector<>(getMethods().size());
        for (int index = 0; index < methodArray.length; index++) {
            sortedMethods.addElement(methodArray[index]);
        }

        setMethods(sortedMethods);
    }

    /**
     * Write the code out to the generator's stream.
     */
    @Override
    public void write(CodeGenerator generator) {
        if (getPackageName().length() > 0) {
            generator.write("package ");
            generator.write(getPackageName());
            generator.writeln(";");
            generator.cr();
        }

        for (Enumeration<String> importsEnum = getImports().elements(); importsEnum.hasMoreElements();) {
            String importLine = importsEnum.nextElement();
            generator.write("import ");
            generator.write(importLine);
            generator.writeln(";");
        }
        if (!getImports().isEmpty()) {
            generator.cr();
        }
        super.write(generator);
    }

    /**
     * Write the code out to the generator's stream.
     */
    @Override
    public void writeBody(CodeGenerator generator) {
        sortMethods();

        if (isInterface()) {
            generator.write("interface ");
        } else {
            generator.write("class ");
        }

        generator.write(getName());

        if (!isInterface() && (getSuperClass() != null)) {
            generator.write(" extends ");
            generator.writeType(getSuperClass());
        }

        boolean isFirst = true;
        for (Enumeration<String> interfacesEnum = getInterfaces().elements();
             interfacesEnum.hasMoreElements();) {
            String interfaceName = interfacesEnum.nextElement();

            if (isFirst) {
                if (isInterface()) {
                    generator.write(" extends");
                } else {
                    generator.write(" implements");
                }

                isFirst = false;
            } else {
                generator.write(",");
            }

            generator.write(" ");
            generator.write(interfaceName);
        }

        generator.writeln(" {");
        generator.cr();

        for (Enumeration<AttributeDefinition> attributesEnum = getAttributes().elements();
             attributesEnum.hasMoreElements();) {
            generator.tab();
            (attributesEnum.nextElement()).write(generator);
            generator.cr();
        }

        if (!getAttributes().isEmpty()) {
            generator.cr();
        }

        for (Enumeration<MethodDefinition> methodsEnum = getMethods().elements(); methodsEnum.hasMoreElements();) {
            methodsEnum.nextElement().write(generator);
            generator.cr();
            generator.cr();
        }

        //used for Oc4j code gen
        for (Enumeration<ClassDefinition> innerClassesEnum = getInnerClasses().elements();
             innerClassesEnum.hasMoreElements();) {
            (innerClassesEnum.nextElement()).write(generator);
            generator.cr();
            generator.cr();
        }

        generator.writeln("}");
    }
}
