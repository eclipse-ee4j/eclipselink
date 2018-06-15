/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
