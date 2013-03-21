/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/13/2009-2.0  mobrien - 294765: MapKey keyType DirectToField processing 
 *       should return attributeClassification class in getMapKeyTargetType when 
 *       accessor.attributeField is null in the absence of a MapKey annotation
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <b>Purpose</b>: Maps an attribute or some other property to the corresponding 
 * database field type. The list of field types that are supported by 
 * EclipseLink's direct to field mapping is dependent on the relational database 
 * being used.
 * 
 * @see org.eclipse.persistence.mappings.foundation.AbstractDirectMapping
 * @see org.eclipse.persistence.mappings.foundation.MultitenantPrimaryKeyMapping
 *
 * @author Guy Pelletier
 * @since TopLink/Java 1.0
 */
public abstract class AbstractColumnMapping extends DatabaseMapping {
    
    /** DatabaseField which this mapping represents. */
    protected DatabaseField field;

    /** Allows user defined conversion between the object attribute value and the database value. */
    protected Converter converter;
    protected String converterClassName;
    
    /** Flag to support insertable JPA setting */
    protected boolean isInsertable = true;
    
    /** Flag to support updatable JPA setting */
    protected boolean isUpdatable = true;
    
    /**
     * Default constructor.
     */
    public AbstractColumnMapping() {
        super();
        this.setWeight(WEIGHT_DIRECT);
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        AbstractColumnMapping clone = (AbstractColumnMapping)super.clone();

        // Field must be cloned so aggregates do not share fields.
        clone.setField(getField().clone());

        return clone;
    }
    
    /**
     * Returns the field this mapping represents.
     */
    @Override
    protected Vector<DatabaseField> collectFields() {
        Vector databaseField = new Vector(1);

        databaseField.addElement(field);
        return databaseField;
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based settings
     * This method is implemented by subclasses as necessary.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        
        // Field may have a type name that needs to be initialize.
        if (field != null) {
            field.convertClassNamesToClasses(classLoader);
        }
        
        if (converter != null) {
            if (converter instanceof TypeConversionConverter) {
                ((TypeConversionConverter)converter).convertClassNamesToClasses(classLoader);
            } else if (converter instanceof ObjectTypeConverter) {
                // To avoid 1.5 dependencies with the EnumTypeConverter check
                // against ObjectTypeConverter.
                ((ObjectTypeConverter) converter).convertClassNamesToClasses(classLoader);
            }
        } 
        
        // Instantiate any custom converter class
        if (converterClassName != null) {
            Class converterClass;
            Converter converter;
    
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        converterClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(converterClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exception.getException());
                    }
                    
                    try {
                        converter = (Converter) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(converterClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exception.getException());
                    }
                } else {
                    converterClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(converterClassName, true, classLoader);
                    converter = (Converter) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(converterClass);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(converterClassName, e);
            }
            
            setConverter(converter);
        }
    }
    
    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    @Override
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
    }
    
    /**
     * PUBLIC:
     * Return the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public Converter getConverter() {
        return converter;
    }
    
    /**
     * INTERNAL:
     * Returns the field which this mapping represents.
     */
    public DatabaseField getField() {
        return field;
    }
 
    /**
     * INTERNAL:
     * Convert the object (attribute or property) value to a field value.
     */
    public abstract Object getFieldValue(Object objectValue, AbstractSession session);
    
    /**
     * INTERNAL:
     * Allows for subclasses to convert the the attribute or property value. 
     */
    public abstract Object getObjectValue(Object fieldValue, Session session);

    /**
     * Indicates if the mapping has a converter set on it.
     * 
     * @return true if there is a converter set on the mapping,
     * false otherwise.
     */
    public boolean hasConverter() {
        return converter != null;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isAbstractColumnMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return true if this mapping is insertable.
     */
    protected boolean isInsertable() {
        return isInsertable;
    }
    
    /**
     * INTERNAL:
     * Return true if this mapping is updatable.
     */
    protected boolean isUpdatable() {
        return isUpdatable;
    }
    
    /**
     * INTERNAL:
     * Iterate on the appropriate attribute.
     */
    @Override
    public void iterate(DescriptorIterator iterator) {
        // PERF: Only iterate when required.
        if (iterator.shouldIterateOnPrimitives()) {
            iterator.iteratePrimitiveForMapping(getAttributeValueFromObject(iterator.getVisitedParent()), this);
        }
    }
    
    /**
     * PUBLIC:
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(Converter converter) {
        this.converter = converter;
    }
    
    /**
     * PUBLIC:
     * Set the converter class name on the mapping. It will be instantiated 
     * during the convertClassNamesToClasses.
     * A converter can be used to convert between the object's value and 
     * database value of the attribute.
     */
    public void setConverterClassName(String converterClassName) {
        this.converterClassName = converterClassName;
    }
    
    /**
     * ADVANCED:
     * Set the field in the mapping.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public void setField(DatabaseField theField) {
        field = theField;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + getAttributeName() + "-->" + getField() + "]";
    }
    
	/**
	 * INTERNAL:
	 */
    protected abstract void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object value);
}
