/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import javax.persistence.AttributeConverter;

/**
 * A JPA attribute converter class wrapped with an EclipseLink converter. This
 * class is placed directly on mappings.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.5
 */
public class ConverterClass implements Converter {
    String attributeConverterClassName;
    AttributeConverter attributeConverter;
    
    /**
     * INTERNAL:
     */
    public ConverterClass(MetadataClass attributeConverterClass) {
        this.attributeConverterClassName = attributeConverterClass.getName();
    }
    
    /**
     * INTERNAL:
     */
    public ConverterClass(String attributeConverterClassName) {
        this.attributeConverterClassName = attributeConverterClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return attributeConverter.convertToEntityAttribute(dataValue);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return attributeConverter.convertToDatabaseColumn(objectValue);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        Class attributeConverterClass = null;
        
        try {
            ClassLoader loader = session.getClass().getClassLoader();
            
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

    /**
     * INTERNAL:
     */
    @Override
    public boolean isMutable() {
        return false;
    }
}
