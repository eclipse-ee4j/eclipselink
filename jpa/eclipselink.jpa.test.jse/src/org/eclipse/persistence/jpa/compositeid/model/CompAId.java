/********************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 ******************************************************************************/

// Contributors:
//     07/09/2018-2.6 Jody Grassel
//        - 536853: MapsID processing sets up to fail validation

package org.eclipse.persistence.jpa.compositeid.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CompAId implements Serializable {
    private CompAIdentifier identifier;
    private Environment environment;

    public CompAIdentifier getIdentifier() {
        return identifier;
    }
    public void setIdentifier(CompAIdentifier identifier) {
        this.identifier = identifier;
    }
    public Environment getEnvironment() {
        return environment;
    }
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((environment == null) ? 0 : environment.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
        CompAId other = (CompAId) obj;
        if (environment == null) {
            if (other.environment != null)
                return false;
        } else if (!environment.equals(other.environment))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        return true;
    }


}
