/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.oxm.mappings.MimeTypePolicy;

public class AttributeMimeTypePolicy implements MimeTypePolicy {
    private String mimeTypePropertyName;

    public AttributeMimeTypePolicy(String propertyName) {
        this.mimeTypePropertyName = propertyName;
    }

    public String getMimeType(Object obj) {
        if (obj instanceof SDODataObject) {
            String value = ((SDODataObject)obj).getString(mimeTypePropertyName);
            return value;
        }
        return null;
    }
}
