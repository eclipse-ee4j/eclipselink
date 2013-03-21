/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.schema.model;

public class Content implements Restrictable {
    private Extension extension;
    private Restriction restriction;
    private ComplexType owner;

    public Content() {
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
        if (extension != null) {
            extension.setOwner(this);
        }
    }

    public Extension getExtension() {
        return extension;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
        if (restriction != null) {
            restriction.setOwner(this);
        }
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public String getOwnerName() {
        if (owner != null) {
            return owner.getNameOrOwnerName();
        }
        return null;
    }

    public void setOwner(ComplexType owner) {
        this.owner = owner;
    }

    public ComplexType getOwner() {
        return owner;
    }
}
