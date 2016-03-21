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

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * CDI test service
 * @author Roman Grigoriadi
 */
@ApplicationScoped
public class CdiTestService {

    @Inject
    private Event<MethodCalledEvent> calledEvent;

    @Inject
    @Hello1
    private IHelloService helloService;

    public CdiTestService() {
    }

    public void runService() {
        helloService.sayHello();
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".runService"));
    }

    @PreDestroy
    public void preDestroy() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".preDestroy"));
    }

    public IHelloService getHelloService() {
        return helloService;
    }
}
