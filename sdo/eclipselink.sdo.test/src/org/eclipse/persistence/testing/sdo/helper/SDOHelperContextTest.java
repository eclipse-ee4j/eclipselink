/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Iaroslav Savytskyi - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper;

import commonj.sdo.helper.HelperContext;
import junit.framework.TestCase;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

/**
 * TestCase for SDOHelperContext
 */
public class SDOHelperContextTest extends TestCase {

    public void testNewContextGeneratedWithResolverReturned() {
        final SDOHelperContext foo = new SDOHelperContext("Foo");
        SDOHelperContext.setHelperContextResolver(new SDOHelperContext.HelperContextResolver() {
            @Override
            public HelperContext getHelperContext(String id, ClassLoader classLoader) {
                return foo;
            }
        });

        SDOHelperContext hc = (SDOHelperContext) SDOHelperContext.getHelperContext("foo");
        assertEquals("Expected helperContext generated with HelperContextResolver to be returned", hc, foo);
        SDOHelperContext.setHelperContextResolver(null);
        hc = (SDOHelperContext) SDOHelperContext.getHelperContext("boo");
        assertFalse("Expected helperContext to be different", foo.equals(hc));
    }

}
