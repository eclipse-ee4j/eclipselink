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
 *     Mike Norman - from Proof-of-concept, become production code
 *     quwang - Aug 9, 2006
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.util.ArrayList;

public class WrapperPackageMetadata {

    private String name;
    private ArrayList<WrapperMethodMetadata> wrapperMethods = new ArrayList<WrapperMethodMetadata>();

    public WrapperPackageMetadata(String name) {
        this.name = name;
    }

    public void addMethod(WrapperMethodMetadata method) {
        wrapperMethods.add(method);
    }

    public WrapperMethodMetadata[] getWrapperMethods() {
        return  wrapperMethods.toArray(new WrapperMethodMetadata[wrapperMethods.size()]);
    }

    public String getName() {
        return name;
    }
}
