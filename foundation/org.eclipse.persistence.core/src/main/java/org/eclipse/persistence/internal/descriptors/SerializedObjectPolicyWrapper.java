/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     24 April 2013-2.5.1 ailitche
//       SerializedObjectPolicy initial API and implementation
package org.eclipse.persistence.internal.descriptors;

import java.util.List;

import org.eclipse.persistence.descriptors.SerializedObjectPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

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

    @Override
    public SerializedObjectPolicyWrapper clone() {
        throw new UnsupportedOperationException("clone");
    }
    @Override
    public SerializedObjectPolicy instantiateChild() {
        throw new UnsupportedOperationException("instantiateChild");
    }
    @Override
    public void initializeField(AbstractSession session) {
        throw new UnsupportedOperationException("initializeField");
    }
    @Override
    public void initialize(AbstractSession session) {
        throw new UnsupportedOperationException("initialize");
    }
    @Override
    public void postInitialize(AbstractSession session) {
        throw new UnsupportedOperationException("postInitialize");
    }
    @Override
    public void putObjectIntoRow(AbstractRecord databaseRow, Object object, AbstractSession session) {
        throw new UnsupportedOperationException("putObjectIntoRow");
    }
    @Override
    public Object getObjectFromRow(AbstractRecord databaseRow, AbstractSession session, ObjectLevelReadQuery query) {
        throw new UnsupportedOperationException("getObjectFromRow");
    }
    @Override
    public List<DatabaseField> getSelectionFields() {
        throw new UnsupportedOperationException("getSelectionFields");
    }
    @Override
    public List<DatabaseField> getAllSelectionFields() {
        throw new UnsupportedOperationException("getAllSelectionFields");
    }
}
