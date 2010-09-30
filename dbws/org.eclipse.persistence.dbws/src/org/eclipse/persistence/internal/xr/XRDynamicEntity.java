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
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

//EclipseLink imports
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

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
public abstract class XRDynamicEntity implements DynamicEntity, PersistenceEntity, ChangeTracker,
    FetchGroupTracker {

    static final Object UNKNOWN_VALUE = new Object();
    public static final String XR_FIELD_INFO_STATIC = "XRFI";

    protected XRField[] _fields;

    public XRDynamicEntity() {
        super();
        int numFields = getFieldInfo().numFields();
        _fields = new XRField[numFields];
        for (int i = 0; i < numFields; i++) {
            _fields[i] = new XRField();
        }
    }

    public abstract XRFieldInfo getFieldInfo();

    protected int getFieldIdx(String fieldName) {
        return getFieldInfo().getFieldIdx(fieldName);
    }

    public <T> T get(String fieldName) throws DynamicException {
        return (T) get(getFieldIdx(fieldName));
    }
    protected Object get(int fieldIdx) {
        XRField df = _fields[fieldIdx];
        Object value = null;
        if (df.isSet) {
            value = df.fieldValue;
            // trigger any indirection
            if (value instanceof ValueHolderInterface) {
                value = ((ValueHolderInterface)value).getValue();
            }
            else if (value instanceof IndirectContainer) {
                value = ((IndirectContainer)value).getValueHolder().getValue();
            }
        }
        return value;
    }

    public boolean isSet(String fieldName) throws DynamicException {
        return isSet(getFieldIdx(fieldName));
    }
    protected boolean isSet(int fieldIdx) {
        return _fields[fieldIdx].isSet;
    }

    public DynamicEntity set(String fieldName, Object value) throws DynamicException {
        set(getFieldIdx(fieldName), value);
        return (DynamicEntity)this;
    }

    protected void set(int fieldIdx, Object value) {
        XRField df = _fields[fieldIdx];
        Object oldValue = UNKNOWN_VALUE;
        Object currentValue = df.fieldValue;
        if (currentValue instanceof ValueHolderInterface) {
            ValueHolderInterface vh = (ValueHolderInterface)currentValue;
            if (vh.isInstantiated()) {
                oldValue = vh.getValue();
            }
            vh.setValue(value);
        }
        else {
            oldValue = currentValue;
            df.fieldValue = value;
        }
        df.isSet = true;
        PropertyChangeListener pcl = _persistence_getPropertyChangeListener();
        if (pcl != null) {
            pcl.propertyChange(new PropertyChangeEvent(this, getFieldInfo().getFieldName(fieldIdx),
                oldValue, value));
        }
    }

    //PersistenceEntity API
    protected Object __pk;
    public Object _persistence_getId() {
        return __pk;
    }
    public void _persistence_setId(Object pk) {
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
        throw new UnsupportedOperationException(
            "XRDynamicEntity._persistence_resetFetchGroup:: NOT SUPPORTED");
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

    @Override
    public String toString() {
        // this will print something like {Emp 10} or {Phone 234-5678 10}
        StringBuilder sb = new StringBuilder(20);
        sb.append('{');
        sb.append(getShortClassName(this.getClass()));
        if (__pk != null) {
            sb.append(' ');
            sb.append(__pk);
        }
        sb.append('}');
        return sb.toString();
    }

    static class XRField {
        protected Object fieldValue = null;
        protected boolean isSet = false;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (isSet) {
                sb.append("[T]");
            }
            else {
                sb.append("[F]");
            }
            if (fieldValue == null) {
                sb.append("<null>");
            }
            else {
                sb.append(fieldValue.toString());
            }
            return sb.toString();
        }
    }
}