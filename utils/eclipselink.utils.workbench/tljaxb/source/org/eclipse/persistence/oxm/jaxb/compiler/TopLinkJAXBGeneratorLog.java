/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;


/**
 *  @version 1.0
 *  @author  mmacivor
 *  @since   10.1.3
 * A class which logs tljaxb generation messages and warnings to system.out.
 */
public class TopLinkJAXBGeneratorLog {
    public static int LOG_LEVEL_NONE = 0;
    public static int LOG_LEVEL_VERBOSE = 1;
    public static int LOG_LEVEL_DEBUG = 2;
    public static int logLevel = 0;

    public static void log(String message) {
        if (logLevel != 0) {
            System.out.println("tljaxb: " + message);
        }
    }

    public static void logDebug(String message) {
        if (logLevel == LOG_LEVEL_DEBUG) {
            System.out.println("tljaxb: " + message);
        }
    }

    public static void warning(String message) {
        System.out.println("tljaxb: " + message);
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int value) {
        logLevel = value;
    }
}