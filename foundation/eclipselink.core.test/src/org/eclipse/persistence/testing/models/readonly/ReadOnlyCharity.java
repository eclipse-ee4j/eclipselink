/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.readonly;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * A Hollywood Charity champions good causes, and actors can be spokespersons
 * for multiple charities.
 * A ReadOnlyCharity is used to represent a Charity object.  Actors can update
 * which charities they support without actually having direct references to 
 * Charities in their object model.  This allows their object model to be 
 * divided into independent sectors.
 */
public class ReadOnlyCharity {
    public Number id;
    public String name;

    public ReadOnlyCharity() {
        super();
    }

    // ReadOnlyCharity descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.ReadOnlyCharity.class);
        descriptor.setTableName("CHARITY");
        descriptor.addPrimaryKeyFieldName("CHARITY_ID");
        descriptor.setSequenceNumberName("CHARITY_SEQ");
        descriptor.setSequenceNumberFieldName("CHARITY_ID");

        descriptor.addDirectMapping("id", "CHARITY_ID");
        descriptor.addDirectMapping("name", "NAME");

        descriptor.setShouldBeReadOnly(true);

        return descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        ReadOnlyCharity other = (ReadOnlyCharity)obj;
        return (getName().equals(other.getName()));
    }

    public String getName() {
        return name;
    }

    public void setName(String newValue) {
        this.name = newValue;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getName() + ") ";
    }
}
