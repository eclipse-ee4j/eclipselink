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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.Dog;

/**
 * Insert the type's description here.
 * Creation date: (1/10/01 9:21:09 AM)
 * @author: Administrator
 */
public class LabradorRetriever extends Dog {

    /**
     * LabradorRetriever constructor comment.
     */
    public LabradorRetriever() {
        super();
    }

    public static LabradorRetriever example5() {
        LabradorRetriever example = new LabradorRetriever();
        example.setId(5);
        example.setName("Stan");
        return example;
    }

    public static LabradorRetriever example6() {
        LabradorRetriever example = new LabradorRetriever();
        example.setId(6);
        example.setName("Dinah");
        return example;
    }
}
