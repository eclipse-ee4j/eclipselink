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
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.PrimitiveType;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnitReader;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.TypeVisitor;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * The main APT processor to generate the JPA 2.0 Canonical model. 
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.0
 */
@SupportedAnnotationTypes("*")  // Process all annotations
@SupportedSourceVersion(RELEASE_6)
public class CanonicalModelProcessor extends AbstractProcessor {
    protected enum AttributeType {CollectionAttribute, ListAttribute, MapAttribute, SetAttribute, SingularAttribute }
    protected static MetadataMirrorFactory m_factory;
    
    /**
     * INTERNAL:
     */
    protected void generateCanonicalModelClass(Element element, ClassAccessor accessor) throws IOException {
        Writer writer = null;
        
        try {                
            String qualifiedName = accessor.getAccessibleObjectName();
            
            JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedName + "_", element);
                        
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            String originalClassName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
            String underScoreClassName = originalClassName + "_";
                        
            writer = file.openWriter();
            writer.append("package " + packageName + ";\n\n");
                        
            HashSet<String> attributeTypes = new HashSet<String>();
            ArrayList<String> attributes = new ArrayList<String>();
                 
            // Go through the accessor list, ignoring any transient accessors.
            // Those accessors come from an XML specification.
            HashSet<String> typeImports = new HashSet<String>();
            for (MappingAccessor mappingAccessor : accessor.getDescriptor().getAccessors()) {
                if (! mappingAccessor.isTransient()) {
                    MetadataAnnotatedElement annotatedElement = mappingAccessor.getAnnotatedElement();
                    MetadataClass rawClass = annotatedElement.getRawClass(mappingAccessor.getDescriptor());
                    
                    // By default, attributeTyps is singular attribute.
                    String attributeType = AttributeType.SingularAttribute.name();

                    // NOTE: order of checking is important.
                    String types = originalClassName;
                    
                    if (mappingAccessor.isBasic()) {
                        types = types + ", " + getUnqualifiedType(getBoxedType(annotatedElement), typeImports);
                        attributeType = AttributeType.SingularAttribute.name();
                    } else {
                        if (rawClass.isList()) {
                            attributeType = AttributeType.ListAttribute.name();
                        } else if (rawClass.isSet()) {
                            attributeType = AttributeType.SetAttribute.name();
                        } else if (rawClass.isMap()) {
                            attributeType = AttributeType.MapAttribute.name();
                        } else if (rawClass.isCollection()) {
                            attributeType = AttributeType.CollectionAttribute.name();
                        }
                        
                        if (mappingAccessor.isMapAccessor()) {
                            if (mappingAccessor.isMappedKeyMapAccessor()) {
                                MetadataClass mapKeyClass = ((MappedKeyMapAccessor) mappingAccessor).getMapKeyClass();
                                types = types + ", " + getUnqualifiedType(mapKeyClass.getName(), typeImports) + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), typeImports);
                            } else {
                                String mapKeyType;
                                if (annotatedElement.isGenericCollectionType()) {
                                    // Grab the map key class from the generic.
                                    mapKeyType = annotatedElement.getGenericType().get(1);
                                } else {
                                    if (mappingAccessor.getReferenceDescriptor().hasIdAccessor()) {
                                        // Grab the id type from the reference descriptor, now there's a handle!
                                        mapKeyType = mappingAccessor.getReferenceDescriptor().getIdAccessors().get(0).getAnnotatedElement().getType();
                                    } else {
                                        // We don't know at this point so just use the catch all default.
                                        mapKeyType = "? extends Object";
                                    }                                    
                                }
                                
                                types = types + ", " + getUnqualifiedType(mapKeyType, typeImports) + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), typeImports);
                            }
                        } else {
                            types = types + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), typeImports);
                        }
                    }
                    
                    // Build a list of attribute types to import.
                    attributeTypes.add(attributeType);
                        
                    // Add the mapping attribute to the list of attributes for this class.
                    attributes.add("\tpublic static volatile " + attributeType + "<" + types + "> " + annotatedElement.getAttributeName() + ";\n");
                }
            }
                        
            // Will import the parent as well if needed
            String parent = writeImportStatements(attributeTypes, typeImports, accessor, writer);
                     
            // Write out the generation annotations.
            writer.append("@Generated(\"EclipseLink JPA 2.0 Canonical Model Generation\")\n");
            writer.append("@StaticMetamodel(" + originalClassName + ".class)\n");
                
            int modifier = accessor.getAccessibleObject().getModifiers();
            writer.append(java.lang.reflect.Modifier.toString(modifier) + " class " + underScoreClassName);
                
            if (parent == null) {
                writer.append(" { \n\n");
            } else {
                writer.append(" extends " + parent + " {\n\n");
            }
                        
            // Go through the attributes and write them out.
            for (String str : attributes) {
                writer.append(str);
            }
                        
            writer.append("\n}");
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected String getBoxedType(MetadataAnnotatedElement annotatedElement) {
        PrimitiveType primitiveType = annotatedElement.getPrimitiveType();
        if (primitiveType != null) {
            return processingEnv.getTypeUtils().boxedClass(primitiveType).toString();
        }
        
        return annotatedElement.getType();
    }
    
    /**
     * INTERNAL:
     */
    protected void generateCanonicalModelClasses(RoundEnvironment roundEnv, PersistenceUnit persistenceUnit) throws IOException {
        for (Element element : roundEnv.getRootElements()) {
            if (persistenceUnit.containsElement(element)) {                
                //processingEnv.getMessager().printMessage(Kind.NOTE, "Generating class: " + element);
                generateCanonicalModelClass(element, persistenceUnit.getClassAccessor(element));
            }    
        }
    }
    
    /**
     * INTERNAL:
     */
    protected String getCanonicalType(MappingAccessor mappingAccessor) {
        MetadataClass rawClass = mappingAccessor.getAnnotatedElement().getRawClass(mappingAccessor.getDescriptor());
        
        // NOTE: order of checking is important.
        if (mappingAccessor.isBasic()) {
            return AttributeType.SingularAttribute.name();
        } else if (rawClass.isList() && mappingAccessor.isBasic()) {
            return AttributeType.ListAttribute.name();
        } else if (rawClass.isSet()) {
            return AttributeType.SetAttribute.name();
        } else if (rawClass.isMap()) {
            return AttributeType.MapAttribute.name();
        } else if (rawClass.isCollection()) {
            return AttributeType.CollectionAttribute.name();
        } else {
            // catch all, but likely would never hit?
            return AttributeType.SingularAttribute.name();
        }
    }
    
    /**
     * INTERNAL: This method will hack off any package qualification. It will 
     * add that type to the import list unless it is a known jdk type that does 
     * not need to be imported (java.lang). This method also trims the type
     * from leading and trailing white spaces.
     */
    protected String getUnqualifiedType(String type, HashSet<String> imports) {
        if (type.contains("void")) {
            // This case hits when the user defines something like: @BasicCollection public Collection responsibilities;
            return TypeVisitor.GENERIC_TYPE;
        } else if (type.startsWith("java.lang")) {
            return type.substring(type.lastIndexOf(".") + 1).trim();   
        } else {
            if (type.indexOf("<") > -1) {
                String raw = type.substring(0, type.indexOf("<"));
                String generic = type.substring(type.indexOf("<") + 1, type.length() - 1);
                
                if (raw.contains("Map")) {
                    String key = generic.substring(0, generic.indexOf(","));
                    String value = generic.substring(generic.indexOf(",") + 1);
                    return getUnqualifiedType(raw, imports) + "<" + getUnqualifiedType(key, imports) + ", " + getUnqualifiedType(value, imports) + ">";
                }
                
                return getUnqualifiedType(raw, imports) + "<" + getUnqualifiedType(generic, imports) + ">";
            } else if (type.indexOf(".") > -1) {
                // Add it to the import list. If the type is used in an array
                // hack off the [].
                if (type.indexOf("[") > 1) {
                    imports.add(type.substring(0, type.indexOf("[")).trim());
                } else {
                    imports.add(type.trim());
                }
                
                return type.substring(type.lastIndexOf(".") + 1).trim();
            } else {
                return type.trim();
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (! roundEnv.processingOver()) {
            try {
                if (m_factory == null) {
                    MetadataLogger logger = new MetadataLogger(new ServerSession(new Project(new DatabaseLogin())));
                    m_factory = new MetadataMirrorFactory(logger, Thread.currentThread().getContextClassLoader());
                }
                
                // Step 1 - The factory is passed around so those who want the 
                // processing or round env can get it off the factory. This 
                // saves us from having to pass around multiple objects.
                m_factory.setEnvironments(processingEnv, roundEnv);
                
                // Step 2 - read the persistence xml classes (gives us extra 
                // classes and mapping files. From them we get transients and 
                // access)
                PersistenceUnitReader puReader = new PersistenceUnitReader(m_factory, processingEnv.getOptions().get("persistence.xml.path"));
                
                // Step 3 - iterate over all the persistence units and generate
                // their canonical model classes.
                for (PersistenceUnit persistenceUnit : puReader.getPersistenceUnits()) {
                    // Step 3a - add the Entities not defined in XML that are 
                    // being compiled.
                    for (Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
                        persistenceUnit.addEntityAccessor(element);
                    }
                
                    // Step 3b - add the Embeddables not defined in XML that are
                    // being compiled.
                    for (Element element : roundEnv.getElementsAnnotatedWith(Embeddable.class)) {
                        persistenceUnit.addEmbeddableAccessor(element);
                    }
                    
                    // Step 3c - add the MappedSuperclasses not defined in XML
                    // that are being compiled.
                    for (Element element : roundEnv.getElementsAnnotatedWith(MappedSuperclass.class)) {
                        persistenceUnit.addMappedSuperclassAccessor(element);
                    }
                    
                    // Step 3d - tell the persistence unit to pre-process itself.
                    persistenceUnit.preProcessForCanonicalModel();
                    
                    // Step 3e - We're set, generate the canonical model classes.
                    generateCanonicalModelClasses(roundEnv, persistenceUnit);
                }
                
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, e.toString());
                
                for (StackTraceElement stElement : e.getStackTrace()) {
                    processingEnv.getMessager().printMessage(Kind.NOTE, stElement.toString());
                }
                
                throw new RuntimeException(e);
            }
        }
    
        return false; // Don't claim any annotations
    }
    
    /**
     * INTERNAL:
     */
    protected String writeImportStatements(HashSet<String> attributeTypes, HashSet<String> typeImports, ClassAccessor accessor, Writer writer) throws IOException {
        String unqualifiedCanonicalParent = null;
        
        // Write the java imports. Sort them?
        for (String typeImport : typeImports) {
            writer.append("import " + typeImport + ";\n");
        }
        
        // Write the javax imports.
        writer.append("import javax.annotation.Generated;\n");
        
        if (attributeTypes.contains("CollectionAttribute")) {
            writer.append("import javax.persistence.metamodel.CollectionAttribute;\n");
        }
        
        if (attributeTypes.contains("ListAttribute")) {
            writer.append("import javax.persistence.metamodel.ListAttribute;\n");
        } 
        
        if (attributeTypes.contains("MapAttribute")) {
            writer.append("import javax.persistence.metamodel.MapAttribute;\n");
        } 
        
        if (attributeTypes.contains("SetAttribute")) {
            writer.append("import javax.persistence.metamodel.SetAttribute;\n");
        } 
            
        if (attributeTypes.contains("SingularAttribute")) {
            writer.append("import javax.persistence.metamodel.SingularAttribute;\n");
        }
        
        writer.append("import javax.persistence.metamodel.StaticMetamodel;\n");
        
        // Write the parent class now and import it as well if need be.
        MetadataClass cls = (MetadataClass) accessor.getAnnotatedElement();
        MetadataClass parentCls = cls.getSuperclass();
        MetadataProject project = accessor.getProject();
        
        if (project.hasEntity(parentCls) || project.hasEmbeddable(parentCls) || project.hasMappedSuperclass(parentCls)) {
            // TODO: Any generics here will need to be taken into consideration.
            
            String parent = parentCls.getName();
            String unqualifiedParent = parent.substring(parent.lastIndexOf(".") + 1);
            String parentPackage = parent.substring(0, parent.lastIndexOf("."));
            String child = accessor.getJavaClassName();
            String childPackage = child.substring(0, child.lastIndexOf("."));
            
            if (! parentPackage.equals(childPackage)) {
                writer.append("\nimport " + parent + "_;\n");
            }
            
            unqualifiedCanonicalParent = unqualifiedParent + "_";
        }
        
        writer.append("\n");
        return unqualifiedCanonicalParent;
    }
}
