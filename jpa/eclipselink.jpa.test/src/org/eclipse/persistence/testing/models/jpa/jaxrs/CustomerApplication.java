/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.4 - inital implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.util.*;

import javax.ws.rs.core.Application;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

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
