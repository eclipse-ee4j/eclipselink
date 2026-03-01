/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.classgen;

import org.eclipse.persistence.sdo.helper.SDOClassGeneratorListener;


public class TestSDOClassGeneratorListener implements SDOClassGeneratorListener {

    private String cr = System.lineSeparator();

    public TestSDOClassGeneratorListener() {
    }

    @Override
    public void preInterfacePackage(StringBuffer buffer) {
        buffer.append("//this is the preInterfacePackage event" + cr);
    }

    @Override
    public void preImplPackage(StringBuffer buffer) {
        buffer.append("//this is the preImplPackage event"+ cr);
    }

    @Override
    public void preInterfaceImports(StringBuffer buffer) {
        buffer.append("//this is the preInterfaceImports event"+ cr);
    }

    @Override
    public void preImplImports(StringBuffer buffer) {
        buffer.append("//this is the preImplImports event"+ cr);
    }

    @Override
    public void preInterfaceClass(StringBuffer buffer) {
        buffer.append("//this is the preInterfaceClass event"+ cr);
    }

    @Override
    public void preImplClass(StringBuffer buffer) {
        buffer.append("//this is the preImplClass event"+ cr);
    }

    @Override
    public void preImplAttributes(StringBuffer buffer) {
        buffer.append("//this is the preImplAttributes event"+ cr);
    }
}
