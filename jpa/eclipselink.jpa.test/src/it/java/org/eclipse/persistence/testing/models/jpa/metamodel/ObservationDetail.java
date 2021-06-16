/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     11/27/2010-2.2  mobrien - JPA 2.0 Metadata API test model
//       - 300626: Nested embeddable test case coverage
package org.eclipse.persistence.testing.models.jpa.metamodel;

import jakarta.persistence.Embeddable;

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

