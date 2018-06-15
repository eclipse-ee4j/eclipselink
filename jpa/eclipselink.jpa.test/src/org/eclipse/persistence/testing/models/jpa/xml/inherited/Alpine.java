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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.AttributeOverride;

// This one should get picked up since there is not attribute override
// specified in the XML for this class.
@AttributeOverride(name="id", column=@Column(name="ALPINE_ID", nullable=false))
public class Alpine extends Beer  {
    public enum Classification { STRONG, BITTER, SWEET }

    private Date bestBeforeDate;
    private Classification classification;

    public Alpine() {}

    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public boolean equals(Object anotherAlpine) {
        if (anotherAlpine.getClass() != Alpine.class) {
            return false;
        }

        return (getId().equals(((Alpine)anotherAlpine).getId()));
    }

    // This is here for testing purposes. It is bogus, the access type has
    // been set to FIELD for this class in XML therefore, this method should
    // not get processed. This processed will cause an error since the
    // Embedded is an int.
    @EmbeddedId
    public int getBogusEmbeddedId() {
        return 0;
    }

    public void setBogusEmbeddedId(int id) {

    }
}
