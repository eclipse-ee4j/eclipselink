/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
