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
//     04/24/2012 ailitchev - Bug 362318 - Custom ValueHolder POJO cannot be Serialized
package org.eclipse.persistence.testing.tests.feature;

import java.io.IOException;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy;

import org.eclipse.persistence.testing.framework.TestCase;

public class ContainerIndirectionPolicySerializationTest extends TestCase {

    public ContainerIndirectionPolicySerializationTest() {
        super();
        setDescription("Verifies that ContainerIndirectionPolicy could be serialized");
    }

    public void test() throws IOException, ClassNotFoundException {
        ContainerIndirectionPolicy policy = new ContainerIndirectionPolicy();
        policy.setContainerClass(IndirectList.class);
        policy.initialize();
        byte[] bytes = SerializationHelper.serialize(policy);
        ContainerIndirectionPolicy deserializedPolicy = (ContainerIndirectionPolicy)SerializationHelper.deserialize(bytes);
        deserializedPolicy.initialize();
        Object container = deserializedPolicy.valueFromRow(Boolean.TRUE);
        assertTrue(container.getClass().equals(IndirectList.class));
    }
}
