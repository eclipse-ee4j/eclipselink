/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.util.ArrayList;

public abstract class SequencedInfo {

    public int sequence;

    public int sequence() {
        return sequence;
    }

    public static SequencedInfo[] reorder(SequencedInfo[] p) {
        boolean reversed = false;
        int seqCur = -1;
        for (SequencedInfo info : p) {
            if (seqCur > 2 && info.sequence() == (seqCur - 1)) {
                reversed = true;
                break;
            } else if (seqCur > 2 && info.sequence() == (seqCur + 1)) {
                reversed = false;
                break;
            }
            seqCur = info.sequence();
        }
        if (reversed) {
            ArrayList<SequencedInfo> v = new ArrayList<>();
            int vLen = p.length;
            for (SequencedInfo sequencedInfo : p) {
                v.add(sequencedInfo);
            }
            for (int i = 0; i < vLen; i++) {
                p[i] = v.get(vLen - i - 1);
            }
        }
        return p;
    }

    protected static int intValue(Integer i) {
        if (i == null) {
            return 0;
        }
        return i;
    }
}
