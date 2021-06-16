/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.helper;

import org.eclipse.persistence.exceptions.ConversionException;

public abstract class CoreConversionManager {

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param sourceObject the object that must be converted
     * @param javaClass the class that the object must be converted to
     * @exception ConversionException all exceptions will be thrown as this type.
     * @return the newly converted object
     */
    public abstract Object convertObject(Object sourceObject, Class javaClass);

    /**
     * INTERNAL
     */
    public abstract ClassLoader getLoader();

}
