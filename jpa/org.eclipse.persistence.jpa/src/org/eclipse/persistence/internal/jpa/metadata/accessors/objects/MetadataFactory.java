/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.Helper;
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
 * A metadata factory is used to extract class information. It is used when
 * processing the metadata model. By default, metadata processing uses an 
 * ASM factory, however tools that require a different form of processing,
 * like the APT processor which uses mirrors, can build their own factory
 * and supply it at processing time.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.1 
 */
public class MetadataFactory {
    /** Set of primitive type codes. */
    public static final String PRIMITIVES = "VJIBZCSFD";
    /** Set of desc token characters. */
    public static final String TOKENS = "()<>;";
    /** Backdoor to allow mapping of JDK classes. */
    public static boolean ALLOW_JDK = false;
    
    /** Stores all metadata for classes. */
    protected Map<String, MetadataClass> m_metadata;
    
    protected MetadataLogger m_logger;
    protected ClassLoader m_loader;

    /**
     * INTERNAL:
     */
    public MetadataFactory(MetadataLogger logger, ClassLoader loader) {
        m_logger = logger;
        m_loader = loader;
        
        m_metadata = new HashMap<String, MetadataClass>();
        m_metadata.put("void", new MetadataClass(this, void.class));
        m_metadata.put("", new MetadataClass(this, void.class));
        m_metadata.put(null, new MetadataClass(this, void.class));
        m_metadata.put("int", new MetadataClass(this, int.class));
        m_metadata.put("long", new MetadataClass(this, long.class));
        m_metadata.put("short", new MetadataClass(this, short.class));
        m_metadata.put("boolean", new MetadataClass(this, boolean.class));
        m_metadata.put("float", new MetadataClass(this, float.class));
        m_metadata.put("double", new MetadataClass(this, double.class));
        m_metadata.put("char", new MetadataClass(this, char.class));
        m_metadata.put("byte", new MetadataClass(this, byte.class));
        m_metadata.put("V", new MetadataClass(this, void.class));
        m_metadata.put("I", new MetadataClass(this, int.class));
        m_metadata.put("J", new MetadataClass(this, long.class));
        m_metadata.put("S", new MetadataClass(this, short.class));
        m_metadata.put("Z", new MetadataClass(this, boolean.class));
        m_metadata.put("F", new MetadataClass(this, float.class));
        m_metadata.put("D", new MetadataClass(this, double.class));
        m_metadata.put("C", new MetadataClass(this, char.class));
        m_metadata.put("B", new MetadataClass(this, byte.class));
    }

    /**
     * Build the class metadata for the class name using ASM to read the class byte
     * codes.
     */
    protected void buildClassMetadata(String className) {
        ClassMetadataVisitor visitor = new ClassMetadataVisitor();
        try {
            ClassReader reader = new ClassReader(m_loader.getResourceAsStream(className.replace('.', '/') + ".class"));
            Attribute[] attributes = new Attribute[] { new RuntimeVisibleAnnotations(), new RuntimeVisibleParameterAnnotations(), new SignatureAttribute() };
            reader.accept(visitor, attributes, false);
        } catch (Exception exception) {
            // Some basic types can't be found, so can just be registered (i.e. arrays).
            getMetadata().put(className, new MetadataClass(this, className));
        }
    }
    
    /**
     * Return the class metadata for the class name.
     */
    public MetadataClass getClassMetadata(String className) {
        if (className == null) {
            return null;
        }
        
        // This may be a temporary thing. From the annotations processor. We 
        // set erasures so the classname may look something like 
        // java.util.Collection<E>. We need to hack off the <E>
        String clsName;
        if (className.indexOf("<") > -1) {
            clsName = className.substring(0, className.indexOf("<"));
        } else {
            clsName = className;
        }
        
        MetadataClass metadataClass = getMetadata().get(clsName);
        if (metadataClass == null) {
            buildClassMetadata(clsName);
            metadataClass = getMetadata().get(clsName);
        }
        return metadataClass;
    }
    
    /**
     * INTERNAL:
     */
    public ClassLoader getLoader() {
        return m_loader;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return m_logger;
    }

    /**
     * INTERNAL:
     */
    protected Map<String, MetadataClass> getMetadata() {
        return m_metadata;
    }
    
    /**
     * INTERNAL:
     */
    public void setLoader(ClassLoader loader) {
        this.m_loader = loader;
    }
    
    /**
     * INTERNAL:
     */
    public void setLogger(MetadataLogger logger) {
        this.m_logger = logger;
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
        MetadataClass m_classMetadata;

        ClassMetadataVisitor() {}
        
        public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
            String className = toClassName(name);
            m_classMetadata = new MetadataClass(MetadataFactory.this, className);
            getMetadata().put(className, m_classMetadata);
            m_classMetadata.setName(className);
            m_classMetadata.setSuperclassName(toClassName(superName));
            m_classMetadata.setModifiers(access);
            if ((!ALLOW_JDK) && (className.startsWith("java") || className.startsWith("javax"))) {
                m_classMetadata.setIsJDK(true);
            }
            
            for (String interfaceName : interfaces) {
                m_classMetadata.addInterface(toClassName(interfaceName));
            }
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            // Reference to the inner class, the inner class with be processed on its own.
        }

        public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
            if (m_classMetadata.isJDK()) {
                return;
            }
            MetadataField field = new MetadataField(MetadataFactory.this);
            field.setName(name);
            field.setAttributeName(name);
            field.setGenericType(getGenericType(attrs));
            field.setType(processDescription(desc, false).get(0));
            field.setModifiers(access);
            addAnnotations(attrs, field.getAnnotations());
            m_classMetadata.getFields().put(name, field);
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
            if (this.m_classMetadata.isJDK()) {
                return null;
            }
            MetadataMethod method = null;
            // Ignore generated constructors.
            if (name.indexOf("init>") != -1) {
                return null;
            }
            List<String> argumentNames = processDescription(desc, false);                
            method = new MetadataMethod(MetadataFactory.this, m_classMetadata);
            method.setName(name);
            method.setAttributeName(Helper.getAttributeNameFromMethodName(name));
            method.setModifiers(access);
            method.setGenericType(getGenericType(attrs));
            method.setReturnType(argumentNames.get(argumentNames.size() - 1));
            argumentNames.remove(argumentNames.size() - 1);
            method.setParameters(argumentNames);
            addAnnotations(attrs, method.getAnnotations());
            // Handle methods with the same name.
            MetadataMethod existing = m_classMetadata.getMethods().get(name);
            if (existing == null) {
                m_classMetadata.getMethods().put(name, method);
            } else {
                while (existing.getNext() != null) {
                    existing = existing.getNext();
                }
                existing.setNext(method);
            }
            return null;
        }

        public void visitAttribute(Attribute attr) {
            if (m_classMetadata.isJDK()) {
                return;
            }
            if (attr instanceof SignatureAttribute) {
                // Process generic signature.
                m_classMetadata.setGenericType(getGenericType(attr));
            } else {
                // Process annotations.
                addAnnotations(attr, m_classMetadata.getAnnotations());
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

        public void visitEnd() {}
    }
}
