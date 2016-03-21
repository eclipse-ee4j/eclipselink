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
 * @author Roman Grigoriadi
 */
@Hello1
@ApplicationScoped
public class HelloService1 implements IHelloService {

    @Inject
    private Event<MethodCalledEvent> calledEvent;

    @Override
    public void sayHello() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".sayHello"));
    }

    @PreDestroy
    public void preDestroy() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".preDestroy"));
    }
}
