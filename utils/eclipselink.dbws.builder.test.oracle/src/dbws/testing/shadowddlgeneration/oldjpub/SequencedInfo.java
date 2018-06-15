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
        for (int i = 0; i < p.length; i++) {
            if (seqCur > 2 && p[i].sequence() == (seqCur - 1)) {
                reversed = true;
                break;
            }
            else if (seqCur > 2 && p[i].sequence() == (seqCur + 1)) {
                reversed = false;
                break;
            }
            seqCur = p[i].sequence();
        }
        if (reversed) {
            ArrayList<SequencedInfo> v = new ArrayList<SequencedInfo>();
            int vLen = p.length;
            for (int i = 0; i < vLen; i++) {
                v.add(p[i]);
            }
            for (int i = 0; i < vLen; i++) {
                p[i] = (SequencedInfo)v.get(vLen - i - 1);
            }
        }
        return p;
    }

    protected static int intValue(Integer i) {
        if (i == null) {
            return 0;
        }
        return i.intValue();
    }
}
