/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/30/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import javax.persistence.AttributeConverter;
import javax.persistence.PersistenceException;

/**
 * A JPA attribute converter class wrapped with an EclipseLink converter. This
 * class is placed directly on mappings.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.5
 */
public class ConverterClass implements Converter {
    protected boolean isForMapKey;
    protected boolean disableConversion;
    protected Class fieldClassification;
    protected String fieldClassificationName;
    protected String attributeConverterClassName;
    protected AttributeConverter attributeConverter;
    
    /**
     * INTERNAL:
     * This method will be called when creating a converter for an embedded
     * mapping attribute. The isForMapKey information will need to be known
     * for proper initialization.
     */
    public ConverterClass(String attributeConverterClassName, boolean isForMapKey, String fieldClassificationName, boolean disableConversion) {
        this.isForMapKey = isForMapKey;
        this.disableConversion = disableConversion;
        this.fieldClassificationName = fieldClassificationName;
        this.attributeConverterClassName = attributeConverterClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        try {
            return attributeConverter.convertToEntityAttribute(dataValue);
        } catch (RuntimeException re) {
            throw new PersistenceException(ExceptionLocalization.buildMessage("wrap_convert_exception", 
                    new Object[]{"convertToEntityAttribute", attributeConverterClassName, dataValue}), re);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        try {
            return attributeConverter.convertToDatabaseColumn(objectValue);
        } catch (RuntimeException re) {
            throw new PersistenceException(ExceptionLocalization.buildMessage("wrap_convert_exception", 
                    new Object[]{"convertToDatabaseColumn", attributeConverterClassName, objectValue}), re);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        Converter converter;
        ClassLoader loader = ConversionManager.getDefaultManager().getLoader();
        
        if (disableConversion) {
            converter = null;
        } else {
            converter = this;
            Class attributeConverterClass = null;
    
            try {        
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        attributeConverterClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(attributeConverterClassName, true, loader));
                        attributeConverter = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(attributeConverterClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception.getException());
                    }
                } else {
                    attributeConverterClass = PrivilegedAccessHelper.getClassForName(attributeConverterClassName, true, loader);
                    attributeConverter = (AttributeConverter) PrivilegedAccessHelper.newInstanceFromClass(attributeConverterClass);   
                }
            } catch (ClassNotFoundException exception) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception);
            } catch (IllegalAccessException exception) {
                throw ValidationException.errorInstantiatingClass(attributeConverterClass, exception);
            } catch (InstantiationException exception) {
                throw ValidationException.errorInstantiatingClass(attributeConverterClass, exception);
            } 
        }
        
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    fieldClassification = (Class) AccessController.doPrivileged(new PrivilegedClassForName(fieldClassificationName, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(fieldClassificationName, exception.getException());
                }
            } else {
                fieldClassification = PrivilegedAccessHelper.getClassForName(fieldClassificationName, true, loader);   
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception);
        }
        
        // Ensure the mapping has the correct field classification set.
        if (mapping.isDirectToFieldMapping()) {
            DirectToFieldMapping m = (DirectToFieldMapping) mapping; 
            m.setConverter(converter);
            m.setFieldClassification(fieldClassification);
            m.setFieldClassificationClassName(fieldClassificationName);
        } else if (mapping.isDirectMapMapping() && isForMapKey) {
            DirectMapMapping m = (DirectMapMapping) mapping;
            m.setKeyConverter(converter);
            m.setDirectKeyFieldClassification(fieldClassification);
            m.setDirectKeyFieldClassificationName(fieldClassificationName);
        }  else if (mapping.isDirectCollectionMapping()) {
            DirectCollectionMapping m = (DirectCollectionMapping) mapping;
            m.setValueConverter(converter);
            m.setDirectFieldClassification(fieldClassification);
            m.setDirectFieldClassificationName(fieldClassificationName);
        } else {
            // TODO: what else could it be???
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isMutable() {
        return false;
    }
}
