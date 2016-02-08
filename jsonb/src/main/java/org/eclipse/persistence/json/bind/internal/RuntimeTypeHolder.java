/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import java.lang.reflect.Type;

/**
 * Holds runtime type and wrapper runtime type info if any.
 * @author Roman Grigoriadi
 */
public class RuntimeTypeHolder implements RuntimeTypeInfo {

    private final RuntimeTypeInfo wrapper;

    private final Type runtimeType;

    public RuntimeTypeHolder(RuntimeTypeInfo wrapper, Type runtimeType) {
        this.wrapper = wrapper;
        this.runtimeType = runtimeType;
    }

    /**
     * Wrapper containing property of this type.
     * @return wrapper
     */
    @Override
    public RuntimeTypeInfo getWrapper() {
        return wrapper;
    }

    /**
     * Runtime type of this item.
     * @return runtime type
     */
    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }
}
