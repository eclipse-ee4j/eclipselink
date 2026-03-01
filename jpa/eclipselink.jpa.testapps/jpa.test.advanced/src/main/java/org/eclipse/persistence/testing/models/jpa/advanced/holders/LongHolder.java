package org.eclipse.persistence.testing.models.jpa.advanced.holders;

import java.util.Objects;

public class LongHolder {
    public Long value1;
    public Long value2;
    public LongHolder(Long value1, Long value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result += value1 != null ? value1.hashCode() : 0;
        result += value2 != null ? value2.hashCode() : 0;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LongHolder other)) {
            return false;
        }
        return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
    }

    @Override
    public String toString() {
        return "LongHolder(" + value1 + ", " + value2 + ")";
    }
}