package comp.assignment.flavora.model;

/**
 * UUID Identifier Interface
 * <p>
 * Provides a unified access contract for objects identified by a UUID.
 * Follows the entity identification pattern used in the reference application.
 *
 * @author Flavora Team
 */
public interface HasUUID {
    /**
     * Returns the entity's UUID identifier.
     *
     * @return UUID string
     */
    String getUUID();
}
