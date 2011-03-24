/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="PaymentInf")
public class PaymentInf {
    
    private String id;
    private Creditor creditor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlPath(".")
    public Creditor getCreditor() {
        return creditor;
    }

    public void setCreditor(Creditor creditor) {
        this.creditor = creditor;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != PaymentInf.class) {
            return false;
        }
        PaymentInf test = (PaymentInf) obj;
        if(!equals(id, test.getId())) {
            return false;
        }
        if(!equals(creditor, test.getCreditor())) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
