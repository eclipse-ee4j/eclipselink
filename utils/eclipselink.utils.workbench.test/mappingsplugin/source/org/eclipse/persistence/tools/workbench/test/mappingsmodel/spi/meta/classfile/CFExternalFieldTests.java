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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalFieldTests;


public class CFExternalFieldTests extends ExternalFieldTests {

    public static Test suite() {
        return new TestSuite(CFExternalFieldTests.class);
    }

    public CFExternalFieldTests(String name) {
        super(name);
    }

    protected ExternalClassRepository buildRepository() {
        return CFExternalClassRepositoryFactory.instance().buildClassRepository(AllModelSPIMetaClassFileTests.buildMinimumSystemClasspath());
    }

}
