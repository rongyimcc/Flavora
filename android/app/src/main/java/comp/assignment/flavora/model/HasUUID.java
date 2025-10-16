package comp.assignment.flavora.model;

/**
 * UUID identifier interface.
 * <p>
 * Provides a unified access contract for objects that expose a UUID.
 * Follows the entity identification pattern from the reference app.
 *
 * @author Flavora Team
 */
public interface HasUUID {
    /**
     * Gets the UUID identifier of the entity.
     *
     * @return UUID string
     */
    String getUUID();
}
