/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.dynamic;

public interface EclipseLinkClassWriter {

    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException;

    public boolean isCompatible(EclipseLinkClassWriter writer);

    public Class<?> getParentClass();

    public String getParentClassName();
}
