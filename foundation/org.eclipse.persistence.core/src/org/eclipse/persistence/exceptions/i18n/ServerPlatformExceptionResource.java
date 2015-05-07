/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
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

public class ServerPlatformExceptionResource extends ListResourceBundle {

    static final Object[][] contents = {
        { "63001", "Server platform class {0} not found." },
        { "63002", "Server platform class is invalid: " }
    };

    /**
     * Return the lookup table.
     * @return lookup table
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }

}
