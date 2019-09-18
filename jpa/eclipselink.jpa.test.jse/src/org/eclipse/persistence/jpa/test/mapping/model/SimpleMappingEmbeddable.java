/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/12/2019 - Will Dazey
 *       - 471144: Add support for AggregateObjectMappings to eclipselink.cursor impl
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Embeddable;

@Embeddable
public class SimpleMappingEmbeddable {
    private String embeddedField1;
    private String embeddedField2;

    public SimpleMappingEmbeddable() { }

    public SimpleMappingEmbeddable(String embeddedField1, String embeddedField2) {
        this.embeddedField1 = embeddedField1;
        this.embeddedField2 = embeddedField2;
    }

    public String getEmbeddedField1() {
        return embeddedField1;
    }
    public void setEmbeddedField1(String embeddedField1) {
        this.embeddedField1 = embeddedField1;
    }
    public String getEmbeddedField2() {
        return embeddedField2;
    }
    public void setEmbeddedField2(String embeddedField2) {
        this.embeddedField2 = embeddedField2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((embeddedField1 == null) ? 0 : embeddedField1.hashCode());
        result = prime * result + ((embeddedField2 == null) ? 0 : embeddedField2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleMappingEmbeddable other = (SimpleMappingEmbeddable) obj;
        if (embeddedField1 == null) {
            if (other.embeddedField1 != null)
                return false;
        } else if (!embeddedField1.equals(other.embeddedField1))
            return false;
        if (embeddedField2 == null) {
            if (other.embeddedField2 != null)
                return false;
        } else if (!embeddedField2.equals(other.embeddedField2))
            return false;
        return true;
    }
}