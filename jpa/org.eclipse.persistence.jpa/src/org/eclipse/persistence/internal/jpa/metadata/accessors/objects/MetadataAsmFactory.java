/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle, Hans Harz, Andrew Rustleund. All rights reserved.
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
 *     04/21/2011-2.3 dclarke: Upgraded to support ASM 3.3.1
 *     08/10/2011-2.3 Lloyd Fernandes : Bug 336133 - Validation error during processing on parameterized generic OneToMany Entity relationship from MappedSuperclass
 *     10/05/2012-2.4.1 Guy Pelletier 
 *       - 373092: Exceptions using generics, embedded key and entity inheritance
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.Attribute;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.commons.EmptyVisitor;

/**
 * INTERNAL: A metadata factory that uses ASM technology and no reflection
 * whatsoever to process the metadata model.
 * 
 * @author James Sutherland
 * @since EclipseLink 1.2
 */
public class MetadataAsmFactory extends MetadataFactory {
    /** Set of primitive type codes. */
    public static final String PRIMITIVES = "VJIBZCSFD";
    /** Set of desc token characters. */
    public static final String TOKENS = "()<>;";

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
     * Build the class metadata for the class name using ASM to read the class
     * byte codes.
     */
    protected void buildClassMetadata(MetadataClass metadataClass, String className, boolean isLazy) {
        ClassMetadataVisitor visitor = new ClassMetadataVisitor(metadataClass, isLazy);
        InputStream stream = null;
        try {
            String resourceString = className.replace('.', '/') + ".class";
            stream = m_loader.getResourceAsStream(resourceString);

            ClassReader reader = new ClassReader(stream);
            Attribute[] attributes = new Attribute[0];
            reader.accept(visitor, attributes, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (Exception exception) {
            // Some basic types can't be found, so can just be registered
            // (i.e. arrays). Also, VIRTUAL classes may also not exist,
            // therefore, tag the MetadataClass as loadable false. This will be
            // used to determine if a class will be dynamically created or not.
            metadataClass = new MetadataClass(this, className, false);
            // If the class is a JDK class, then maybe there is a class loader issues,
            // since it is a JDK class, just use reflection.
            if ((className.length() > 5) && className.substring(0, 5).equals("java.")) {
                try {
                    Class reflectClass = Class.forName(className);
                    if (reflectClass.getSuperclass() != null) {
                        metadataClass.setSuperclassName(reflectClass.getSuperclass().getName());
                    }
                    for (Class reflectInterface : reflectClass.getInterfaces()) {
                        metadataClass.addInterface(reflectInterface.getName());
                    }
                } catch (Exception failed) {
                    metadataClass.setIsAccessible(false);                    
                }
            } else {
                metadataClass.setIsAccessible(false);                
            }
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
        return getMetadataClass(className, false);
    }

    /**
     * Return the class metadata for the class name.
     */
    public MetadataClass getMetadataClass(String className, boolean isLazy) {
        if (className == null) {
            return null;
        }

        MetadataClass metaClass = m_metadataClasses.get(className);
        if ((metaClass == null) || (!isLazy && metaClass.isLazy())) {
            if (metaClass != null) {
                metaClass.setIsLazy(false);
            }
            buildClassMetadata(metaClass, className, isLazy);
            metaClass = m_metadataClasses.get(className);
        }

        return metaClass;
    }

    /**
     * INTERNAL: This method resolves generic types based on the ASM class
     * metadata. Unless every other factory (e.g. APT mirror factory) respects
     * the generic format as built from ASM this method will not work since it
     * is very tied to it.
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
                    // Ignore extra types on the end of the child, such as
                    // interface generics.
                    if (parentIndex >= parentGenericTypes.size()) {
                        break;
                    }
                    String variable = parentGenericTypes.get(parentIndex);
                    
                    // if we get as far as the superclass name in the parent generic type list,
                    // there is nothing more to process.  We have processed all the generics in the type definition
                    if (variable.equals(parent.getSuperclassName())){
                        break;
                    }
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
     * Walk the class byte codes and collect the class info.
     */
    public class ClassMetadataVisitor implements ClassVisitor {

        private boolean isLazy;
        private boolean processedMemeber;
        private MetadataClass classMetadata;

        ClassMetadataVisitor(MetadataClass metadataClass, boolean isLazy) {
            this.isLazy = isLazy;
            this.classMetadata = metadataClass;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            String className = toClassName(name);
            if ((this.classMetadata == null) || !this.classMetadata.getName().equals(className)) {
                this.classMetadata = new MetadataClass(MetadataAsmFactory.this, className, isLazy);
                addMetadataClass(this.classMetadata);
            }
            this.classMetadata.setName(className);
            this.classMetadata.setSuperclassName(toClassName(superName));
            this.classMetadata.setModifiers(access);
            this.classMetadata.setGenericType(processDescription(signature, true));

            for (String interfaceName : interfaces) {
                this.classMetadata.addInterface(toClassName(interfaceName));
            }
        }

        /**
         * Reference to the inner class, the inner class must be processed
         * independently
         */
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            this.processedMemeber = true;
            if (this.classMetadata.isLazy()) {
                return null;
            }
            return new MetadataFieldVisitor(this.classMetadata, access, name, desc, signature, value);
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            this.processedMemeber = true;
            if (this.classMetadata.isLazy() || name.indexOf("init>") != -1) {
                return null;
            }
            return new MetadataMethodVisitor(this.classMetadata, access, name, signature, desc, exceptions);
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.startsWith("Ljavax/persistence") || desc.startsWith("Lorg/eclipse/persistence")) {
                // If an annotation is found, parse the whole class.
                if (!this.processedMemeber && this.classMetadata.isLazy()) {
                    this.classMetadata.setIsLazy(false);
                }
                return new MetadataAnnotationVisitor(this.classMetadata, desc);
            }
            return null;
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitEnd() {
        }

        public void visitSource(String source, String debug) {
        }

        public void visitOuterClass(String owner, String name, String desc) {
        }
    }

    /**
     * {@link AnnotationVisitor} used to process class, field , and method
     * annotations populating a {@link MetadataAnnotation} and its nested state.
     * 
     * @see MetadataAnnotationArrayVisitor for population of array attributes
     */
    class MetadataAnnotationVisitor implements AnnotationVisitor {

        /**
         * Element the annotation is being applied to. If this is null the
         * {@link MetadataAnnotation} being constructed is a nested annotation
         * and is already referenced from its parent.
         */
        private MetadataAnnotatedElement element;

        /**
         * {@link MetadataAnnotation} being populated
         */
        private MetadataAnnotation annotation;

        MetadataAnnotationVisitor(MetadataAnnotatedElement element, String name) {
            this.element = element;
            this.annotation = new MetadataAnnotation();
            this.annotation.setName(processDescription(name, false).get(0));
        }

        public MetadataAnnotationVisitor(MetadataAnnotation annotation) {
            this.annotation = annotation;
        }

        public void visit(String name, Object value) {
            this.annotation.addAttribute(name, annotationValue(null, value));
        }

        public void visitEnum(String name, String desc, String value) {
            this.annotation.addAttribute(name, annotationValue(desc, value));
        }

        public AnnotationVisitor visitAnnotation(String name, String desc) {
            MetadataAnnotation mda = new MetadataAnnotation();
            mda.setName(processDescription(desc, false).get(0));
            this.annotation.addAttribute(name, mda);
            return new MetadataAnnotationVisitor(mda);
        }

        public AnnotationVisitor visitArray(String name) {
            return new MetadataAnnotationArrayVisitor(this.annotation, name);
        }

        public void visitEnd() {
            if (this.element != null) {
                this.element.addAnnotation(this.annotation);
            }
        }
    }

    /**
     * Specialized visitor to handle the population of arrays of annotation
     * values.
     */
    class MetadataAnnotationArrayVisitor implements AnnotationVisitor {

        private MetadataAnnotation annotation;

        private String attributeName;

        private List<Object> values;

        public MetadataAnnotationArrayVisitor(MetadataAnnotation annotation, String name) {
            this.annotation = annotation;
            this.attributeName = name;
            this.values = new ArrayList<Object>();
        }

        public void visit(String name, Object value) {
            this.values.add(annotationValue(null, value));
        }

        public void visitEnum(String name, String desc, String value) {
            this.values.add(annotationValue(desc, value));
        }

        public AnnotationVisitor visitAnnotation(String name, String desc) {
            MetadataAnnotation mda = new MetadataAnnotation();
            mda.setName(processDescription(desc, false).get(0));
            this.values.add(mda);
            return new MetadataAnnotationVisitor(mda);
        }

        public AnnotationVisitor visitArray(String name) {
            // Ignore nested array case?
            return null;
        }

        public void visitEnd() {
            this.annotation.addAttribute(this.attributeName, this.values.toArray());
        }
    }

    /**
     * Factory for the creation of {@link MetadataField} handling basic type,
     * generics, and annotations.
     */
    class MetadataFieldVisitor implements FieldVisitor {

        private MetadataField field;

        public MetadataFieldVisitor(MetadataClass classMetadata, int access, String name, String desc, String signature, Object value) {
            this.field = new MetadataField(classMetadata);
            this.field.setModifiers(access);
            this.field.setName(name);
            this.field.setAttributeName(name);
            this.field.setGenericType(processDescription(signature, true));
            this.field.setType(processDescription(desc, false).get(0));
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.startsWith("Ljavax/persistence") || desc.startsWith("Lorg/eclipse/persistence")) {
                return new MetadataAnnotationVisitor(this.field, desc);
            }
            return null;
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitEnd() {
            this.field.getDeclaringClass().addField(this.field);
        }
    }

    /**
     * Factory for the creation of {@link MetadataMethod} handling basic type,
     * generics, and annotations.
     */
    // Note: Subclassed EmptyListener to minimize signature requirements for
    // ignored MethodVisitor API
    class MetadataMethodVisitor extends EmptyVisitor {

        private MetadataMethod method;

        public MetadataMethodVisitor(MetadataClass classMetadata, int access, String name, String desc, String signature, String[] exceptions) {
            this.method = new MetadataMethod(MetadataAsmFactory.this, classMetadata);

            this.method.setName(name);
            this.method.setAttributeName(Helper.getAttributeNameFromMethodName(name));
            this.method.setModifiers(access);

            this.method.setGenericType(processDescription(desc, true));

            List<String> argumentNames = processDescription(signature, false);
            if (argumentNames != null && !argumentNames.isEmpty()) {
                this.method.setReturnType(argumentNames.get(argumentNames.size() - 1));
                argumentNames.remove(argumentNames.size() - 1);
                this.method.setParameters(argumentNames);
            }
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.startsWith("Ljavax/persistence") || desc.startsWith("Lorg/eclipse/persistence")) {
                return new MetadataAnnotationVisitor(this.method, desc);
            }
            return null;
        }
        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return null;
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return null;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            return null;
        }

        /**
         * At the end of visiting this method add it to the
         * {@link MetadataClass} and handle duplicate method names by chaining
         * them.
         */
        @Override
        public void visitEnd() {
            MetadataClass classMetadata = this.method.getMetadataClass();

            MetadataMethod existing = classMetadata.getMethods().get(this.method.getName());
            if (existing == null) {
                classMetadata.getMethods().put(this.method.getName(), this.method);
            } else {
                // Handle methods with the same name.
                while (existing.getNext() != null) {
                    existing = existing.getNext();
                }
                existing.setNext(this.method);
            }
        }

    }

    /**
     * Process the byte-code argument description and return the array of Java
     * class names. i.e.
     * "(Lorg/foo/Bar;Z)Ljava/lang/Boolean;"=>[org.foo.Bar,boolean
     * ,java.lang.Boolean]
     */
    private static List<String> processDescription(String desc, boolean isGeneric) {
        if (desc == null) {
            return null;
        }
        List<String> arguments = new ArrayList<String>();
        int index = 0;
        int length = desc.length();
        boolean isGenericTyped=false;
        // PERF: Use char array to make char index faster (note this is a heavily optimized method, be very careful on changes)
        char[] chars = desc.toCharArray();
        while (index < length) {
            char next = chars[index];
            if (('(' != next) && (')' != next) && ('<' != next) && ('>' != next) && (';' != next)) {
                if (next == 'L') {
                    index++;
                    int start = index;
                    next = chars[index];
                    while (('(' != next) && (')' != next) && ('<' != next) && ('>' != next) && (';' != next)) {
                        index++;
                        next = chars[index];
                    }
                    arguments.add(toClassName(desc.substring(start, index)));
                    if(isGenericTyped) {
                        isGenericTyped=false;
                        if(next == '<') {
                            int cnt = 1;
                            while((cnt > 0) && (++index<desc.length())) {
                               switch (desc.charAt(index)) {
                                    case '<': cnt ++; break;
                                    case '>': cnt --; break;
                               }
                            }
                         }
                     }
                } else if (!isGeneric && (PRIMITIVES.indexOf(next) != -1)) {
                    // Primitives.
                    arguments.add(getPrimitiveName(next));
                } else if (next == '[') {
                    // Arrays.
                    int start = index;
                    index++;
                    next = chars[index];
                    // Nested arrays.
                    while (next == '[') {
                        index++;
                        next = chars[index];
                    }
                    if (PRIMITIVES.indexOf(next) == -1) {
                        while (next != ';') {
                            index++;
                            next = chars[index];
                        }
                        arguments.add(toClassName(desc.substring(start, index + 1)));
                    } else {
                        arguments.add(desc.substring(start, index + 1));
                    }
                } else {
                    // Is a generic type variable.
                    int start = index;
                    int end = start;
                    
                    char myNext = next;
                    
                    while (':' != myNext && '(' != myNext && ')' != myNext && '<' != myNext && '>' != myNext && ';' != myNext && end < length - 1) {
                        end++;
                        myNext = chars[end];
                    }
                    
                    if (myNext == ':') {
                        arguments.add(desc.substring(start, end));
                        isGenericTyped=true;
                        index = end;
                        arguments.add(":");
                        if(desc.charAt(index+1)==':') {
                            index ++;
                        }
                    } else if (myNext == ';' && next == 'T') {
                        arguments.add(new String(new char[] { next }));
                        arguments.add(desc.substring(start + 1, end));
                        index = end - 1;
                    } else {
                        arguments.add(new String(new char[] { next }));
                    }
                }
            }
            index++;
        }
        return arguments;

    }

    /**
     * Return the Java type name for the primitive code.
     */
    private static String getPrimitiveName(char primitive) {
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
        } else if (primitive == 'S') {
            return "short";
        } else {
            return new String(new char[] { primitive });
        }
    }

    private static String toClassName(String classDescription) {
        if (classDescription == null) {
            return "void";
        }
        return classDescription.replace('/', '.');
    }

    /**
     * Convert the annotation value into the value used in the meta model
     */
    private static Object annotationValue(String description, Object value) {
        if (value instanceof Type) {
            return ((Type) value).getClassName();
        }
        return value;
    }
}
