/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for JMSProcessingException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: TopLink maintenance team
 */
public class JMSProcessingExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "18001", "Error while processing incomming JMS message" },
                                           { "18002", "The Topic created in the JMS Service for the interconnection of Sessions must be set in the JMSClusteringService" },
                                           { "18003", "Failed to lookup the session's name defined it the env-entry element of the Message Driven Bean" },
                                           { "18004", "The Message Driven Bean (MDB) cannot find the session.  The MDB getSession() method must returns a not null sessiion" }   
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
