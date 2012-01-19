/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollectionHolderInitialized extends CollectionHolder{

    public static final List TEST_LIST = new ArrayList();
    public static final Map TEST_MAP = new HashMap();

    public CollectionHolderInitialized(){
        collection1 = TEST_LIST;
        collection2 = TEST_LIST;
        collection3 = TEST_LIST;
        collection4 = TEST_LIST;
        collection5 = TEST_LIST;
        collection6 = TEST_LIST;
        collection7 = TEST_LIST;
        collection8 = TEST_LIST;
        collection9 = TEST_LIST;
        collection10 = TEST_MAP;
        collection11 = TEST_LIST;
    }

}