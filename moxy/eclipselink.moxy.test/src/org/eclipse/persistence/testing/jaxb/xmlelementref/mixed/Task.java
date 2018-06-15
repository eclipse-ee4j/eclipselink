/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// mmacivor - May 31, 2010 - 2.1 - Initial implementation

package org.eclipse.persistence.testing.jaxb.xmlelementref.mixed;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="task")
public class Task {

    @XmlValue
    public String theTask;

    public boolean equals(Object obj) {
        if(!(obj instanceof Task)) {
            return false;
        }
        return theTask.equals(((Task)obj).theTask);
    }
}
