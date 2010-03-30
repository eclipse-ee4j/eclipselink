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
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.*;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.queries.MapContainerPolicy.MapContainerPolicyIterator;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A MapContainerPolicy is ContainerPolicy whose container class
 * implements the Map interface.
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a Map.
 *
 * @see MappedKeyMapContainerPolicy
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 */
public class DirectMapContainerPolicy extends InterfaceContainerPolicy implements DirectMapUsableContainerPolicy {
    protected DatabaseField keyField;
    protected DatabaseField valueField;
    protected Converter keyConverter;
    protected String keyConverterClassName;
    protected Converter valueConverter;

    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public DirectMapContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public DirectMapContainerPolicy(Class containerClass) {
        super(containerClass);
    }

    /**
     * INTERNAL:
     * Called when the selection query is being initialized to add any required additional fields to the
     * query.  
     */
    @Override
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression){
        if (baseExpression == null){
            ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField((DatabaseField)keyField.clone());
            ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addTable((DatabaseTable)keyField.getTable().clone());
        } else {
            ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(baseExpression.getTable(keyField.getTable()).getField(keyField));
        }
    }
    
    /**
     * INTERNAL:
     * Called when the insert query is being initialized to ensure the fields for the key are in the insert query
     * 
     * @see MappedKeyMapContainerPolicy
     */
    @Override
    public void addFieldsForMapKey(AbstractRecord joinRow){
        joinRow.put(keyField, null);
    }
    
    /**
     * INTERNAL:
     * Add key, value pair into container which implements the Map interface.
     */
    @Override
    public boolean addInto(Object key, Object value, Object container, AbstractSession session) {
        try {
            ((Map)container).put(key, value);
        } catch (ClassCastException ex1) {
            throw QueryException.cannotAddElement(key, container, ex1);
        }
        return true;
    }

    /**
     * INTERNAL:
     * Add element into container which implements the Map interface.
     * The may be used by merging/cloning passing a Map.Entry.
     */
    @Override
    public boolean addInto(Object element, Object container, AbstractSession session) {
        if (element instanceof Map.Entry) {
            Map.Entry record = (Map.Entry)element;
            Object key = record.getKey();
            Object value = record.getValue();
            return addInto(key, value, container, session);
        }
        throw QueryException.cannotAddToContainer(element, container, this);
    }

    /**
     * INTERNAL:
     * This is used for ordered List containers to add all of the elements
     * to the collection in the order of the index field in the row.
     * This is currently only used by OrderListContainerPolicy, so this is just a stub.
     * The passing of the query is to allow future compatibility with Maps (ordered Map).
     */
    @Override
    public boolean addInto(Object element, Object container, AbstractSession session, AbstractRecord row, DataReadQuery query) {
        Object key = row.get(this.keyField);
        Object value = row.get(this.valueField);
        if (this.keyConverter != null) {
            key = this.keyConverter.convertDataValueToObjectValue(key, session);
        }
        if (this.valueConverter != null) {
            value = this.valueConverter.convertDataValueToObjectValue(value, session);
        }
        return addInto(key, value, container, session);
    }

    /**
     * Extract the key for the map from the provided row.
     */
    @Override
    public Object buildKey(AbstractRecord row, ObjectBuildingQuery query, AbstractSession session){
        Object key = row.get(keyField);
        if (keyConverter != null){
            key = keyConverter.convertDataValueToObjectValue(key, session);
        }
        return key;
    }
    
    /**
     * INTERNAL:
     * Remove all the elements from container.
     */
    @Override
    public void clear(Object container) {
        try {
            ((Map)container).clear();
        } catch (UnsupportedOperationException ex) {
            throw QueryException.methodNotValid(container, "clear()");
        }
    }

    /**
     * INTERNAL:
     * Return true if keys are the same.  False otherwise
     */
    public boolean compareContainers(Object firstObjectMap, Object secondObjectMap) {
        if (sizeFor(firstObjectMap) != sizeFor(secondObjectMap)) {
            return false;
        }

        for (Object firstIterator = iteratorFor(firstObjectMap); hasNext(firstIterator);) {
            Map.Entry entry = (Map.Entry)nextEntry(firstIterator);
            Object key = entry.getKey();
            if (!((Map)firstObjectMap).get(key).equals(((Map)secondObjectMap).get(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Return true if keys are the same in the source as the backup.
     * Always true if read-only.
     */
    @Override
    public boolean compareKeys(Object sourceValue, AbstractSession session) {
        if (((UnitOfWorkImpl)session).isClassReadOnly(sourceValue.getClass())) {
            return true;
        }
        Object backUpVersion = ((UnitOfWorkImpl)session).getBackupClone(sourceValue, getElementDescriptor());
        return (keyFrom(backUpVersion, session).equals(keyFrom(sourceValue, session)));
    }

    /**
     * INTERNAL:
     * Return the true if element exists in container.
     * @return boolean true if container 'contains' element
     */
    @Override
    protected boolean contains(Object element, Object container) {
        return ((Map)container).containsValue(element);
    }
    
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        
        if (keyConverter != null) {
            if (keyConverter instanceof TypeConversionConverter){
                ((TypeConversionConverter)keyConverter).convertClassNamesToClasses(classLoader);
            } else if (keyConverter instanceof ObjectTypeConverter) {
                // To avoid 1.5 dependencies with the EnumTypeConverter check
                // against ObjectTypeConverter.
                ((ObjectTypeConverter) keyConverter).convertClassNamesToClasses(classLoader);
            }
        }
        
        // Instantiate any custom converter class
        if (keyConverterClassName != null) {
            Class keyConverterClass;
            Converter keyConverter;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        keyConverterClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(keyConverterClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(keyConverterClassName, exception.getException());
                    }
                    
                    try {
                        keyConverter = (Converter) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(keyConverterClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(keyConverterClassName, exception.getException());
                    }
                } else {
                    keyConverterClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(keyConverterClassName, true, classLoader);
                    keyConverter = (Converter) org.eclipse.persistence.internal.security.PrivilegedAccessHelper.newInstanceFromClass(keyConverterClass);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(keyConverterClassName, exc);
            } catch (Exception e) {
                // Catches IllegalAccessException and InstantiationException
                throw ValidationException.classNotFoundWhileConvertingClassNames(keyConverterClassName, e);
            }
            
            this.keyConverter = keyConverter;;
        }
    }
    
    /**
     * INTERNAL:
     * Create a query key that links to the map key.  DirectMapContainerPolicy does not have a specific mapping for the
     * key, so return null.
     */
    @Override
    public QueryKey createQueryKeyForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the DatabaseField that represents the key in a DirectMapMapping.
     */
    @Override
    public DatabaseField getDirectKeyField(CollectionMapping baseMapping){
        return keyField;
    }

    @Override
    public Class getInterfaceType() {
        return ClassConstants.Map_Class;
    }
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields
     * @return
     */
    @Override
    public List<DatabaseField> getIdentityFieldsForMapKey(){
        ArrayList list = new ArrayList(1);
        list.add(keyField);
        return list;
    }
    
    /**
     * INTERNAL:
     * Return whether the iterator has more objects.
     * The iterator is the one returned from #iteratorFor().
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    @Override
    public boolean hasNext(Object iterator){
        return ((MapContainerPolicyIterator)iterator).hasNext();
    }
    
    /**
     * INTERNAL:
     * Add any non-Foreign-key data from an Object describe by a MapKeyMapping to a database row
     * This is typically used in write queries to ensure all the data stored in the collection table is included
     * in the query.
     */
    @Override
    public Map getKeyMappingDataForWriteQuery(Object object, AbstractSession session){
        Object keyValue = ((Map.Entry)object).getKey();
        Map fields = new HashMap();
        if (keyConverter != null){
            keyValue = keyConverter.convertObjectValueToDataValue(keyValue , session);
        }
        fields.put(keyField, keyValue);
        return fields;
    }
    
    /**
     * INTERNAL:
     * Return the type of the map key, this will be overridden by container policies that allow maps.
     */
    @Override
    public Object getKeyType(){
        return keyField.getType();
    }

    @Override
    public boolean isDirectMapPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Return whether a map key this container policy represents is an attribute
     * DirectMapContainerPolicy's can only have non-mapped values as keys, so return true.
     */
    @Override
    public boolean isMapKeyAttribute(){
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the key mapping
     */
    @Override
    public void initialize(AbstractSession session, DatabaseTable keyTable){
        if (getDirectKeyField(null) == null) {
            throw DescriptorException.directKeyNotSet(elementDescriptor);
        }

        getDirectKeyField(null).setTable(keyTable);
        getDirectKeyField(null).setIndex(1);
    }
    
    /**
     * INTERNAL:
     * Return an Iterator for the given container.
     */
    @Override
    public Object iteratorFor(Object container) {
        return new MapContainerPolicy.MapContainerPolicyIterator((Map)container);
    }

    @Override
    public Object keyFromIterator(Object iterator){
        return ((MapContainerPolicyIterator)iterator).getCurrentKey();
    }
    
    /**
     * Get the key from the passed in Map.Entry.
     */
    @Override
    public Object keyFromEntry(Object entry){
        if (entry instanceof Map.Entry){
            return ((Map.Entry)entry).getKey();
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    @Override
    protected Object next(Object iterator){
        return ((MapContainerPolicyIterator)iterator).next().getValue();
    }

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * This will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    @Override
    public Object nextEntry(Object iterator){
        return ((MapContainerPolicyIterator)iterator).next();
    }
    
    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * This will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(Object iterator, AbstractSession session)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    @Override
    public Object nextEntry(Object iterator, AbstractSession session) {
        return nextEntry(iterator);
    }
    
    /**
     * INTERNAL: 
     * MapContainerPolicy's iterator iterates on the Entries of a Map.
     * This method returns the object from the iterator
     * 
     * @see MapContainerPolicy.nextWrapped(Object iterator)
     */
    @Override
    public Object unwrapIteratorResult(Object object){
        if (object instanceof Map.Entry){
            return ((Map.Entry)object).getValue();
        } else {
            return object;
        }
    }
    
    /**
     * INTERNAL:
     * Remove element from container which implements the Map interface.
     */
    @Override
    public boolean removeFrom(Object key, Object element, Object container, AbstractSession session) {
        try {
            Object returnValue = null;
            returnValue = ((Map)container).remove(key);
            if (returnValue == null) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedOperationException ex) {
            throw QueryException.methodNotValid(container, "remove(Object element)");
        }
    }

    /**
     * INTERNAL:
     * Used during initialization of DirectMapMapping.  Sets the descriptor associated with
     * the key
     * @param descriptor
     */
    public void setDescriptorForKeyMapping(ClassDescriptor descriptor){
        this.elementDescriptor = descriptor;
    }
    
    public void setKeyField(DatabaseField field) {
        keyField = field;
    }
    
    /**
     * INTERNAL:
     * Set the DatabaseField that will represent the key in a DirectMapMapping
     * @param keyField
     * @param descriptor
     */
    public void setKeyField(DatabaseField field, ClassDescriptor descriptor) {
        setKeyField(field);
    }
    
    /**
     * INTERNAL:
     * Used during initialization of DirectMapMapping to make the ContainerPolicy aware of the 
     * DatabaseField used for the value portion of the mapping
     * @param directField
     * @param valueConverter
     */
    public void setValueField(DatabaseField field, Converter converter) {
        valueField = field;
        valueConverter = converter;
    }

    /**
     * INTERNAL:
     * Return the size of container.
     */
    @Override
    public int sizeFor(Object container) {
        return ((Map)container).size();
    }

    /**
     * INTERNAL:
     * Validate the container type.
     */
    @Override
    public boolean isValidContainer(Object container) {
        // PERF: Use instanceof which is inlined, not isAssignable which is very inefficient.
        return container instanceof Map;
    }

    /**
     * INTERNAL:
     * Get the Converter for the key of this mapping if one exists.
     */
    public Converter getKeyConverter() {
        return keyConverter;
    }

    /**
     * INTERNAL:
     * Set a converter on the KeyField of a DirectCollectionMapping
     * @param keyConverter
     * @param mapping
     */
    public void setKeyConverter(Converter keyConverter, DirectMapMapping mapping) {
        this.keyConverter = keyConverter;
    }
    
    /**
     * INTERNAL:
     * Set the name of the class to be used as a converter for the key of a DirectMapMaping
     * @param keyConverterClassName
     * @param mapping
     */
    public void setKeyConverterClassName(String keyConverterClassName, DirectMapMapping mapping) {
        this.keyConverterClassName = keyConverterClassName;
    }

}
