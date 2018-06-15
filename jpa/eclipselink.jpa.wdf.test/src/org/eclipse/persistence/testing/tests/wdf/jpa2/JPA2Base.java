/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa2;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;

public abstract class JPA2Base extends AbstractBaseTest {

    public JPA2Base() {
        super("jpa2testmodel");
    }

    final protected String[] getClearableTableNames() {
        return new String[0];
    }

}
