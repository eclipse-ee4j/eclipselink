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

import java.util.List;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class AdaptedPojo<T> {
    public String strField;
    public Integer intField;
    public Box box;
    public GenericBox<Integer> intBox;
    public GenericBox<String> strBox;
    public GenericBox<T> tBox;
    public List<GenericBox<T>> tGenericBoxList;
    public List<Integer> integerList;
    public List<String> stringList;
    public Map<String, Integer> stringIntegerMap;
    public Map<String, T> tMap;
    public T tVar;
}
