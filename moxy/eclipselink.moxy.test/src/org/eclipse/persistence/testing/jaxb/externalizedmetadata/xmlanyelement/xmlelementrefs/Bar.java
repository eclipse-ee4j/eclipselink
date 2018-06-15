/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="otherns")
public class Bar {
    public String id;

    public boolean equals(Object obj){
        if(!(obj instanceof Bar)){
            return false;
        }
        return id.equals(((Bar)obj).id);
    }
}
