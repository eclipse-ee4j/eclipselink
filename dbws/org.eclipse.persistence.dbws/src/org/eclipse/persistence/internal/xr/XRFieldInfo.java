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
package org.eclipse.persistence.internal.xr;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.DynamicException;

public class XRFieldInfo {

    protected HashMap<String, Index> fieldInfo = new HashMap<String, Index>();

    public XRFieldInfo() {
        super();
    }

    public int numFields() {
        return fieldInfo.size();
    }

    public void addFieldInfo(String fieldName, int idx) {
        fieldInfo.put(fieldName, new Index(idx));
    }

    public int getFieldIdx(String fieldName) {
        Index i = fieldInfo.get(fieldName);
        if (i == null) {
            throw DynamicException.invalidPropertyName(null, fieldName);
        }
        return i.idx;
    }

    public String getFieldName(int fieldIdx) {
        String fieldName = null;
        for (Map.Entry<String, Index> me : fieldInfo.entrySet()) {
            if (me.getValue().idx == fieldIdx) {
                fieldName = me.getKey();
                break;
            }
        }
        return fieldName;
    }

   // Made static final for performance reasons.
   private static final class Index {
        int idx;
        public Index(int idx) {
            this.idx = idx;
        }
    }
}