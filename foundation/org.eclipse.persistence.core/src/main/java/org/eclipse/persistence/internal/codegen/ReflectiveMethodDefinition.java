/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model a method for code generation purposes,
 * using java.lang.Class for the parameter types.
 *
 * @since TopLink 5.0
 * @author Paul Fullbright
 */
public class ReflectiveMethodDefinition extends MethodDefinition {
    protected List<Class<?>> argumentTypes;
    protected Class<?> type;

    public ReflectiveMethodDefinition() {
        super();
        this.argumentTypes = new ArrayList<>(5);
        this.type = null;
    }

    public void addArgument(Class<?> argumentType, String argumentName) {
        getArgumentNames().add(argumentName);
        argumentTypes.add(argumentType);
    }

    @Override
    protected boolean argumentsEqual(MethodDefinition methodDefinition) {
        Object[] args1 = this.getArgumentTypes().toArray();
        Object[] args2 = methodDefinition.getArgumentTypes().toArray();
        if (args1.length == args2.length) {
            for (int i = 0; i < args1.length; i++) {
                if (args1[i] != args2[i]) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    protected List<String> getArgumentTypeNames() {
        List<String> argumentTypeNames = new ArrayList<>();

        for (Class<?> argumentType : argumentTypes) {
            argumentTypeNames.add(argumentType.getName());
        }

        return argumentTypeNames;
    }

    @Override
    public List<String> getArgumentTypes() {
        return getArgumentTypeNames();
    }

    @Override
    protected void writeArguments(CodeGenerator generator) {
        boolean isFirst = true;
        for (int index = 0; index < argumentTypes.size(); ++index) {
            Class<?> argument = argumentTypes.get(index);
            if (isFirst) {
                isFirst = false;
            } else {
                generator.write(", ");
            }

            //fixed for CR#4228
            //Bug# 4587853, if argument type is an inner class, convert name
            //from $ notation to . notation.
            if (argument.isArray()) {
                String componentType = argument.getComponentType().getName();
                if(componentType.indexOf('$') != -1) {
                    componentType = componentType.replace('$', '.');
                }
                String componentTypeArrays = componentType + "[]";
                generator.write(componentTypeArrays);
            } else {
                String name = argument.getName();
                if(name.indexOf('$') != -1) {
                    name = name.replace('$', '.');
                }
                generator.write(name);
            }
            generator.write(" ");
            generator.write(getArgumentNames().get(index));
        }
    }

    public Class<?> getReturnTypeClass() {
        return type;
    }

    public void setReturnTypeClass(Class<?> type) {
        this.type = type;
    }

    @Override
    public String getReturnType() {
        if (getReturnTypeClass() != null) {
            if (getReturnTypeClass().isArray()) {
                return this.getReturnTypeClass().getComponentType().getName() + "[]";
            } else {
                return this.getReturnTypeClass().getName();
            }
        }
        return returnType;
    }
}
