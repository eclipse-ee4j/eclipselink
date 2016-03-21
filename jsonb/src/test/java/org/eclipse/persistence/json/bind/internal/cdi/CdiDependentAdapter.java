/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.adapter.JsonbAdapter;

/**
 * Test scope adapter for testing CDI injection and cleanup.
 *
 * @author Roman Grigoriadi
 */
@ApplicationScoped
public class CdiDependentAdapter implements JsonbAdapter<String, Integer> {

    @Inject
    private CdiTestService cdiTestService;

    @Inject
    @Hello2
    private IHelloService helloService;

    @Override
    public Integer adaptToJson(String obj) throws Exception {
        cdiTestService.runService();
        helloService.sayHello();
        return Integer.valueOf(obj);
    }

    @Override
    public String adaptFromJson(Integer obj) throws Exception {
        return String.valueOf(obj);
    }

    public CdiTestService getCdiTestService() {
        return cdiTestService;
    }

    public IHelloService getHelloService() {
        return helloService;
    }
}
