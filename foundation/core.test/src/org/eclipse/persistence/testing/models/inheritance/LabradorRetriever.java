/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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