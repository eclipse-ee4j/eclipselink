/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
