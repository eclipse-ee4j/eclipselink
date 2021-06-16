/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.conversion;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;

// This works as a wrapper for CM and Oracle9Platform.  It is used in DataTypesConvertedXX tests.
public class CMAndPlatformWrapper {
    Object wrappedObj;

    public CMAndPlatformWrapper(Object object) {
        wrappedObj = object;
    }

    public Vector getDataTypesConvertedFrom(Class aClass) {
        if (wrappedObj instanceof ConversionManager) {
            return ((ConversionManager)wrappedObj).getDataTypesConvertedFrom(aClass);
        } else if (wrappedObj instanceof Oracle9Platform) {
            return ((Oracle9Platform)wrappedObj).getDataTypesConvertedFrom(aClass);
        }
        return new Vector();
    }

    public Vector getDataTypesConvertedTo(Class aClass) {
        if (wrappedObj instanceof ConversionManager) {
            return ((ConversionManager)wrappedObj).getDataTypesConvertedTo(aClass);
        } else if (wrappedObj instanceof Oracle9Platform) {
            return ((Oracle9Platform)wrappedObj).getDataTypesConvertedTo(aClass);
        }
        return new Vector();
    }

    public Object convertObject(Object object, Class aClass) {
        if (wrappedObj instanceof ConversionManager) {
            return ((ConversionManager)wrappedObj).convertObject(object, aClass);
        } else if (wrappedObj instanceof Oracle9Platform) {
            return ((Oracle9Platform)wrappedObj).convertObject(object, aClass);
        }
        return null;
    }
}
