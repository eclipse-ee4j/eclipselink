/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        return null;
    }

    public Vector getDataTypesConvertedTo(Class aClass) {
        if (wrappedObj instanceof ConversionManager) {
            return ((ConversionManager)wrappedObj).getDataTypesConvertedTo(aClass);
        } else if (wrappedObj instanceof Oracle9Platform) {
            return ((Oracle9Platform)wrappedObj).getDataTypesConvertedTo(aClass);
        }
        return null;
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
