/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class Tesla implements Serializable {
    private static final long serialVersionUID = -4527454018244022883L;

    protected int integer;

    @Lob
    protected byte[] sound;

    public byte[] getSound() {
        return sound;
    }

    public void setSound(byte[] sound) {
        this.sound = sound;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }
}
