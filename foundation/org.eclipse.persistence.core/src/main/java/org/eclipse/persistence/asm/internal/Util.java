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
//     Oracle - initial API and implementation
package org.eclipse.persistence.asm.internal;

import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.exceptions.ValidationException;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;

public final class Util {

    //java.lang.invoke.VarHandle >= JDK 9


    private Util() {
        // no instance
    }

    public static Object getFieldValue(Map<String, String> targetNames, String name, Class<?> type) {
        String asmService = ASMFactory.getAsmService();
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            String className = targetNames.get(asmService);
            if (className == null) {
                throw ValidationException.incorrectASMServiceProvided();
            }
            Class<?> clazz = Class.forName(className);
            VarHandle field = lookup.findStaticVarHandle(clazz, name, type);
            return field.get();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw ValidationException.notAvailableASMService();
        }
    }

}
