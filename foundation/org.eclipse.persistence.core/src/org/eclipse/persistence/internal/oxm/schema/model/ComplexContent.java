/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
