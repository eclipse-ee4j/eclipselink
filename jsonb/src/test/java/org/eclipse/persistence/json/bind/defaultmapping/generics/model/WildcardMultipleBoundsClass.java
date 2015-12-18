/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class WildcardMultipleBoundsClass<T extends Number & Serializable & Comparable<? extends T>> {

    public T wildcardField;

    public GenericTestClass<String, T> genericTestClassPropagatedWildCard;

    public List<? extends T> propagatedWildcardList;
}
