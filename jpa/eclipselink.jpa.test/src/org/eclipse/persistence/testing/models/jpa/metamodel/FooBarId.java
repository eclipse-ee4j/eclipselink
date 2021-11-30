/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FooBarId implements Serializable {

	@Column(name = "foo_id")
	private Long fooId;

	@Column(name = "bar_id")
	private Long barId;

	public Long getFooId() {
		return fooId;
	}

	public void setFooId(Long fooId) {
		this.fooId = fooId;
	}

	public Long getBarId() {
		return barId;
	}

	public void setBarId(Long barId) {
		this.barId = barId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FooBarId fooBarId = (FooBarId) o;
		return fooId.equals(fooBarId.fooId) && barId.equals(fooBarId.barId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fooId, barId);
	}
}
