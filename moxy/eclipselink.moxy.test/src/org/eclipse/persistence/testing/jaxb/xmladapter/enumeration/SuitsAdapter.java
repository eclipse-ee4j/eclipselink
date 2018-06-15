/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

public class SuitsAdapter extends GenericSuitsAdapter<CardSuit> {
    @Override
    public CardSuit convert(String value) {
        return CardSuit.valueOf(value);
    }
}
