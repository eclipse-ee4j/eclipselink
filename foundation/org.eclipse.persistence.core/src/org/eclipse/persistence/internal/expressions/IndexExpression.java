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
//     06/10/2009 Andrei Ilitchev
//       - JPA 2.0 - OrderedList support.
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.expressions.Expression;

/**
 * Index expression may be created by QueryKeyExpression.index() method
 * in case the QueryKeyExpression corresponds to a CollectionMapping with
 * non-null listOrderField.
 */
public class IndexExpression extends FieldExpression {

    /**
     * Only base QueryKeyExpression.index method can create IndexExpression - that's why no public constructor provided.
     */
    IndexExpression(QueryKeyExpression baseExpression) {
        super(null, baseExpression);
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        // IndexExpression always has base QueryKeyExpression.
        // Base expression should be normalized first: it sets the field,
        // and may changes base expression
        // from the original base QueryKeyExpression to TableExpression.
        // That's why the base expression may be normalized again in super.normalize.
        getBaseExpression().normalize(normalizer);
        return super.normalize(normalizer);
    }
}
