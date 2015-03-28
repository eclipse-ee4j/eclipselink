/*******************************************************************************
 * Copyright (c) 1998, 2015  -2014 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.conversion;

import java.util.BitSet;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Convert to/from XML base64Binary.</p>
 */

public class Base64 {
    private static final byte[] Base64EncMap;
    private static final byte[] Base64DecMap;

    private static final byte PADDING = 127;

    // Class Initializer
    static {
        // rfc-2045: Base64 Alphabet
        Base64EncMap = initEncodeMap();
        Base64DecMap = initDecodeMap();
    }

    private static byte[] initEncodeMap() {
        byte[] map = new byte[64];
        int i;
        for (i = 0; i < 26; i++) {
            map[i] = (byte) ('A' + i);
        }
        for (i = 26; i < 52; i++) {
            map[i] = (byte) ('a' + (i - 26));
        }
        for (i = 52; i < 62; i++) {
            map[i] = (byte) ('0' + (i - 52));
        }
        map[62] = '+';
        map[63] = '/';

        return map;
    }

    private static byte[] initDecodeMap() {
        byte[] map = new byte[128];
        int i;
        for (i = 0; i < 128; i++) {
            map[i] = -1;
        }

        for (i = 'A'; i <= 'Z'; i++) {
            map[i] = (byte) (i - 'A');
        }
        for (i = 'a'; i <= 'z'; i++) {
            map[i] = (byte) (i - 'a' + 26);
        }
        for (i = '0'; i <= '9'; i++) {
            map[i] = (byte) (i - '0' + 52);
        }
        map['+'] = 62;
        map['/'] = 63;
        map['='] = PADDING;

        return map;
    }

    /**
     * Base64 constructor comment.
     */
    private Base64() {
    }

    /**
     * computes the length of binary data speculatively.
     *
     * <p>
     * Our requirement is to create byte[] of the exact length to store the binary data.
     * If we do this in a straight-forward way, it takes two passes over the data.
     * Experiments show that this is a non-trivial overhead (35% or so is spent on
     * the first pass in calculating the length.)
     *
     * <p>
     * So the approach here is that we compute the length speculatively, without looking
     * at the whole contents. The obtained speculative value is never less than the
     * actual length of the binary data, but it may be bigger. So if the speculation
     * goes wrong, we'll pay the cost of reallocation and buffer copying.
     *
     * <p>
     * If the base64 text is tightly packed with no indentation nor illegal char
     * (like what most web services produce), then the speculation of this method
     * will be correct, so we get the performance benefit.
     */
    private static int guessLength(byte[] data) {
        final int len = data.length;

        // compute the tail '=' chars
        int j = len - 1;
        for (; j >= 0; j--) {
            byte code = Base64DecMap[data[j]];
            if (code == PADDING)
                continue;
            if (code == -1) // most likely this base64 data is indented. go with the upper bound
                return data.length / 4 * 3;
            break;
        }

        j++;    // data.charAt(j) is now at some base64 char, so +1 to make it the size
        int padSize = len - j;
        if (padSize > 2) // something is wrong with base64. be safe and go with the upper bound
            return data.length / 4 * 3;

        // so far this base64 looks like it's unindented tightly packed base64.
        // take a chance and create an array with the expected size
        return data.length / 4 * 3 - padSize;
    }

    /**
     * base64Binary data is likely to be long, and decoding requires
     * each character to be accessed twice (once for counting length, another
     * for decoding.)
     *
     * This method decodes the given byte[] using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  data the base64-encoded data.
     * @return the decoded <var>data</var>.
     */
    public static byte[] base64Decode(byte[] data) {
        final int buflen = guessLength(data);
        final byte[] out = new byte[buflen];
        int o = 0;

        final int len = data.length;
        int i;

        final byte[] quadruplet = new byte[4];
        int q = 0;

        // convert each quadruplet to three bytes.
        for (i = 0; i < len; i++) {
            byte ch = data[i];
            byte v = Base64DecMap[ch];

            if (v != -1)
                quadruplet[q++] = v;

            if (q == 4) {
                // quadruplet is now filled.
                out[o++] = (byte) ((quadruplet[0] << 2) | (quadruplet[1] >> 4));
                if (quadruplet[2] != PADDING)
                    out[o++] = (byte) ((quadruplet[1] << 4) | (quadruplet[2] >> 2));
                if (quadruplet[3] != PADDING)
                    out[o++] = (byte) ((quadruplet[2] << 6) | (quadruplet[3]));
                q = 0;
            }
        }

        if (buflen == o) // speculation worked out to be OK
            return out;

        // we overestimated, so need to create a new buffer
        byte[] nb = new byte[o];
        System.arraycopy(out, 0, nb, 0, o);
        return nb;
    }

    /**
     * This method encodes the given byte[] using the base64-encoding
     * specified in RFC-2045 (Section 6.8).
     *
     * @param  data the data
     * @return the base64-encoded <var>data</var>
     */
    public static byte[] base64Encode(byte[] data) {
        if (data == null) {
            return null;
        }

        int sidx;
        int didx;
        byte[] dest = new byte[((data.length + 2) / 3) * 4];

        // 3-byte to 4-byte conversion + 0-63 to ascii printable conversion
        for (sidx = 0, didx = 0; sidx < (data.length - 2); sidx += 3) {
            dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
            dest[didx++] = Base64EncMap[((data[sidx + 1] >>> 4) & 017) | ((data[sidx] << 4) & 077)];
            dest[didx++] = Base64EncMap[((data[sidx + 2] >>> 6) & 003) | ((data[sidx + 1] << 2) & 077)];
            dest[didx++] = Base64EncMap[data[sidx + 2] & 077];
        }
        if (sidx < data.length) {
            dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
            if (sidx < (data.length - 1)) {
                dest[didx++] = Base64EncMap[((data[sidx + 1] >>> 4) & 017) | ((data[sidx] << 4) & 077)];
                dest[didx++] = Base64EncMap[(data[sidx + 1] << 2) & 077];
            } else {
                dest[didx++] = Base64EncMap[(data[sidx] << 4) & 077];
            }
        }

        // add padding
        for (; didx < dest.length; didx++) {
            dest[didx] = (byte)'=';
        }

        return dest;
    }
}
