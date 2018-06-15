/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/04/2014 - Rick Curtis
//       - 450010 : Add java se test bucket
package org.eclipse.persistence.jpa.test.framework;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class EmfRunner extends BlockJUnit4ClassRunner {
    private final EmfRunnerInjector _mgr;

    public EmfRunner(Class<?> cls) throws InitializationError {
        super(cls);
        _mgr = new EmfRunnerInjector();
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();

        _mgr.inject(testInstance);

        return testInstance;
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            super.run(notifier);
        } finally {
            _mgr.close();
        }
    }
}
