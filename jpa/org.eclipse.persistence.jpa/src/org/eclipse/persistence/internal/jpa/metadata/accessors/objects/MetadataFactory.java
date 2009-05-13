/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.libraries.asm.Attribute;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.attrs.Annotation;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleParameterAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;

/**
 * INTERNAL:
 * Common helper methods for the metadata processing.
 * Extracts class information without using reflection, this
 * allows reflection class loader issues to be avoided. This uses ASM, and does
 * not use Java reflection in any form.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.1 
 */
public class MetadataFactory {
    /** Set of primitive type codes. */
    public static final String PRIMITIVES = "VJIBZCSFD";
    /** Set of desc token characters. */
    public static final String TOKENS = "()<>;";
    
    /** Stores all metadata for classes. */
    // TODO: probably can't be static as can have multiple deployments.
    protected static Map<String, MetadataClass> metadata;
    
    public static MetadataLogger logger;
    public static ClassLoader loader;
    
    public static void main(String[] args) {
        loader = MetadataFactory.class.getClassLoader();
        MetadataFactory.getClassMetadata("org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass");
    }
    
    static {
        clear();
    }

    public static void clear() {
        metadata = new HashMap<String, MetadataClass>();
        metadata.put("void", new MetadataClass(void.class));
        metadata.put("", new MetadataClass(void.class));
        metadata.put(null, new MetadataClass(void.class));
        metadata.put("int", new MetadataClass(int.class));
        metadata.put("long", new MetadataClass(long.class));
        metadata.put("short", new MetadataClass(short.class));
        metadata.put("boolean", new MetadataClass(boolean.class));
        metadata.put("float", new MetadataClass(float.class));
        metadata.put("double", new MetadataClass(double.class));
        metadata.put("char", new MetadataClass(char.class));
        metadata.put("byte", new MetadataClass(byte.class));
        metadata.put("V", new MetadataClass(void.class));
        metadata.put("I", new MetadataClass(int.class));
        metadata.put("J", new MetadataClass(long.class));
        metadata.put("S", new MetadataClass(short.class));
        metadata.put("Z", new MetadataClass(boolean.class));
        metadata.put("F", new MetadataClass(float.class));
        metadata.put("D", new MetadataClass(double.class));
        metadata.put("C", new MetadataClass(char.class));
        metadata.put("B", new MetadataClass(byte.class));
    }

    public static String toClassName(String classDescription) {
        if (classDescription == null) {
            return "void";
        }
        return classDescription.replace('/', '.');
    }
    
    /**
     * Walk the class byte codes and collect the class info.
     */
    public static class ClassMetadataVisitor implements ClassVisitor {
        
        MetadataClass classMetadata;

        ClassMetadataVisitor() {
        }
        
        public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
            this.classMetadata = new MetadataClass(toClassName(name));
            metadata.put(toClassName(name), this.classMetadata);
            this.classMetadata.setName(toClassName(name));
            this.classMetadata.setSuperclassName(toClassName(superName));
            this.classMetadata.setModifiers(access);
            
            List<String> interfacesNames = new ArrayList<String>();
            for (String interfaceName : interfaces) {
                interfacesNames.add(toClassName(interfaceName));
            }
            this.classMetadata.setInterfaces(interfacesNames);
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            // Reference to the inner class, the inner class with be processed on its own.
        }

        public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
            MetadataField field = new MetadataField(logger);
            field.setName(name);
            field.setAttributeName(name);
            field.setGenericType(getGenericType(attrs));
            field.setType(processDescription(desc, false).get(0));
            field.setModifiers(access);
            addAnnotations(attrs, field.getAnnotations());
            this.classMetadata.getFields().put(name, field);
        }
        
        /**
         * Parse and return the generic type from the signature if available.
         * i.e. "Ljava.util.Map<Ljava.lang.String;Ljava.lang.Object>"=>[java.util.Map,java.lang.String,java.lang.Object]
         */
        public List<String> getGenericType(Attribute attrs) {
            if (attrs == null) {
                return null;
            }
            if (attrs instanceof SignatureAttribute) {
                return processDescription(((SignatureAttribute)attrs).signature, true);
            } else {
                return getGenericType(attrs.next);
            }
        }
        
        /**
         * Return the Java type name for the primitive code.
         */
        public String getPrimitiveName(char primitive) {
            if (primitive == 'V') {
                return "void";
            } else if (primitive == 'I') {
                return "int";
            } else if (primitive == 'Z') {
                return "boolean";
            } else if (primitive == 'J') {
                return "long";
            } else if (primitive == 'F') {
                return "float";
            } else if (primitive == 'D') {
                return "double";
            } else if (primitive == 'B') {
                return "byte";
            } else if (primitive == 'C') {
                return "char";
            }  else if (primitive == 'S') {
                return "short";
            } else {
                return new String(new char[]{primitive});
            }
        }

        /**
         * Process the byte-code argument description
         * and return the array of Java class names.
         * i.e. "(Lorg/foo/Bar;Z)Ljava/lang/Boolean;"=>[org.foo.Bar,boolean,java.lang.Boolean]
         */
        public List<String> processDescription(String desc, boolean isGeneric) {
           List<String> arguments = new ArrayList<String>();
           int index = 0;
           while (index < desc.length()) {
               char next = desc.charAt(index);
               if (TOKENS.indexOf(next) == -1) {
                   if (next == 'L') {
                       index++;
                       int start = index;
                       next = desc.charAt(index);
                       while (TOKENS.indexOf(next) == -1) {
                           index++;
                           next = desc.charAt(index);
                       }
                       arguments.add(toClassName(desc.substring(start, index)));
                   } else if (!isGeneric && (PRIMITIVES.indexOf(next) != -1)) {
                       // Primitives.
                       arguments.add(getPrimitiveName(next));
                   } else if (next == '[') {
                       // Arrays.
                       int start = index;
                       index++;
                       next = desc.charAt(index);
                       // Nested arrays.
                       while (next == '[') {
                           index++;
                           next = desc.charAt(index);
                       }
                       if (PRIMITIVES.indexOf(next) == -1) {
                           while (next != ';') {
                               index++;
                               next = desc.charAt(index);
                           }
                           arguments.add(toClassName(desc.substring(start, index + 1)));
                       } else {
                           arguments.add(desc.substring(start, index + 1));
                       }
                   } else {
                       // Is a generic type variable.
                       arguments.add(new String(new char[]{next}));
                   }
               }
               index++;
           }
           return arguments;
           
        }
        
        public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
            MetadataMethod method = null;
            // Ignore generated constructors.
            if (name.indexOf("init>") != -1) {
                return null;
            }
            List<String> argumentNames = processDescription(desc, false);                
            method = new MetadataMethod(this.classMetadata, logger);
            method.setName(name);
            method.setAttributeName(method.getAttributeNameFromMethodName(name));
            method.setModifiers(access);
            method.setGenericType(getGenericType(attrs));
            method.setReturnType(argumentNames.get(argumentNames.size() - 1));
            argumentNames.remove(argumentNames.size() - 1);
            method.setParameters(argumentNames);
            addAnnotations(attrs, method.getAnnotations());
            // Handle methods with the same name.
            MetadataMethod existing = this.classMetadata.getMethods().get(name);
            if (existing == null) {
                this.classMetadata.getMethods().put(name, method);
            } else {
                while (existing.getNext() != null) {
                    existing = existing.getNext();
                }
                existing.setNext(method);
            }
            return null;
        }

        public void visitAttribute(Attribute attr) {
            if (attr instanceof SignatureAttribute) {
                // Process generic signature.
                this.classMetadata.setGenericType(getGenericType(attr));
            } else {
                // Process annotations.
                addAnnotations(attr, this.classMetadata.getAnnotations());
            }
        }

        /**
         * If the attribute is an annotations attribute, add all annotations attached to it.
         */
        public void addAnnotations(Attribute attr, Map<String, MetadataAnnotation> annotations) {
            if (!(attr instanceof RuntimeVisibleAnnotations)) {
                return;
            }
            RuntimeVisibleAnnotations visibleAnnotations = (RuntimeVisibleAnnotations) attr;
            for (Iterator iterator = visibleAnnotations.annotations.iterator(); iterator.hasNext(); ) {
                Annotation visibleAnnotation = (Annotation)iterator.next();
                // Only add annotations that we care about.
                if ((visibleAnnotation.type.indexOf("javax/persistence") != -1)
                        || (visibleAnnotation.type.indexOf("org/eclipse/persistence") != -1)) {
                    MetadataAnnotation annotation = buildAnnotation(visibleAnnotation);
                    annotations.put(annotation.getName(), annotation);
                }
            }
        }

        /**
         * Build the metadata annotation from the asm values.
         */
        public MetadataAnnotation buildAnnotation(Annotation visibleAnnotation) {
            MetadataAnnotation annotation = new MetadataAnnotation();
            annotation.setName(processDescription(visibleAnnotation.type, false).get(0));
            for (Iterator iterator = visibleAnnotation.elementValues.iterator(); iterator.hasNext(); ) {
                Object[] attribute = (Object[])iterator.next();
                String attributeName = (String)attribute[0];
                Object attributeValue = buildAnnotationValue(attribute[1]);
                annotation.getAttributes().put(attributeName, attributeValue);
            }
            return annotation;
        }

        /**
         * Build the metadata annotation value from the asm values.
         */
        public Object buildAnnotationValue(Object value) {            
            if (value instanceof Annotation) {
                return buildAnnotation((Annotation)value);
            } else if (value instanceof Object[]) {
                Object[] values = (Object[])value;
                for (int index = 0; index < values.length; index++) {
                    values[index] = buildAnnotationValue(values[index]);
                }
                return values;
            } else if (value instanceof Type) {
                return ((Type)value).getClassName();
            } else if (value instanceof Annotation.EnumConstValue) {
                return ((Annotation.EnumConstValue)value).constName;
            } else {
                return value;
            }
        }

        public void visitEnd() {
        }
    }

    /**
     * Return the class metadata for the class name.
     */
    public static MetadataClass getClassMetadata(String className) {
        if (className == null) {
            return null;
        }
        MetadataClass metadataClass = metadata.get(className);
        if (metadataClass == null) {
            buildClassMetadata(className);
            metadataClass = metadata.get(className);
        }
        return metadataClass;
    }
    
    /**
     * Build the class metadata for the class name using ASM to read the class byte
     * codes.
     */
    public static void buildClassMetadata(String className) {
        try {
            ClassMetadataVisitor visitor = new ClassMetadataVisitor();
            try {
                ClassReader reader = new ClassReader(loader.getResourceAsStream(className.replace('.', '/') + ".class"));
                Attribute[] attributes = new Attribute[] { new RuntimeVisibleAnnotations(), new RuntimeVisibleParameterAnnotations(), new SignatureAttribute() };
                reader.accept(visitor, attributes, false);
            } catch (IOException exception) {
                // Some basic types can't be found, so can just be registered (i.e. arrays).
                metadata.put(className, new MetadataClass(className));
            }
        } catch (Exception temp) {
            throw new Error(temp);
        }
    }
}
