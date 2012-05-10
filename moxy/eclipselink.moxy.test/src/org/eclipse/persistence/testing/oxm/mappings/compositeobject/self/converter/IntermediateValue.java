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
 *     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import java.util.ArrayList;
import java.util.List;

public class IntermediateValue {

    private String partA;
    private List<String> partB;

    public IntermediateValue() {
        partB = new ArrayList<String>();
    }

    public String getPartA() {
        return partA;
    }

    public void setPartA(String partA) {
        this.partA = partA;
    }

    public List<String> getPartB() {
        return partB;
    }

    public void setPartB(List<String> partB) {
        this.partB = partB;
    }

}