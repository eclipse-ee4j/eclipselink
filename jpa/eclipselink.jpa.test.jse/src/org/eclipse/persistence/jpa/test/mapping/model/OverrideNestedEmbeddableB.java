/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/30/2020 - Will Dazey
 *       - 564260: ElementCollection lowercase AttributeOverride is ignored
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OverrideNestedEmbeddableB {

    @Column(name = "nested_value")
    private Integer nestedValue;
    
    @Column(name = "nested_value2")
    private Integer nestedValue2;

    public OverrideNestedEmbeddableB() { }

    public OverrideNestedEmbeddableB(Integer nestedValue, Integer nestedValue2) {
        this.nestedValue = nestedValue;
        this.nestedValue2 = nestedValue2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestedValue == null) ? 0 : nestedValue.hashCode());
        result = prime * result + ((nestedValue2 == null) ? 0 : nestedValue2.hashCode());
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
        OverrideNestedEmbeddableB other = (OverrideNestedEmbeddableB) obj;
        if (nestedValue == null) {
            if (other.nestedValue != null)
                return false;
        } else if (!nestedValue.equals(other.nestedValue))
            return false;
        if (nestedValue2 == null) {
            if (other.nestedValue2 != null)
                return false;
        } else if (!nestedValue2.equals(other.nestedValue2))
            return false;
        return true;
    }
}
