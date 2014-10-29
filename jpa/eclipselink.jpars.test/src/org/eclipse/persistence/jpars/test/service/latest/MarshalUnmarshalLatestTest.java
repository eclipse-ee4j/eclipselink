/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		Dmitry Kornilov - Initial implementation
 ******************************************************************************/
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
