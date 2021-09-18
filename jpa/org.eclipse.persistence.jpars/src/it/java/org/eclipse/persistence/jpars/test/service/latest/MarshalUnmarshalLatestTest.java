/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//         Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.service.latest;

import org.eclipse.persistence.jpars.test.service.MarshalUnmarshalTestBase;
import org.eclipse.persistence.jpars.test.service.v2.MarshalUnmarshalV2Test;
import org.junit.BeforeClass;

/**
 * MarshalUnmarshalTestBase adapted for JPARS 2.0.
 * {@link MarshalUnmarshalTestBase}
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
