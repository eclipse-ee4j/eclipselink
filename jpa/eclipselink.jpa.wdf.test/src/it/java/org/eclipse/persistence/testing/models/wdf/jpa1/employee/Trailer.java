/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_TRAILER")
public class Trailer {

    public Trailer() {

    };

    public Trailer(int low, int high, int load) {
        pk = new PK(low, high);
        this.load = load;
    }

    @EmbeddedId
    PK pk;

    @Column(name = "LOAD1")
    int load;

    @Embeddable
    public static class PK implements Serializable {
        private static final long serialVersionUID = -5564031963078804716L;

        public PK() {

        }

        public PK(int low, int high) {
            this.low = low;
            this.high = high;
        }

        int low;
        int high;

        public int getHigh() {
            return high;
        }

        public void setHigh(int higher) {
            this.high = higher;
        }

        public int getLow() {
            return low;
        }

        public void setLow(int lower) {
            this.low = lower;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PK)) {
                return false;
            }
            PK other = (PK) obj;

            return low == other.low && high == other.high;
        }

        @Override
        public int hashCode() {
            return low + 37 * high;
        }
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

}
