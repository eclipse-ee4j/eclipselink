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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-1.1 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.ClassInstanceConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * A direct accessor.
 * 
 * Subclasses: BasicAccessor, BasicCollectionAccessor, BasicMapAccessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class DirectAccessor extends MappingAccessor {
    // Reserved converter names
    private static final String CONVERT_NONE = "none";
    private static final String CONVERT_SERIALIZED = "serialized";
    private static final String CONVERT_CLASS_INSTANCE = "class-instance";
    
    private final static String DEFAULT_MAP_KEY_COLUMN_SUFFIX = "_KEY";
    
    private boolean m_lob;
    private Boolean m_optional;
    
    private Enum m_fetch;
    private Enum m_enumerated;
    private Enum m_temporal;
    
    private String m_convert;
    
    /**
     * INTERNAL:
     */
    protected DirectAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected DirectAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        // Set the lob if one is present.
        m_lob = isAnnotationPresent(Lob.class);
        
        // Set the enumerated if one is present.
        Annotation enumerated = getAnnotation(Enumerated.class);
        if (enumerated != null) {
            m_enumerated = (Enum) MetadataHelper.invokeMethod("value", enumerated);
        }
        
        // Set the temporal type if one is present.
        Annotation temporal = getAnnotation(Temporal.class);
        if (temporal != null) {
            m_temporal = (Enum) MetadataHelper.invokeMethod("value", temporal);
        }
        
        // Set the convert value if one is present.
        Annotation convert = getAnnotation(Convert.class);
        if (convert != null) {
            m_convert = (String) MetadataHelper.invokeMethod("value", convert);
        }
    }
    
    /**
     * INTERNAL:
     * This is used to return the column for a BasicAccessor. In the case
     * of a BasicCollectionAccessor or BasicMapAccessor, this method should
     * return the value column. NOTE: In the case of a BasicMapAccessor, the
     * key column could be returned instead.
     */
    protected abstract ColumnMetadata getColumn(String loggingCtx);
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConvert() {
        return m_convert;
    }
    
    /**
     * INTERNAL:
     * Process column metadata details into a database field. This will set 
     * correct metadata and log defaulting messages to the user. It also looks 
     * for an attribute override.
     * 
     * This method will call getColumn() which assumes the subclasses will
     * return the appropriate ColumnMetadata to process based on the context
     * provided.
     * 
     * @See BasicCollectionAccessor and BasicMapAccessor.
     */
    protected DatabaseField getDatabaseField(DatabaseTable defaultTable, String loggingCtx) {
        // Check if we have an attribute override first, otherwise process for 
        // a column (ignoring if for a key column on a basic map)
        ColumnMetadata column;
        if (getDescriptor().hasAttributeOverrideFor(getAttributeName()) && ! loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            column = getDescriptor().getAttributeOverrideFor(getAttributeName()).getColumn();
        } else {
            column = getColumn(loggingCtx);
        }
        
        // Get the actual database field and apply any defaults.
        DatabaseField field = column.getDatabaseField();
        
        // Make sure there is a table name on the field.
        if (field.getTableName().equals("")) {
            field.setTable(defaultTable);
        }
        
        // Set the correct field name, defaulting and logging when necessary.
        String defaultName = column.getUpperCaseAttributeName();
        
        // If this is for a map key column, append a suffix.
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            defaultName += DEFAULT_MAP_KEY_COLUMN_SUFFIX;
        }
        
        field.setName(getName(field.getName(), defaultName, loggingCtx));
                    
        return field;
    }
    
    /**
     * INTERNAL:
     */
    public abstract FetchType getDefaultFetchType();
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getEnumerated() {
        return m_enumerated;
    }
     
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getFetch() {
        return m_fetch;
    }
    
    /**
     * INTERNAL:
     * Return the field classification for the given temporal type.
     */
    protected Class getFieldClassification(Enum type) {
        if (type.name().equals(TemporalType.DATE.name())){
            return java.sql.Date.class;
        } else if(type.name().equals(TemporalType.TIME.name())){
            return java.sql.Time.class;
        } else if(type.name().equals(TemporalType.TIMESTAMP.name())){
            return java.sql.Timestamp.class;
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getLob() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getOptional() {
        return m_optional;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getTemporal() {
        return m_temporal;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected boolean hasConvert() {
        return m_convert != null;
    }
    
    /**
     * INTERNAL:
     * Return true if this represents an enum type mapping. Will return true
     * if the accessor's reference class is an enum or if a @Enumerated exists.
     */
    public boolean isEnumerated() {        
        if (hasConvert()) {
            // If we have an @Enumerated with a @Convert, the @Convert takes
            // precedence and we will ignore the @Enumerated and log a message.
            if (m_enumerated != null) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_ENUMERATED, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return m_enumerated != null || isValidEnumeratedType(getReferenceClass());
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a BLOB/CLOB mapping.
     */
    public boolean isLob() {
        if (hasConvert()) {
            // If we have a Lob specified with a Convert, the Convert takes 
            // precedence and we will ignore the Lob and log a message.
            if (m_lob) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_LOB, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return m_lob;
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean isOptional() {
        if (m_optional == null) {
            return true;
        } else {
            return m_optional.booleanValue();
        }
    }
    
    /**
     * INTERNAL:
     * Returns true is the given class is primitive wrapper type.
     */
    protected boolean isPrimitiveWrapperClass(Class cls) {
        return Long.class.isAssignableFrom(cls) ||
               Short.class.isAssignableFrom(cls) ||
               Float.class.isAssignableFrom(cls) ||
               Byte.class.isAssignableFrom(cls) ||
               Double.class.isAssignableFrom(cls) ||
               Number.class.isAssignableFrom(cls) ||
               Boolean.class.isAssignableFrom(cls) ||
               Integer.class.isAssignableFrom(cls) ||
               Character.class.isAssignableFrom(cls) ||
               String.class.isAssignableFrom(cls) ||
               java.math.BigInteger.class.isAssignableFrom(cls) ||
               java.math.BigDecimal.class.isAssignableFrom(cls) ||
               java.util.Date.class.isAssignableFrom(cls) ||
               java.util.Calendar.class.isAssignableFrom(cls);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a serialized mapping.
     */
    public boolean isSerialized() {
        if (hasConvert()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_SERIALIZED, getJavaClass(), getAnnotatedElement());
            return false;
        } else {
            return isValidSerializedType(getReferenceClass());
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this represents a temporal type mapping. Will return true
     * if the accessor's reference class is a temporal type or if a @Temporal 
     * exists.
     */
    public boolean isTemporal() {
        if (hasConvert()) {
            // If we have a Temporal specification with a Convert specification, 
            // the Convert takes precedence and we will ignore the Temporal and 
            // log a message.
            if (m_temporal != null) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_TEMPORAL, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return m_temporal != null || isValidTemporalType(getReferenceClass());
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid blob type.
     */ 
    protected boolean isValidBlobType(Class cls) {
        return cls.equals(byte[].class) ||
               cls.equals(Byte[].class) ||
               cls.equals(java.sql.Blob.class);
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid clob type.
     */  
    protected boolean isValidClobType(Class cls) {
        return cls.equals(char[].class) ||
               cls.equals(String.class) ||
               cls.equals(Character[].class) ||
               cls.equals(java.sql.Clob.class);
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid lob type.
     */
    protected boolean isValidLobType(Class cls) {
        return isValidClobType(cls) || isValidBlobType(cls);
    }
    
    /**
     * INTERNAL:
     * Return true if the given class is a valid enum type.
     */
    protected boolean isValidEnumeratedType(Class cls) {
        return cls.isEnum();    
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is valid for SerializedObjectMapping.
     */
    protected boolean isValidSerializedType(Class cls) {
        if (cls.isPrimitive()) {
            return false;
        }
        
        if (isPrimitiveWrapperClass(cls)) {    
            return false;
        }   
        
        if (isValidLobType(cls)) {
            return false;
        }
        
        if (isValidTemporalType(cls)) {
            return false;
        }
     
        return true;   
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid temporal type and must be
     * marked temporal.
     */
    protected boolean isValidTemporalType(Class cls) {
        return (cls.equals(java.util.Date.class) ||
                cls.equals(java.util.Calendar.class) ||
                cls.equals(java.util.GregorianCalendar.class));
    }
    
    /**
     * INTERNAL:
     * 
     * Process a potential class-instance attribute. If the reference class is a Class, 
     * set the converter on the mapping
     */
    protected void processClassInstance(DatabaseMapping mapping) {
            ClassInstanceConverter converter = new ClassInstanceConverter();
            setConverter(mapping, converter);
    }
    
    /**
     * INTERNAL:
     * Process a convert value to apply the specified EclipseLink converter 
     * (Converter, TypeConverter, ObjectTypeConverter) to the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a convert value specified.
     */
    public void processConvert() {
        processConvert(getDescriptor().getMappingForAttributeName(getAttributeName()), m_convert);
    }
    
    /**
     * INTERNAL:
     * Process a Convert annotation or convert element to apply to specified 
     * EclipseLink converter (Converter, TypeConverter, ObjectTypeConverter) 
     * to the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a Convert annotation specified.
     */
    protected void processConvert(DatabaseMapping mapping, String converterName) {
        // There is no work to do if the converter's name is "none".
        if (! converterName.equals(CONVERT_NONE)) {
            if (converterName.equals(CONVERT_SERIALIZED)) {
                processSerialized(mapping);
            } else if (converterName.equals(CONVERT_CLASS_INSTANCE)){
                processClassInstance(mapping);
            } else {
                AbstractConverterMetadata converter = getProject().getConverter(converterName);
                
                if (converter == null) {
                    throw ValidationException.converterNotFound(getJavaClass(), converterName, getAnnotatedElement());
                } else {
                    // Process the converter for this mapping.
                    converter.process(mapping, this);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process an Enumerated annotation. The method may still be called if no 
     * Enumerated annotation has been specified but the accessor's reference 
     * class is a valid enumerated type.
     */
    protected void processEnumerated(DatabaseMapping mapping) {
        // If this accessor is tagged as an enumerated type, validate the
        // reference class.
        if (m_enumerated != null && !isValidEnumeratedType(getReferenceClass())) {
            throw ValidationException.invalidTypeForEnumeratedAttribute(mapping.getAttributeName(), getReferenceClass(), getJavaClass());
        } else {
            // Create an EnumTypeConverter and set it on the mapping.
            if (m_enumerated == null) {
                // TODO: Log a defaulting message
                setConverter(mapping, new EnumTypeConverter(mapping, getReferenceClass(), true));
            } else {
                setConverter(mapping, new EnumTypeConverter(mapping, getReferenceClass(), m_enumerated.name().equals(EnumType.ORDINAL.name())));
            }
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Process an Enumerated, Lob or Temporal specification. Will default
     * a serialized converter if necessary.
     */
    protected void processJPAConverters(DatabaseMapping mapping) {
        // Check for an enum first since it will fall into a serializable 
        // mapping otherwise (Enums are serialized)
        
        if (isEnumerated()) {
            processEnumerated(mapping);
        } else if (isLob()) {
            processLob(mapping);
        } else if (isTemporal()) {
            processTemporal(mapping);
        } else if (isSerialized()) {
            processSerialized(mapping);
        }
    }
    
    /**
     * INTERNAL: (Overridden in BasicAccessor)
     * 
     * Process a lob specification. The lob must be specified to process and 
     * create a lob type mapping.
     */
    protected void processLob(DatabaseMapping mapping) {
        // Set the field classification type on the mapping based on the
        // referenceClass type.
        if (isValidClobType(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Clob.class);   
            setConverter(mapping, new TypeConversionConverter(mapping));
        } else if (isValidBlobType(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Blob.class);
            setConverter(mapping, new TypeConversionConverter(mapping));
        } else if (Helper.classImplementsInterface(getReferenceClass(), Serializable.class)) {
            setFieldClassification(mapping, java.sql.Blob.class);
            setConverter(mapping, new SerializedObjectConverter(mapping));
        } else {
            // The referenceClass is neither a valid BLOB or CLOB attribute.   
            throw ValidationException.invalidTypeForLOBAttribute(mapping.getAttributeName(), getReferenceClass(), getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping. EclipseLink converters 
     * (which are global to the persistent unit) can not be processed till we 
     * have processed all the classes in the persistence unit. So for now, add 
     * this accessor to the project list of convert dependant accessors, and 
     * process it in stage 2, that is, during the project process.
     * 
     * The method will look for an EclipseLink converter first (based on the 
     * converter name provided) and will override any JPA annotations. Log 
     * warnings will be issued for any annotations that are being ignore 
     * because of a Convert override.     
     */
    protected void processMappingConverter(DatabaseMapping mapping, String convertValue) {
        if (convertValue != null && ! convertValue.equals(CONVERT_NONE)) {
            // EclipseLink converter specified, defer this accessors converter
            // processing to stage 2 project processing.
            getProject().addConvertAccessor(this);
        } 
        
        // Regardless if we found a convert or not, look for JPA converters. 
        // This ensures two things; 
        // 1 - if no Convert is specified, then any JPA converter that is 
        // specified will be applied (see BasicMapAccessor's override of the
        // method hasConvert()). 
        // 2 - if a convert and a JPA converter are specified, then a log 
        // warning will be issued stating that we are ignoring the JPA 
        // converter.
        processJPAConverters(mapping);
    }
    
    /**
     * INTERNAL:
     * 
     * Process a potential serializable attribute. If the class implements 
     * the Serializable interface then set a SerializedObjectConverter on 
     * the mapping.
     */
    protected void processSerialized(DatabaseMapping mapping) {
        if (Helper.classImplementsInterface(getReferenceClass(), Serializable.class)) {
            SerializedObjectConverter converter = new SerializedObjectConverter(mapping);
            setConverter(mapping, converter);
        } else {
            throw ValidationException.invalidTypeForSerializedAttribute(mapping.getAttributeName(), getReferenceClass(), getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Process a temporal type accessor.
     */
    protected void processTemporal(DatabaseMapping mapping) {
        if (m_temporal == null) {
            // We have a temporal basic, but the temporal type was not
            // specified. Per the JPA spec we must throw an exception.
            throw ValidationException.noTemporalTypeSpecified(getAttributeName(), getJavaClass());
        } else {
            if (isValidTemporalType(getReferenceClass())) {
                // Set a TypeConversionConverter on the mapping.
                if (m_temporal.name().equals(TemporalType.DATE.name())) {
                    setFieldClassification(mapping, java.sql.Date.class);
                } else if(m_temporal.name().equals(TemporalType.TIME.name())) {
                    setFieldClassification(mapping,java.sql.Time.class);
                } else {
                    // Through annotation and XML validation, it must be 
                    // TemporalType.TIMESTAMP and can't be anything else.
                    setFieldClassification(mapping, java.sql.Timestamp.class);
                }
                
                setConverter(mapping, new TypeConversionConverter(mapping));
            } else {
                throw ValidationException.invalidTypeForTemporalAttribute(getAttributeName(), getReferenceClass(), getJavaClass());
            }    
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConvert(String convert) {
        m_convert = convert;
    }
    
    /**
     * INTERNAL:
     */
    public abstract void setConverter(DatabaseMapping mapping, Converter converter);
    
    /**
     * INTERNAL:
     */
    public abstract void setConverterClassName(DatabaseMapping mapping, String converterClassName);
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEnumerated(Enum enumerated) {
        m_enumerated = enumerated;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFetch(Enum fetch) {
        m_fetch = fetch;
    }
    
    /**
     * INTERNAL:
     */
    public abstract void setFieldClassification(DatabaseMapping mapping, Class classification);
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setLob(String ignore) {
        m_lob = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptional(Boolean optional) {
        m_optional = optional;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTemporal(Enum temporalType) {
        m_temporal = temporalType;
    }
        
    /**
     * INTERNAL:
     */
    public boolean usesIndirection() {
        Enum fetchType = getFetch();
        
        if (fetchType == null) {
            fetchType = getDefaultFetchType();
        }
        
        return fetchType.name().equals(FetchType.LAZY.name());
    }
}
