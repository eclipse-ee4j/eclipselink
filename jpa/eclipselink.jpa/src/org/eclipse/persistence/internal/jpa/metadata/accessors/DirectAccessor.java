/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import java.io.Serializable;

import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Lob;
import javax.persistence.Temporal;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataAbstractConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataStructConverter;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * A direct accessor.
 * 
 * Subclasses: BasicAccessor, XMLBasicAccessor, BasicCollectionAccessor,
 * BasicMapAccessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class DirectAccessor extends NonRelationshipAccessor {
    protected final static String DEFAULT_MAP_KEY_COLUMN_SUFFIX = "_KEY";
    
    /**
     * INTERNAL:
     */
    public DirectAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     * This is used to return the column for a BasicAccessor. In the case
     * of a BasicCollectionAccessor or BasicMapAccessor, this method should
     * return the value column.
     * 
     * See BasicMapAccessor for processing on the key column.
     */
    protected abstract MetadataColumn getColumn(String loggingCtx);
    
    /**
     * INTERNAL:
     * Process column details from an @Column or column element into a 
     * MetadataColumn and return it. This will set correct metadata and log 
     * defaulting messages to the user. It also looks for attribute overrides.
     * 
     * This method will call getColumn() which assumes the subclasses will
     * return the appropriate MetadataColumn to process based on the context
     * provided.
     * 
     * @See BasicCollectionAccessor and BasicMapAccessor.
     */
    protected DatabaseField getDatabaseField(String loggingCtx) {
        // Check if we have an attribute override first, otherwise process for 
        // a column (ignoring if for a key column on a basic map)
        MetadataColumn column;
        if (m_descriptor.hasAttributeOverrideFor(getAttributeName()) && ! loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            column = m_descriptor.getAttributeOverrideFor(getAttributeName());
        } else {
            column = getColumn(loggingCtx);
        }
        
        // Get the actual database field and apply any defaults.
        DatabaseField field = column.getDatabaseField();
        
        // Make sure there is a table name on the field.
        if (field.getTableName().equals("")) {
            field.setTable(getDefaultTable());
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
     * 
     * Return the default table name to be used with the database field of this 
     * direct accessor.
     */
    protected abstract DatabaseTable getDefaultTable();
    
    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     */
     public String getEnumeratedType() {
        Enumerated enumerated = getAnnotation(Enumerated.class);
        
        if (enumerated == null) {
            return EnumType.ORDINAL.name();
        } else {
            return enumerated.value().name();
        }
     }
     
    /**
     * INTERNAL:
     * 
     * Return the temporal type for this accessor. Assumes there is a @Temporal.
     */
    public String getTemporalType() {
        Temporal temporal = getAnnotation(Temporal.class);
        return temporal.value().name();
    }
    
    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
	 * Return true if this accessor has a @Enumerated.
     */
	public boolean hasEnumerated() {
		return isAnnotationPresent(Enumerated.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
	 * Return true if this accessor has a @Lob.
     */
	public boolean hasLob() {
		return isAnnotationPresent(Lob.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
     * Return true if this accessor has a @Temporal.
     */
	public boolean hasTemporal() {
        return isAnnotationPresent(Temporal.class);
    }

    /**
     * INTERNAL:
     * 
     * Return true if this represents an enum type mapping. Will return true
     * if the accessor's reference class is an enum or if a @Enumerated exists.
     */
    public boolean isEnumerated() {
        if (hasConvert()) {
            // If we have an @Enumerated with a @Convert, the @Convert takes
            // precedence and we will ignore the @Enumerated and log a message.
            if (hasEnumerated()) {
                m_logger.logWarningMessage(MetadataLogger.IGNORE_ENUMERATED, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return hasEnumerated() || MetadataHelper.isValidEnumeratedType(getReferenceClass());
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Return true if this accessor represents a BLOB/CLOB mapping.
     */
	public boolean isLob() {
        if (hasLob() && hasConvert()) {
            // If we have a @Lob with a @Convert, the @Convert takes
            // precedence and we will ignore the @Lob and log a message.
            m_logger.logWarningMessage(MetadataLogger.IGNORE_LOB, getJavaClass(), getAnnotatedElement());
            return false;
        }
        
        return hasLob();
    }
    
    /**
     * INTERNAL:
     * 
     * Return true if this accessor represents a serialized mapping.
     */
	public boolean isSerialized() {
        if (hasConvert()) {
            m_logger.logWarningMessage(MetadataLogger.IGNORE_SERIALIZED, getJavaClass(), getAnnotatedElement());
            return false;
        } else {
            return MetadataHelper.isValidSerializedType(getReferenceClass());
        }
    }
    
    /**
     * (Overridden in BasicMapAccessor)
     * Return true if this represents a temporal type mapping. Will return true
     * if the accessor's reference class is a temporal type or if a @Temporal 
     * exists.
     */
	public boolean isTemporal() {
        if (hasConvert()) {
            // If we have a @Temporal with a @Convert, the @Convert takes
            // precedence and we will ignore the @Temporal and log a message.
            if (hasTemporal()) {
                m_logger.logWarningMessage(MetadataLogger.IGNORE_TEMPORAL, getJavaClass(), getAnnotatedElement());
            }
            
            return false;
        } else {
            return hasTemporal() || MetadataHelper.isValidTemporalType(getReferenceClass());
        }
    }
    
    /**
     * (OVERRIDE) and (Overridden in BasicMapAccessor)
     * Process a @Convert annotation or convert element to apply the specified 
     * TopLink converter (@Converter, @TypeConverter, @ObjectTypeConverter) to
     * the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a @Convert specified.
     */
    public void processConvert() {
        processConvert(m_descriptor.getMappingForAttributeName(getAttributeName()), getConvertValue());
    }
    
    /**
     * Process a @Convert annotation or convert element to apply to specfiied 
     * TopLink converter (@Converter, @TypeConverter, @ObjectTypeConverter) to
     * the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a @Convert specified.
     */
    protected void processConvert(DatabaseMapping mapping, String converterName) {
        // There is no work to do if the converter's name is "none".
        if (! converterName.equals(MetadataConstants.CONVERT_NONE)) {
            if (converterName.equals(MetadataConstants.CONVERT_SERIALIZED)) {
                processSerialized(mapping);
            } else {

                MetadataAbstractConverter converter = m_project.getConverter(converterName);
                if (converter == null) {
                    MetadataStructConverter structConverter = m_project.getStructConverter(converterName);
                    if (structConverter != null){
                        structConverter.process(mapping, this);
                        return;
                    } else {
                        m_validator.throwConverterNotFound(getJavaClass(), converterName, getAnnotatedElement());
                    }
                }
            
                // Process the converter for this mapping.
                converter.process(mapping, this);
            }
        }
    }
    
    /**
     * INTERNAL: (Overridden in BasicAccessor and BasicMapAccessor)
     * 
     * Process an @Enumerated. The method may still be called if no @Enumerated
     * has been specified but the accessor's reference class is a valid 
     * enumerated type.
     */
    protected void processEnumerated(DatabaseMapping mapping) {
        // If this accessor is tagged as an enumerated type, validate the
        // reference class.
        if (hasEnumerated()) {
            if (! MetadataHelper.isValidEnumeratedType(getReferenceClass())) {
                m_validator.throwInvalidTypeForEnumeratedAttribute(getJavaClass(), mapping.getAttributeName(), getReferenceClass());
            }
        }
        
        // Create an EnumTypeConverter and set it on the mapping.
        setConverter(mapping, new EnumTypeConverter(mapping, getReferenceClass(), getEnumeratedType().equals(EnumType.ORDINAL.name())));
    }
    
    /**
     * INTERNAL:
     * 
     * Process an @Enumerated, @Lob or @Temporal annotation. Will default
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
     * Process a @Lob or lob sub-element. The lob must be specified to process 
     * and create a lob type mapping.
     */
    protected void processLob(DatabaseMapping mapping) {
        // Set the field classification type on the mapping based on the
        // referenceClass type.
        if (MetadataHelper.isValidClobType(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Clob.class);   
            setConverter(mapping, new TypeConversionConverter(mapping));
        } else if (MetadataHelper.isValidBlobType(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Blob.class);
            setConverter(mapping, new TypeConversionConverter(mapping));
        } else if (Helper.classImplementsInterface(getReferenceClass(), Serializable.class)) {
            setFieldClassification(mapping, java.sql.Blob.class);
            setConverter(mapping, new SerializedObjectConverter(mapping));
        } else {
            // The referenceClass is neither a valid BLOB or CLOB attribute.   
            m_validator.throwInvalidTypeForLOBAttribute(getJavaClass(), mapping.getAttributeName(), getReferenceClass());
        }
    }
 
    /**
     * INTERNAL:
     * 
     * Process a converter for the given mapping. Will look for a converter
     * name from a @Convert specified on this accessor.
     */
    protected void processMappingConverter(DatabaseMapping mapping) {
        processMappingConverter(mapping, getConvertValue());
    }
    
    /**
     * INTERNAL:
     * 
     * Process a converter for the given mapping. The method will look for
     * a TopLink converter first (based on the converter name provided) and
     * will override any JPA annotations. Log warnings will be issued for any
     * annotations that are being ignore because of a @Convert override.
     */
    protected void processMappingConverter(DatabaseMapping mapping, String convertValue) {
        super.processConvert(convertValue);
        
        // Regardless if we found a @Covert or not, look for JPA converters. 
        // This ensures two things; 
        // 1 - if no @Convert is specified, then any JPA converter that is 
        // specified will be applied (see BasicMapAccessor's override of the
        // method hasConvert()). 
        // 2 - if a @Convert and a JPA converter are specified, then a log 
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
            m_validator.throwInvalidTypeForSerializedAttribute(getJavaClass(), mapping.getAttributeName(), getReferenceClass());
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Process a temporal type accessor.
     */
    protected void processTemporal(DatabaseMapping mapping) {
        if (hasTemporal()) {
            if (MetadataHelper.isValidTemporalType(getReferenceClass())) {
                // Set a TypeConversionConverter on the mapping.
                setFieldClassification(mapping, MetadataHelper.getFieldClassification(getTemporalType()));
                setConverter(mapping, new TypeConversionConverter(mapping));
            } else {
                m_validator.throwInvalidTypeForTemporalAttribute(getJavaClass(), getAttributeName(), getReferenceClass());
            }    
        } else {
            m_validator.throwNoTemporalTypeSpecified(getJavaClass(), getAttributeName());
        }
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
     */
    public abstract void setFieldClassification(DatabaseMapping mapping, Class classification);
}
