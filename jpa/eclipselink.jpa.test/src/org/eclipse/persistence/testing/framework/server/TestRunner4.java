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
 package org.eclipse.persistence.testing.framework.server;

import java.util.Properties;

/**
 * Remote business interface for TestRunner session bean.
 *
 * @author mschinca
 */
public interface TestRunner4 {
    public Throwable runTest(String className, String test, Properties props);
}
