/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.cycle.inverse;

import java.util.TreeSet;
import javax.xml.bind.annotation.*;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class Foo {

    @XmlInverseReference(mappedBy="foo")
    @XmlElement
    public TreeSet<Bar> bar = new TreeSet<Bar>(new BarComparator());

}
