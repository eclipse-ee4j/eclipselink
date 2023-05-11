/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman (Oracle) - initial implementation
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.Comparator;

public class SchemaCompareByNamespace implements Comparator<Schema>{

    @Override
    public int compare(Schema arg1, Schema arg2) {
        String targetNamespace1 = arg1.getTargetNamespace() != null ? arg1.getTargetNamespace() : "";
        String targetNamespace2 = arg2.getTargetNamespace() != null ? arg2.getTargetNamespace() : "";
        return targetNamespace1.compareTo(targetNamespace2);
    }
}
