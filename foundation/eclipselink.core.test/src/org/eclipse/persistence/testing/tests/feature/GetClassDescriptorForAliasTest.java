/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;

import org.eclipse.persistence.descriptors.*;

/**
 * Bug 5166035
 * Ensure the getClassDescriptorForAlias(String) method works on both Session and Project
 * @author tware
 *
 */
public class GetClassDescriptorForAliasTest extends TestCase {

    private ClassDescriptor empDescriptorSession = null;
    private ClassDescriptor empDescriptorProject = null;

    public void test(){
        empDescriptorSession = getSession().getClassDescriptorForAlias("SecureSystem");
        empDescriptorProject = getSession().getProject().getDescriptorForAlias("SecureSystem");
    }

    public void verify(){
        if (empDescriptorSession == null){
            throw new TestErrorException("getClassDescriptorForAlias() on Session failed.");
        }
        if (empDescriptorProject == null){
            throw new TestErrorException("getClassDescriptorForAlias() on Project failed.");
        }
    }
}
