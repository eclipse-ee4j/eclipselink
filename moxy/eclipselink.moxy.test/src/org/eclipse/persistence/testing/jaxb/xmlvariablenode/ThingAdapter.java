/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ThingAdapter extends XmlAdapter<OtherThing, Thing>{

    @Override
    public OtherThing marshal(Thing arg0) throws Exception {
        OtherThing otherThing = new OtherThing();
        otherThing.otherThingName = arg0.thingName;
        otherThing.otherThingValue = arg0.thingValue;
        otherThing.otherThingInt = arg0.thingInt;
        return otherThing;
    }

    @Override
    public Thing unmarshal(OtherThing arg0) throws Exception {
        Thing thing = new Thing();
        thing.thingName = arg0.otherThingName;
        thing.thingValue = arg0.otherThingValue;
        thing.thingInt = arg0.otherThingInt;
        return thing;
    }

}
