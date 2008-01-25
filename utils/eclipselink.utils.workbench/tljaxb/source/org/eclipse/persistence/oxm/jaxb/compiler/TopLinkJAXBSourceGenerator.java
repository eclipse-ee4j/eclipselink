/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;


/**
 * This class generates JAXB implementation classes from information gleaned
 * from <CODE>orajaxb</CODE>.
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/14/2004 11:54:48
 */
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;

public class TopLinkJAXBSourceGenerator {
    // ===========================================================================
    // JaxbBindingSchemas, keyed on node name.
    private Hashtable bindingSchemas;
    private MWXmlSchema mwSchema;
    private String sourceDir;
    private String resourceDir;
    private String implClassPackage;

    // Source Gen Stuff
    private String indent = "";
    private static int INDENT_TAB = 3;
    private static final String fsep = System.getProperty("file.separator");
    private static final String lsep = System.getProperty("line.separator");
    private static final String lsep2 = lsep + lsep;

    //	private static final String IMPL = JaxbConstants.IMPL;
    private static final String IMPL = "Impl";

    // ===========================================================================
    public void generate(Hashtable bindingSchemas, MWXmlSchema mwSchema, String sourceDir, String resourceDir, String implClassPackage) throws TopLinkJAXBGenerationException {
        this.bindingSchemas = bindingSchemas;
        this.mwSchema = mwSchema;
        this.sourceDir = sourceDir;
        this.resourceDir = resourceDir;
        this.implClassPackage = implClassPackage;
        generateJavaSource(bindingSchemas);
    }

    // ==============================================================================================================================================
    // JAVA SOURCE GENERATION
    // ==============================================================================================================================================
    private void generateJavaSource(Hashtable bindingSchemas) throws TopLinkJAXBGenerationException {
        Enumeration e = bindingSchemas.elements();

        TopLinkJAXBBindingSchema schema = null;
        while (e.hasMoreElements()) {
            schema = (TopLinkJAXBBindingSchema)e.nextElement();
            String packageName = "";
            if ((this.implClassPackage == null) || this.implClassPackage.equals(schema.getPackageName())) {
                packageName = schema.getPackageName();
            } else {
                packageName = this.implClassPackage;
            }
            writeJavaFile(generateClassCode(schema, false, packageName), schema);
        }
        if ((this.resourceDir != null) && (schema != null)) {
            writePropertiesFile(getResourceDirWithPackage(schema));
            writeObjectFactoryClass(bindingSchemas, getSourceDirWithPackage(schema));
        }
    }

    // ===========================================================================
    private StringBuffer generateClassCode(TopLinkJAXBBindingSchema schema, boolean isInnerClass, String packageName) {
        StringBuffer classBuffer = new StringBuffer();

        if (!isInnerClass) {
//            classBuffer.append(this.indent).append(JaxbUtil.getPrefaceString());

            if (!packageName.equals("")) {
                classBuffer.append(this.indent);
                classBuffer.append("package ");
                classBuffer.append(packageName);
                classBuffer.append(";").append(lsep2).append(lsep);
            }

            //if impl classes are going somewhere else, import the interfaces
            if (!packageName.equals(schema.getPackageName())) {
                classBuffer.append(this.indent);
                classBuffer.append("import ");
                classBuffer.append(schema.getPackageName());
                classBuffer.append(".*;").append(lsep2).append(lsep);
            }
            classBuffer.append(this.indent);
            classBuffer.append("public class ");
        } else {
            classBuffer.append(this.indent);
            classBuffer.append("public static class ");
        }

        classBuffer.append(schema.getClassName()).append(IMPL);
        if (schema.getExtendsNode() != null) {
            //            Enumeration bindingSchemasEnum = bindingSchemas.elements();
            TopLinkJAXBBindingSchema superSchema = null;
            superSchema = findParentSchema(schema);

            if (superSchema != null) {
                classBuffer.append(" extends ");
                classBuffer.append(getFullImplClassName(superSchema).replace('$', '.'));
            }
        }
        classBuffer.append(" implements ");
        classBuffer.append(getFullClassName(schema).replace('$', '.')).append(lsep);
        classBuffer.append(this.indent).append("{").append(lsep2);

        StringBuffer attributeBuffer = new StringBuffer();
        StringBuffer methodBuffer = new StringBuffer();
        StringBuffer innerClassBuffer = new StringBuffer();

        pushIndent();

        if (schema.getSimpleTypeName() != null) {
            generateSimpleTypeMethods(schema.getSimpleTypeName(), attributeBuffer, methodBuffer);
        } else if (schema.getSimpleContentTypeName() != null) {
            generateSimpleTypeMethods(schema.getSimpleContentTypeName(), attributeBuffer, methodBuffer);
        }

        if (schema.getIsNillable()) {
            generateNilAttribute(attributeBuffer);
            generateIsNilAndSetNilMethods(methodBuffer);
        }

        Vector properties = schema.getProperties();
        if (properties != null) {
            TopLinkJAXBProperty property = null;
            for (int i = 0; i < properties.size(); i++) {
                property = (TopLinkJAXBProperty)properties.elementAt(i);
                generateAttributeAndMethodCode(property, attributeBuffer, methodBuffer);
            }
        }

        classBuffer.append(attributeBuffer.toString()).append(lsep);

        classBuffer.append(methodBuffer.toString());

        Vector innerClasses = schema.getInnerInterfaces();
        if (innerClasses != null) {
            for (int i = 0; i < innerClasses.size(); i++) {
                TopLinkJAXBBindingSchema innerSchema = (TopLinkJAXBBindingSchema)innerClasses.elementAt(i);
                innerClassBuffer.append(generateClassCode(innerSchema, true, packageName)).append(lsep2);
            }
            classBuffer.append(innerClassBuffer.toString());
        }

        popIndent();

        classBuffer.append(this.indent).append("}");

        return classBuffer;
    }

    public void generateSimpleTypeMethods(String simpleTypeName, StringBuffer attributeBuffer, StringBuffer methodBuffer) {
        boolean isCollection = false;
        if (simpleTypeName.equals("java.util.List")) {
            isCollection = true;
        }

        generateAttributeCode(attributeBuffer, "Value", simpleTypeName, isCollection);
        generateSetter(methodBuffer, "Value", simpleTypeName, null, isCollection, false, false);
        generateGetter(methodBuffer, "Value", simpleTypeName, isCollection, false);

    }

    public void generateAttributeAndMethodCode(TopLinkJAXBProperty property, StringBuffer attributeBuffer, StringBuffer methodBuffer) {
        String type = null;
        boolean isCollection = property.isCollectionType();
        if (isCollection) {
            type = property.getCollectionTypeName();
        } else {
            type = property.getJavaTypeName();
        }
        generateAttributeCode(attributeBuffer, property.getName(), type, isCollection);

        boolean generateIsSet = property.getGenerateIsSetMethod();

        //String attrName = JaxbUtil.className(property.getName());
        String attrName = "";
        
        //generate isSetX boolean for each attribute
        if (generateIsSet) {
            attributeBuffer.append(this.indent).append("private boolean ").append("isSet").append(attrName).append(" = false").append(";").append(lsep);
        }

        generateGetter(methodBuffer, property.getName(), type, isCollection, false);

        String defaultValue = getUnsetValue(type, property.getXMLTypes(), property.getDefaultValue(), isCollection);
        generateSetter(methodBuffer, property.getName(), type, defaultValue, isCollection, generateIsSet, false);

        // to avoid setting default values for null primitives, generate wrapper class methods for use in TopLink
        if (isPrimitive(type)) {
            String wrapper = getWrapper(type);
            generateGetter(methodBuffer, property.getName(), wrapper, isCollection, true);

            defaultValue = getUnsetValue(wrapper, property.getXMLTypes(), property.getDefaultValue(), isCollection);
            generateSetter(methodBuffer, property.getName(), wrapper, defaultValue, isCollection, generateIsSet, true);
        }

        if (generateIsSet) {
            generateIsSetAndUnsetMethods(methodBuffer, property.getName(), type, property.getXMLTypes(), defaultValue, isCollection);
        }
    }

    private void generateAttributeCode(StringBuffer attributeBuffer, String name, String javaType, boolean isCollection) {
        attributeBuffer.append(this.indent);
        attributeBuffer.append("private ");
        if (isCollection) {
            attributeBuffer.append(javaType);
//            attributeBuffer.append(" _").append(JaxbUtil.className(name));
            if (javaType.equals("java.util.List")) {
                attributeBuffer.append(" = new java.util.ArrayList()");
            }
            attributeBuffer.append(";").append(lsep);
        } else {
            attributeBuffer.append(isPrimitive(javaType) ? getWrapper(javaType) : javaType);
//            attributeBuffer.append(" _").append(JaxbUtil.className(name)).append(";").append(lsep);
        }
    }

    private void generateGetter(StringBuffer methodBuffer, String name, String javaType, boolean isCollection, boolean wrapperMethod) {
        methodBuffer.append(this.indent);
        methodBuffer.append("public ");
        methodBuffer.append(javaType).append(" ");

        // handle wrapper class usage - append '_' to differentiate from primitive setter
        if (wrapperMethod) {
//            methodBuffer.append("_").append(JaxbUtil.getMethodName(name, javaType)).append("()").append(lsep);
        } else {
//            methodBuffer.append(JaxbUtil.getMethodName(name, javaType)).append("()").append(lsep);
        }
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        methodBuffer.append(this.indent);

        // handle primitives - must watch for null when unwrapping
        if (isPrimitive(javaType)) {
//            methodBuffer.append("if (_").append(JaxbUtil.className(name)).append(" != null)").append(lsep);
            methodBuffer.append(this.indent);
            methodBuffer.append("{").append(lsep);
            pushIndent();
            methodBuffer.append(this.indent);
//            methodBuffer.append("return _").append(JaxbUtil.className(name)).append(".").append(javaType).append("Value()");
            methodBuffer.append(";");
            methodBuffer.append(lsep);
            popIndent();
            methodBuffer.append(this.indent);
            methodBuffer.append("}").append(lsep);
            methodBuffer.append(this.indent);
            methodBuffer.append("return ").append(getDefaultValueForPrimitive(javaType)).append(";");
            methodBuffer.append(this.indent);
        } else {
            methodBuffer.append("return _");
//            methodBuffer.append(JaxbUtil.className(name));
            methodBuffer.append(";");
        }
        methodBuffer.append(lsep);

        popIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("}").append(lsep2);
    }

    private void generateSetter(StringBuffer methodBuffer, String name, String javaType, String defaultValue, boolean isCollection, boolean generateIsSet, boolean wrapperMethod) {
//        String attrName = JaxbUtil.className(name);
    	String attrName = "";
        methodBuffer.append(this.indent);
        methodBuffer.append("public void ");

        // handle wrapper class usage - append '_' to differentiate from primitive setter
        if (wrapperMethod) {
//            methodBuffer.append("_").append(JaxbUtil.setMethodName(name));
        } else {
//            methodBuffer.append(JaxbUtil.setMethodName(name));
        }
        methodBuffer.append("(").append(javaType).append(" value)").append(lsep);
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();

        if (generateIsSet) {
            String nullValue = defaultValue;
            if (isPrimitive(javaType)) {
                methodBuffer.append(this.indent).append("if (value == ").append(nullValue).append(") {").append(lsep);
            } else {
                if (nullValue.equalsIgnoreCase("null")) {
                    methodBuffer.append(this.indent).append("if (value == ").append(nullValue).append(") {").append(lsep);
                } else if (javaType.equals("java.util.Calendar")) {
                    methodBuffer.append(defaultValue);
                    methodBuffer.append(this.indent).append("if (value.equals(").append("cal").append(")) {").append(lsep);
                } else if (javaType.equalsIgnoreCase("byte[]")) {
                    methodBuffer.append(defaultValue);
                    methodBuffer.append(this.indent).append("boolean isDefault = true;").append(lsep).append(lsep);
                    methodBuffer.append(this.indent).append("for(int i=0; i<value.length; i++) {").append(lsep);
                    pushIndent();
                    methodBuffer.append(this.indent).append("if(value[i] != bytes[i]){").append(lsep);
                    pushIndent();
                    methodBuffer.append(this.indent).append("isDefault = false;").append(lsep);
                    methodBuffer.append(this.indent).append("break;").append(lsep);
                    popIndent();
                    methodBuffer.append(this.indent).append("}").append(lsep);
                    popIndent();
                    methodBuffer.append(this.indent).append("}").append(lsep).append(lsep);
                    methodBuffer.append(this.indent).append("if (isDefault) {").append(lsep);
                } else {
                    methodBuffer.append(this.indent).append("if (value.equals(");
                    methodBuffer.append(defaultValue);
                    methodBuffer.append(")");
                    methodBuffer.append(")");
                    methodBuffer.append("{").append(lsep);
                }
            }

            pushIndent();
            methodBuffer.append(this.indent).append("unset").append(attrName).append("();").append(lsep);
            methodBuffer.append(this.indent).append("return;").append(lsep);
            popIndent();
            methodBuffer.append(this.indent).append("}").append(lsep);

            if (isCollection) {
                if (javaType.equals("java.util.List")) {
                    methodBuffer.append(this.indent).append("else if (value.isEmpty()) ").append("{").append(lsep);
                    pushIndent();
                    methodBuffer.append(this.indent).append("unset").append(attrName).append("();").append(lsep);
                    methodBuffer.append(this.indent).append("return;").append(lsep);
                    popIndent();
                    methodBuffer.append(this.indent).append("}").append(lsep);
                }
            }

            methodBuffer.append(this.indent);
            methodBuffer.append("else ").append("{").append(lsep);
            pushIndent();
        }
        methodBuffer.append(this.indent);
        methodBuffer.append("_");
//        methodBuffer.append(JaxbUtil.className(name));

        // handle primitives
        if (isPrimitive(javaType)) {
            methodBuffer.append(" = new ");
            methodBuffer.append(getWrapper(javaType)).append("(value);");
        } else {
            methodBuffer.append(" = value;");
        }
        methodBuffer.append(lsep);

        if (generateIsSet) {
            methodBuffer.append(this.indent);
            methodBuffer.append("isSet").append(attrName).append(" = true;").append(lsep);
            popIndent();
            methodBuffer.append(this.indent);
            methodBuffer.append("}").append(lsep);
        }
        popIndent();

        methodBuffer.append(this.indent).append("}").append(lsep2);
    }

    private void generateIsSetAndUnsetMethods(StringBuffer methodBuffer, String name, String javaType, ArrayList schemaTypes, String defaultValue, boolean isCollection) {
//        String attrName = JaxbUtil.className(name);
    	String attrName = "";
        methodBuffer.append(this.indent);
        methodBuffer.append("public boolean ");
        methodBuffer.append("isSet").append(attrName).append("()").append(lsep);
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("return ");
        methodBuffer.append("isSet").append(attrName).append(";").append(lsep);
        popIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("}").append(lsep2);

        //generate unset method
        methodBuffer.append(this.indent);
        methodBuffer.append("public void ");
        methodBuffer.append("unset").append(attrName).append("()").append(lsep);
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        if (javaType.equals("java.util.Calendar")) {
            methodBuffer.append(defaultValue);
            methodBuffer.append(this.indent).append("_").append(attrName).append(" = ").append("cal");
        } else if (javaType.equalsIgnoreCase("byte[]")) {
            methodBuffer.append(defaultValue);
            methodBuffer.append(this.indent).append("_").append(attrName).append(" = ").append("bytes");
        } else {
            methodBuffer.append(this.indent);
            methodBuffer.append("_").append(attrName).append(" = ");
            methodBuffer.append(defaultValue);

        }
        methodBuffer.append(";").append(lsep);

        methodBuffer.append(this.indent);
        methodBuffer.append("isSet").append(attrName).append(" = false").append(";").append(lsep);
        popIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("}").append(lsep2);
    }

    private void generateNilAttribute(StringBuffer attributeBuffer) {
        attributeBuffer.append(this.indent);
        attributeBuffer.append("private boolean _Nil;");
        attributeBuffer.append(lsep);
    }

    private void generateIsNilAndSetNilMethods(StringBuffer methodBuffer) {
        //generate isNil
        methodBuffer.append(this.indent);
        methodBuffer.append("public boolean isNil()").append(lsep);
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("return _Nil;").append(lsep);

        popIndent();
        methodBuffer.append(this.indent).append("}").append(lsep2);

        //generate setNil
        methodBuffer.append(this.indent);
        methodBuffer.append("public void setNil");
        methodBuffer.append("(boolean value)").append(lsep);
        methodBuffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        methodBuffer.append(this.indent);
        methodBuffer.append("_Nil = value;").append(lsep);
        popIndent();

        methodBuffer.append(this.indent).append("}").append(lsep2);
    }

    public void writeJavaFile(StringBuffer code, TopLinkJAXBBindingSchema schema) throws TopLinkJAXBGenerationException {
        String dirName = getSourceDirWithPackage(schema, true);
        String fileName = schema.getClassName() + IMPL + ".java";
        TopLinkJAXBGeneratorLog.log(getFullImplClassName(schema));
        writeFile(dirName, fileName, code);
    }

    public void writePropertiesFile(String outputDir) throws TopLinkJAXBGenerationException {
        StringBuffer buffer = new StringBuffer();

//        buffer.append(JaxbUtil.getPrefaceString());
        buffer.append("# Generated by Oracle JAXB Class Generator on ").append(new Date().toString()).append(lsep);
        buffer.append("javax.xml.bind.context.factory = org.eclipse.persistence.oxm.jaxb.JAXBContextFactory");

        writeFile(outputDir, "jaxb.properties", buffer);
    }

    public void writeObjectFactoryClass(Hashtable bindingSchemas, String outputDir) throws TopLinkJAXBGenerationException {
        StringBuffer buffer = new StringBuffer();

        boolean appendedHeader = false;

        Enumeration bindingSchemasEnum = bindingSchemas.elements();
        while (bindingSchemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)bindingSchemasEnum.nextElement();

            if (!appendedHeader) {
                // We need the package name to write the header, that's why this
                // code is in the while() statement
//                buffer.append(this.indent).append(JaxbUtil.getPrefaceString());

                if (!schema.getPackageName().equals("")) {
                    buffer.append("package ");
                    buffer.append(schema.getPackageName());
                    buffer.append(";").append(lsep2).append(lsep);
                }

                buffer.append("public class ObjectFactory").append(lsep);
                buffer.append(this.indent).append("{").append(lsep2);

                pushIndent();
                buffer.append(this.indent).append("private java.util.Hashtable props = null;").append(lsep2);

                buffer.append(this.indent).append("public ObjectFactory()").append(lsep);
                buffer.append(this.indent).append("{").append(lsep);
                buffer.append(this.indent).append("}").append(lsep2);

                appendedHeader = true;
            }

            generateCreateMethod(schema, buffer);

        }

        // Dynamic instance factory allocator
        buffer.append(this.indent).append("public Object newInstance(Class javaContentInterface) throws javax.xml.bind.JAXBException").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("Object newInstance = null;").append(lsep);
        buffer.append(this.indent).append("try").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("newInstance = javaContentInterface.newInstance();").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep);
        buffer.append(this.indent).append("catch (Exception e)").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("throw new javax.xml.bind.JAXBException(e.toString());").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep);
        buffer.append(this.indent).append("return newInstance;").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep2);

        // Property getter
        buffer.append(this.indent).append("public Object getProperty(String name)").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("if (props == null)").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("return null;").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep);
        buffer.append(this.indent).append("return props.get(name);").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep2);

        // Property setter
        buffer.append(this.indent).append("public void setProperty(String name, Object value) {").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("if (props == null)").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append("props = new java.util.Hashtable();").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep);
        buffer.append(this.indent).append("props.put(name, value);").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep2);

        buffer.append("}");

        writeFile(outputDir, "ObjectFactory.java", buffer);
    }

    private void generateCreateMethod(TopLinkJAXBBindingSchema schema, StringBuffer buffer) {
        String className = getFullClassName(schema);
        String implClassName = getFullImplClassName(schema);
        String methodSuffix = getFullClassName(schema, false, true);

        if (schema.getEnclosingClassName() != null) {
            implClassName = implClassName.replace('$', '.');
        }

        buffer.append(this.indent).append("public ").append(className).append(" create").append(methodSuffix).append("() throws javax.xml.bind.JAXBException").append(lsep);
        buffer.append(this.indent).append("{").append(lsep);
        pushIndent();
        buffer.append(this.indent).append(className).append(" elem = new ").append(implClassName).append("();").append(lsep);
        buffer.append(this.indent).append("return elem;").append(lsep);
        popIndent();
        buffer.append(this.indent).append("}").append(lsep2);

        if (schema.getSimpleTypeName() != null) {
            // Generate a create method for the simlpe-type case - Oracle Bug# 3461647
            buffer.append(this.indent).append("public ").append(className).append(" create").append(methodSuffix).append("(").append(schema.getSimpleTypeName()).append(" value) throws javax.xml.bind.JAXBException").append(lsep);
            buffer.append(this.indent).append("{").append(lsep);
            pushIndent();
            buffer.append(this.indent).append(className).append(" elem = new ").append(implClassName).append("();").append(lsep);
            if (schema.getSimpleTypeName().equals("java.util.List")) {
                buffer.append(this.indent).append("elem.getValue().addAll(value);").append(lsep);
            } else {
                buffer.append(this.indent).append("elem.setValue(value);").append(lsep);
            }
            buffer.append(this.indent).append("return elem;").append(lsep);
            popIndent();
            buffer.append(this.indent).append("}").append(lsep2);
        }
        Vector innerClasses = schema.getInnerInterfaces();
        if (innerClasses != null) {
            for (int i = 0; i < innerClasses.size(); i++) {
                TopLinkJAXBBindingSchema innerSchema = (TopLinkJAXBBindingSchema)innerClasses.elementAt(i);
                generateCreateMethod(innerSchema, buffer);
            }
        }
    }

    // ==============================================================================================================================================
    // UTILITY METHODS
    // ==============================================================================================================================================
    public void writeFile(String dir, String filename, StringBuffer content) throws TopLinkJAXBGenerationException {
        try {
            File directory = new File(dir);
            directory.mkdirs();
            File file = new File(dir, filename);
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);

            pw.print(content.toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            throw new TopLinkJAXBGenerationException(e);
        }
    }

    private String getFullClassName(TopLinkJAXBBindingSchema schema) {
        return getFullClassName(schema, false, false);
    }

    private String getFullClassName(TopLinkJAXBBindingSchema schema, boolean fullImpl, boolean methodName) {
        String packageName = "";
        if (!fullImpl || (this.implClassPackage == null) || this.implClassPackage.equals(schema.getPackageName())) {
            packageName = schema.getPackageName();
        } else {
            packageName = this.implClassPackage;
        }
        if (schema.getEnclosingClassName() == null) {
            String fullClassName = "";
            if (!methodName) {
                fullClassName += (packageName + ".");
            }
            fullClassName += schema.getClassName();
            if (fullImpl) {
                fullClassName += IMPL;
            }
            return fullClassName;
        }

        Iterator enclosingClasses = schema.getEnclosingClassNames().iterator();
        String fullClassName = "";
        if (!methodName) {
            fullClassName = packageName + ".";
        }
        while (enclosingClasses.hasNext()) {
            String next = (String)enclosingClasses.next();
            fullClassName += next;
            if (fullImpl) {
                fullClassName += ("Impl" + "$");
            } else if (!methodName) {
                fullClassName += ".";
            }
        }
        fullClassName += schema.getClassName();
        if (fullImpl) {
            fullClassName += "Impl";
        }
        return fullClassName;
    }

    private String getFullImplClassName(TopLinkJAXBBindingSchema schema) {
        return getFullClassName(schema, true, false);
    }

    private String getResourceDirWithPackage(TopLinkJAXBBindingSchema schema) {
        String dirName = this.resourceDir;
        if ((schema != null) && (schema.getPackageName() != null)) {
            dirName += (fsep + schema.getPackageName().replace('.', fsep.charAt(0)));
        }
        return dirName;
    }

    private String getSourceDirWithPackage(TopLinkJAXBBindingSchema schema) {
        return getSourceDirWithPackage(schema, false);
    }

    private String getSourceDirWithPackage(TopLinkJAXBBindingSchema schema, boolean impl) {
        String dirName = this.sourceDir;
        if ((schema != null) && (schema.getPackageName() != null)) {
            if (!impl || (this.implClassPackage == null) || this.implClassPackage.equals(schema.getPackageName())) {
                dirName += (fsep + schema.getPackageName().replace('.', fsep.charAt(0)));
            } else {
                dirName += (fsep + this.implClassPackage.replace('.', fsep.charAt(0)));
            }
        }
        return dirName;
    }

    private String getNullValue(String javaType, boolean isCollection) {
        if (!isCollection) {
            if ((javaType.equals("int")) || (javaType.equals("float")) || (javaType.equals("double")) || (javaType.equals("long")) || (javaType.equals("byte")) || (javaType.equals("short"))) {
                return "0";
            } else if (javaType.equals("boolean")) {
                return "false";
            }
        }
        return "null";
    }

    private String getUnsetValue(String javaType, ArrayList schemaTypes, String defaultValue, boolean isCollection) {
        if ((defaultValue != null) && (!defaultValue.equals(""))) {
            if (((javaType != null) && (javaType.equals("boolean"))) || (javaType.equals("java.lang.Boolean"))) {
                if (defaultValue == "0") {
                    defaultValue = "false";
                } else if (defaultValue == "1") {
                    defaultValue = "true";
                }
            } else if ((javaType.equals("javax.xml.namespace.QName")) && (defaultValue.indexOf(':') != -1)) {
                int index = defaultValue.indexOf(':');
                StringBuffer defaultBuffer = new StringBuffer();

                String localName = defaultValue.substring(index + 1);
                String prefix = defaultValue.substring(0, index);
                String ns = getNamespaceForPrefix(prefix);

                defaultBuffer.append("new ").append(javaType).append("(");
                if ((ns != null) && !ns.equals("")) {
                    defaultBuffer.append('"').append(ns).append('"');
                    defaultBuffer.append(", ");
                }
                defaultBuffer.append('"');
                defaultBuffer.append(localName);
                defaultBuffer.append('"');
                defaultBuffer.append(")");

                defaultValue = defaultBuffer.toString();
            } else if (javaType.equals("java.util.Calendar")) {
                StringBuffer defaultBuffer = new StringBuffer();

                Calendar calendar = null;
                if ((schemaTypes != null) && (schemaTypes.size() == 1)) {
                    String schemaType = (String)schemaTypes.get(0);
                    QName qname = getSimpleTypeQName(schemaType);
                    calendar = (Calendar)XMLConversionManager.getDefaultXMLManager().convertObject(defaultValue, Calendar.class, qname);
                } else {
                    calendar = (Calendar)XMLConversionManager.getDefaultXMLManager().convertObject(defaultValue, Calendar.class);
                }

                pushIndent();
                defaultBuffer.append(this.indent).append(javaType).append(" cal = ").append(javaType).append(".getInstance();").append(lsep);
                Calendar cal2 = Calendar.getInstance();
                defaultBuffer.append(this.indent).append("cal.clear();").append(lsep);

                if (calendar.isSet(Calendar.YEAR)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.YEAR,").append(calendar.get(Calendar.YEAR)).append(");").append(lsep);
                }
                if (calendar.isSet(Calendar.MONTH)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.MONTH,").append(calendar.get(Calendar.MONTH)).append(");").append(lsep);
                }
                if (calendar.isSet(Calendar.DATE)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.DATE,").append(calendar.get(Calendar.DATE)).append(");").append(lsep);
                }
                if (calendar.isSet(Calendar.HOUR)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.HOUR,").append(calendar.get(Calendar.HOUR)).append(");").append(lsep);
                }
                if (calendar.isSet(Calendar.MINUTE)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.MINUTE,").append(calendar.get(Calendar.MINUTE)).append(");").append(lsep);
                }
                if (calendar.isSet(Calendar.SECOND)) {
                    defaultBuffer.append(this.indent).append("cal.set(java.util.Calendar.SECOND,").append(calendar.get(Calendar.SECOND)).append(");").append(lsep);
                }

                defaultValue = defaultBuffer.toString();
                popIndent();
            } else if (javaType.equalsIgnoreCase("byte[]")) {
                StringBuffer defaultBuffer = new StringBuffer();

                defaultBuffer.append(this.indent);
                defaultBuffer.append(this.indent);
                defaultBuffer.append(javaType).append(" bytes = (").append(javaType).append(")org.eclipse.persistence.internal.ox.XMLConversionManager.getDefaultXMLManager().convertObject(").append('"').append(defaultValue).append('"').append(", ").append(javaType).append(".class");
                if (schemaTypes.size() == 1) {
                    String schemaType = (String)schemaTypes.get(0);
                    QName qname = getSimpleTypeQName(schemaType);
                    defaultBuffer.append(", new javax.xml.namespace.QName(").append('"').append(qname.getNamespaceURI()).append('"').append(", ").append('"').append(qname.getLocalPart()).append('"').append(")");
                }
                defaultBuffer.append(");").append(lsep);

                defaultValue = defaultBuffer.toString();
            } else {
                StringBuffer defaultBuffer = new StringBuffer();
                defaultBuffer.append("new ").append(javaType).append("(");
                defaultBuffer.append('"');
                defaultBuffer.append(defaultValue);
                defaultBuffer.append('"');
                defaultBuffer.append(")");
                defaultValue = defaultBuffer.toString();
            }

            return defaultValue;
        } else {
            return getNullValue(javaType, isCollection);
        }
    }

    private boolean isPrimitive(String type) {
        if (type == null) {
            return false;
        }
        if ((type.equals("int")) || (type.equals("boolean")) || (type.equals("float")) || (type.equals("double")) || (type.equals("long")) || (type.equals("byte")) || (type.equals("short"))) {
            return true;
        }
        return false;

    }

    /**
    * This conveinence method returns the wrapper class name for a given primitive.  The isPrimitive
    * method should always be called first to make sure that 'classType' is a primitive value.
    *
    * @param classType
    * @return wrapper class as a string, null if classType is not a primitive
    */
    private String getWrapper(String classType) {
        if (classType.equals("int")) {
            return "java.lang.Integer";
        } else if (classType.equals("boolean")) {
            return "java.lang.Boolean";
        } else if (classType.equals("char")) {
            return "java.lang.Character";
        } else if (classType.equals("short")) {
            return "java.lang.Short";
        } else if (classType.equals("byte")) {
            return "java.lang.Byte";
        } else if (classType.equals("float")) {
            return "java.lang.Float";
        } else if (classType.equals("double")) {
            return "java.lang.Double";
        } else if (classType.equals("long")) {
            return "java.lang.Long";
        }

        // always test for isPrimitive first, so should never get here
        return null;
    }

    /**
    * Return the default value for the supplied primitive.
    *
    * @param javaType the primitive value
    * @return "false" for boolean, "0" otherwise
    */
    private String getDefaultValueForPrimitive(String javaType) {
        if (javaType.equals("boolean")) {
            return "false";
        }
        return "0";
    }

    private void pushIndent() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < INDENT_TAB; i++) {
            buf.append(" ");
        }
        this.indent = this.indent + buf.toString();
    }

    private void popIndent() {
        StringBuffer buf = new StringBuffer();
        int size = this.indent.length() - INDENT_TAB;
        for (int i = 0; i < size; i++) {
            buf.append(" ");
        }
        this.indent = buf.toString();
    }

    private TopLinkJAXBBindingSchema findParentSchema(TopLinkJAXBBindingSchema schema) {
        Enumeration bindingSchemasEnum = this.bindingSchemas.elements();
        while (bindingSchemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema next = (TopLinkJAXBBindingSchema)bindingSchemasEnum.nextElement();
            if (isSuperSchema(next, schema)) {
                return next;
            }
            Vector innerInterfaces = next.getInnerInterfaces();
            if (innerInterfaces != null) {
                for (int i = 0; i < innerInterfaces.size(); i++) {
                    TopLinkJAXBBindingSchema inner = (TopLinkJAXBBindingSchema)innerInterfaces.elementAt(i);
                    if (isSuperSchema(inner, schema)) {
                        return inner;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSuperSchema(TopLinkJAXBBindingSchema parent, TopLinkJAXBBindingSchema child) {
        String extendsNode = child.getExtendsNode();
        String fullParentName = "";
        if ((parent.getEnclosingClassName() != null) && !parent.getEnclosingClassName().equals("")) {
            fullParentName = parent.getEnclosingClassName() + ".";
        } else {
            fullParentName += (parent.getPackageName() + ".");
        }
        fullParentName += parent.getClassName();
        return fullParentName.equals(extendsNode);
    }

    /**
    * A convenience method for converting a string to a QName
    *
    * @param simpleTypeName
    * @return
    */
    private QName getSimpleTypeQName(String simpleTypeName) {
        int prefixIndex = simpleTypeName.indexOf(":");

        // if non-qualified simply return a new QName with localName = simpleTypeName
        if (prefixIndex == -1) {
            return new QName(simpleTypeName);
        }

        String localName = simpleTypeName.substring(prefixIndex + 1);
        String prefix = simpleTypeName.substring(0, prefixIndex);
        String ns = getNamespaceForPrefix(prefix);

        if ((ns != null) && !ns.equals("")) {
            return new QName(ns, localName, prefix);
        }

        return new QName(localName);
    }

    private String getNamespaceForPrefix(String prefix) {
        String ns = this.mwSchema.namespaceUrlForPrefix(prefix);

        // prefix is not user-defined, so look in the the built-in namespace list
        if ((ns == null) || ns.equals("")) {
            boolean found = false;

            // if a namespace wasn't found, look for one in the list of 'built in' namespaces 
            for (Iterator stream = this.mwSchema.builtInNamespaces(); stream.hasNext();) {
                MWNamespace namespace = (MWNamespace)stream.next();

                if (prefix.equals(namespace.getNamespacePrefix())) {
                    ns = namespace.getNamespaceUrl();
                    found = true;
                }
            }

            // orajaxb always prefixes the schema URL with "xs", regardless of what is actually in the schema
            if (!found && prefix.equals("xs")) {
                ns = XMLConstants.SCHEMA_URL;
            }
        }
        return ns;
    }
    // ===========================================================================
}