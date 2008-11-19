/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.converters.*;
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
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 */
public class DirectMapContainerPolicy extends InterfaceContainerPolicy {
    protected DatabaseField keyField;
    protected DatabaseField valueField;
    protected Converter keyConverter;
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
     * Add key, value pair into container which implements the Map interface.
     */
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
     * Add element into container which implements the Map interface. Not used since key is not obtained from the object
     */
    public boolean addInto(Object element, Object container, AbstractSession session) {
        throw ValidationException.operationNotSupported("addInto(Object element, Object container, Session session)");
    }

    /**
     * INTERNAL:
     * Return a container populated with the contents of the specified Vector.
     */
    public Object buildContainerFromVector(Vector vector, AbstractSession session) {
        Map container = (Map)containerInstance(vector.size());
        AbstractRecord row;

        for (Enumeration e = vector.elements(); e.hasMoreElements();) {
            row = (AbstractRecord)e.nextElement();
            Object key = row.get(keyField);
            Object value = row.get(valueField);
            if (getKeyConverter() != null) {
                key = getKeyConverter().convertDataValueToObjectValue(key, session);
            }
            if (getValueConverter() != null) {
                value = getValueConverter().convertDataValueToObjectValue(value, session);
            }
            if (key != null) {
                container.put(key, value);
            }
        }
        return container;
    }

    /**
     * INTERNAL:
     * Remove all the elements from container.
     */
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
            Object key = next(firstIterator);
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
    protected boolean contains(Object element, Object container) {
        return ((Map)container).containsValue(element);
    }

    public Class getInterfaceType() {
        return ClassConstants.Map_Class;
    }

    public boolean isDirectMapPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Return an Iterator for the given container.
     */
    public Object iteratorFor(Object container) {
        if (((Map)container).keySet() == null) {
            return null;
        }
        return ((Map)container).keySet().iterator();
    }

    /**
     * INTERNAL:
     * Return an Iterator for the given container.
     */
    public Object iteratorForValue(Object container) {
        if (((Map)container).values() == null) {
            return null;
        }
        return ((Map)container).values().iterator();
    }

    /**
     * INTERNAL:
     * Remove element from container which implements the Map interface.
     */
    public boolean removeFrom(Object key, Object element, Object container, AbstractSession session) {
        try {
            Object returnValue = null;
            if (key != null) {
                returnValue = ((Map)container).remove(key);
            } else {
                returnValue = ((Map)container).remove(keyFrom(element, session));
            }
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
     * Remove element from container which implements the Map interface.
     */
    public boolean removeFromWithIdentity(Object element, Object container, AbstractSession session) {
        boolean found = false;
        Vector knownKeys = new Vector(1);
        try {
            Iterator iterator = ((Map)container).keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                if (((Map)container).get(key) == element) {
                    knownKeys.addElement(key);
                    found = true;
                }
            }
            if (found) {
                for (int index = 0; index < knownKeys.size(); ++index) {
                    ((Map)container).remove(knownKeys.elementAt(index));
                }
            }
            return found;
        } catch (UnsupportedOperationException ex) {
            throw QueryException.methodNotValid(container, "remove(Object element)");
        }
    }

    public void setKeyField(DatabaseField field) {
        keyField = field;
    }

    public void setValueField(DatabaseField field) {
        valueField = field;
    }

    /**
     * INTERNAL:
     * Return the size of container.
     */
    public int sizeFor(Object container) {
        return ((Map)container).size();
    }

    /**
     * INTERNAL:
     * If the key has changed, remove the element and add it back into the target.
     */
    public void validateElementAndRehashIfRequired(Object sourceValue, Object targetMap, AbstractSession session, Object targetVersionOfSource) {
        if (session.isUnitOfWork()) {
            //this must be a unit of work at this point
            Object backupValue = ((UnitOfWorkImpl)session).getBackupClone(sourceValue, getElementDescriptor());
            if (!keyFrom(backupValue, session).equals(keyFrom(sourceValue, session))) {
                //the key has been changed.  Remove the old value and put back the new one
                removeFrom(backupValue, targetMap, session);
                addInto(targetVersionOfSource, targetMap, session);
            }
        }
    }

    /**
     * INTERNAL:
     * Validate the container type.
     */
    public boolean isValidContainer(Object container) {
        // PERF: Use instanceof which is inlined, not isAssignable which is very inefficent.
        return container instanceof Map;
    }

    /**
     * INTERNAL:
     * Return an value of the key from container
     */
    public Object valueFromKey(Object key, Object container) {
        return ((Map)container).get(key);
    }

    public Converter getKeyConverter() {
        return keyConverter;
    }

    public void setKeyConverter(Converter keyConverter) {
        this.keyConverter = keyConverter;
    }

    public void setValueConverter(Converter valueConverter) {
        this.valueConverter = valueConverter;
    }

    public Converter getValueConverter() {
        return valueConverter;
    }
}