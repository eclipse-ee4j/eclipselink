/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.classgen;

import org.eclipse.persistence.sdo.helper.SDOClassGeneratorListener;


public class TestSDOClassGeneratorListener implements SDOClassGeneratorListener {

    private String cr = System.getProperty("line.separator");

    public TestSDOClassGeneratorListener() {
    }

    public void preInterfacePackage(StringBuffer buffer) {
        buffer.append("//this is the preInterfacePackage event" + cr);
    }

    public void preImplPackage(StringBuffer buffer) {
        buffer.append("//this is the preImplPackage event"+ cr);
    }

    public void preInterfaceImports(StringBuffer buffer) {
        buffer.append("//this is the preInterfaceImports event"+ cr);
    }

    public void preImplImports(StringBuffer buffer) {
        buffer.append("//this is the preImplImports event"+ cr);
    }

    public void preInterfaceClass(StringBuffer buffer) {
        buffer.append("//this is the preInterfaceClass event"+ cr);
    }

    public void preImplClass(StringBuffer buffer) {
        buffer.append("//this is the preImplClass event"+ cr);
    }

    public void preImplAttributes(StringBuffer buffer) {
        buffer.append("//this is the preImplAttributes event"+ cr);
    }
}
