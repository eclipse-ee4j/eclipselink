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
//         Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.service.latest;

import org.eclipse.persistence.jpars.test.service.v2.MarshalUnmarshalV2Test;
import org.junit.BeforeClass;

/**
 * MarshalUnmarshalTest adapted for JPARS 2.0.
 * {@see MarshalUnmarshalTest}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class MarshalUnmarshalLatestTest extends MarshalUnmarshalV2Test {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", "latest");
        em = context.getEmf().createEntityManager();
        initData();
    }
}
