/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import javax.tools.JavaFileObject;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.graphs.NamedEntityGraphMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnitReader;
import org.eclipse.persistence.internal.jpa.modelgen.visitors.TypeVisitor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * The main APT processor to generate the JPA 2.0 Canonical model.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class CanonicalModelProcessor extends AbstractProcessor {
    protected enum AttributeType {CollectionAttribute, ListAttribute, MapAttribute, SetAttribute, SingularAttribute }
    protected MetadataMirrorFactory nonStaticFactory;
    protected static MetadataMirrorFactory staticFactory;
    private SessionLog log;
    private boolean useStaticFactory;
    private boolean generateComments;
    private boolean generateTimestamp;
    private boolean generateGenerated;

    private static final String TYPE_TEMPLATE = "    public static volatile %s<%s> class_;%n";
    private static final String ATTRIBUTE_NAME_TEMPLATE = "    public static final String %s = \"%s\";%n";
    private static final String NAMED_NAME_TEMPLATE = "    public static final String %s_%s = \"%s\";%n";
    private static final String ATTRIBUTE_TYPE_TEMPLATE = "    public static volatile %s<%s> %s;%n";
    private static final String REFERENCE_TEMPLATE = "    public static volatile TypedQueryReference<%s> _%s_;%n";
    private static final String GRAPH_TEMPLATE = "    public static volatile EntityGraph<%s> _%s;%n";

    private record Attributes(Collection<String> attributes, Map<String, String> imports) {}

    private static final Set<String> SUPPORTED_ANNOTATIONS = Collections.unmodifiableSet(new HashSet<>() {{
        if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0) {
            add("java.persistence/jakarta.persistence.*");
            add("jakarta.persistence/jakarta.persistence.*");
        }
        add("jakarta.persistence.*");
        add("org.eclipse.persistence.annotations.*");
    }});

    //shortcut to enable FINER logging
    private static final Set<String> SUPPORTED_OPTIONS = Set.of(CanonicalModelProperties.CANONICAL_MODEL_PREFIX, CanonicalModelProperties.CANONICAL_MODEL_SUFFIX, CanonicalModelProperties.CANONICAL_MODEL_SUB_PACKAGE, CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML, CanonicalModelProperties.CANONICAL_MODEL_USE_STATIC_FACTORY, CanonicalModelProperties.CANONICAL_MODEL_GENERATE_GENERATED, CanonicalModelProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP, CanonicalModelProperties.CANONICAL_MODEL_GENERATE_COMMENTS, CanonicalModelProperties.CANONICAL_MODEL_PROCESSOR_LOG_LEVEL, CanonicalModelProperties.CANONICAL_MODEL_GLOBAL_LOG_LEVEL, PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "verbose");

    /**
     * Default constructor.
     */
    public CanonicalModelProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Map<String, String> options = processingEnv.getOptions();

        log = new MessagerLog(processingEnv.getMessager(), options);
        if (Boolean.parseBoolean(options.get("verbose")) && log.getLevel() > SessionLog.FINER) {
            log.setLevel(SessionLog.FINER);
        }
        AbstractSessionLog.setLog(log);

        // Log processing environment options
        for (Map.Entry<String, String> option : options.entrySet()) {
            log(SessionLog.CONFIG, "Found Option: {0}, with value: {1}",
                    option.getKey(), option.getValue());
        }

        useStaticFactory = Boolean.parseBoolean(CanonicalModelProperties.getOption(
                CanonicalModelProperties.CANONICAL_MODEL_USE_STATIC_FACTORY,
                CanonicalModelProperties.CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT,
                options));
        generateGenerated = Boolean.parseBoolean(CanonicalModelProperties.getOption(
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_GENERATED,
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_GENERATED_DEFAULT,
                options));
        if (generateGenerated) {
            generateTimestamp = Boolean.parseBoolean(CanonicalModelProperties.getOption(
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP,
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT,
                options));
            generateComments = Boolean.parseBoolean(CanonicalModelProperties.getOption(
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_COMMENTS,
                CanonicalModelProperties.CANONICAL_MODEL_GENERATE_COMMENTS_DEFAULT,
                options));
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (! roundEnv.processingOver() && ! roundEnv.errorRaised()) {
            MetadataMirrorFactory factory = null;
            try {
                if (useStaticFactory) {
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
                        AbstractSession session = new ServerSession(new Project(new DatabaseLogin()));
                        session.setSessionLog(log);
                        final MetadataLogger logger = new MetadataLogger(session);
                        staticFactory = new MetadataMirrorFactory(logger,
                                Thread.currentThread().getContextClassLoader());
                        log(SessionLog.INFO, "Creating static metadata factory ...");
                    }

                    factory = staticFactory;
                } else {
                    if (nonStaticFactory == null) {
                        AbstractSession session = new ServerSession(new Project(new DatabaseLogin()));
                        session.setSessionLog(log);
                        final MetadataLogger logger = new MetadataLogger(session);
                        nonStaticFactory = new MetadataMirrorFactory(logger,
                                Thread.currentThread().getContextClassLoader());
                        log(SessionLog.INFO, "Creating non-static metadata factory ...");
                    }

                    factory = nonStaticFactory;
                }

                final MetadataLogger logger = factory.getLogger();

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
                }
            } catch (Exception e) {
                log.logThrowable(SessionLog.SEVERE, SessionLog.PROCESSOR, e);
                throw new RuntimeException(e);
            }
        }

        return false; // Don't claim any annotations
    }

    /**
     * INTERNAL:
     */
    protected void generateCanonicalModelClass(MetadataClass metadataClass, Element element, PersistenceUnit persistenceUnit) throws IOException {
        ClassAccessor accessor = persistenceUnit.getClassAccessor(metadataClass);
        String qualifiedName = accessor.getAccessibleObjectName();
        String className = getName(qualifiedName);
        String classPackage = getPackage(qualifiedName);

        String qualifiedCanonicalName = persistenceUnit.getQualifiedCanonicalName(qualifiedName);
        String canonicalName = getName(qualifiedCanonicalName);
        String canonicalpackage = getPackage(qualifiedCanonicalName);

        boolean isNewJava = SourceVersion.RELEASE_8.compareTo(processingEnv.getSourceVersion()) < 0;

        // Go through the accessor list, ignoring any transient accessors
        // to build our attributes and import list.
        List<String> attributes = new ArrayList<>();
        Map<String, String> imports = new HashMap<>();

        if (generateGenerated) {
            if (isNewJava) {
                imports.put("Generated", "javax.annotation.processing.Generated");
            } else {
                imports.put("Generated", "jakarta.annotation.Generated");
            }
        }

        // Import the model class if the canonical class is generated elsewhere.
        if (!classPackage.equals(canonicalpackage)) {
            imports.put(className, qualifiedName);
        }

        // process accessor to find all necessary imports
        MetadataProject project = accessor.getProject();

        Attributes processedAttributes = processAccessorType(accessor);
        imports.putAll(processedAttributes.imports());
        if (attributes.addAll(processedAttributes.attributes())) {
            attributes.add(System.lineSeparator());
        }

        processedAttributes = processNamedQueries(project, accessor);
        imports.putAll(processedAttributes.imports());
        if (attributes.addAll(processedAttributes.attributes())) {
            attributes.add(System.lineSeparator());
        }

        processedAttributes = processNamedGraphs(project, accessor);
        imports.putAll(processedAttributes.imports());
        if (attributes.addAll(processedAttributes.attributes())) {
            attributes.add(System.lineSeparator());
        }

        processedAttributes = processNamedSqlResultSets(project, accessor);
        imports.putAll(processedAttributes.imports());
        if (attributes.addAll(processedAttributes.attributes())) {
            attributes.add(System.lineSeparator());
        }

        processedAttributes = processMappings(accessor);
        imports.putAll(processedAttributes.imports());
        if (attributes.addAll(processedAttributes.attributes())) {
            attributes.add(System.lineSeparator());
        }

        JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedCanonicalName, element);
        try (Writer writer = file.openWriter()) {

            // Print the package if we have one.
            if (!canonicalpackage.isEmpty()) {
                writer.append("package ").append(canonicalpackage).append(";")
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
            }

            // avoid same package imports
            Map<String, String> filteredImports = imports.entrySet().stream()
                    .filter(entry -> !canonicalpackage.equals(getPackage(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Will import the parent as well if needed.
            String parent = writeImportStatements(filteredImports, accessor, writer, persistenceUnit, canonicalpackage);

            if (generateGenerated) {
                // Write out the generation annotations.
                String elVersion = "EclipseLink-" + Version.getVersion() + ".v" + Version.getBuildDate() + "-r" + Version.getBuildRevision();
                writer.append("@Generated(value=\"");
                if (isNewJava) {
                    writer.append(CanonicalModelProcessor.class.getName());
                } else {
                    writer.append(elVersion);
                }
                writer.append("\"");
                if (generateTimestamp) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    writer.append(", date=\"").append(sdf.format(date)).append("\"");
                }
                if (isNewJava && generateComments) {
                    writer.append(", comments=\"");
                    writer.append(elVersion);
                    writer.append("\"");
                }
                writer.append(")");
                writer.append(System.lineSeparator());
            }
            writer.append("@StaticMetamodel(").append(className).append(".class)").append(System.lineSeparator());
            writer.append("@SuppressWarnings({\"rawtypes\", \"deprecation\"})").append(System.lineSeparator());

            int modifier = accessor.getAccessibleObject().getModifiers();
            writer.append(java.lang.reflect.Modifier.toString(modifier)).append(" class ").append(canonicalName);

            if (parent != null) {
                writer.append(" extends ").append(parent);
            }
            writer.append(" {").append(System.lineSeparator()).append(System.lineSeparator());

            // Go through the attributes and write them out.
            for (String str : attributes) {
                writer.append(str);
            }

            writer.append("}");
            writer.flush();
        }
    }

    /**
     * INTERNAL:
     */
    protected void generateCanonicalModelClasses(MetadataMirrorFactory factory, PersistenceUnit persistenceUnit) throws IOException {
        Map<Element, MetadataClass> roundElements = factory.getRoundElements();

        for (Map.Entry<Element, MetadataClass> entry : roundElements.entrySet()) {
            Element roundElement = entry.getKey();
            MetadataClass roundClass = entry.getValue();

            if (persistenceUnit.containsClass(roundClass) && !factory.isProcessed(roundElement)) {
                log(SessionLog.FINEST, "Generating class: {0}", roundClass.getName());
                generateCanonicalModelClass(roundClass, roundElement, persistenceUnit);
                factory.addProcessed(roundElement);
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

    private String toJavaIdentifier(String s) {
        StringBuilder sb = new StringBuilder();
        if (Character.isJavaIdentifierStart(s.charAt(0))) {
            sb.append(s.charAt(0));
        } else {
            sb.append('_');
        }
        for (int i = 1; i < s.length(); i++) {
            if (Character.isJavaIdentifierPart(s.charAt(i))) {
                sb.append(s.charAt(i));
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    private String toUpperCase(String s) {
        StringBuilder sb = new StringBuilder();
        if (Character.isJavaIdentifierStart(s.charAt(0))) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1 && Character.isUpperCase(s.charAt(1))) {
                sb.append('_');
            }
        } else {
            sb.append('_');
        }
        for (int i = 1; i < s.length() - 1; i++) {
            if (Character.isJavaIdentifierPart(s.charAt(i))) {
                sb.append(Character.toUpperCase(s.charAt(i)));
                if (Character.isUpperCase(s.charAt(i + 1))) {
                    sb.append('_');
                }
            } else {
                sb.append('_');
            }
        }
        sb.append(Character.toUpperCase(s.charAt(s.length() - 1)));
        return sb.toString();
    }

    /**
     * INTERNAL: This method will hack off any package qualification. It will
     * add that type to the import list unless it is a known JDK type that does
     * not need to be imported (java.lang). This method also trims the type
     * from leading and trailing white spaces.
     */
    protected String getUnqualifiedType(String type, Map<String, String> imports) {
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

    private Attributes processAccessorType(ClassAccessor accessor) {
        Set<String> attributes = new TreeSet<>();
        Map<String, String> imports = new HashMap<>();
        String type = null;
        if (accessor.isEntityAccessor()) {
            type = "EntityType";
        } else if (accessor.isEmbeddableAccessor()) {
            type = "EmbeddableType";
        } else if (accessor.isMappedSuperclass()) {
            type = "MappedSuperclassType";
        }
        if (type != null) {
            attributes.add(TYPE_TEMPLATE.formatted(type, getUnqualifiedType(accessor.getAccessibleObjectName(), imports)));
            imports.put(type, "jakarta.persistence.metamodel." + type);
        }
        return new Attributes(attributes, imports);
    }

    private Attributes processMappings(ClassAccessor accessor) {
        Set<String> attributes = new TreeSet<>();
        Map<String, String> imports = new HashMap<>();
        String className = getName(accessor.getAccessibleObjectName());

        for (MappingAccessor mappingAccessor : accessor.getDescriptor().getMappingAccessors()) {
            if (!mappingAccessor.isTransient()) {
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
                } else {
                    if (rawClass.isList()) {
                        attributeType = AttributeType.ListAttribute.name();
                    } else if (rawClass.isSet()) {
                        attributeType = AttributeType.SetAttribute.name();
                    } else if (rawClass.isMap()) {
                        attributeType = AttributeType.MapAttribute.name();
                    } else if (rawClass.isCollection()) {
                        attributeType = AttributeType.CollectionAttribute.name();
                    } else {
                        attributeType = AttributeType.SingularAttribute.name();
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

                imports.put(attributeType, "jakarta.persistence.metamodel." + attributeType);
                // Add the mapping attribute to the list of attributes for this class.
                attributes.add(ATTRIBUTE_NAME_TEMPLATE.formatted(toUpperCase(annotatedElement.getAttributeName()), annotatedElement.getAttributeName()));
                attributes.add(ATTRIBUTE_TYPE_TEMPLATE.formatted(attributeType, types, annotatedElement.getAttributeName()));
            }
        }

        return new Attributes(attributes, imports);
    }

    private Attributes processNamedGraphs(MetadataProject project, ClassAccessor accessor) {
        Set<String> attributes = new TreeSet<>();
        for (NamedEntityGraphMetadata namedEntityGraph : project.getNamedEntityGraphs(accessor)) {
            String name = namedEntityGraph.getName();
            // if name is not present, default to the entity name
            if (name == null) {
                if (accessor.isEntityAccessor()) {
                    name = ((EntityAccessor) accessor).getEntityName();
                } else {
                    name = getName(accessor.getAccessibleObjectName());
                }
            }
            attributes.add(NAMED_NAME_TEMPLATE.formatted("GRAPH",
                    toUpperCase(name), name));
            attributes.add(GRAPH_TEMPLATE.formatted(getName(accessor.getAccessibleObjectName()), toJavaIdentifier(name)));
        }
        return new Attributes(attributes,
                attributes.isEmpty()
                        ? Collections.emptyMap()
                        : Map.of("EntityGraph", "jakarta.persistence.EntityGraph"));
    }

    private Attributes processNamedQueries(MetadataProject project, ClassAccessor accessor) {
        Set<String> attributes = new TreeSet<>();
        Set<String> refAttributes = new HashSet<>();
        Map<String, String> imports = new HashMap<>();
        for (NamedQueryMetadata namedQueryMetadata : project.getNamedQueries(accessor)) {
            attributes.add(NAMED_NAME_TEMPLATE.formatted("QUERY",
                    toUpperCase(namedQueryMetadata.getName()), namedQueryMetadata.getName()));

            if (!namedQueryMetadata.getResultClass().isVoid()) {
                MetadataClass resultClass = namedQueryMetadata.getResultClass();
                refAttributes.add(REFERENCE_TEMPLATE.formatted(getUnqualifiedType(resultClass.getName(), imports), toJavaIdentifier(namedQueryMetadata.getName())));
            }
        }
        if (!refAttributes.isEmpty()) {
            imports.put("TypedQueryReference", "jakarta.persistence.TypedQueryReference");
        }
        attributes.addAll(refAttributes);
        return new Attributes(attributes, imports);
    }

    private Attributes processNamedSqlResultSets(MetadataProject project, ClassAccessor accessor) {
        Set<String> attributes = new TreeSet<>();
        for (SQLResultSetMappingMetadata sqlResultSetMapping : project.getNamedSQLResultSetMappings(accessor)) {
            attributes.add(NAMED_NAME_TEMPLATE.formatted("MAPPING",
                    toUpperCase(sqlResultSetMapping.getName()), sqlResultSetMapping.getName()));
        }
        return new Attributes(attributes, Collections.emptyMap());
    }

    /**
     * INTERNAL:
     */
    protected String writeImportStatements(Map<String, String> typeImports, ClassAccessor accessor, Writer writer, PersistenceUnit persistenceUnit, String childCanonicalpackage) throws IOException {
        String parentCanonicalName = null;

        // Get the sorted import list ready.
        Set<String> imps = new TreeSet<>(typeImports.values());

        // Add the standard canonical model generator imports.
        imps.add("jakarta.persistence.metamodel.StaticMetamodel");

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

        // Write out the imports.
        for (String typeImport : imps) {
            writer.append("import ").append(typeImport).append(";").append(System.lineSeparator());
        }

        writer.append(System.lineSeparator());
        return parentCanonicalName;
    }

    private void log(int level, String msg, Object... args) {
        log.log(level, SessionLog.PROCESSOR, msg, args, false);
    }
}
