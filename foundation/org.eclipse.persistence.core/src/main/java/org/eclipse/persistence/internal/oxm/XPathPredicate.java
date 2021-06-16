/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.internal.oxm;

public class XPathPredicate {

    private XPathFragment xPathFragment;
    private String value;

    public XPathPredicate(XPathFragment xPathFragment, String value) {
        this.xPathFragment = xPathFragment;
        this.value = value;
    }

    public XPathFragment getXPathFragment() {
        return xPathFragment;
    }

    public void setXPathFragment(XPathFragment xmlFragment) {
        this.xPathFragment = xmlFragment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != XPathPredicate.class) {
            return false;
        }
        XPathPredicate test = (XPathPredicate) obj;
        if(!xPathFragment.equals(test.getXPathFragment())) {
            return false;
        }
        return value.equals(test.getValue());
    }

    @Override
    public int hashCode() {
        int result = xPathFragment != null ? xPathFragment.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
