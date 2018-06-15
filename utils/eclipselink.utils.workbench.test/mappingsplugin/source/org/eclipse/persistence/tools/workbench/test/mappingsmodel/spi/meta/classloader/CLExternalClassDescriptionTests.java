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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader.CLExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalClassDescriptionTests;


public class CLExternalClassDescriptionTests extends ExternalClassDescriptionTests {

    public static Test suite() {
        return new TestSuite(CLExternalClassDescriptionTests.class);
    }

    public CLExternalClassDescriptionTests(String name) {
        super(name);
    }

    protected ExternalClassRepository buildRepository() {
        return CLExternalClassRepositoryFactory.instance().buildClassRepository(new File[0]);
    }

}
