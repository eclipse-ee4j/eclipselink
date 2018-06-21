/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {

    public List<DataHandler> data;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Root)) {
            return false;
        }
        if(data.size() != ((Root)obj).data.size()) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        return data != null ? data.size() : 0;
    }
}
