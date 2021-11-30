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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_MM_FOOBAR")
public class FooBar {

	@EmbeddedId
	private FooBarId id;

	@ManyToOne
	@JoinColumn(name = "bar_id")
	@MapsId("barId")
	private Bar bar;

	@ManyToOne
	@JoinColumn(name = "foo_id")
	@MapsId("fooId")
	private Foo foo;

	public FooBarId getId() {
		return id;
	}

	public void setId(FooBarId id) {
		this.id = id;
	}

	public Bar getBar() {
		return bar;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public Foo getFoo() {
		return foo;
	}

	public void setFoo(Foo foo) {
		this.foo = foo;
	}

}
