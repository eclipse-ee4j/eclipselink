/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//javase imports
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import org.w3c.dom.Document;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * The EntityTypeBuilder is a factory class for creating and extending dynamic
 * entity types. After being constructed in either usage the application can
 * then use the provided API to customize mapping information of the type.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicTypeBuilder {

    static XMLParser xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();

    /**
     * The type being configured for dynamic use or being created/extended
     */
    protected DynamicTypeImpl entityType;

    /**
     * Create an EntityType for a new dynamic type. The contained EntityType and
     * its wrapped descriptor are not automatically added to any session. This
     * must be done by the application after the type's is fully configured.
     * <p>
     * <b>Creating new type Example</b>: <code>
     *  DynamicHelper helper = new DynamicHelper(session);
     *  DynamicClassLoader dcl = helper.getDynamicClassLoader();<br>
     *  <br>
     *  Class<?> javaType = dcl.creatDynamicClass("model.Simple");<br>
     *  <br>
     *  DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(javaType, null, "SIMPLE_TYPE");<br>
     *  typeBuilder.setPrimaryKeyFields("SID");<br>
     *  typeBuilder.addDirectMapping("id", int.class, "SID");<br>
     *  typeBuilder.addDirectMapping("value1", String.class, "VAL_1");<br>
     *  typeBuilder.addDirectMapping("value2", boolean.class, "VAL_2");<br>
     *  typeBuilder.addDirectMapping("value3", Calendar.class, "VAL_3");<br>
     *  typeBuilder.addDirectMapping("value4", Character.class, "VAL_4");<br>
     *  <br>
     *  helper.addTypes(true, true, typeBuilder.getType());<br>
     * </code>
     * 
     * @param dynamicClass
     * @param parentType
     * @param tableNames
     */
    public DynamicTypeBuilder(Class<?> dynamicClass, DynamicType parentType, String... tableNames) {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(dynamicClass);
        this.entityType = new DynamicTypeImpl(descriptor, parentType);
        // JAXB generates some classes that do not conform to DynamicEntity interface - ignore
        if (DynamicEntity.class.isAssignableFrom(dynamicClass)) {
            try {
                Field dpmField = dynamicClass.getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
                DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
                dpm.setType(entityType);
                entityType.setDynamicPropertiesManager(dpm);
            }
            catch (Exception e) {
                // this is bad! Without the dpmField, not much to do but die :-( ...
                e.printStackTrace();
                return;
            }
        }
        configure(descriptor, tableNames);
    }

    /**
     * Create an EntityTypeBuilder for an existing descriptor. This is used
     * 
     * @param dcl
     * @param descriptor
     * @param parentType
     *            provided since the InheritancePolicy on the descriptor may not
     *            have its parent descriptor initialized.
     */
    public DynamicTypeBuilder(DynamicClassLoader dcl, ClassDescriptor descriptor, DynamicType parentType) {
        this.entityType = new DynamicTypeImpl(descriptor, parentType);
        Class<?> dynamicClass = descriptor.getJavaClass();
        if (dynamicClass == null) {
            addDynamicClasses(dcl, descriptor.getJavaClassName(), parentType);
        }
        else {
            // JAXB generates some classes that do not conform to DynamicEntity interface - ignore
            if (DynamicEntity.class.isAssignableFrom(dynamicClass)) {
                try {
                    Field dpmField = 
                        descriptor.getJavaClass().getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
                    DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
                    dpm.setType(entityType);
                    entityType.setDynamicPropertiesManager(dpm);
                }
                catch (Exception e) {
                    // this is bad! Without the dpmField, not much to do but die :-( ...
                    e.printStackTrace();
                    return;
                }
            }
        }
        configure(descriptor);
    }

    /**
     * Register a {@link DynamicClassWriter} with the provided
     * {@link DynamicClassLoader} so that a dynamic class can be generated when
     * needed.
     */
    protected void addDynamicClasses(DynamicClassLoader dcl, String className, DynamicType parentType) {
        if (parentType == null) {
            dcl.addClass(className);
        } else {
            if (parentType.getJavaClass() == null) {
                dcl.addClass(className, new DynamicClassWriter(parentType.getClassName()));
            } else {
                dcl.addClass(className, parentType.getJavaClass());
            }
        }
    }

    /**
     * Initialize a new or existing descriptor configuring the necessary
     * policies as well as
     */
    protected void configure(ClassDescriptor descriptor, String... tableNames) {
        // Configure Table names if provided
        if (tableNames != null) {
            if (tableNames.length == 0) {
                if (descriptor.getTables().size() == 0) {
                    descriptor.descriptorIsAggregate();
                }
            } else {
                for (int index = 0; index < tableNames.length; index++) {
                    descriptor.addTableName(tableNames[index]);
                }
            }

        }

        for (int index = 0; index < descriptor.getMappings().size(); index++) {
            addMapping(descriptor.getMappings().get(index));
        }

        descriptor.setProperty(DynamicType.DESCRIPTOR_PROPERTY, entityType);
    }

    public DynamicType getType() {
        return this.entityType;
    }

    /**
     * Test if a mapping requires initialization when a new instance is created.
     * This is true for:
     * <ul>
     * <li>primitives
     * <li>collection mappings
     * <li>basic indirection references
     * </ul>
     * 
     * @see #newDynamicEntity() for creation and initialization
     */
    private boolean requiresInitialization(DatabaseMapping mapping) {
        if (mapping.isDirectToFieldMapping() && mapping.getAttributeClassification() != null && mapping.getAttributeClassification().isPrimitive()) {
            return true;
        }
        if (mapping.isForeignReferenceMapping()) {
            ForeignReferenceMapping frMapping = (ForeignReferenceMapping) mapping;
            return frMapping.usesIndirection() || frMapping.isCollectionMapping();
        }
        if (mapping.isAggregateObjectMapping() && !mapping.isXMLMapping()) {
            return !((AggregateObjectMapping) mapping).isNullAllowed();
        }
        return false;
    }

    /**
     * Set the PK field names on the underlying descriptor ensuring no duplicate
     * names are added.
     * 
     * @param pkFieldNames
     *            qualified or unqualified field names
     */
    public void setPrimaryKeyFields(String... pkFieldNames) {
        if (pkFieldNames != null && pkFieldNames.length > 0) {
            for (int index = 0; index < pkFieldNames.length; index++) {
                DatabaseField pkField = new DatabaseField(pkFieldNames[index]);

                if (!getType().getDescriptor().getPrimaryKeyFields().contains(pkField)) {
                    getType().getDescriptor().addPrimaryKeyFieldName(pkFieldNames[index]);
                }
            }
        }
    }

    /**
     * Allows {@link DirectToFieldMapping} (@Basic) mapping to be added to a
     * dynamic type through API. This method can be used on a new
     * {@link DynamicTypeImpl} that has yet to be added to a session and have
     * its descriptor initialized, or it can be called on an active
     * (initialized) descriptor.
     * <p>
     * There is no support currently for having the EclipseLink
     * {@link SchemaManager} generate ALTER TABLE calls so any new columns
     * expected must be added without the help of EclipseLink or use the
     * {@link SchemaManager#replaceObject(org.eclipse.persistence.tools.schemaframework.DatabaseObjectDefinition)}
     * to DROP and CREATE the table. WARNING: This will cause data loss.
     * 
     * @param javaType
     *            is the type of the attribute. If the type is a primitive it
     *            will be converted to the comparable non-primitive type.
     */
    public DirectToFieldMapping addDirectMapping(String name, Class<?> javaType, String fieldName) {
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setAttributeName(name);
        mapping.setFieldName(fieldName);
        mapping.setAttributeClassification(javaType);

        return (DirectToFieldMapping) addMapping(mapping);
    }

    /**
     * Allows {@link OneToOneMapping} (@OneToOne and @ManyToOne) mappings to be
     * added to a dynamic type through API. This method can be used on a new
     * {@link DynamicTypeImpl} that has yet to be added to a session and have
     * its descriptor initialized, or it can be called on an active
     * (initialized) descriptor.
     * <p>
     * There is no support currently for having the EclipseLink
     * {@link SchemaManager} generate ALTER TABLE calls so any new columns
     * expected must be added without the help of EclipseLink or use the
     * {@link SchemaManager#replaceObject(org.eclipse.persistence.tools.schemaframework.DatabaseObjectDefinition)}
     * to DROP and CREATE the table. WARNING: This will cause data loss.
     */
    public OneToOneMapping addOneToOneMapping(String name, DynamicType refType, String... fkFieldNames) {
        if (fkFieldNames == null || refType.getDescriptor().getPrimaryKeyFields().size() != fkFieldNames.length) {
            throw new IllegalArgumentException("Invalid FK field names: " + fkFieldNames + " for target: " + refType);
        }

        OneToOneMapping mapping = new OneToOneMapping();
        mapping.setAttributeName(name);
        mapping.setReferenceClass(refType.getJavaClass());

        for (int index = 0; index < fkFieldNames.length; index++) {
            String targetField = refType.getDescriptor().getPrimaryKeyFields().get(index).getName();
            mapping.addForeignKeyFieldName(fkFieldNames[index], targetField);
        }

        return (OneToOneMapping) addMapping(mapping);
    }

    /**
     * Add a {@link OneToManyMapping} to the {@link #entityType} being built or
     * extended. This mapping is created using standard foreign keys from the
     * source table(s) to the target table(s) and transparent indirection (
     * {@link IndirectList}).
     * 
     * @param name
     *            attribute name to use in the dynamic entity. Also the property
     *            name used to access the state of the entity
     * @param refType
     * @param fkFieldNames
     *            the FK field names specified in the same order to match the PK
     *            field names of the target class
     * 
     * @return the newly created, configured mappin. It will be initialized if
     *         the descriptor is already initialized.
     */
    public OneToManyMapping addOneToManyMapping(String name, DynamicType refType, String... fkFieldNames) {
        if (fkFieldNames == null || getType().getDescriptor().getPrimaryKeyFields().size() != fkFieldNames.length) {
            throw new IllegalArgumentException("Invalid FK field names: " + fkFieldNames + " for target: " + refType);
        }

        OneToManyMapping mapping = new OneToManyMapping();
        mapping.setAttributeName(name);
        mapping.setReferenceClass(refType.getJavaClass());

        for (int index = 0; index < fkFieldNames.length; index++) {
            String targetField = getType().getDescriptor().getPrimaryKeyFields().get(index).getName();
            mapping.addTargetForeignKeyFieldName(fkFieldNames[index], targetField);
        }

        mapping.useTransparentList();

        return (OneToManyMapping) addMapping(mapping);
    }

    /**
     * Add a {@link DirectCollectionMapping} to the {@link #entityType} being
     * built or extended. This mapping is created using standard foreign keys
     * from the target table(s) to the source table(s) and transparent
     * indirection ( {@link IndirectList}).
     * 
     * @param name
     *            attribute name to use in the dynamic entity. Also the property
     *            name used to access the state of the entity
     * @param targetTable
     *            name of table holding the direct values
     * @param valueColumn
     *            name of column in target table holding the direct value for
     *            the collection
     * @param valueType
     *            the type of the attribute assumed to be a known basic type
     * @param fkFieldNames
     *            the FK field names on the source table specified in order to
     *            match the PK field names on the source.
     * @return the new mapping configured and initialized (if the descriptor is
     *         already initialized.
     * @throws IllegalArgumentException
     */
    public DirectCollectionMapping addDirectCollectionMapping(String name, String targetTable, String valueColumn, Class<?> valueType, String... fkFieldNames) throws IllegalArgumentException {
        if (fkFieldNames == null || getType().getDescriptor().getPrimaryKeyFields().size() != fkFieldNames.length) {
            throw new IllegalArgumentException("Invalid FK field names: " + fkFieldNames + " for target: ");
        }

        DirectCollectionMapping mapping = new DirectCollectionMapping();
        mapping.setAttributeName(name);
        mapping.setReferenceTableName(targetTable);
        mapping.setDirectFieldName(valueColumn);
        mapping.setDirectFieldClassification(valueType);

        for (int index = 0; index < fkFieldNames.length; index++) {
            String targetField = getType().getDescriptor().getPrimaryKeyFields().get(index).getName();
            mapping.addReferenceKeyFieldName(fkFieldNames[index], targetField);
        }

        mapping.useTransparentList();

        return (DirectCollectionMapping) addMapping(mapping);
    }

    /**
     * Add a {@link AggregateObjectMapping} ({@link Embeddable} in JPA) to the
     * {@link #entityType} being built or extended.
     * 
     * @param name
     *            attribute name to use in the dynamic entity. Also the property
     *            name used to access the state of the entity
     * @param refType
     *            dynamic type re[presenting the Embeddable/AggregateObject
     * @param allowsNull
     *            true indicates that the attribute can be null if all values
     *            are null.
     * @return the new mapping configured and initialized (if the descriptor has
     *         been initialized).
     */
    public AggregateObjectMapping addAggregateObjectMapping(String name, DynamicType refType, boolean allowsNull) {
        AggregateObjectMapping mapping = new AggregateObjectMapping();
        mapping.setAttributeName(name);
        mapping.setReferenceClass(refType.getJavaClass());
        mapping.setIsNullAllowed(allowsNull);

        return (AggregateObjectMapping) addMapping(mapping);
    }

    /**
     * Add a {@link ManyToManyMapping} to the {@link #entityType} being built or
     * extended. This method assumes that the columns names on the relationship
     * table match the PK columns names they relate to. In the case of the
     * target keys from the relationship table a '_' will be appended to the
     * column names if they collide with the names from the source table.
     * 
     * @param name
     *            attribute name to use in the dynamic entity. Also the property
     *            name used to access the state of the entity
     * @param refType
     *            target dynamic entity
     * @param relationshipTableName
     */
    public void addManyToManyMapping(String name, DynamicType refType, String relationshipTableName) {
        ManyToManyMapping mapping = new ManyToManyMapping();
        mapping.setAttributeName(name);
        mapping.setReferenceClass(refType.getJavaClass());
        mapping.setRelationTableName(relationshipTableName);

        for (DatabaseField sourcePK : getType().getDescriptor().getPrimaryKeyFields()) {
            mapping.addSourceRelationKeyFieldName(sourcePK.getName(), sourcePK.getQualifiedName());
        }
        for (DatabaseField targetPK : refType.getDescriptor().getPrimaryKeyFields()) {
            String relField = targetPK.getName();
            if (mapping.getSourceRelationKeyFieldNames().contains(relField)) {
                relField = refType.getName() + "_" + relField;
            }
            mapping.addTargetRelationKeyFieldName(relField, targetPK.getQualifiedName());
        }
        mapping.useTransparentList();

        addMapping(mapping);
    }

    /**
     * Add the mapping to the {@link #entityType}'s descriptor being built or
     * extended. This is where the {@link ValuesAccessor} is created and the
     * position of the mapping in the descriptor is captured to use as its
     * index.
     */
    public DatabaseMapping addMapping(DatabaseMapping mapping) {
        ClassDescriptor descriptor = getType().getDescriptor();

        if (!descriptor.getMappings().contains(mapping)) {
            descriptor.addMapping(mapping);
        }

        int index = descriptor.getMappings().indexOf(mapping);

        // Need to account for inherited mappings. When initialized a child
        // descriptor has all of its parent mappings added ahead of its
        // mappings. This adds the necessary offset.
        if (getType().getParentType() != null) {
            DynamicType current = getType();
            while (current.getParentType() != null) {
                index += current.getParentType().getDescriptor().getMappings().size();
                current = current.getParentType();
            }
        }

        // Try to configure the attribute classification if name is available
        if (mapping.getAttributeClassification() == null && mapping.isAbstractDirectMapping()) {
            String typeName = ((AbstractDirectMapping) mapping).getAttributeClassificationName();
            if (typeName != null) {
                // Remove any additional padding
                typeName = typeName.trim();
                try {
                    Class<?> attrType = ConversionManager.getDefaultManager().convertClassNameToClass(typeName);
                    ((AbstractDirectMapping) mapping).setAttributeClassification(attrType);
                } catch (Exception e) {
                }
            }
        }

        mapping.setAttributeAccessor(new ValuesAccessor(mapping));

        if (requiresInitialization(mapping)) {
            this.entityType.getMappingsRequiringInitialization().add(mapping);
        }

        return mapping;
    }

    /**
     * Configure default sequencing.
     */
    public void configureSequencing(String numberName, String numberFieldName) {
        getType().getDescriptor().setSequenceNumberName(numberName);
        getType().getDescriptor().setSequenceNumberFieldName(numberFieldName);
    }

    /**
     * Configure sequencing specifying the sequence type to use.
     */
    public void configureSequencing(Sequence sequence, String numberName, String numberFieldName) {
        configureSequencing(numberName, numberFieldName);
        getType().getDescriptor().setSequence(sequence);
    }
    
    public DynamicEnumBuilder addEnum(String fieldName, String className, String columnName,
        DynamicClassLoader dcl) {
        dcl.addEnum(className, (Object)null);
        ClassDescriptor desc = getType().getDescriptor();
        AbstractDirectMapping adm = addDirectMappingForEnum(fieldName, className, columnName);
        return new DynamicEnumBuilder(className, adm, dcl);
    }

    protected AbstractDirectMapping addDirectMappingForEnum(String fieldName, String className,
        String columnName) {
        DirectToFieldMapping dtfm = addDirectMapping(fieldName, null, columnName);
        dtfm.setConverter(new EnumTypeConverter(dtfm, className));
        addMapping(dtfm);
        return dtfm;
    }

    /**
     * Load a dynamic project from deployment XML creating dynamic types for all
     * descriptors where the provided class name does not exist.
     * 
     * @param resourcePath
     * @param login
     * @param dynamicClassLoader
     * @return a Project with {@link DynamicClassLoader} and associated
     *         {@link DynamicClassWriter} configured. Ensure if a new
     *         Login/Platform is being configured that the
     *         {@link ConversionManager#getLoader()} is maintained.
     *         <p>
     *         <tt>null</tt> is returned if the resourcePath cannot locate a
     *         deployment XML
     * @throws IOException
     */
    public static Project loadDynamicProject(String resourcePath, DatabaseLogin login, DynamicClassLoader dynamicClassLoader) throws IOException {

        if (resourcePath == null) {
            throw new NullPointerException("null resourceStream");
        }
        if (dynamicClassLoader == null) {
            throw new NullPointerException("null dynamicClassLoader");
        }
        return loadDynamicProject(dynamicClassLoader.getResourceAsStream(resourcePath), login, dynamicClassLoader);
    }

    
    
    /**
     * Load a dynamic project from deployment XML creating dynamic types for all
     * descriptors where the provided class name does not exist.
     * 
     * @param resourceStream
     * @param login
     * @param dynamicClassLoader
     * @return a Project with {@link DynamicClassLoader} and associated
     *         {@link DynamicClassWriter} configured. Ensure if a new
     *         Login/Platform is being configured that the
     *         {@link ConversionManager#getLoader()} is maintained.
     *         <p>
     *         <tt>null</tt> is returned if the resourcePath cannot locate a
     *         deployment XML
     * @throws IOException
     */
    public static Project loadDynamicProject(InputStream resourceStream, DatabaseLogin login, DynamicClassLoader dynamicClassLoader) throws IOException {

        if (resourceStream == null) {
            throw new NullPointerException("null resourceStream");
        }
        if (dynamicClassLoader == null) {
            throw new NullPointerException("null dynamicClassLoader");
        }

        // Build an OXM project that loads the deployment XML without converting
        // the class names into classes
        ObjectPersistenceWorkbenchXMLProject opmProject = new ObjectPersistenceWorkbenchXMLProject();
        Document document = xmlParser.parse(resourceStream);
        Project project = XMLProjectReader.readObjectPersistenceRuntimeFormat(document, dynamicClassLoader, opmProject);
        return loadDynamicProject(project, login, dynamicClassLoader);
    }

    public static Project loadDynamicProject(Project project, DatabaseLogin login, DynamicClassLoader dynamicClassLoader) {
        if (project != null) {
            if (login == null) {
                if (project.getLogin() == null) {
                    project.setLogin(new DatabaseLogin());
                }
            } else {
                project.setLogin(login);
            }
            if (project.getLogin().getPlatform() == null) {
                project.getLogin().setPlatform(new DatabasePlatform());
            }

            project.getLogin().getPlatform().getConversionManager().setLoader(dynamicClassLoader);

            for (Iterator<?> i = project.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor descriptor = (ClassDescriptor) i.next();
                if (descriptor.getJavaClass() == null) {
                    createType(dynamicClassLoader, descriptor, project);
                }
            }
            project.convertClassNamesToClasses(dynamicClassLoader);
            for (Iterator<?> i = project.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor descriptor = (ClassDescriptor) i.next();
                Class<?> dynamicClass = descriptor.getJavaClass();
                // JAXB generates some classes that do not conform to DynamicEntity interface - ignore
                if (dynamicClass != null && DynamicEntity.class.isAssignableFrom(dynamicClass)) {
                    DynamicType type = DynamicHelper.getType(descriptor);
                    try {
                        Field dpmField = 
                            descriptor.getJavaClass().getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
                        DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
                        dpm.setType(type);
                        ((DynamicTypeImpl)type).setDynamicPropertiesManager(dpm);
                    }
                    catch (Exception e) {
                        // this is bad! Without the dpmField, not much to do but die :-( ...
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return project;
    }

    /**
     * Create EntityType for a descriptor including the creation of a new
     * dynamic type. This method needs to handle inheritance where the parent
     * type needs to be defined before this type.
     */
    private static DynamicType createType(DynamicClassLoader dcl, ClassDescriptor descriptor, Project project) {
        Class<?> javaClass = null;
        try {
            javaClass = dcl.loadClass(descriptor.getJavaClassName());
        } catch (ClassNotFoundException e) {
        }

        if (javaClass != null) {
            descriptor.setJavaClass(javaClass);
        }

        DynamicType parent = null;

        if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().getParentClassName() != null) {
            ClassDescriptor parentDesc = null;
            for (Iterator<?> i = project.getOrderedDescriptors().iterator(); parentDesc == null && i.hasNext();) {
                ClassDescriptor d = (ClassDescriptor) i.next();
                if (d.getJavaClassName().equals(descriptor.getInheritancePolicy().getParentClassName())) {
                    parentDesc = d;
                }
            }

            if (parentDesc == null) {
                throw ValidationException.missingDescriptor(descriptor.getInheritancePolicy().getParentClassName());
            }

            parent = DynamicHelper.getType(parentDesc);
            if (parent == null) {
                parent = createType(dcl, parentDesc, project);
            }
        }

        DynamicType type = DynamicHelper.getType(descriptor);
        if (type == null) {
            type = new DynamicTypeBuilder(dcl, descriptor, parent).getType();
        }

        // JAXB generates some classes that do not conform to DynamicEntity interface - ignore
        if (javaClass != null && DynamicEntity.class.isAssignableFrom(javaClass)) {
            try {
                Field dpmField = javaClass.getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
                DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
                dpm.setType(type);
                ((DynamicTypeImpl)type).setDynamicPropertiesManager(dpm);
            }
            catch (Exception e) {
                // this is bad! Without the dpmField, not much to do but die :-( ...
                e.printStackTrace();
                return null;
            }
        }
        
        return type;
    }
}