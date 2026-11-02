/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.jpqlconstructor.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Consumer;

public final class Results {
    public static final ThreadLocal<Consumer<Book>> BOOK_OBSERVER = new ThreadLocal<>();

    private Results() {
    }

    public record Summary(Integer id, String title) {
    }

    public record Reversed(String title, Integer id) {
    }

    public static final class Ordinary {
        public final Integer id;
        public final String title;

        public Ordinary(Integer id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public record Text(String value) {
    }

    public record Number(Integer value) {
    }

    public record LongNumber(Long value) {
    }

    public record FloatNumber(Float value) {
    }

    public record DoubleNumber(Double value) {
    }

    public record ObjectValue(Object value) {
    }

    public record Code(String value) {
    }

    public record CodeResult(Code value) {
    }

    public record Nullable(Integer id, String value) {
    }

    public record Primitives(int id, String title) {
    }

    public record AuthorResult(Author value) {
    }

    public record Nested(Summary value) {
    }

    public record Aggregates(Long count, Long total, Double average, Integer minimum, Integer maximum) {
    }

    public record EntryResult(Map.Entry<?, ?> value) {
    }

    public record EntryTitle(Map.Entry<?, ?> entry, String title) {
    }

    public record TitleEntry(String title, Map.Entry<?, ?> entry) {
    }

    public record Entries(Map.Entry<?, ?> first, Map.Entry<?, ?> second, String title) {
    }

    public record MixedEntries(Integer id, Map.Entry<?, ?> first, String title, Map.Entry<?, ?> second, Integer quantity) {
    }

    public record ConvertedEntry(Map.Entry<Code, Code> entry) {
        public ConvertedEntry {
            if (!(entry.getKey() instanceof Code) || !(entry.getValue() instanceof Code)) {
                throw new IllegalArgumentException("Map entry conversion must precede constructor invocation");
            }
        }
    }

    public record Decimal(BigDecimal value) {
    }

    public record Functions(String coalesced, String nullified, Integer arithmetic, String chosen) {
    }

    public record BookResult(Book value) {
        public BookResult {
            Consumer<Book> observer = BOOK_OBSERVER.get();
            if (observer != null) {
                observer.accept(value);
            }
        }
    }

    public static final class Overloaded {
        public final String selected;

        public Overloaded(Integer value) {
            selected = "Integer";
        }

        public Overloaded(int value) {
            selected = "int";
        }
    }

    public static final class Bindings {
        public final Object value;

        public Bindings(String value) {
            this.value = value;
        }

        public Bindings(Integer value) {
            this.value = value;
        }
    }

    public static final class CachedBinding {
        public final String selected;
        public final Object value;

        public CachedBinding(String value) {
            selected = "String";
            this.value = value;
        }

        public CachedBinding(Integer value) {
            selected = "Integer";
            this.value = value;
        }
    }

    public static final class Ambiguous {
        public Ambiguous(int first, Integer second) {
        }

        public Ambiguous(Integer first, int second) {
        }
    }

    public static final class Assignable {
        public Assignable(Object id, CharSequence title) {
        }
    }

    public abstract static class AbstractResult {
        public AbstractResult(Integer id, String title) {
        }
    }

    public interface InterfaceResult {
    }

    public static final class Hidden {
        public final String value;

        private Hidden(String value) {
            this.value = value;
        }
    }

    public static final class Throwing {
        public Throwing(String value) {
            throw new IllegalStateException("ICR constructor failure: " + value);
        }
    }
}
