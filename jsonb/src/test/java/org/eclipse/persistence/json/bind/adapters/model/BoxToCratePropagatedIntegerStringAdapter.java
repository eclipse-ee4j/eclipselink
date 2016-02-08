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

package org.eclipse.persistence.json.bind.adapters.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class BoxToCratePropagatedIntegerStringAdapter extends BoxToCratePropagatedTypeArgsAdapter<Integer, String> {
    @Override
    public GenericCrate<GenericBox<List<String>>> adaptFrom(GenericBox<Integer> integerGenericBox) {
        if (integerGenericBox == null) {
            return null;
        }
        GenericCrate<GenericBox<List<String>>> crate = new GenericCrate<>();
        crate.setCrateStrField(integerGenericBox.getStrField());
        crate.setT(new GenericBox<>());
        crate.getT().setX(new ArrayList<>());
        crate.getT().getX().add(integerGenericBox.getX().toString());
        return crate;
    }

    @Override
    public GenericBox<Integer> adaptTo(GenericCrate<GenericBox<List<String>>> boxGenericCrate) {
        if (boxGenericCrate == null) {
            return null;
        }
        GenericBox<Integer> genericBox = new GenericBox<>();
        genericBox.setStrField(boxGenericCrate.getCrateStrField());
        genericBox.setX(Integer.parseInt(boxGenericCrate.getT().getX().get(0)));
        return genericBox;
    }
}
