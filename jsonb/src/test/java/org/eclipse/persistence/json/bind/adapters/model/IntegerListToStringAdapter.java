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

import javax.json.bind.adapter.JsonbAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class IntegerListToStringAdapter implements JsonbAdapter<List<Integer>, String> {

    @Override
    public String adaptFrom(List<Integer> integers) {
        StringBuilder sb = new StringBuilder();
        for (Integer integer : integers) {
            if (!sb.toString().isEmpty()) {
                sb.append("#");
            }
            sb.append(integer);
        }
        return sb.toString();
    }

    @Override
    public List<Integer> adaptTo(String s) {
        String[] items = s.split("#");
        List<Integer> integerList = new ArrayList<>();
        for (String item : items) {
            integerList.add(Integer.parseInt(item));
        }
        return integerList;
    }

}
