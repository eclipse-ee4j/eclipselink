/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 09 October 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.util.ArrayList;
import java.util.List;

public class RootTwo {

    private List<Sub> refList;
    private Sub ref;

    public RootTwo() {
        refList = new ArrayList<Sub>();
    }

    public List<Sub> getRefList() {
        return refList;
    }

    public void setRefList(List<Sub> refList) {
        this.refList = refList;
    }

    public Sub getRef() {
        return ref;
    }

    public void setRef(Sub ref) {
        this.ref = ref;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RootTwo other = (RootTwo) obj;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (refList == null) {
            if (other.refList != null)
                return false;
        } else if (!refList.equals(other.refList))
            return false;
        return true;
    }

}