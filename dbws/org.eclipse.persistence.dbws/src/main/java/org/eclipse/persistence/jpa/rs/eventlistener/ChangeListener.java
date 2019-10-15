/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      dclarke/tware - initial
package org.eclipse.persistence.jpa.rs.eventlistener;

/**
 * A ChangeListener is used to extend a PersistenceContext to react to database sent change
 * events.
 * @author tware
 *
 */
public interface ChangeListener {

    void objectUpdated(String queryName, String entityName, String transactionId, String rowId);

    void objectInserted(String queryName, String entityName, String transactionId, String rowId);

    void register();

    void unregister();

}

