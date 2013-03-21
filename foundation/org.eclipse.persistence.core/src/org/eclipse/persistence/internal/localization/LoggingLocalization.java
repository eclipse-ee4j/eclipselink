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
package org.eclipse.persistence.internal.localization;


/**
 *  <p>
 * <b>Purpose</b>: This is for any logging related messages
 *
 * @author: Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class LoggingLocalization extends EclipseLinkLocalization {
    public static String buildMessage(String key, Object[] arguments) {
        return buildMessage("LoggingLocalization", key, arguments);
    }

    public static String buildMessage(String key) {
        return buildMessage(key, (Object[])null);
    }
}
