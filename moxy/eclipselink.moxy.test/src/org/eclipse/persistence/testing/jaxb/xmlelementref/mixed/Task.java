/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - May 31, 2010 - 2.1 - Initial implementation
 ******************************************************************************/

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
