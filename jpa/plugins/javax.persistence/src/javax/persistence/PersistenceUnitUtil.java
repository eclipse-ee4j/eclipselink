package javax.persistence;

/**
 * Utility interface between the application and the persistence
 * provider managing the persistence unit.
 *
 * The methods of this interface should only be invoked on entity
 * instances obtained from or managed by entity managers for this
 * persistence unit or on new entity instances.
 */
public interface PersistenceUnitUtil extends PersistenceUtil {

    /**
     * Determine the load state of a given persistent attribute
     * of an entity belonging to the persistence unit.
     * @param entity containing the attribute
     * @param attributeName name of attribute whose load state is
     *    to be determined
     * @return false if entity's state has not been loaded or
     *  if the attribute state has not been loaded, otherwise true
     */
    public boolean isLoaded(Object entity, String attributeName);

    /**
     * Determine the load state of an entity belonging to the
     * persistence unit.
     * This method can be used to determine the load state 
     * of an entity passed as a reference.  An entity is
     * considered loaded if all attributes for which FetchType
     * EAGER has been specified have been loaded.
     * The isLoaded(Object, String) method should be used to 
     * determine the load state of an attribute.
     * Not doing so might lead to unintended loading of state.
     * @param entity whose load state is to be determined
     * @return false if the entity has not been loaded, else true.
     */
    public boolean isLoaded(Object entity);

    /**
     *  Returns the id of the entity.
     *  A generated id is not guaranteed to be available until after
     *  the database insert has occurred.
     *  Returns null if the entity does not yet have an id
     *  @param entity
     *  @return id of the entity
     *  @throws IllegalStateException if the entity is found not to be
     *          an entity.
     */
    public Object getIdentifier(Object entity);
} 
