/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
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

}