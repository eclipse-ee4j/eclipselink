/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    @Override
    public FieldTransformer buildTransformer() throws Exception {
        return new MethodBasedFieldTransformer(getMethodName());
    }
}
