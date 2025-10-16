package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.model.HasUUID;

import java.util.List;

/**
 * Abstract base class for data access objects (DAO).
 * Provides a common interface for CRUD operations while hiding the underlying data source (Firebase in this project).
 * Follows the reference-app pattern for data access abstraction.
 *
 * @param <T> Entity type managed by the DAO, must implement HasUUID.
 * @author Flavora Team
 * @version 1.0
 */
public abstract class DAO<T extends HasUUID> {

    /**
     * Adds an entity to the backing store.
     *
     * @param element  Entity to add.
     * @param listener Completion callback invoked with the result.
     */
    public abstract void add(T element, OnCompleteListener<Void> listener);

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id       Entity identifier.
     * @param listener Completion callback returning the fetched entity.
     */
    public abstract void get(String id, OnCompleteListener<T> listener);

    /**
     * Retrieves every entity of this type.
     *
     * @param listener Completion callback returning the entity list.
     */
    public abstract void getAll(OnCompleteListener<List<T>> listener);

    /**
     * Deletes an entity by its unique identifier.
     *
     * @param id       Identifier of the entity to remove.
     * @param listener Completion callback invoked with the result.
     */
    public abstract void delete(String id, OnCompleteListener<Void> listener);

    /**
     * Updates an existing entity.
     *
     * @param element  Entity containing updated data.
     * @param listener Completion callback invoked with the result.
     */
    public abstract void update(T element, OnCompleteListener<Void> listener);
}
