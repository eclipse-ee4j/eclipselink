package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;
/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle, Hans Harz, Andrew Rustleund. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     Hans Harz, Andrew Rustleund - Bug 324862 - IndexOutOfBoundsException in 
 *           DatabaseSessionImpl.initializeDescriptors because @MapKey Annotation is not found.
 ******************************************************************************/  
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
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
 * A metadata factory that uses ASM technology and no reflection whatsoever
 * to process the metadata model.
 * 
 * @author James Sutherland
 * @since EclipseLink 1.2 
 */
public class MetadataAsmFactory extends MetadataFactory {
    /** Set of primitive type codes. */
    public static final String PRIMITIVES = "VJIBZCSFD";
    /** Set of desc token characters. */
    public static final String TOKENS = "()<>;";
    /** Backdoor to allow mapping of JDK classes. */
    public static boolean ALLOW_JDK = false;

    /**
     * INTERNAL:
     */
    public MetadataAsmFactory(MetadataLogger logger, ClassLoader loader) {
        super(logger, loader);
        
        addMetadataClass("I", new MetadataClass(this, int.class));
        addMetadataClass("J", new MetadataClass(this, long.class));
        addMetadataClass("S", new MetadataClass(this, short.class));
        addMetadataClass("Z", new MetadataClass(this, boolean.class));
        addMetadataClass("F", new MetadataClass(this, float.class));
        addMetadataClass("D", new MetadataClass(this, double.class));
        addMetadataClass("C", new MetadataClass(this, char.class));
        addMetadataClass("B", new MetadataClass(this, byte.class));
    }

    /**
     * Build the class metadata for the class name using ASM to read the class byte
     * codes.
     */
    protected void buildClassMetadata(String className) {
        ClassMetadataVisitor visitor = new ClassMetadataVisitor();
        InputStream stream = null;
        try {
            String resourceString = className.replace('.', '/') + ".class";
            stream = m_loader.getResourceAsStream(resourceString);
            if (stream == null){
                stream = m_loader.getResourceAsStream(JPAInitializer.BUNDLE_RESOURCE_PREFIX + resourceString);
            }
            ClassReader reader = new ClassReader(stream);
            Attribute[] attributes = new Attribute[] { new RuntimeVisibleAnnotations(), new RuntimeVisibleParameterAnnotations(), new SignatureAttribute() };
            reader.accept(visitor, attributes, false);
        } catch (Exception exception) {
            // Some basic types can't be found, so can just be registered 
            // (i.e. arrays). Also, VIRTUAL classes may also not exist,
            // therefore, tag the MetadataClass as loadable false. This will be
            // used to determine if a class will be dynamically created or not.
            MetadataClass metadataClass = new MetadataClass(this, className);
            metadataClass.setIsAccessible(false);
            addMetadataClass(metadataClass);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ignore) {
                // Ignore.
            }
        }
    }
    
    /**
     * Return the class metadata for the class name.
     */
    public MetadataClass getMetadataClass(String className) {
        if (className == null) {
            return null;
        }
        
        if (! metadataClassExists(className)) {
            buildClassMetadata(className);
        }
        
        return getMetadataClasses().get(className);
    }
    
    /**
     * INTERNAL:
     * This method resolves generic types based on the ASM class metadata.
     * Unless every other factory (e.g. APT mirror factory) respects the generic
     * format as built from ASM this method will not work since it is very tied
     * to it.
     */
    public void resolveGenericTypes(MetadataClass child, List<String> genericTypes, MetadataClass parent, MetadataDescriptor descriptor) {        
        // If we have a generic parent we need to grab our generic types
        // that may be used (and therefore need to be resolved) to map
        // accessors correctly.
        if (genericTypes != null) {
            // The generic types provided map to its parents generic types. The 
            // generics also include the superclass, and interfaces. The parent 
            // generics include the type and ":" and class. 
            
            List<String> parentGenericTypes = parent.getGenericType();
            if (parentGenericTypes != null) {
                List genericParentTemp = new ArrayList(genericTypes);
                genericParentTemp.removeAll(child.getInterfaces());
                
                int size = genericParentTemp.size();
                int parentIndex = 0;
                
                for (int index = genericTypes.indexOf(parent.getName()) + 1; index < size; index++) {
                    String actualTypeArgument = genericTypes.get(index);
                    // Ignore extra types on the end of the child, such as interface generics.
                    if (parentIndex >= parentGenericTypes.size()) {
                        break;
                    }
                    String variable = parentGenericTypes.get(parentIndex);
                    parentIndex = parentIndex + 3;
                    
                    // We are building bottom up and need to link up any 
                    // TypeVariables with the actual class from the originating 
                    // entity.
                    if (actualTypeArgument.length() == 1) {
                        index++;
                        actualTypeArgument = genericTypes.get(index);
                        descriptor.addGenericType(variable, descriptor.getGenericType(actualTypeArgument));
                    } else {
                        descriptor.addGenericType(variable, actualTypeArgument);
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected String toClassName(String classDescription) {
        if (classDescription == null) {
            return "void";
        }
        return classDescription.replace('/', '.');
    }
    
    /**
     * Walk the class byte codes and collect the class info.
     */
    public class ClassMetadataVisitor implements ClassVisitor {
        MetadataClass classMetadata;

        ClassMetadataVisitor() {}
        
        public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
            String className = toClassName(name);
            classMetadata = new MetadataClass(MetadataAsmFactory.this, className);
            addMetadataClass(classMetadata);
            classMetadata.setName(className);
            classMetadata.setSuperclassName(toClassName(superName));
            classMetadata.setModifiers(access);
            if ((!ALLOW_JDK) && (className.startsWith("java.") || className.startsWith("javax."))) {
                classMetadata.setIsJDK(true);
            }
            
            for (String interfaceName : interfaces) {
                classMetadata.addInterface(toClassName(interfaceName));
            }
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            // Reference to the inner class, the inner class with be processed on its own.
        }

        public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
            if (classMetadata.isJDK()) {
                return;
            }
            MetadataField field = new MetadataField(classMetadata);
            field.setName(name);
            field.setAttributeName(name);
            field.setGenericType(getGenericType(attrs));
            field.setType(processDescription(desc, false).get(0));
            field.setModifiers(access);
            addAnnotations(attrs, field.getAnnotations());
            classMetadata.getFields().put(name, field);
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
            if (classMetadata.isJDK()) {
                return null;
            }
            MetadataMethod method = null;
            // Ignore generated constructors.
            if (name.indexOf("init>") != -1) {
                return null;
            }
            List<String> argumentNames = processDescription(desc, false);                
            method = new MetadataMethod(MetadataAsmFactory.this, classMetadata);
            method.setName(name);
            method.setAttributeName(Helper.getAttributeNameFromMethodName(name));
            method.setModifiers(access);
            method.setGenericType(getGenericType(attrs));
            method.setReturnType(argumentNames.get(argumentNames.size() - 1));
            argumentNames.remove(argumentNames.size() - 1);
            method.setParameters(argumentNames);
            addAnnotations(attrs, method.getAnnotations());
            // Handle methods with the same name.
            MetadataMethod existing = classMetadata.getMethods().get(name);
            if (existing == null) {
                classMetadata.getMethods().put(name, method);
            } else {
                while (existing.getNext() != null) {
                    existing = existing.getNext();
                }
                existing.setNext(method);
            }
            return null;
        }

        public void visitAttribute(Attribute attr) {
            if (classMetadata.isJDK()) {
                return;
            }
            if (attr instanceof SignatureAttribute) {
                // Process generic signature.
                classMetadata.setGenericType(getGenericType(attr));
            } else {
                // Process annotations.
                addAnnotations(attr, classMetadata.getAnnotations());
            }
        }
        
        /**
         * If the attribute is an annotations attribute, add all annotations attached to it.
         */
        public void addAnnotations(Attribute attr, Map<String,MetadataAnnotation> annotations) {
            Attribute toUse = attr;
            while (toUse != null) {
                if (toUse instanceof RuntimeVisibleAnnotations) {
                    addAnnotationsHelper(annotations,(RuntimeVisibleAnnotations) toUse);
                }
                toUse = toUse.next;
            }
        }

        private void addAnnotationsHelper(Map<String, MetadataAnnotation> annotations, RuntimeVisibleAnnotations visibleAnnotations) {
            for (Iterator iterator = visibleAnnotations.annotations.iterator();iterator.hasNext();) {
                Annotation visibleAnnotation = (Annotation) iterator.next();
                // Only add annotations that we care about.
                if ((visibleAnnotation.type.indexOf("javax/persistence") != -1) || (visibleAnnotation.type.indexOf("org/eclipse/persistence") != -1)) {
                    MetadataAnnotation annotation =buildAnnotation(visibleAnnotation);
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

        public void visitEnd() {}
    }
}

