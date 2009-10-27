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
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Vector;

//java extension imports

//EclipseLink imports
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;
import static org.eclipse.persistence.internal.xr.XRDynamicEntity.EmptySlot.EMPTY_SLOT;

/**
 * <p>
 * <b>INTERNAL:</b> XRDynamicEntity is used for models where Java classes do not
 * exist.
 * <p>
 * EclipseLink is based around mapping attributes of a Java class to a table (or
 * tables) with the attributes representing either the column data or
 * foreign-key contraints as relationships to other (mapped) classes. For
 * applications that are based around meta-data and the Java class is either not
 * needed or not available, this basic entity can be used. Subclasses of this
 * abstract class can be dynamically generated at runtime.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings("unchecked")
public class XRDynamicEntity implements DynamicEntity, PersistenceEntity, ChangeTracker,
    FetchGroupTracker, Cloneable {

    static class EmptySlot {
        static EmptySlot EMPTY_SLOT = new EmptySlot();
    }
    
    // propertyNames -> index cache  
    protected Map<String, IndexInfo> propertyNames2indexes = null;
    protected Object[] fields; // XRDynamicEntity only deal with Objects, never primitives

    public XRDynamicEntity(Map<String, IndexInfo> propertyNames2indexes) {
        this.propertyNames2indexes = propertyNames2indexes;
        int maxIdx = -1;
        for (Map.Entry<String, IndexInfo> me : propertyNames2indexes.entrySet()) {
            int currIdx = me.getValue().index;
            if (maxIdx < currIdx) {
                maxIdx = currIdx;
            }
        }
        int size = maxIdx + 1;
        fields = new Object[size];
        for (int i = 0; i < size; i++) {
            fields[i] = EMPTY_SLOT;
        }
    }

    public <T> T get(String propertyName) throws DynamicException {
        IndexInfo indexInfo = getIndexInfoFor(propertyName);
        if (indexInfo.index < 0) {
            throw DynamicException.invalidPropertyIndex((DynamicType)null, indexInfo.index);
        }
        Object o = get(indexInfo.index);
        if (o == EMPTY_SLOT) {
            if (indexInfo.derefVH) {
                fields[indexInfo.index] = new ValueHolder();
            }
            return null;
        }
        if (indexInfo.derefVH) {
            o = ((ValueHolderInterface)o).getValue();
        }
        return (T)o;
    }

    public boolean isSet(String propertyName) throws DynamicException {
        IndexInfo indexInfo = getIndexInfoFor(propertyName);
        Object o = fields[indexInfo.index];
        if (o == EMPTY_SLOT) {
            return false;
        }
        return true;
    }

    public DynamicEntity set(String propertyName, Object value) throws DynamicException {
        IndexInfo indexInfo = getIndexInfoFor(propertyName);
        if (indexInfo.index < 0) {
            throw DynamicException.invalidPropertyIndex((DynamicType)null, indexInfo.index);
        }
        Object o = get(indexInfo.index);
        if (o == EMPTY_SLOT) {
            if (indexInfo.derefVH) {
                fields[indexInfo.index] = new ValueHolder();
            }
            else {
                fields[indexInfo.index] = value;
            }
        }
        else {
            if (indexInfo.derefVH) {
                ((ValueHolderInterface)o).setValue(value);
            }
            else {
                fields[indexInfo.index] = value;
            }
        }
        return (DynamicEntity)this;
    }

    protected IndexInfo getIndexInfoFor(String propertyName) {
        IndexInfo indexInfo = propertyNames2indexes.get(propertyName);
        if (indexInfo == null) {
            throw DynamicException.invalidPropertyName((DynamicType)null, propertyName);
        }
        return indexInfo;
    }

    public Object get(int i) {
        return fields[i];
    }

    public Object set(int i, Object aFieldValue) {
        fields[i] = aFieldValue;
        return this;
    }

    //PersistenceEntity API
    protected CacheKey __cacheKey;
    protected Vector __pk;
    // perf. optimization - cache the __cacheKey and pkVector
    public CacheKey _persistence_getCacheKey() {
        return __cacheKey;
    }
    public void _persistence_setCacheKey(CacheKey cacheKey) {
        this.__cacheKey = cacheKey;
    }
    public Vector _persistence_getPKVector() {
        return __pk;
    }
    public void _persistence_setPKVector(Vector pk) {
        this.__pk = pk;
    }
    
    //ChangeTracker API
    /**
     * ChangeListener used for attribute change tracking processed in the
     * property. Set through
     * {@link ChangeTracker#_persistence_setPropertyChangeListener(PropertyChangeListener)}
     */
    protected PropertyChangeListener __changeListener = null;
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return this.__changeListener;
    }
    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.__changeListener = listener;
    }

    //FetchGroup API
    /**
     * FetchGroup cached by
     * {@link FetchGroupTracker#_persistence_setFetchGroup(FetchGroup)}
     */
    protected FetchGroup __fetchGroup;
    /**
     * {@link FetchGroupTracker#_persistence_setShouldRefreshFetchGroup(boolean)}
     */
    protected boolean __refreshFetchGroup = false;
    /**
     * Session cached by
     * {@link FetchGroupTracker#_persistence_setSession(Session)}
     */
    protected Session __session;
    public FetchGroup _persistence_getFetchGroup() {
        return this.__fetchGroup;
    }
    public Session _persistence_getSession() {
        return this.__session;
    }
    /**
     * Return true if the attribute is in the fetch group being tracked.
     */
    public boolean _persistence_isAttributeFetched(String attribute) {
        return this.__fetchGroup == null || this.__fetchGroup.containsAttribute(attribute);
    }
    /**
     * Reset all attributes of the tracked object to the un-fetched state with
     * initial default values.
     */
    public void _persistence_resetFetchGroup() {
        throw new UnsupportedOperationException("XRDynamicEntity._persistence_resetFetchGroup:: NOT SUPPORTED");
    }
    public void _persistence_setFetchGroup(FetchGroup group) {
        this.__fetchGroup = group;
    }
    public void _persistence_setSession(Session session) {
        this.__session = session;
    }
    public void _persistence_setShouldRefreshFetchGroup(boolean shouldRefreshFetchGroup) {
        this.__refreshFetchGroup = shouldRefreshFetchGroup;
    }
    public boolean _persistence_shouldRefreshFetchGroup() {
        return this.__refreshFetchGroup;
    }

    //Cloneable
    public Object clone() {
        XRDynamicEntity entity = null;
        try {
            entity = (XRDynamicEntity)super.clone();
        }
        catch (Exception error) {
            throw new Error(error);
        }
        entity.fields = entity.fields.clone();
        return entity;
    }
    
    @Override
    public String toString() {
        // this will print something like {Emp 10} or {Phone 234-5678 10}
        StringBuilder sb = new StringBuilder(20);
        sb.append('{');
        sb.append(getShortClassName(this.getClass()));
        if (__pk != null) {
            for (int i = 0; i < __pk.size(); i++) {
                sb.append(' ');
                sb.append(__pk.elementAt(i));
            }
        }
        sb.append('}');
        return sb.toString();
    }

}