/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/10/2009-2.0 Guy Pelletier
//       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     08/12/2010-2.2 Guy Pelletier
//       - 298118: canonical metamodel generation with untyped Map throws NPE
//     08/25/2010-2.2 Guy Pelletier
//       - 309445: CannonicalModelProcessor process all files
//     10/18/2010-2.2 Guy Pelletier
//       - 322921: OutOfMemory in annotation processor
//     11/23/2010-2.2 Guy Pelletier
//       - 330660: Canonical model generator throws ClassCastException when using package-info.java
package org.eclipse.persistence.internal.jpa.modelgen;

import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_GLOBAL_LOG_LEVEL;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PREFIX;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PROCESSOR_LOG_LEVEL;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_SUB_PACKAGE;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_SUFFIX;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_USE_STATIC_FACTORY;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnitReader;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.TypeVisitor;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * The main APT processor to generate the JPA 2.0 Canonical model.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
@SupportedOptions({CANONICAL_MODEL_PREFIX,
        CANONICAL_MODEL_SUFFIX,
        CANONICAL_MODEL_SUB_PACKAGE,
        CANONICAL_MODEL_LOAD_XML,
        CANONICAL_MODEL_USE_STATIC_FACTORY,
        CANONICAL_MODEL_GENERATE_TIMESTAMP,
        ECLIPSELINK_PERSISTENCE_XML,
        CANONICAL_MODEL_PROCESSOR_LOG_LEVEL,
        CANONICAL_MODEL_GLOBAL_LOG_LEVEL})
@SupportedAnnotationTypes({"javax.persistence.*", "org.eclipse.persistence.annotations.*"})
public class CanonicalModelProcessor extends AbstractProcessor {
    protected enum AttributeType {CollectionAttribute, ListAttribute, MapAttribute, SetAttribute, SingularAttribute }
    protected MetadataMirrorFactory nonStaticFactory;
    protected static MetadataMirrorFactory staticFactory;

    /**
     * INTERNAL:
     */
    protected void generateCanonicalModelClass(MetadataClass metadataClass, Element element, PersistenceUnit persistenceUnit) throws IOException {
        Writer writer = null;

        try {
            ClassAccessor accessor = persistenceUnit.getClassAccessor(metadataClass);
            String qualifiedName = accessor.getAccessibleObjectName();
            String className = getName(qualifiedName);
            String classPackage = getPackage(qualifiedName);

            String qualifiedCanonicalName = persistenceUnit.getQualifiedCanonicalName(qualifiedName);
            String canonicalName = getName(qualifiedCanonicalName);
            String canonicalpackage = getPackage(qualifiedCanonicalName);

            JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedCanonicalName, element);
            writer = file.openWriter();

            // Print the package if we have one.
            if (! canonicalpackage.equals("")) {
                writer.append("package " + canonicalpackage + ";\n\n");
            }

            // Go through the accessor list, ignoring any transient accessors
            // to build our attributes and import list.
            ArrayList<String> attributes = new ArrayList<String>();
            HashMap<String, String> imports = new HashMap<String, String>();

            // Import the model class if the canonical class is generated elsewhere.
            if (! classPackage.equals(canonicalpackage)) {
                imports.put(className, qualifiedName);
            }

            for (MappingAccessor mappingAccessor : accessor.getDescriptor().getMappingAccessors()) {
                if (! mappingAccessor.isTransient()) {
                    MetadataAnnotatedElement annotatedElement = mappingAccessor.getAnnotatedElement();
                    // Must go through the mapping accessor for the raw class
                    // since it may be a virtual mapping accessor with an
                    // attribute type.
                    MetadataClass rawClass = mappingAccessor.getRawClass();

                    // NOTE: order of checking is important.
                    String attributeType;
                    String types = className;

                    if (mappingAccessor.isBasic()) {
                        types = types + ", " + getUnqualifiedType(getBoxedType(annotatedElement, rawClass), imports);
                        attributeType = AttributeType.SingularAttribute.name();
                        imports.put(attributeType, "javax.persistence.metamodel.SingularAttribute");
                    } else {
                        if (rawClass.isList()) {
                            attributeType = AttributeType.ListAttribute.name();
                            imports.put(attributeType, "javax.persistence.metamodel.ListAttribute");
                        } else if (rawClass.isSet()) {
                            attributeType = AttributeType.SetAttribute.name();
                            imports.put(attributeType, "javax.persistence.metamodel.SetAttribute");
                        } else if (rawClass.isMap()) {
                            attributeType = AttributeType.MapAttribute.name();
                            imports.put(attributeType, "javax.persistence.metamodel.MapAttribute");
                        } else if (rawClass.isCollection()) {
                            attributeType = AttributeType.CollectionAttribute.name();
                            imports.put(attributeType, "javax.persistence.metamodel.CollectionAttribute");
                        } else {
                            attributeType = AttributeType.SingularAttribute.name();
                            imports.put(attributeType, "javax.persistence.metamodel.SingularAttribute");
                        }

                        if (mappingAccessor.isMapAccessor()) {
                            if (mappingAccessor.isMappedKeyMapAccessor()) {
                                MetadataClass mapKeyClass = ((MappedKeyMapAccessor) mappingAccessor).getMapKeyClass();
                                types = types + ", " + getUnqualifiedType(mapKeyClass.getName(), imports) + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), imports);
                            } else {
                                String mapKeyType;
                                if (annotatedElement.isGenericCollectionType()) {
                                    // Grab the map key class from the generic.
                                    mapKeyType = annotatedElement.getGenericType().get(1);
                                } else {
                                    if (mappingAccessor.getReferenceDescriptor().hasIdAccessor()) {
                                        // Grab the id type from the reference descriptor, now there's a handle!
                                        MappingAccessor idAccessor = mappingAccessor.getReferenceDescriptor().getIdAccessors().values().iterator().next();
                                        mapKeyType = idAccessor.getReferenceClassName();
                                    } else {
                                        // We don't know at this point so just use the catch all default.
                                        mapKeyType = TypeVisitor.GENERIC_TYPE;
                                    }
                                }

                                types = types + ", " + getUnqualifiedType(mapKeyType, imports) + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), imports);
                            }
                        } else {
                            types = types + ", " + getUnqualifiedType(mappingAccessor.getReferenceClassName(), imports);
                        }
                    }

                    // Add the mapping attribute to the list of attributes for this class.
                    attributes.add("    public static volatile " + attributeType + "<" + types + "> " + annotatedElement.getAttributeName() + ";\n");
                }
            }

            // Will import the parent as well if needed.
            String parent = writeImportStatements(imports, accessor, writer, persistenceUnit, canonicalpackage);

            // Write out the generation annotations.
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            writer.append("@Generated(value=\"EclipseLink-" + Version.getVersion() + ".v" + Version.getBuildDate() + "-r" + Version.getBuildRevision() + "\"");
            if (Boolean.valueOf(CanonicalModelProperties.getOption(CANONICAL_MODEL_GENERATE_TIMESTAMP, CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT, processingEnv.getOptions()))) {
                writer.append(", date=\"" +  sdf.format(date) + "\"");
            }
            writer.append(")\n");
            writer.append("@StaticMetamodel(" + className + ".class)\n");

            int modifier = accessor.getAccessibleObject().getModifiers();
            writer.append(java.lang.reflect.Modifier.toString(modifier) + " class " + canonicalName);

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
    protected void generateCanonicalModelClasses(MetadataMirrorFactory factory, PersistenceUnit persistenceUnit) throws IOException {
        Map<Element, MetadataClass> roundElements = factory.getRoundElements();

        for (Element roundElement : roundElements.keySet()) {
            MetadataClass roundClass = roundElements.get(roundElement);

            if (persistenceUnit.containsClass(roundClass)) {
                if (factory.getLogger().shouldLog(LogLevel.FINEST, LogCategory.PROCESSOR)) {
                    processingEnv.getMessager().printMessage(Kind.NOTE, "Generating class: " + roundClass.getName());
                }
                generateCanonicalModelClass(roundClass, roundElement, persistenceUnit);
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected String getBoxedType(MetadataAnnotatedElement annotatedElement, MetadataClass rawClass) {
        PrimitiveType primitiveType = (PrimitiveType) annotatedElement.getPrimitiveType();
        if (primitiveType != null) {
            return processingEnv.getTypeUtils().boxedClass(primitiveType).toString();
        }

        String type = annotatedElement.getType();
        return (type == null) ? rawClass.getType() : type;
    }

    /**
     * INTERNAL:
     */
    protected String getName(String qualifiedName) {
        if (qualifiedName.indexOf('.') > -1) {
            return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        }

        return qualifiedName;
    }

    /**
     * INTERNAL:
     */
    protected String getPackage(String qualifiedName) {
        if (qualifiedName.indexOf('.') > -1) {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        }

        return "";
    }

    /**
     * INTERNAL: This method will hack off any package qualification. It will
     * add that type to the import list unless it is a known JDK type that does
     * not need to be imported (java.lang). This method also trims the type
     * from leading and trailing white spaces.
     */
    protected String getUnqualifiedType(String type, HashMap<String, String> imports) {
        // Remove any leading and trailing white spaces.
        type = type.trim();

        // Convert any $ (enums, inner classes to valid dot notation for import statement)
        // org.eclipse.persistence.testing.models.jpa.xml.advanced.ShovelSections$MaterialType
        type = type.replace("$", ".");

        if (type.contains("void")) {
            // This case hits when the user defines something like:
            // @BasicCollection public Collection responsibilities;
            return TypeVisitor.GENERIC_TYPE;
        } else if (type.startsWith("java.lang")) {
            return type.substring(type.lastIndexOf('.') + 1);
        } else {
            if (type.indexOf('<') > -1) {
                String raw = type.substring(0, type.indexOf('>'));
                String generic = type.substring(type.indexOf('<') + 1, type.length() - 1);

                if (raw.contains("Map")) {
                    String key = generic.substring(0, generic.indexOf(','));
                    String value = generic.substring(generic.indexOf(',') + 1);
                    return getUnqualifiedType(raw, imports) + "<" + getUnqualifiedType(key, imports) + ", " + getUnqualifiedType(value, imports) + ">";
                }

                return getUnqualifiedType(raw, imports) + "<" + getUnqualifiedType(generic, imports) + ">";
            } else if (type.indexOf('.') > -1) {
                String shortClassName = type.substring(type.lastIndexOf('.') + 1);

                // We already have an import for this class, look at it further.
                if (imports.containsKey(shortClassName)) {
                    if (imports.get(shortClassName).equals(type)) {
                        // We're hitting the same class from the same package,
                        // return the short name for this class.
                        return type.substring(type.lastIndexOf('.') + 1);
                    } else {
                        // Same class name different package. Don't hack off the
                        // qualification and don't add it to the import list.
                        return type;
                    }
                } else {
                    // Add it to the import list. If the type is used in an array
                    // hack off the [].
                    if (shortClassName.indexOf('[') > 1) {
                        imports.put(shortClassName, type.substring(0, type.indexOf('[')));
                    } else {
                        imports.put(shortClassName, type);
                    }

                    return shortClassName;
                }
            } else {
                return type;
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (! roundEnv.processingOver() && ! roundEnv.errorRaised()) {
            MetadataMirrorFactory factory = null;
            try {
                if (Boolean.valueOf(CanonicalModelProperties.getOption(CANONICAL_MODEL_USE_STATIC_FACTORY, CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT, processingEnv.getOptions()))) {
                    if (staticFactory == null) {
                        // We must remember some state from one round to another.
                        // In some rounds, the user may only change one class
                        // meaning we only have one root element from the round.
                        // If it is a child class to an existing already generated
                        // parent class we need to know about this class, so the
                        // factory will also hang onto static projects for each
                        // persistence unit. Doing this is going to need careful
                        // cleanup thoughts though. Adding classes ok, but what
                        // about removing some?
                        final MetadataLogger logger = new MetadataLogger(new ServerSession(new Project(new DatabaseLogin())));
                        staticFactory = new MetadataMirrorFactory(logger,
                                LoggerContext.buildContext(processingEnv.getOptions()),
                                Thread.currentThread().getContextClassLoader());
                        staticFactory.getLoggerContext().updateMetadataLogger(logger);
                        if (logger.shouldLog(LogLevel.INFO, LogCategory.PROCESSOR)) {
                            processingEnv.getMessager().printMessage(Kind.NOTE, "Creating static metadata factory ...");
                        }
                    }

                    factory = staticFactory;
                } else {
                    if (nonStaticFactory == null) {
                        final MetadataLogger logger = new MetadataLogger(new ServerSession(new Project(new DatabaseLogin())));
                        nonStaticFactory = new MetadataMirrorFactory(logger,
                                LoggerContext.buildContext(processingEnv.getOptions()),
                                Thread.currentThread().getContextClassLoader());
                        nonStaticFactory.getLoggerContext().updateMetadataLogger(logger);
                        if (logger.shouldLog(LogLevel.INFO, LogCategory.PROCESSOR)) {
                            processingEnv.getMessager().printMessage(Kind.NOTE, "Creating non-static metadata factory ...");
                        }
                    }

                    factory = nonStaticFactory;
                }

                final MetadataLogger logger = factory.getLogger();
                final LoggerContext logCtx = factory.getLoggerContext();

                // Log processing environment options
                if (logger.shouldLog(LogLevel.CONFIG, LogCategory.PROCESSOR)) {
                    for (String optionKey : processingEnv.getOptions().keySet()) {
                        processingEnv.getMessager().printMessage(Kind.OTHER, "Found Option : " + optionKey + ", with value: " + processingEnv.getOptions().get(optionKey));
                    }
                }

                // Step 1 - The factory is passed around so those who want the
                // processing or round env can get it off the factory. This
                // saves us from having to pass around multiple objects.
                factory.setEnvironments(processingEnv, roundEnv);

                // Step 2 - read the persistence xml classes (gives us extra
                // classes and mapping files. From them we get transients and
                // access). Metadata read from XML causes new accessors to be
                // created and override existing ones (causing them to be un-
                // pre-processed. We can never tell what changes in XML so we
                // have to do this.
                final PersistenceUnitReader puReader = new PersistenceUnitReader(logger, processingEnv);
                puReader.initPersistenceUnits(factory);

                // Step 3 - iterate over all the persistence units and generate
                // their canonical model classes.
                for (PersistenceUnit persistenceUnit : factory.getPersistenceUnits()) {

                    // Update log level using PU property when no command line logging level option is set
                    final boolean updateLogger = logger != null && !logCtx.isAny();
                    if (updateLogger) {
                        LoggerContext.updateMetadataLogger(logger, persistenceUnit);
                    }

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
                    generateCanonicalModelClasses(factory, persistenceUnit);

                    if (updateLogger) {
                        logCtx.updateMetadataLogger(logger);
                    }
                }
            } catch (Exception e) {
                final MetadataLogger logger = (factory != null) ? factory.getLogger() : null;
                if (logger == null || logger.shouldLog(LogLevel.SEVERE, LogCategory.PROCESSOR)) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, e.toString());
                }
                throw new RuntimeException(e);
            }
        }

        return false; // Don't claim any annotations
    }

    /**
     * INTERNAL:
     */
    protected String writeImportStatements(HashMap<String, String> typeImports, ClassAccessor accessor, Writer writer, PersistenceUnit persistenceUnit, String childCanonicalpackage) throws IOException {
        String parentCanonicalName = null;

        // Get the import list ready to be sorted.
        ArrayList<String> imps = new ArrayList<String>();
        imps.addAll(typeImports.values());

        // Add the standard canonical model generator imports.
        imps.add("javax.annotation.Generated");
        imps.add("javax.persistence.metamodel.StaticMetamodel");

        // Import the parent canonical class if need be.
        MetadataClass parentCls = accessor.getJavaClass().getSuperclass();
        MetadataProject project = accessor.getProject();

        if (project.hasEntity(parentCls) || project.hasEmbeddable(parentCls) || project.hasMappedSuperclass(parentCls)) {
            String qualifiedParentCanonicalName = persistenceUnit.getQualifiedCanonicalName(parentCls.getName());
            parentCanonicalName = getName(qualifiedParentCanonicalName);
            String parentCanonicalPackage = getPackage(qualifiedParentCanonicalName);

            if (! parentCanonicalPackage.equals(childCanonicalpackage)) {
                imps.add(qualifiedParentCanonicalName);
            }
        }

        // Sort the list of imports before writing them.
        Collections.sort(imps);

        // Write out the imports.
        for (String typeImport : imps) {
            writer.append("import " + typeImport + ";\n");
        }

        writer.append("\n");
        return parentCanonicalName;
    }

}
