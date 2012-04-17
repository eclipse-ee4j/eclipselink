/****************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.eventlistener;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.sessions.Session;

/**
 * Extends EclipseLink's database event listening capabilities by allowing a listener to subscribe to
 * to change notifications from the database
 * 
 * This listener also expands the interface to support subscription on a descriptor basis rather than 
 * the wholesale subscription provided by its superclass.
 * 
 * @author tware
 *
 */
public interface DescriptorBasedDatabaseEventListener extends DatabaseEventListener {

    /**
     * Register for change notifications on a particular descriptor
     * @param session
     * @param descriptor
     */
    public void register(Session session, ClassDescriptor descriptor);
    
    public void addChangeListener(ChangeListener listener);
    
    public void removeChangeListener(ChangeListener listener);
}
