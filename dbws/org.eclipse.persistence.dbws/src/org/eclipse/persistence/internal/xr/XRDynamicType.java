/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.xr;

import org.eclipse.persistence.dynamic.DynamicType;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

/**
 * Dummy implementation of {@link DynamicType}
 * @author mnorman
 *
 */
public class XRDynamicType extends DynamicTypeImpl {

    public XRDynamicType() {
        super();
    }

    @Override
    public String getName() {
        return "<null>";
    }
}
