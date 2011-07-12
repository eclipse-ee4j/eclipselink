/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     08/19/2010-2.2 Guy Pelletier 
 *       - 282733: Add plural converter annotations
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     09/16/2010-2.2 Guy Pelletier 
 *       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification 
 *     07/11/2011-2.2.1 Guy Pelletier
 *       - 343632: Can't map a compound constraint because of exception: 
 *                 The reference column name [y] mapped on the element [field x] 
 *                 does not correspond to a valid field on the mapping reference
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.eclipse.persistence.annotations.HashPartitioning;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.Partitioning;
import org.eclipse.persistence.annotations.PinnedPartitioning;
import org.eclipse.persistence.annotations.RangePartitioning;
import org.eclipse.persistence.annotations.ReplicationPartitioning;
import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.annotations.StructConverters;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.TypeConverters;
import org.eclipse.persistence.annotations.UnionPartitioning;
import org.eclipse.persistence.annotations.ValuePartitioning;
import org.eclipse.persistence.annotations.RoundRobinPartitioning;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.HashPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PinnedPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ReplicationPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RoundRobinPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.UnionPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * INTERNAL:
 * Common metadata accessor level for mappings and classes.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class MetadataAccessor extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.
    
    private AccessMethodsMetadata m_accessMethods;
    
    private List<ConverterMetadata> m_converters = new ArrayList<ConverterMetadata>();
    private List<ObjectTypeConverterMetadata> m_objectTypeConverters = new ArrayList<ObjectTypeConverterMetadata>();
    private List<StructConverterMetadata> m_structConverters = new ArrayList<StructConverterMetadata>();
    private List<TypeConverterMetadata> m_typeConverters = new ArrayList<TypeConverterMetadata>();
    private List<PropertyMetadata> m_properties = new ArrayList<PropertyMetadata>();
    
    private MetadataDescriptor m_descriptor;
    private MetadataProject m_project;

    protected PartitioningMetadata partitioning;
    protected ReplicationPartitioningMetadata replicationPartitioning;
    protected RoundRobinPartitioningMetadata roundRobinPartitioning;
    protected PinnedPartitioningMetadata pinnedPartitioning;
    protected RangePartitioningMetadata rangePartitioning;
    protected ValuePartitioningMetadata valuePartitioning;
    protected UnionPartitioningMetadata unionPartitioning;
    protected HashPartitioningMetadata hashPartitioning;
    protected String partitioned;
    
    private String m_access;
    private String m_name;
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public MetadataAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project) {
        super(annotation, accessibleObject);
        
        m_project = project;
        setDescriptor(descriptor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataAccessor) {
            MetadataAccessor accessor = (MetadataAccessor) objectToCompare;
                        
            if (! valuesMatch(m_accessMethods, accessor.getAccessMethods())) {
                return false;
            }
            
            if (! valuesMatch(m_converters, accessor.getConverters())) {
                return false;
            }
            
            if (! valuesMatch(m_objectTypeConverters, accessor.getObjectTypeConverters())) {
                return false;
            }
            
            if (! valuesMatch(m_structConverters, accessor.getStructConverters())) {
                return false;
            }
            
            if (! valuesMatch(m_typeConverters, accessor.getTypeConverters())) {
                return false;
            }
            
            if (! valuesMatch(m_properties, accessor.getProperties())) {
                return false;
            }
           
            if (! valuesMatch(m_access, accessor.getAccess())) {
                return false;
            }
            
            return valuesMatch(m_name, accessor.getName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getAccess() {
        return m_access;
    }
    
    /**
     * INTERNAL:
     * Returns the accessible object for this accessor.
     */
    public MetadataAnnotatedElement getAccessibleObject() {
        return (MetadataAnnotatedElement) super.getAccessibleObject();
    }
    
    /**
     * INTERNAL:
     * Returns the name of the accessible object. If it is a field, it will 
     * return the field name. For a method it will return the method name.
     */
    public String getAccessibleObjectName() {
        return getAccessibleObject().getName();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public AccessMethodsMetadata getAccessMethods() {
        return m_accessMethods;
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public MetadataAnnotatedElement getAnnotatedElement() {
        return getAccessibleObject();
    }
    
    /**
     * INTERNAL:
     * Return the annotated element name for this accessor.
     */
    public String getAnnotatedElementName() {
        return getAnnotatedElement().toString();
    }
    
    /**
     * INTERNAL:
     * Return the annotation if it exists.
     */
    protected MetadataAnnotation getAnnotation(Class annotation) {
        return getAnnotation(annotation.getName());
    }
    
    /**
     * INTERNAL:
     * Return the annotation if it exists.
     */
    protected abstract MetadataAnnotation getAnnotation(String annotation);
    
    /**
     * INTERNAL:
     * Return the attribute name for this accessor.
     */
    public String getAttributeName() {
        return getAccessibleObject().getAttributeName();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConverterMetadata> getConverters() {
        return m_converters;
    }
    
    /**
     * INTERNAL:
     * Return the MetadataDescriptor for this accessor.
     */
    public MetadataDescriptor getDescriptor() {
        return m_descriptor;
    }
    
    /**
     * INTERNAL:
     * Return the java class tied to this class accessor's descriptor.
     */
    public MetadataClass getDescriptorJavaClass() {
        return m_descriptor.getJavaClass();
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
    }
    
    /**
     * INTERNAL:
     * Return the java class associated with this accessor's descriptor.
     */
    public MetadataClass getJavaClass() {
        return m_descriptor.getJavaClass();
    }
    
    /**
     * INTERNAL:
     * Return the java class that defines this accessor.
     */
    protected String getJavaClassName() {
        return getJavaClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the metadata logger.
     */
    public MetadataLogger getLogger() {
        return m_project.getLogger();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     *
     * In some cases, both the name and defaultName could be "" or null,
     * therefore, don't log a message and return name.
     */
    protected String getName(String name, String defaultName, String context) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getName(name, defaultName, context, getLogger(), getAnnotatedElementName());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ObjectTypeConverterMetadata> getObjectTypeConverters() {
        return m_objectTypeConverters;
    }
    
    /**
     * INTERNAL:
     * Return the MetadataProject.
     */
    public MetadataProject getProject() {
        return m_project;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */    
    public List<PropertyMetadata> getProperties() {
        return m_properties;
    }
    
    /**
     * INTERNAL:
     * Return the referenced field.  If the referencedColumnName is not 
     * specified, it will default to the primary key of the referenced table.
     */
    protected DatabaseField getReferencedField(String referencedColumnName, MetadataDescriptor referenceDescriptor, String context) {
        return getReferencedField(referencedColumnName, referenceDescriptor, context, false);
    }
    
    /**
     * INTERNAL:
     * Return the referenced field.  If the referencedColumnName is not 
     * specified, it will default to the primary key of the referenced table.
     */
    protected DatabaseField getReferencedField(String referencedColumnName, MetadataDescriptor referenceDescriptor, String context, boolean isForAggregateCollection) {
        DatabaseField referenceField;
        
        if (referencedColumnName == null || referencedColumnName.equals("")) {
            referenceField = referenceDescriptor.getPrimaryKeyField();
            
            // <hack> If we are an inheritance subclass in a joined strategy,
            // for an aggregate collection, we need to return the multi table 
            // primary key field if there is one. All other mappings seem to be
            // happy with the primary key field ... this seems to be a bug with
            // AggregateCollectionMapping in that it doesn't resolve the
            // primary key fields correctly </hack>
            // TODO: have a look at this again at some point.
            if (referenceDescriptor.isInheritanceSubclass() && isForAggregateCollection) {
                referenceField = referenceDescriptor.getPrimaryKeyJoinColumnAssociationField(referenceField);
            }
            
            // Log the defaulting field name based on the given context.
            getLogger().logConfigMessage(context, getAnnotatedElementName(), referenceField.getName());
        } else {
            // Let's try to look up the referenced field using the referenced
            // column name.
            referenceField = referenceDescriptor.getField(referencedColumnName);
            
            if (referenceField == null) {
                // Ok so we still haven't found the referenced field. We will
                // need to take the delimeters and the upper cassing flag into
                // consideration. To do that, we must use a DatabaseField since
                // the logic resides there. It would be nice to avoid creating
                // database fields to look up actual referenced fields but for
                // now this will do (better than what we had before :-) ).
                DatabaseField lookupField = new DatabaseField();
                setFieldName(lookupField, referencedColumnName);
                referenceField = referenceDescriptor.getField(lookupField.getNameForComparisons());
            
                if (referenceField == null) {
                    // So here's the thing, there are a few reasons why we 
                    // wouldn't have a reference field at this point:
                    //
                    // 1 - if we are processing a mapping accessor (e.g. element 
                    // collection) for a mapped superclass descriptor 
                    // (metamodel) where the mapped superclass may or may not 
                    // define a source ID field. So we just don't know and can't 
                    // know in this context)
                    //
                    // 2 - the user has mapped a reference column to a non id 
                    // field. See bug 343632 for more information. It's an 
                    // example of going beyond the spec since the user has 
                    // mapped a reference column name to another relationship 
                    // mapping. We can't lookup the field for this mapping since 
                    // we can't guaranty that its accessor has been processed. 
                    // Basic (id) accessors are all processed in stage one, 
                    // relationship accessors are processed only in stage 3 
                    // after we know all the basics (ids) have been processed.
                    //
                    // 3 - the field just doesn't exist. In this case, the user
                    // will eventually get a DB error on persist.
                    //
                    // By returning the lookup field, what do we lose? Well, we 
                    // could lose some field definitions (precision, length, 
                    // scale) which would generate different DDL (if turned on).
                    //
                    // What do we gain? We continue to do our best to lookup
                    // reference fields and keep them in sync. We do that
                    // correctly up to what the spec requires. Anything beyond
                    // that we just assume the field is valid and return it (as 
                    // we did in the past, before bug 317286) and users can map 
                    // to non-id fields.
                    //
                    referenceField = lookupField;
                    referenceField.setTable(referenceDescriptor.getPrimaryKeyTable());
                    
                    // Log warning to the user that we will use the referenced
                    // column name they provided.
                    getLogger().logWarningMessage(MetadataLogger.REFERENCED_COLUMN_NOT_FOUND, referencedColumnName, getAnnotatedElement());
                }
            }
        }
        
        return referenceField;   
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<StructConverterMetadata> getStructConverters() {
        return m_structConverters;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<TypeConverterMetadata> getTypeConverters() {
        return m_typeConverters;
    }
    
    /**
     * INTERNAL:
     * Return the upper cased attribute name for this accessor. Used when
     * defaulting.
     */
    protected String getDefaultAttributeName() {
        return (m_project.useDelimitedIdentifier()) ? getAttributeName() : getAttributeName().toUpperCase();
    }
    
    /**
     * INTERNAL:
     * Return the upper case java class that defines this accessor.
     */
    protected String getUpperCaseShortJavaClassName() {
        return Helper.getShortClassName(getJavaClassName()).toUpperCase();
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value. 
     */
    protected Integer getValue(Integer value, Integer defaultValue) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getValue(value, defaultValue);
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value.
     */
    protected String getValue(String value, String defaultValue) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getValue(value, defaultValue);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAccess() {
        return m_access != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAccessMethods() {
        return m_accessMethods != null;
    }
    
    /**
     * INTERNAL: 
     * Called from annotation and xml initialization.
     */
    public void initAccess() {
        // Look for an annotation as long as an access type hasn't already been 
        // loaded from XML (meaning m_access will not be null at this point)
        if (m_access == null) {
            MetadataAnnotation access = getAnnotation(MetadataConstants.ACCESS_ANNOTATION);
            if (access != null) {
                setAccess((String) access.getAttribute("value"));
            }
        }
    }
    
    /**
     * INTERNAL: 
     * This method should be subclassed in those methods that need to do 
     * extra initialization.
     */
    public void initXMLAccessor(MetadataDescriptor descriptor, MetadataProject project) {
        m_project = project;
        setDescriptor(descriptor);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize single objects.
        initXMLObject(m_accessMethods, accessibleObject);
        
        initXMLObject(this.partitioning, accessibleObject);
        initXMLObject(this.replicationPartitioning, accessibleObject);
        initXMLObject(this.roundRobinPartitioning, accessibleObject);
        initXMLObject(this.pinnedPartitioning, accessibleObject);
        initXMLObject(this.rangePartitioning, accessibleObject);
        initXMLObject(this.valuePartitioning, accessibleObject);
        initXMLObject(this.hashPartitioning, accessibleObject);
        initXMLObject(this.unionPartitioning, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_converters, accessibleObject);
        initXMLObjects(m_objectTypeConverters, accessibleObject);
        initXMLObjects(m_structConverters, accessibleObject);
        initXMLObjects(m_typeConverters, accessibleObject);
        initXMLObjects(m_properties, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Return the annotation if it exists.
     */
    protected abstract boolean isAnnotationPresent(Class<? extends Annotation> annotation);
    
    /**
     * Subclasses must handle this flag.
     * @return
     */
    public abstract boolean isProcessed();
    
    /**
     * INTERNAL:
     * We currently limit this merging to the ClassAccessor level.
     */
    @Override
    public void merge(ORMetadata metadata) {
        MetadataAccessor accessor = (MetadataAccessor) metadata;
        
        // Simple object merging.
        m_access = (String) mergeSimpleObjects(m_access, accessor.getAccess(), accessor, "@access");
        
        // ORMetadata object merging.        
        m_accessMethods = (AccessMethodsMetadata) mergeORObjects(m_accessMethods, accessor.getAccessMethods());
        
        // ORMetadata list merging.
        m_converters = mergeORObjectLists(m_converters, accessor.getConverters());
        m_objectTypeConverters = mergeORObjectLists(m_objectTypeConverters, accessor.getObjectTypeConverters());
        m_structConverters = mergeORObjectLists(m_structConverters, accessor.getStructConverters());
        m_typeConverters = mergeORObjectLists(m_typeConverters, accessor.getTypeConverters());
        m_properties = mergeORObjectLists(m_properties, accessor.getProperties());
        
        this.partitioned = (String) mergeSimpleObjects(this.partitioned, accessor.getPartitioned(), accessor, "<partitioned>");
        this.partitioning = (PartitioningMetadata) mergeORObjects(this.partitioning, accessor.getPartitioning());
        this.replicationPartitioning = (ReplicationPartitioningMetadata) mergeORObjects(this.replicationPartitioning, accessor.getReplicationPartitioning());
        this.roundRobinPartitioning = (RoundRobinPartitioningMetadata) mergeORObjects(this.roundRobinPartitioning, accessor.getRoundRobinPartitioning());
        this.pinnedPartitioning = (PinnedPartitioningMetadata) mergeORObjects(this.pinnedPartitioning, accessor.getPinnedPartitioning());
        this.rangePartitioning = (RangePartitioningMetadata) mergeORObjects(this.rangePartitioning, accessor.getRangePartitioning());
        this.valuePartitioning = (ValuePartitioningMetadata) mergeORObjects(this.valuePartitioning, accessor.getValuePartitioning());
        this.hashPartitioning = (HashPartitioningMetadata) mergeORObjects(this.hashPartitioning, accessor.getHashPartitioning());
        this.unionPartitioning = (UnionPartitioningMetadata) mergeORObjects(this.unionPartitioning, accessor.getUnionPartitioning());
    }
    
    /**
     * INTERNAL:
     * Every accessor knows how to process themselves since they have all the
     * information they need.
     */
    public abstract void process();
    
    /**
     * INTERNAL:
     * Process and add the globally defined converters to the project.
     */
    public void processConverters() {
        // Process the custom converters if defined.
        processCustomConverters();
        
        // Process the object type converters if defined.
        processObjectTypeConverters();
        
        // Process the type converters if defined.
        processTypeConverters();
        
        // Process the struct converters if defined
        processStructConverters();
    }
    
    /**
     * INTERNAL:
     * Process the XML defined converters and check for a Converter annotation. 
     */
    protected void processCustomConverters() {
        // Check for XML defined converters.
        for (ConverterMetadata converter : m_converters) {
            m_project.addConverter(converter);
        }
        
        // Check for a Converters annotation
        MetadataAnnotation converters = getAnnotation(Converters.class);
        if (converters != null) {
            for (Object converter : (Object[]) converters.getAttributeArray("value")) {
                m_project.addConverter(new ConverterMetadata((MetadataAnnotation) converter, getAccessibleObject()));
            }
        }
        
        // Check for a Converter annotation.
        MetadataAnnotation converter = getAnnotation(Converter.class);
        if (converter != null) {
            m_project.addConverter(new ConverterMetadata(converter, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the XML defined object type converters and check for an 
     * ObjectTypeConverter annotation. 
     */
    protected void processObjectTypeConverters() {
        // Check for XML defined object type converters.
        for (ObjectTypeConverterMetadata objectTypeConverter : m_objectTypeConverters) {
            m_project.addConverter(objectTypeConverter);
        }
        
        // Check for an ObjectTypeConverters annotation
        MetadataAnnotation objectTypeConverters = getAnnotation(ObjectTypeConverters.class);
        if (objectTypeConverters != null) {
            for (Object objectTypeConverter : (Object[]) objectTypeConverters.getAttributeArray("value")) {
                m_project.addConverter(new ObjectTypeConverterMetadata((MetadataAnnotation) objectTypeConverter, getAccessibleObject()));
            }
        }
        
        // Check for an ObjectTypeConverter annotation.
        MetadataAnnotation objectTypeConverter = getAnnotation(ObjectTypeConverter.class);
        if (objectTypeConverter != null) {
            m_project.addConverter(new ObjectTypeConverterMetadata(objectTypeConverter, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the primary key join columms for this accessors annotated element.
     */    
    protected List<PrimaryKeyJoinColumnMetadata> processPrimaryKeyJoinColumns(PrimaryKeyJoinColumnsMetadata primaryKeyJoinColumns) {
        // If the primary key join columns were not specified (that is empty),
        // this call will add any defaulted columns as necessary.
         List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = primaryKeyJoinColumns.values(m_descriptor);
        
        if (m_descriptor.hasCompositePrimaryKey()) {
            // Validate the number of primary key fields defined.
            if (pkJoinColumns.size() != m_descriptor.getPrimaryKeyFields().size()) {
                throw ValidationException.incompletePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
            }
            
            // All the primary and foreign key field names should be specified.
            for (PrimaryKeyJoinColumnMetadata pkJoinColumn : pkJoinColumns) {
                if (pkJoinColumn.isPrimaryKeyFieldNotSpecified() || pkJoinColumn.isForeignKeyFieldNotSpecified()) {
                    throw ValidationException.incompletePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
                }
            }
        } else {
            if (pkJoinColumns.size() > 1) {
                throw ValidationException.excessivePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
            }
        }
        
        return pkJoinColumns;
    }
      
    /**
     * INTERNAL:
     * Process the XML defined struct converters and check for a StructConverter 
     * annotation. 
     */
    protected void processStructConverters() {
        // Check for XML defined struct converters.
        for (StructConverterMetadata structConverter : m_structConverters) {
            m_project.addConverter(structConverter);
        }
        
        // Check for a StructConverters annotation
        MetadataAnnotation structConverters = getAnnotation(StructConverters.class);
        if (structConverters != null) {
            for (Object structConverter : (Object[]) structConverters.getAttributeArray("value")) {
                m_project.addConverter(new StructConverterMetadata((MetadataAnnotation) structConverter, getAccessibleObject()));
            }
        }
        
        // Check for a StructConverter annotation.
        MetadataAnnotation converter = getAnnotation(StructConverter.class);
        if (converter != null) {
            m_project.addConverter(new StructConverterMetadata(converter, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Common table processing for table, secondary table, join table and
     * collection table.
     */
    protected void processTable(TableMetadata table, String defaultName) {
        getProject().processTable(table, defaultName, m_descriptor.getDefaultCatalog(), m_descriptor.getDefaultSchema());
    }
    
    /**
     * INTERNAL:
     * Process a the XML defined type converters and check for a TypeConverter 
     * annotation. 
     */
    protected void processTypeConverters() {
        // Check for XML defined type converters.
        for (TypeConverterMetadata typeConverter : m_typeConverters) {
            m_project.addConverter(typeConverter);
        }
        
        // Check for a TypeConverters annotation
        MetadataAnnotation typeConverters = getAnnotation(TypeConverters.class);
        if (typeConverters != null) {
            for (Object typeConverter : (Object[]) typeConverters.getAttributeArray("value")) {
                m_project.addConverter(new TypeConverterMetadata((MetadataAnnotation) typeConverter, getAccessibleObject()));
            }
        }
        
        // Check for an TypeConverter annotation.
        MetadataAnnotation typeConverter = getAnnotation(TypeConverter.class);
        if (typeConverter != null) {
            m_project.addConverter(new TypeConverterMetadata(typeConverter, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccess(String access) {
        m_access = access;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccessMethods(AccessMethodsMetadata accessMethods){
        m_accessMethods = accessMethods;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConverters(List<ConverterMetadata> converters) {
        m_converters = converters;
    }
    
    /**
     * INTERNAL: 
     * Set the metadata descriptor for this accessor. When setting the
     * descriptor on entities, the owning descriptor is set to this descriptor.
     */
    public void setDescriptor(MetadataDescriptor descriptor) {
        m_descriptor = descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public void setFieldName(DatabaseField field, String name) {
        // This may set the use delimited identifier flag to true.
        field.setName(name, Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
        
        // The check is necessary to avoid overriding a true setting (set after 
        // setting the name of the field). We don't want to override it at this
        // point if the global flag is set to false. 
        if (m_project.useDelimitedIdentifier()) {
            field.setUseDelimiters(true);
        } else if (m_project.getShouldForceFieldNamesToUpperCase() && ! field.shouldUseDelimiters()) {
            field.useUpperCaseForComparisons(true);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setFieldName(DatabaseField field, String defaultName, String context) {
        setFieldName(field, getName(field.getName(), defaultName, context));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setObjectTypeConverters(List<ObjectTypeConverterMetadata> objectTypeConverters) {
        m_objectTypeConverters = objectTypeConverters;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */    
    public void setProperties(List<PropertyMetadata> properties) {
        m_properties = properties;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStructConverters(List<StructConverterMetadata> structConverters) {
        m_structConverters = structConverters;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTypeConverters(List<TypeConverterMetadata> typeConverters) {
        m_typeConverters = typeConverters;
    }
    
    /**
     * Process the partitioning policies defined on this element.
     */
    protected void processPartitioning() {
        boolean found = false;
        // Check for XML defined partitioning.
        if (this.replicationPartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.replicationPartitioning);
        }
        if (this.roundRobinPartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.roundRobinPartitioning);
        }
        if (this.partitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.partitioning);
        }
        if (this.rangePartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.rangePartitioning);
        }
        if (this.valuePartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.valuePartitioning);
        }
        if (this.hashPartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.hashPartitioning);
        }
        if (this.unionPartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.unionPartitioning);
        }
        if (this.pinnedPartitioning != null) {
            found = true;
            m_project.addPartitioningPolicy(this.pinnedPartitioning);
        }
        
        // Check for partitioning annotations.
        MetadataAnnotation annotation = getAnnotation(Partitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new PartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(ReplicationPartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new ReplicationPartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(RoundRobinPartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new RoundRobinPartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(UnionPartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new UnionPartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(RangePartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new RangePartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(ValuePartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new ValuePartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(ValuePartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new ValuePartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(PinnedPartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new PinnedPartitioningMetadata(annotation, getAccessibleObject()));
        }
        annotation = getAnnotation(HashPartitioning.class);
        if (annotation != null) {
            found = true;
            m_project.addPartitioningPolicy(new HashPartitioningMetadata(annotation, getAccessibleObject()));
        }
        boolean processed = false;
        if (this.partitioned != null) {
            processed = true;
            processPartitioned(this.partitioned);
        }
        annotation = getAnnotation(Partitioned.class);
        if (!processed && annotation != null) {
            processed = true;
            processPartitioned((String)annotation.getAttribute("value"));
        }
        if (found && !processed) {
            getLogger().logWarningMessage(MetadataLogger.WARNING_PARTIONED_NOT_SET, getJavaClass(), getAccessibleObject());
        }
    }
    
    /**
     * Set the policy on the descriptor or mapping.
     */
    public void processPartitioned(String name) {
        if (this instanceof ClassAccessor) {
            if (getDescriptor().getClassDescriptor().getPartitioningPolicy() != null) {
                // We must be processing a mapped superclass setting for an
                // entity that has its own setting. Ignore it and log a warning.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_ANNOTATION, Partitioned.class, getJavaClass(), getDescriptor().getJavaClass());
            }
            getDescriptor().getClassDescriptor().setPartitioningPolicyName(name);
        } else if (this instanceof MappingAccessor) {
            ((ForeignReferenceMapping)((MappingAccessor)this).getMapping()).setPartitioningPolicyName(name);
        }
    }
    
    public PartitioningMetadata getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(PartitioningMetadata partitioning) {
        this.partitioning = partitioning;
    }

    public RangePartitioningMetadata getRangePartitioning() {
        return rangePartitioning;
    }

    public void setRangePartitioning(RangePartitioningMetadata rangePartitioning) {
        this.rangePartitioning = rangePartitioning;
    }

    public ValuePartitioningMetadata getValuePartitioning() {
        return valuePartitioning;
    }

    public void setValuePartitioning(ValuePartitioningMetadata valuePartitioning) {
        this.valuePartitioning = valuePartitioning;
    }

    public UnionPartitioningMetadata getUnionPartitioning() {
        return unionPartitioning;
    }

    public void setUnionPartitioning(UnionPartitioningMetadata unionPartitioning) {
        this.unionPartitioning = unionPartitioning;
    }

    public ReplicationPartitioningMetadata getReplicationPartitioning() {
        return replicationPartitioning;
    }

    public void setReplicationPartitioning(ReplicationPartitioningMetadata replicationPartitioning) {
        this.replicationPartitioning = replicationPartitioning;
    }

    public RoundRobinPartitioningMetadata getRoundRobinPartitioning() {
        return roundRobinPartitioning;
    }

    public void setRoundRobinPartitioning(RoundRobinPartitioningMetadata roundRobinPartitioning) {
        this.roundRobinPartitioning = roundRobinPartitioning;
    }

    public HashPartitioningMetadata getHashPartitioning() {
        return hashPartitioning;
    }

    public void setHashPartitioning(HashPartitioningMetadata hashPartitioning) {
        this.hashPartitioning = hashPartitioning;
    }

    public PinnedPartitioningMetadata getPinnedPartitioning() {
        return pinnedPartitioning;
    }

    public void setPinnedPartitioning(PinnedPartitioningMetadata pinnedPartitioning) {
        this.pinnedPartitioning = pinnedPartitioning;
    }
    
    public String getPartitioned() {
        return partitioned;
    }

    public void setPartitioned(String partitioned) {
        this.partitioned = partitioned;
    }

}

