/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 *     quwang - Aug 9, 2006
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class WrapperPackageMetadata {
    public WrapperPackageMetadata(String name) {
        this.name = name;
    }

    public void addMethod(WrapperMethodMetadata method) {
        wrapperMethods.add(method);
    }

    public WrapperMethodMetadata[] getWrapperMethods() {
        return (WrapperMethodMetadata[])wrapperMethods.toArray(new WrapperMethodMetadata[0]);
    }

    public String getName() {
        return name;
    }

    private String name;
    private ArrayList wrapperMethods = new ArrayList();
}
