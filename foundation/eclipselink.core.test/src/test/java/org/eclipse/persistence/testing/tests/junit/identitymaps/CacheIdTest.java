/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.junit.identitymaps;

import static org.junit.Assert.*;

import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.junit.Test;

public class CacheIdTest {

	@Test
	public void compareToForByteArrays() {
		equal("many primaryKeys - equal",
			newCacheId(new byte[] {1, 2}, new byte[] {2, 3}),
			newCacheId(new byte[] {1, 2}, new byte[] {2, 3}));
		equal("one primaryKey - equal",
			newCacheId(new byte[] {1, 2}),
			newCacheId(new byte[] {1, 2}));

		smaller("one primaryKey - smaller on first element",
			newCacheId(new byte[] {1, 2}), newCacheId(new byte[] {2, 3}));
		greater("one primaryKey - greater on first element",
			newCacheId(new byte[] {2, 3}), newCacheId(new byte[] {1, 3}));
		smaller("one primaryKey - smaller on last element",
			newCacheId(new byte[] {2, 1, 2}), newCacheId(new byte[] {2, 1, 3}));
		greater("one primaryKey - greater  on last element",
			newCacheId(new byte[] {0, 0, 3}), newCacheId(new byte[] {0, 0, 2}));

		smaller("many primaryKey - smaller",
			newCacheId(new byte[] {1, 2}, new byte[] {2, 2}), newCacheId(new byte[] {1, 2}, new byte[] {2, 3}));
		greater("many primaryKey - greater",
			newCacheId(new byte[] {2, 3}, new byte[] {1, 4}), newCacheId(new byte[] {2, 3}, new byte[] {1, 3}));

		greater("one primaryKey - smaller by different size",
			newCacheId(new byte[] {1, 2}), newCacheId(new byte[] {1}));
		smaller("one primaryKey - greater by different size",
			newCacheId(new byte[] {2}), newCacheId(new byte[] {2, 1}));
	}

	@Test
	public void compareToForCharArrays() {
		equal("many primaryKeys - equal",
			newCacheId(new char[] {'a', 'b'}, new char[] {'b', 'c'}),
			newCacheId(new char[] {'a', 'b'}, new char[] {'b', 'c'}));
		equal("one primaryKey - equal",
			newCacheId(new char[] {'a', 'b'}),
			newCacheId(new char[] {'a', 'b'}));

		smaller("one primaryKey - smaller on first element",
			newCacheId(new char[] {'a', 'b'}), newCacheId(new char[] {'b', 'c'}));
		greater("one primaryKey - greater on first element",
			newCacheId(new char[] {'b', 'c'}), newCacheId(new char[] {'a', 'c'}));
		smaller("one primaryKey - smaller on last element",
			newCacheId(new char[] {'b', 'a', 'b'}), newCacheId(new char[] {'b', 'a', 'c'}));
		greater("one primaryKey - greater  on last element",
			newCacheId(new char[] {'0', '0', 'c'}), newCacheId(new char[] {'0', '0', 'b'}));

		smaller("many primaryKey - smaller",
			newCacheId(new char[] {'a', 'b'}, new char[] {'b', 'b'}), newCacheId(new char[] {'a', 'b'}, new char[] {'b', 'c'}));
		greater("many primaryKey - greater",
			newCacheId(new char[] {'b', 'c'}, new char[] {'a', 'd'}), newCacheId(new char[] {'b', 'c'}, new char[] {'a', 'c'}));

		greater("one primaryKey - smaller by different size",
			newCacheId(new char[] {'a', 'b'}), newCacheId(new char[] {'a'}));
		smaller("one primaryKey - greater by different size",
			newCacheId(new char[] {'b'}), newCacheId(new char[] {'b', 'a'}));
	}

	@Test
	public void compareToForStringArrays() {
		equal("many primaryKeys - equal",
			newCacheId(new Object[] {"a", "b"}, new Object[] {"b", "c"}),
			newCacheId(new Object[] {"a", "b"}, new Object[] {"b", "c"}));
		equal("one primaryKey - equal",
			newCacheId(new Object[] {"a", "b"}),
			newCacheId(new Object[] {"a", "b"}));

		smaller("one primaryKey - smaller on first element",
			newCacheId(new Object[] {"a", "b"}), newCacheId(new Object[] {"b", "c"}));
		greater("one primaryKey - greater on first element",
			newCacheId(new Object[] {"b", "c"}), newCacheId(new Object[] {"a", "c"}));
		smaller("one primaryKey - smaller on last element",
			newCacheId(new Object[] {"b", "a", "b"}), newCacheId(new Object[] {"b", "a", "c"}));
		greater("one primaryKey - greater  on last element",
			newCacheId(new Object[] {"0", "0", "c"}), newCacheId(new Object[] {"0", "0", "b"}));

		smaller("many primaryKey - smaller",
			newCacheId(new Object[] {"a", "b"}, new Object[] {"b", "b"}), newCacheId(new Object[] {"a", "b"}, new Object[] {"b", "c"}));
		greater("many primaryKey - greater",
			newCacheId(new Object[] {"b", "c"}, new Object[] {"a", "d"}), newCacheId(new Object[] {"b", "c"}, new Object[] {"a", "c"}));

		greater("one primaryKey - smaller by different size",
			newCacheId(new Object[] {"a", "b"}), newCacheId(new Object[] {"a"}));
		smaller("one primaryKey - greater by different size",
			newCacheId(new Object[] {"b"}), newCacheId(new Object[] {"b", "a"}));
	}

	@Test
	public void compareToForString() {
		equal("many primaryKeys - equal",
			newCacheId("a", "b"), newCacheId("a", "b"));
		equal("one primaryKey - equal",
			newCacheId("ab"), newCacheId("ab"));

		smaller("one primaryKey - smaller",
			newCacheId("ab"), newCacheId("bc"));
		greater("one primaryKey - greater",
			newCacheId("bc"), newCacheId("ac"));

		smaller("many primaryKey - smaller",
			newCacheId("ab", "bb"), newCacheId("ab", "bc"));
		greater("many primaryKey - greater",
			newCacheId("bc", "ad"), newCacheId("bc", "ac"));

		greater("one primaryKey - smaller by different size",
			newCacheId("ab"), newCacheId("a"));
		smaller("one primaryKey - greater by different size",
			newCacheId("b"), newCacheId("ba"));
	}

	@Test
	public void compareToForNulls() {
		equal("many primaryKeys - equal",
			newCacheId("a", null), newCacheId("a", null));
		equal("one primaryKey - equal",
			newCacheId((Object)null), newCacheId((Object)null));

		equal("null on first element - equal",
			newCacheId(null, "a"), newCacheId(null, "a"));
		greater("null on first element - greater",
			newCacheId(null, "b"), newCacheId(null, "a"));

		smaller("one primaryKey - smaller",
			newCacheId((Object)null), newCacheId("any"));
		greater("one primaryKey - greater",
			newCacheId("any"), newCacheId((Object)null));

		smaller("many primaryKey - smaller",
			newCacheId("ab", (Object)null), newCacheId("ab", "any"));
		greater("many primaryKey - greater",
			newCacheId("bc", "any"), newCacheId("bc", (Object)null));
	}

	@Test
	public void compareToForDifferentSize() {
		smaller("smaller number of primary keys",
			newCacheId("a"), newCacheId("a", "b"));
		greater("greater number of primary keys",
			newCacheId("a", "b"), newCacheId("a"));
	}

	@Test
	public void compareToForNotComparable() {
		equal("NotComparable on both sides - equal",
			newCacheId(new NotComparable(1)), newCacheId(new NotComparable(1)));
		greater("NotComparable on both sides - greater based on hashcode",
			newCacheId(new NotComparable(2)), newCacheId(new NotComparable(1)));
		smaller("NotComparable on both sides - smaller based on hashcode",
			newCacheId(new NotComparable(1)), newCacheId(new NotComparable(2)));

		smaller("not comparable on left side - result based on hashcode",
			newCacheId(new NotComparable(1)), newCacheId("a"));
		greater("not comparable on right side - result based on hashcode",
			newCacheId("b"), newCacheId(new NotComparable(1)));
	}

	private CacheId newCacheId(Object... objects) {
		return new CacheId(objects);
	}

	private void equal(String description, CacheId id1, CacheId id2) {
		assertEquals(description, 0, id1.compareTo(id2));
		assertEquals(description + " (equals consistency)", id1, id2);
	}

	private void smaller(String description, CacheId id1, CacheId id2) {
		assertEquals(description, -1, id1.compareTo(id2));
		assertNotEquals(description + " (equals consistency)", id1, id2);
	}
	private void greater(String description, CacheId id1, CacheId id2) {
		assertEquals(description, 1, id1.compareTo(id2));
		assertNotEquals(description + " (equals consistency)", id1, id2);
	}

	private static class NotComparable {
		private int code;

		private NotComparable(int code) {
			this.code = code;
		}

		@Override
		public int hashCode() {
			return code;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			NotComparable other = (NotComparable) obj;
			return code == other.code;
		}
	}
}
