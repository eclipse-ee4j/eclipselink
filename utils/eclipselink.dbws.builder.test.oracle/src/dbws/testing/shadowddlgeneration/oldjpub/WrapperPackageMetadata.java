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
//     Mike Norman - from Proof-of-concept, become production code
//     quwang - Aug 9, 2006
package dbws.testing.shadowddlgeneration.oldjpub;

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
