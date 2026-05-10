/*
 * Copyright (c) 2009, 2025 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Embeddable
public class Phone {
    @Pattern(regexp = "^\\+1 \\([0-9]{3}\\) [0-9]{3}-[0-9]{4}$", groups = { USPhone.class })
    @Pattern(regexp = "^\\+49 \\([1-9]\\d{1,4}\\) \\d{3,8}-\\d{4}$", groups = { GermanPhone.class })
    String phone;

    public Phone() {}

    public Phone(String phone) {
         this.phone = phone;
    }
}
