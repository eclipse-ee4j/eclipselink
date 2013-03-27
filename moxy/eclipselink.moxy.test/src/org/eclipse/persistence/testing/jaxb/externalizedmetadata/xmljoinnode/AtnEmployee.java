/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 11 October 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.util.ArrayList;
import java.util.List;

public class AtnEmployee {

    public int id;
    public String name;

    public List<AtnEmployee> reports;
    public AtnEmployee manager;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AtnEmployee other = (AtnEmployee) obj;
        if (id != other.id)
            return false;
        if (manager == null) {
            if (other.manager != null)
                return false;
        } else if (!manager.equals(other.manager))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reports == null) {
            if (other.reports != null)
                return false;
        } else if (!reports.equals(other.reports))
            return false;
        return true;
    }

}