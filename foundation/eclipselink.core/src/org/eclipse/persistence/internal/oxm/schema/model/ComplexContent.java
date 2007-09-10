/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

public class ComplexContent extends Content {
    private boolean mixed;

    // private Extension extension;
    //private Restriction restriction;
    public ComplexContent() {
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public boolean isMixed() {
        return mixed;
    }

    /*
        public void setExtension(Extension extension) {
            this.extension = extension;
        }

        public Extension getExtension() {
            return extension;
        }

        public void setRestriction(Restriction restriction) {
            this.restriction = restriction;
        }

        public Restriction getRestriction() {
            return restriction;
        }*/
}