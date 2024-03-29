/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;

/**
 * INTERNAL:
 * An implementation of FieldTransformation which holds onto a transformer class-name
 * which will be instantiated to do transformations
 *
 * @author  mmacivor
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class TransformerBasedFieldTransformation extends FieldTransformation {
    protected Class<?> transformerClass;
    protected String transformerClassName;
    protected FieldTransformer transformer;

    public TransformerBasedFieldTransformation() {
        super();
    }

    public TransformerBasedFieldTransformation(FieldTransformer aTransformer) {
        transformer = aTransformer;
        if (transformer != null) {
            setTransformerClass(transformer.getClass());
            setTransformerClassName(transformer.getClass().getName());
        }
    }

    public Class<?> getTransformerClass() {
        return transformerClass;
    }

    public void setTransformerClass(Class<?> transformerClass) {
        this.transformerClass = transformerClass;
    }

    public String getTransformerClassName() {
        return transformerClassName;
    }

    public void setTransformerClassName(String transformerClassName) {
        this.transformerClassName = transformerClassName;
    }

    @Override
    public FieldTransformer buildTransformer() throws Exception {
        if (transformer == null) {
            transformer = PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> (FieldTransformer) PrivilegedAccessHelper.newInstanceFromClass(getTransformerClass())
            );
        }
        return transformer;
    }
}
