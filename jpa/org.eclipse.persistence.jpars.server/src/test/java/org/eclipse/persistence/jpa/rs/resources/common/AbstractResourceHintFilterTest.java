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

// Contributors:
//     EclipseLink contributors - initial API and implementation
package org.eclipse.persistence.jpa.rs.resources.common;

import org.eclipse.persistence.config.QueryHints;
import org.junit.Assert;
import org.junit.Test;

public class AbstractResourceHintFilterTest {

    @Test
    public void classLoadingHintsAreRejected() {
        Assert.assertTrue(AbstractResource.loadsClassFromHint(QueryHints.QUERY_REDIRECTOR));
        Assert.assertTrue(AbstractResource.loadsClassFromHint(QueryHints.QUERY_TYPE));
        Assert.assertTrue(AbstractResource.loadsClassFromHint(QueryHints.RESULT_COLLECTION_TYPE));
    }

    @Test
    public void pagingHintsAreKept() {
        Assert.assertFalse(AbstractResource.loadsClassFromHint("offset"));
        Assert.assertFalse(AbstractResource.loadsClassFromHint("limit"));
        Assert.assertFalse(AbstractResource.loadsClassFromHint("eclipselink.jdbc.fetch-size"));
    }
}
