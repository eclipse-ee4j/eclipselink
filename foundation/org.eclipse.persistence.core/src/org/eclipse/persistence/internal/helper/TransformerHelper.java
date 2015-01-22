/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.core.mappings.transformers.CoreFieldTransformer;

/**
 * Transformer helper class. Returns transformer method name and possible transformer method parameters.
 * This class shields client of the class from internal details of transformer method.
 *
 * @author Martin Vojtek
 *
 */
public class TransformerHelper {

    public String getTransformerMethodName() {
        return CoreFieldTransformer.BUILD_FIELD_VALUE_METHOD;
    }

    public List<Class[]> getTransformerMethodParameters(boolean isSetTransformerClass) {
        List<Class[]> methodParameters = new ArrayList<>();
        if (isSetTransformerClass) {
            methodParameters.add(new Class[] {Object.class, String.class, ClassConstants.SessionsSession_Class});
        } else {
            methodParameters.add(new Class[0]);
            methodParameters.add(new Class[] {ClassConstants.PublicInterfaceSession_Class});
            methodParameters.add(new Class[] {ClassConstants.SessionsSession_Class});
        }
        return methodParameters;
    }
}
