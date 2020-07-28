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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.transformers.*;

/**
 * INTERNAL:
 * This is an implementation of FieldTransformation which stores a method name and
 * uses that name to instantiate a MethodBasedFieldTransformer.
 * @author  mmacivor
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class MethodBasedFieldTransformation extends FieldTransformation {
    protected String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String name) {
        methodName = name;
    }

    public FieldTransformer buildTransformer() throws Exception {
        return new MethodBasedFieldTransformer(getMethodName());
    }
}
