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
import org.eclipse.persistence.internal.helper.*;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a class for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public class ClassDefinition extends CodeDefinition {
    protected String packageName;
    protected Vector imports;
    protected int type;
    public static final int CLASS_TYPE = 1;
    public static final int INTERFACE_TYPE = 2;
    protected String superClass;
    protected Vector interfaces;
    protected Vector attributes;
    protected Vector methods;
    protected Vector innerClasses;

    public ClassDefinition() {
        this.packageName = "";
        this.imports = new Vector(3);
        this.type = CLASS_TYPE;
        this.interfaces = new Vector(3);
        this.attributes = new Vector();
        this.methods = new Vector();
        this.innerClasses = new Vector(3);
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

    private void addImports(Map typeNameMap) {
        for (Iterator shortNameIt = typeNameMap.keySet().iterator(); shortNameIt.hasNext();) {
            String shortName = (String)shortNameIt.next();
            Set packageNames = (Set)typeNameMap.get(shortName);

            if (packageNames.size() > 1) {
                continue;
            }

            for (Iterator packageNameIt = ((Set)typeNameMap.get(shortName)).iterator();
                     packageNameIt.hasNext();) {
                String packageName = (String)packageNameIt.next();

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

    private void addTypeNamesToMap(HashMap typeNameMap) {
        putTypeNameInMap(getSuperClass(), typeNameMap);

        for (Iterator i = getInterfaces().iterator(); i.hasNext();) {
            putTypeNameInMap((String)i.next(), typeNameMap);
        }

        for (Iterator i = getAttributes().iterator(); i.hasNext();) {
            ((AttributeDefinition)i.next()).putTypeNamesInMap(typeNameMap);
        }

        for (Iterator i = getMethods().iterator(); i.hasNext();) {
            ((MethodDefinition)i.next()).putTypeNamesInMap(typeNameMap);
        }
    }

    private void adjustTypeNames(HashMap typeNameMap) {
        setSuperClass(adjustTypeName(getSuperClass(), typeNameMap));

        for (Iterator i = new Vector(getInterfaces()).iterator(); i.hasNext();) {
            String interfaceName = (String)i.next();
            replaceInterface(interfaceName, adjustTypeName(interfaceName, typeNameMap));
        }

        for (Iterator i = getAttributes().iterator(); i.hasNext();) {
            ((AttributeDefinition)i.next()).adjustTypeNames(typeNameMap);
        }

        for (Iterator i = getMethods().iterator(); i.hasNext();) {
            ((MethodDefinition)i.next()).adjustTypeNames(typeNameMap);
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
        HashMap typeNameMap = new HashMap();
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

    protected Vector getAttributes() {
        return attributes;
    }

    protected Vector getImports() {
        return imports;
    }

    protected Vector getInnerClasses() {
        return innerClasses;
    }

    protected Vector getInterfaces() {
        return interfaces;
    }

    protected Vector getMethods() {
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

    private void setImports(Vector imports) {
        this.imports = imports;
    }

    private void setMethods(Vector methods) {
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
        setImports(new Vector(new TreeSet(getImports())));
    }

    protected void sortMethods() {
        //Object methodArray[] = getMethods().toArray();
        Object[] methodArray = Helper.arrayFromVector(getMethods());

        Comparator comparison = new Comparator() {
            public int compare(Object first, Object second) {
                if (((MethodDefinition)first).isConstructor()) {
                    return -1;
                } else if (((MethodDefinition)second).isConstructor()) {
                    return 1;
                } else {
                    return ((MethodDefinition)first).getName().compareTo(((MethodDefinition)second).getName());
                }
            }
        };

        Arrays.sort(methodArray, comparison);

        Vector sortedMethods = new Vector(getMethods().size());
        for (int index = 0; index < methodArray.length; index++) {
            sortedMethods.addElement(methodArray[index]);
        }

        setMethods(sortedMethods);
    }

    /**
     * Write the code out to the generator's stream.
     */
    public void write(CodeGenerator generator) {
        if (getPackageName().length() > 0) {
            generator.write("package ");
            generator.write(getPackageName());
            generator.writeln(";");
            generator.cr();
        }

        for (Enumeration importsEnum = getImports().elements(); importsEnum.hasMoreElements();) {
            String importLine = (String)importsEnum.nextElement();
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
        for (Enumeration interfacesEnum = getInterfaces().elements();
                 interfacesEnum.hasMoreElements();) {
            String interfaceName = (String)interfacesEnum.nextElement();

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

        for (Enumeration attributesEnum = getAttributes().elements();
                 attributesEnum.hasMoreElements();) {
            generator.tab();
            ((AttributeDefinition)attributesEnum.nextElement()).write(generator);
            generator.cr();
        }

        if (!getAttributes().isEmpty()) {
            generator.cr();
        }

        for (Enumeration methodsEnum = getMethods().elements(); methodsEnum.hasMoreElements();) {
            ((MethodDefinition)methodsEnum.nextElement()).write(generator);
            generator.cr();
            generator.cr();
        }

        //used for Oc4j code gen
        for (Enumeration innerClassesEnum = getInnerClasses().elements();
                 innerClassesEnum.hasMoreElements();) {
            ((ClassDefinition)innerClassesEnum.nextElement()).write(generator);
            generator.cr();
            generator.cr();
        }

        generator.writeln("}");
    }
}
