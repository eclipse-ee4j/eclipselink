/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

import org.w3c.dom.Element;

import java.util.List;

public class Annotation {
    private java.util.List<String> documentation;
    private java.util.List<org.w3c.dom.Element> appInfo;

    public Annotation() {
    }

    public void setDocumentation(List<String> documentation) {
        this.documentation = documentation;
    }

    public List<String> getDocumentation() {
        return documentation;
    }

    public void setAppInfo(List<org.w3c.dom.Element> appInfo) {
        this.appInfo = appInfo;
    }

    public List<Element> getAppInfo() {
        return appInfo;
    }
}
