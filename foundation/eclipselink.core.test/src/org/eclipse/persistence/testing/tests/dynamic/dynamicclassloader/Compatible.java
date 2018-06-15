/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.dynamic.dynamicclassloader;

import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager;

/**
 * Test class - when created by a {@link DynamicClassLoader}, should <b>not</b> throw {@link IllegalArgumentException}
 * @author mnorman
 *
 */
public class Compatible extends DynamicEntityImpl {

    public static DynamicPropertiesManager DPM = new DynamicPropertiesManager();
    protected Compatible() {
        super();
    }

    @Override
    public DynamicPropertiesManager fetchPropertiesManager() {
        return DPM;
    }

}
