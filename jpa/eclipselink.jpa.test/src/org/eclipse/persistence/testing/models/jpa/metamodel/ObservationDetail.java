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
//     11/27/2010-2.2  mobrien - JPA 2.0 Metadata API test model
//       - 300626: Nested embeddable test case coverage
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Embeddable;

@Embeddable
public class ObservationDetail {
    // This class is embedded inside a GalacticPosition via an embbedded Observation

    private String data;

    public ObservationDetail() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

