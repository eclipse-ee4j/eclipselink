/*******************************************************************************
 * Copyright (c) 2012, 2015 Oracle and/or its affiliates. All rights reserved.
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
 *     06/03/2013-2.5.1 Guy Pelletier    
 *       - 402380: 3 jpa21/advanced tests failed on server with 
 *         "java.lang.NoClassDefFoundError: org/eclipse/persistence/testing/models/jpa21/advanced/enums/Gender"
 *     07/16/2013-2.5.1 Guy Pelletier 
 *       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work 
 ******************************************************************************/  
package org.eclipse.persistence.mappings.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.ClassNameConversionRequired;
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
public class ConverterClass implements Converter, ClassNameConversionRequired {
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
     * Convert all the class-name-based settings in this converter to actual 
     * class-based settings. This method is used when converting a project 
     * that has been built with class names to a project with classes.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Class attributeConverterClass = null;
    
        try {        
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    attributeConverterClass = AccessController.doPrivileged(new PrivilegedClassForName(attributeConverterClassName, true, classLoader));
                    attributeConverter = (AttributeConverter) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(attributeConverterClass));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception.getException());
                }
            } else {
                attributeConverterClass = PrivilegedAccessHelper.getClassForName(attributeConverterClassName, true, classLoader);
                attributeConverter = (AttributeConverter) PrivilegedAccessHelper.newInstanceFromClass(attributeConverterClass);   
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception);
        } catch (IllegalAccessException exception) {
            throw ValidationException.errorInstantiatingClass(attributeConverterClass, exception);
        } catch (InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(attributeConverterClass, exception);
        } 
        
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    fieldClassification = AccessController.doPrivileged(new PrivilegedClassForName(fieldClassificationName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(fieldClassificationName, exception.getException());
                }
            } else {
                fieldClassification = PrivilegedAccessHelper.getClassForName(fieldClassificationName, true, classLoader);   
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(attributeConverterClassName, exception);
        }
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
        // Ensure the mapping has the correct field classification set.
        if (mapping.isDirectToFieldMapping()) {
            DirectToFieldMapping m = (DirectToFieldMapping) mapping; 
            
            if (disableConversion) {
                m.setConverter(null);
            } else {
                m.setConverter(this);
                m.setFieldClassification(fieldClassification);
                m.setFieldClassificationClassName(fieldClassificationName);
            }
        } else if (mapping.isDirectMapMapping() && isForMapKey) {
            DirectMapMapping m = (DirectMapMapping) mapping;
            
            if (disableConversion) {
                m.setKeyConverter(null);
            } else {
                m.setKeyConverter(this);
                m.setDirectKeyFieldClassification(fieldClassification);
                m.setDirectKeyFieldClassificationName(fieldClassificationName);
            }
        }  else if (mapping.isDirectCollectionMapping()) {
            DirectCollectionMapping m = (DirectCollectionMapping) mapping;
            
            if (disableConversion) {
                m.setValueConverter(null);
            } else {
                m.setValueConverter(this);
                m.setDirectFieldClassification(fieldClassification);
                m.setDirectFieldClassificationName(fieldClassificationName);
            }
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
