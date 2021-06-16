/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     01/21/2020: Will Dazey
//       - #559346: ClassCastException: DataReadQuery incompatible with ObjectLevelReadQuery
package org.eclipse.persistence.jpa.test.criteria.model;

import java.io.Serializable;
import java.util.Objects;

public class CriteriaCarId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private int version;

    public CriteriaCarId() { }

    public CriteriaCarId(String id, int version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean equals(final Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof CriteriaCarId)) {
        return false;
      }
      final CriteriaCarId carDtoId = (CriteriaCarId) other;
      return this.version == carDtoId.version &&
          this.id.equals(carDtoId.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.id, this.version);
    }
}
