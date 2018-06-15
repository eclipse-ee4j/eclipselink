/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.internal.xr;

import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesInitializatonPolicy;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

public class XRDynamicPropertiesInitializatonPolicy extends DynamicPropertiesInitializatonPolicy {

    public XRDynamicPropertiesInitializatonPolicy() {
        super();
    }

    @Override
    public void initializeProperties(DynamicTypeImpl type, DynamicEntityImpl entity) {
        // no-op
    }

}
