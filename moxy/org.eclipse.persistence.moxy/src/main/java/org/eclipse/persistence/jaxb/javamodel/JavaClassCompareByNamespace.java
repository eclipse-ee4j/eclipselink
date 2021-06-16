/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jaxb.javamodel;

import org.eclipse.persistence.jaxb.compiler.TypeInfo;

import java.util.Comparator;
import java.util.Map;

public class JavaClassCompareByNamespace implements Comparator<JavaClass>{

    private final Map<String, TypeInfo> typeInfo;

    public JavaClassCompareByNamespace(final Map<String, TypeInfo> typeInfo) {
        this.typeInfo = typeInfo;
    }

    @Override
    public int compare(JavaClass arg1, JavaClass arg2) {
        int result;
        String namespaceUri1 = this.typeInfo.get(arg1.getQualifiedName()).getClassNamespace();
        String namespaceUri2 = this.typeInfo.get(arg2.getQualifiedName()).getClassNamespace();
        result = namespaceUri1.compareTo(namespaceUri2);
        if (result != 0) {
            return result;
        } else {
            return arg1.getQualifiedName().compareTo(arg2.getQualifiedName());
        }
    }
}

