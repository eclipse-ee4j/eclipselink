/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.criteria.model;

import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PluralCriteriaOwner.class)
public class PluralCriteriaOwner_ {
    public static volatile SingularAttribute<PluralCriteriaOwner, Integer> id;
    public static volatile SingularAttribute<PluralCriteriaOwner, String> nullableText;
    public static volatile SetAttribute<PluralCriteriaOwner, String> tags;
    public static volatile ListAttribute<PluralCriteriaOwner, String> labels;
    public static volatile CollectionAttribute<PluralCriteriaOwner, String> values;
    public static volatile MapAttribute<PluralCriteriaOwner, String, String> codes;
    public static volatile SetAttribute<PluralCriteriaOwner, PluralCriteriaElement> elements;
    public static volatile SingularAttribute<PluralCriteriaOwner, PluralCriteriaOwner> parent;
    public static volatile SingularAttribute<PluralCriteriaOwner, PluralCriteriaDetails> details;
}
