/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 23 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.cycle;

public class Email {

    public String user;
    public String domain;
    public ContactInfo parentInfo;
    public Email forward;

    public boolean equals(Object obj) {
        return false;
    }

}
