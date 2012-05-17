/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

public class JPARSLogger {

    /**
     * INTERNAL:
     * Logging utility method.
     */
    public static void log(String message, int level, Object[] params) {
        AbstractSessionLog.getLog().log(level, SessionLog.JPARS, message, params);
    }
    
    public static void finest(String message, Object[] params){
        log(message, SessionLog.FINEST, params);
    }

    public static void fine(String message, Object[] params){
        log(message, SessionLog.FINEST, params);
    }
    
}
