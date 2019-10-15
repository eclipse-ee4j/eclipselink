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
package org.eclipse.persistence.testing.models.jpa.datatypes.arraypks;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "CMP3_PBYTEARRAYPK_TYPE")
public class PrimByteArrayPKType implements java.io.Serializable{
    public PrimByteArrayPKType() {
    }

    private byte[] id;

    public PrimByteArrayPKType(byte[] primitiveByteArrayData)
    {
        this.id = primitiveByteArrayData;
    }

    @Id
    public byte[] getId()
    {
        return id;
    }

    public void setId(byte[] id)
    {
        this.id= id;
    }

    private static final int UUID_LENGTH = 0x10;
    private static int BITSPERLONG = 0x40;
    private static int BITSPERBYTE = 0x8;

    public void createRandomId() {
        UUID uuid = UUID.randomUUID();
        id = getBytes(uuid);
    }

    public static byte[] getBytes(UUID u) {
        byte [] raw = new byte [UUID_LENGTH];
        long msb = u.getMostSignificantBits();
        long lsb = u.getLeastSignificantBits();

        /*
         * Convert 2 longs to 16 bytes.
         */
        int i = 0;
        for (int sh = BITSPERLONG - BITSPERBYTE; sh >= 0; sh -= BITSPERBYTE) {
            raw [i++] = (byte) (msb >> sh);
        }
        for (int sh = BITSPERLONG - BITSPERBYTE; sh >= 0; sh -= BITSPERBYTE) {
            raw [i++] = (byte) (lsb >> sh);
        }
        return raw;
    }
}
