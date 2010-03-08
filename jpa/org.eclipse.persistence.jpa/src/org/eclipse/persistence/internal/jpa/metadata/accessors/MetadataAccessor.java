/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import java.util.ArrayList;
import java.util.List;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

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

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

/**
 * INTERNAL:
 * Common metadata accessor level for mappings and classes.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class MetadataAccessor extends ORMetadata {
    private String m_access;
    
    private List<ConverterMetadata> m_converters = new ArrayList<ConverterMetadata>();
    private List<ObjectTypeConverterMetadata> m_objectTypeConverters = new ArrayList<ObjectTypeConverterMetadata>();
    private List<StructConverterMetadata> m_structConverters = new ArrayList<StructConverterMetadata>();
    private List<TypeConverterMetadata> m_typeConverters = new ArrayList<TypeConverterMetadata>();
    private List<PropertyMetadata> m_properties = new ArrayList<PropertyMetadata>();
    
    private MetadataDescriptor m_descriptor;
    private MetadataProject m_project;
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
        
        // Look for an explicit access type specification.
        initAccess();
    }
    
    /**
     * INTERNAL:
     * Process and add the globally defined converters to the project.
     */
    public void addConverters() {
        // Process the custom converters if defined.
        processCustomConverters();
        
        // Process the object type converters if defined.
        processObjectTypeConverters();
        
        // Process the type converters if defined.
        processTypeConverters();
        
        // Process the struct converters if defined
        processStructConverter();
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
    protected MetadataAnnotation getAnnotation(String annotation) {
        return getAccessibleObject().getAnnotation(annotation, m_descriptor);
    }
    
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
     * Return the fully qualified className using the package (if any) setting
     * from XML.
     */
    protected String getFullyQualifiedClassName(String className) {
        Class primitiveClass = getPrimitiveClassForName(className);
        
        if (primitiveClass == null) {
            if (loadedFromXML()) {
                return getEntityMappings().getPackageQualifiedClassName(className);
            }
            
            return className;
        } else {
            return primitiveClass.getName();
        }
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
     */
    protected String getName(DatabaseField field, String defaultName, String context) {
        return getName(field.getName(), defaultName, context);
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
    public boolean hasPropertyAccess() {
        return hasAccess() && m_access.equals(MetadataConstants.PROPERTY);
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
        
        // Initialize lists of objects.
        initXMLObjects(m_converters, accessibleObject);
        initXMLObjects(m_objectTypeConverters, accessibleObject);
        initXMLObjects(m_structConverters, accessibleObject);
        initXMLObjects(m_typeConverters, accessibleObject);
        initXMLObjects(m_properties, accessibleObject);
    }
    
    /** 
     * INTERNAL:
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    protected boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return getAccessibleObject().isAnnotationPresent(annotation, m_descriptor);
    }
    
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
        
        // ORMetadata list merging.
        m_converters = mergeORObjectLists(m_converters, accessor.getConverters());
        m_objectTypeConverters = mergeORObjectLists(m_objectTypeConverters, accessor.getObjectTypeConverters());
        m_structConverters = mergeORObjectLists(m_structConverters, accessor.getStructConverters());
        m_typeConverters = mergeORObjectLists(m_typeConverters, accessor.getTypeConverters());
        m_properties = mergeORObjectLists(m_properties, accessor.getProperties());
    }
    
    /**
     * INTERNAL:
     * Every accessor knows how to process themselves since they have all the
     * information they need.
     */
    public abstract void process();
    
    /**
     * INTERNAL:
     * Process the XML defined converters and check for a Converter annotation. 
     */
    protected void processCustomConverters() {
        // Check for XML defined converters.
        for (ConverterMetadata converter : m_converters) {
            m_project.addConverter(converter);
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
    protected void processStructConverter() {
        // Check for XML defined struct converters.
        for (StructConverterMetadata structConverter : m_structConverters) {
            m_project.addConverter(structConverter);
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
     * Note: the order of calls in this method are important.
     */
    protected void setFieldName(DatabaseField field, String defaultName, String context) {
        // This may set the use delimited identifier flag to true.
        field.setName(getName(field, defaultName, context), Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
        
        // The check is necessary to avoid overriding a true setting (set after 
        // setting the name of the field). We don't want to override it at this
        // point if the global flag is set to false. 
        if (useDelimitedIdentifier()) {
            field.setUseDelimiters(useDelimitedIdentifier());
        }
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
     * INTERNAL:
     */
    protected boolean useDelimitedIdentifier() {
        return m_project.useDelimitedIdentifier();
    }
}

