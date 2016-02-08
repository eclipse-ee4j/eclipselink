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
 * Holds runtime type info of the class. Used for generic type resolution, especially during unmarshalling.
 *
 * @author Roman Grigoriadi
 */
public interface RuntimeTypeInfo {

    /**
     * Runtime type holder of a wrapper class of this runtime type.
     *
     * @return Runtime type info
     */
    RuntimeTypeInfo getWrapper();

    /**
     * Runtime type of a class. Can be a class, ParameterizedType, or TypeVariable.
     * When a field or a class is declared including generic information this will return runtime type info.
     *
     * @return
     */
    Type getRuntimeType();
}
