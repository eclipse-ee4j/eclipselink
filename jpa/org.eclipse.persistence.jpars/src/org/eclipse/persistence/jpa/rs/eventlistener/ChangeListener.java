/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.eventlistener;

/**
 * A ChangeListener is used to extend a PersistenceContext to react to database sent change 
 * events.
 * @author tware
 *
 */
public interface ChangeListener {

    void objectUpdated(String entityName, String transactionId, String rowId); 

    void objectInserted(String entityName, String transactionId, String rowId);
    
    void register();
    
    void unregister();

}

