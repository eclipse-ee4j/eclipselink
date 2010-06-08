/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

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
