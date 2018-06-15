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
//     Praba Vijayaratnam - 2.4 - inital implementation
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@javax.ws.rs.ApplicationPath("rest/*")
public class CustomerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>(2);
        set.add(CustomerService.class);
        set.add(AddressService.class);
        set.add(PhoneNumberService.class);
        set.add(AddressContextResolver.class);
        set.add(PhoneNumberReader.class);
        set.add(PhoneNumberWriter.class);
        set.add(MOXyJsonProvider.class);
        return set;
    }
}
