/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
