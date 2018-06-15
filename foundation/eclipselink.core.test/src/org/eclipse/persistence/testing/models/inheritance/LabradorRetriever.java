/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
