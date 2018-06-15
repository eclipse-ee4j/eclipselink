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

    @Override
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
