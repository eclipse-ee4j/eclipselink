/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     24 April 2013-2.5.1 ailitche
 *       SerializedObjectPolicy initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import java.util.List;

import org.eclipse.persistence.descriptors.SerializedObjectPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * It's a transition class that holds SerializedObjectPolicy class name
 * and field obtained from matadata before it can be instantiated.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 2.5.1
 */
public class SerializedObjectPolicyWrapper extends AbstractSerializedObjectPolicy {
    protected String serializedObjectPolicyClassName;
    
    public SerializedObjectPolicyWrapper(String serializedObjectPolicyClassName) {
        super();
        this.serializedObjectPolicyClassName = serializedObjectPolicyClassName;
    }
    
    public String getSerializedObjectPolicyClassName() {
        return this.serializedObjectPolicyClassName;
    }

    public SerializedObjectPolicyWrapper clone() {
        throw new UnsupportedOperationException("clone");
    }
    public SerializedObjectPolicy instantiateChild() {
        throw new UnsupportedOperationException("instantiateChild");
    }

    public void initializeField(AbstractSession session) {
        throw new UnsupportedOperationException("initializeField");
    }
    public void initialize(AbstractSession session) {
        throw new UnsupportedOperationException("initialize");
    }
    public void postInitialize(AbstractSession session) {
        throw new UnsupportedOperationException("postInitialize");
    }

    public void putObjectIntoRow(AbstractRecord databaseRow, Object object, AbstractSession session) {
        throw new UnsupportedOperationException("putObjectIntoRow");
    }
    public Object getObjectFromRow(AbstractRecord databaseRow, AbstractSession session) {
        throw new UnsupportedOperationException("getObjectFromRow");
    }
    public List<DatabaseField> getFieldsToSelect() {
        throw new UnsupportedOperationException("getFieldsToSelect");
    }
    public List<DatabaseField> getAllFieldsToSelect() {
        throw new UnsupportedOperationException("getAllFieldsToSelect");
    }
    protected Class getFieldType() {
        throw new UnsupportedOperationException("getFieldType");
    }
}
