/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.oxm.mappings.MimeTypePolicy;

public class AttributeMimeTypePolicy implements MimeTypePolicy {
    private String mimeTypePropertyName;

    public AttributeMimeTypePolicy(String propertyName) {
        this.mimeTypePropertyName = propertyName;
    }

    @Override
    public String getMimeType(Object obj) {
        if (obj instanceof SDODataObject) {
            String value = ((SDODataObject)obj).getString(mimeTypePropertyName);
            return value;
        }
        return null;
    }
}
