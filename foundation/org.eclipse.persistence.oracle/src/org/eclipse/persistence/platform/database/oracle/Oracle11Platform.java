/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs for the Oracle 11 database.
 */
public class Oracle11Platform extends Oracle10Platform {
    public Oracle11Platform() {
        super();
        // Locator is no longer required to write LOB values
        usesLocatorForLOBWrite = false;
    }
}
