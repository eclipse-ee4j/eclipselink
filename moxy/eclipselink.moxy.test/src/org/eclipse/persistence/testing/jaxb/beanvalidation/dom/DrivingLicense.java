/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.dom;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Date;

/**
* @author Marcel Valovy - marcel.valovy@oracle.com
* @since 2.6
*/
public class DrivingLicense {

    @Digits(integer = 6, fraction = 0, groups = Drivers.class)
    @XmlAttribute
    int id;

    @Future(groups = Drivers.class)
    @XmlAttribute
    Date validThrough;

    public DrivingLicense(){
    }

    public DrivingLicense(int id, Date validThrough){
        this.id = id;
        this.validThrough = validThrough;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DrivingLicense that = (DrivingLicense) o;

        if (id != that.id) {
            return false;
        }
        if (validThrough != null && that.validThrough != null && !(validThrough.getTime() == that.validThrough.getTime())) {
            return false;
        } else if ((validThrough == null && that.validThrough != null) || (validThrough != null && that.validThrough == null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (validThrough != null ? validThrough.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrivingLicense{" +
                "id=" + id +
                ", validThrough=" + validThrough +
                '}';
    }
}
