package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.model.HasUUID;

import java.util.List;

/**
 * Abstract base class for Data Access Objects (DAO).
 * Provides a generic interface for all data access operations, 
 * abstracting the details of the underlying data source (Firebase in this project).
 * Follows the reference-app DAO abstraction pattern.
 *
 * @param <T> The entity type managed by this DAO, must implement HasUUID.
 * @author Flavora Team
 * @version 1.0
 */
public abstract class DAO<T extends HasUUID> {

    /**
     * Adds an entity to the data store.
     *
     * @param element  the entity to add
     * @param listener completion listener handling the operation result
     */
    public abstract void add(T element, OnCompleteListener<Void> listener);

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id       the unique identifier of the entity
     * @param listener completion listener returning the retrieved entity
     */
    public abstract void get(String id, OnCompleteListener<T> listener);

    /**
     * Retrieves all entities of this type.
     *
     * @param listener completion listener returning a list of all entities
     */
    public abstract void getAll(OnCompleteListener<List<T>> listener);

    /**
     * Deletes an entity by its unique identifier.
     *
     * @param id       the unique identifier of the entity to delete
     * @param listener completion listener handling the operation result
     */
    public abstract void delete(String id, OnCompleteListener<Void> listener);

    /**
     * Updates an existing entity.
     *
     * @param element  the entity containing updated data
     * @param listener completion listener handling the operation result
     */
    public abstract void update(T element, OnCompleteListener<Void> listener);
}
