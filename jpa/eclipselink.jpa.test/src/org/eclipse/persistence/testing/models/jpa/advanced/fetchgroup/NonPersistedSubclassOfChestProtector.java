package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

/**
 * A non-Entity Superclass.
 * The state here is non-persistent
 * This class should NOT be annotated with @MappedSuperclass or @Entity
 * See p.55 section 2.11.3 "Non-Entity Classes in the Entity Inheritance Hierarchy
 * of the JPA 2.0 JSR-317 specification
 */
public class NonPersistedSubclassOfChestProtector extends ChestProtector {

    private long nonPersistentPrimitive;

    public long getNonPersistentPrimitive() {
        return nonPersistentPrimitive;
    }

    public void setNonPersistentPrimitive(long nonPersistentPrimitive) {
        this.nonPersistentPrimitive = nonPersistentPrimitive;
    }
    
}
