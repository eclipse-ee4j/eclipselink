package javax.persistence;

/**
 * Specifies whether a transaction-scoped or extended 
 * persistence context is to be used in {@link PersistenceContext}. 
 * If the {@link PersistenceContext#type type} element is not 
 * specified, a transaction-scoped persistence context is used.
 *
 * @since Java Persistence 1.0
 */
public enum PersistenceContextType {

    /** Transaction-scoped persistence context */
    TRANSACTION,

    /** Extended persistence context */
    EXTENDED
}
