/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.helper;

public abstract class CoreConversionManager {

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param object - the object that must be converted
     * @param javaClass - the class that the object must be converted to
     * @exception - ConversionException, all exceptions will be thrown as this type.
     * @return - the newly converted object
     */
    public abstract Object convertObject(Object sourceObject, Class javaClass);

    /**
     * INTERNAL
     */
    public abstract ClassLoader getLoader();

}
