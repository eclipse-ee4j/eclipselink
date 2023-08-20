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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a class for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public class ClassDefinition extends CodeDefinition {
    protected String packageName;
    protected List<String> imports;
    protected int type;
    public static final int CLASS_TYPE = 1;
    public static final int INTERFACE_TYPE = 2;
    protected String superClass;
    protected List<String> interfaces;
    protected List<AttributeDefinition> attributes;
    protected List<MethodDefinition> methods;
    protected List<ClassDefinition> innerClasses;

    public ClassDefinition() {
        this.packageName = "";
        this.imports = new ArrayList<>(3);
        this.type = CLASS_TYPE;
        this.interfaces = new ArrayList<>(3);
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.innerClasses = new ArrayList<>(3);
    }

    public void addAttribute(AttributeDefinition attribute) {
        getAttributes().add(attribute);
    }

    /**
     * The importStatement should be of the form
     * "{packageName}.{shortName or '*'}"
     */
    public void addImport(String importStatement) {
        if (!getImports().contains(importStatement)) {
            getImports().add(importStatement);
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
                if (!packageName.equals(JAVA_LANG_PACKAGE_NAME) && !packageName.equals(getPackageName()) && !packageName.isEmpty()) {
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
        getInterfaces().add(interfaceClassName);
    }

    public void addMethod(MethodDefinition method) {
        getMethods().add(method);
    }

    private void addTypeNamesToMap(HashMap<String, Set<String>> typeNameMap) {
        putTypeNameInMap(getSuperClass(), typeNameMap);

        for (String s : getInterfaces()) {
            putTypeNameInMap(s, typeNameMap);
        }

        for (AttributeDefinition attributeDefinition : getAttributes()) {
            attributeDefinition.putTypeNamesInMap(typeNameMap);
        }

        for (MethodDefinition methodDefinition : getMethods()) {
            methodDefinition.putTypeNamesInMap(typeNameMap);
        }
    }

    private void adjustTypeNames(HashMap<String, Set<String>> typeNameMap) {
        setSuperClass(adjustTypeName(getSuperClass(), typeNameMap));

        for (String interfaceName : new ArrayList<>(getInterfaces())) {
            replaceInterface(interfaceName, adjustTypeName(interfaceName, typeNameMap));
        }

        for (AttributeDefinition attributeDefinition : getAttributes()) {
            attributeDefinition.adjustTypeNames(typeNameMap);
        }

        for (MethodDefinition methodDefinition : getMethods()) {
            methodDefinition.adjustTypeNames(typeNameMap);
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
     * ?? - Should un-qualification occur during writing?  That way, reflective definitions could take advantage.
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

    protected List<AttributeDefinition> getAttributes() {
        return attributes;
    }

    protected List<String> getImports() {
        return imports;
    }

    protected List<ClassDefinition> getInnerClasses() {
        return innerClasses;
    }

    protected List<String> getInterfaces() {
        return interfaces;
    }

    protected List<MethodDefinition> getMethods() {
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

    private void setImports(List<String> imports) {
        this.imports = imports;
    }

    private void setMethods(List<MethodDefinition> methods) {
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
        setImports(new ArrayList<>(new TreeSet<>(getImports())));
    }

    protected void sortMethods() {
        Comparator<MethodDefinition> comparison = (first, second) -> {
            if (first.isConstructor()) {
                return -1;
            } else if (second.isConstructor()) {
                return 1;
            } else {
                return first.getName().compareTo(second.getName());
            }
        };
        getMethods().sort(comparison);
    }

    /**
     * Write the code out to the generator's stream.
     */
    @Override
    public void write(CodeGenerator generator) {
        if (!getPackageName().isEmpty()) {
            generator.write("package ");
            generator.write(getPackageName());
            generator.writeln(";");
            generator.cr();
        }

        for (String importLine : getImports()) {
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
        for (String interfaceName : getInterfaces()) {
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

        for (AttributeDefinition attributeDefinition : getAttributes()) {
            generator.tab();
            attributeDefinition.write(generator);
            generator.cr();
        }

        if (!getAttributes().isEmpty()) {
            generator.cr();
        }

        for (MethodDefinition methodDefinition : getMethods()) {
            methodDefinition.write(generator);
            generator.cr();
            generator.cr();
        }

        //used for Oc4j code gen
        for (ClassDefinition classDefinition : getInnerClasses()) {
            classDefinition.write(generator);
            generator.cr();
            generator.cr();
        }

        generator.writeln("}");
    }
}
