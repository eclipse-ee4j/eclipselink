/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 * A Hollywood Agent makes movie deals for many actors, while an actor has only
 * a single agent at one time.
 * A ReadOnlyHollywoodAgent is used to represent a HollywoodAgent object.
 * Actors can update their HollywoodAgent without actually having direct
 * references to HollywoodAgents in their object model.  This allows their
 * object model to be divided into independent sectors.
 * This is for bug 3013948.
 */
public class ReadOnlyHollywoodAgent {
    public Number id;
    public String name;
    public int numberOfConnections;

    public ReadOnlyHollywoodAgent() {
        super();
    }

    // ReadOnlyHollywoodAgent descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.ReadOnlyHollywoodAgent.class);
        descriptor.setTableName("HOLLYWOODAGENT");
        descriptor.addPrimaryKeyFieldName("HOLLYWOODAGENT_ID");
        descriptor.setSequenceNumberName("HOLLYWOODAGENT_SEQ");
        descriptor.setSequenceNumberFieldName("HOLLYWOODAGENT_ID");

        descriptor.addDirectMapping("id", "HOLLYWOODAGENT_ID");
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
        ReadOnlyHollywoodAgent other = (ReadOnlyHollywoodAgent)obj;
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
