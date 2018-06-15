/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains only inherited method constraint.
 *
 * @author Marcel Valovy
 */
@XmlRootElement
public class InheritanceAnnotatedEmployee extends MethodAnnotatedEmployee {

    public InheritanceAnnotatedEmployee(){
    }

}
