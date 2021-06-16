/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - 2.6 - initial implementation
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
