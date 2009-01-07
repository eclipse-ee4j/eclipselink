package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

public interface ViewRow extends java.io.Serializable {
    public boolean equals(String key, Object value);
}
