/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial design and implementation
 ******************************************************************************/
package org.eclipse.persistence.annotations;


/**
 * Configures what type of database change notification an entity/descriptor should use.
 * This is only relevant if the persistence unit/session has been configured with a DatabaseEventListener,
 * such as the OracleChangeNotificationListener that receives database change events.
 * This allows for the EclipseLink cache to be invalidated or updated from database changes.
 * 
 * @see Cache#databaseChangeNotificationType()
 * @see org.eclipse.persistence.descriptors.CachePolicy#setDatabaseChangeNotificationType(DatabaseChangeNotificationType)
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public enum DatabaseChangeNotificationType {
    /**
     * No database change events will be processed.
     */
    NONE,

    /**
     * Invalidates the EclipseLink cache when a database change event is received for an object.
     * This requires a DatabaseEventListener to be configured with the persistence unit or session.
     * This requires that the database change can be determine to affect the object, some database change events,
     * such as the OracleChangeNotificationListener (Oracle DCN/QCN) only give the ROWID, so changes to secondary
     * table or relationships may not be able to be determined to affect and object unless its version is also changed.
     */
    INVALIDATE
}
