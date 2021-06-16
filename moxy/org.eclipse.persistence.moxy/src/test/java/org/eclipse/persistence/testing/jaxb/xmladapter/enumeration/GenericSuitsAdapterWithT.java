/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.internal.jaxb.GenericsClassHelper;

/**
 * <p>
 * The correct values of BoundType and ValueType of Generic XmlAdapter are resolved by GenericsClassHelper.
 *
 * <p>
 * ReflectionHelper does not help when you define XmlAdapter as XmlAdapter&lt;Object, Object> or XmlAdapter&lt;Object, T>
 *
 * @see GenericsClassHelper
 *
 */
public abstract class GenericSuitsAdapterWithT<T extends Enum> extends XmlAdapter<String, T> {
  @Override
  public T unmarshal(String v) throws Exception {
      return convert(v);
  }

  public abstract T convert(String value);

  @Override
  public String marshal(T v) throws Exception {
      return v.name();
  }
}
