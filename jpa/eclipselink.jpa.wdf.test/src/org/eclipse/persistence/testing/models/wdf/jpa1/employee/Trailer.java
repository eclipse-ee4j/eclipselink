/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
