/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-06-16 10:40:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.simpledocument;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {

    public String id;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Root) {
            Root testRoot = (Root) obj;
            if (this.id == null && testRoot.id == null) {
                return true;
            }
            if (this.id == null && testRoot.id != null) {
                return false;
            }
            if (this.id != null && testRoot.id == null) {
                return false;
            }
            return this.id.equals(testRoot.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Root: id=" + id;
    }

}