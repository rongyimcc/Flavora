package reactions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for managing reactions.
 * Uses a three-level nested HashMap for O(1) access performance:
 * Message -> User -> ReactionType -> Reaction
 *
 * This design ensures:
 * - Fast addition, removal, and retrieval operations
 * - Each user can only have one reaction of each type per message
 * - Thread-safe operations for concurrent access
 */
public class ReactionDAO {
    private static ReactionDAO instance;

    // Message UUID -> (User UUID -> (ReactionType -> Reaction))
    private final Map<UUID, Map<UUID, Map<ReactionType, Reaction>>> reactionStore;

    // Message UUID -> List of all reactions (for efficient iteration)
    private final Map<UUID, List<Reaction>> messageReactions;

    private ReactionDAO() {
        this.reactionStore = new ConcurrentHashMap<>();
        this.messageReactions = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of ReactionDAO.
     *
     * @return the singleton instance
     */
    public static ReactionDAO getInstance() {
        if (instance == null) {
            synchronized (ReactionDAO.class) {
                if (instance == null) {
                    instance = new ReactionDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a reaction to the store.
     * If a reaction with the same user, message, and type already exists, returns false.
     *
     * @param reaction the reaction to add
     * @return true if added successfully, false if already exists
     */
    public boolean addReaction(Reaction reaction) {
        if (reaction == null) {
            return false;
        }

        UUID messageId = reaction.getMessageId();
        UUID userId = reaction.getUserId();
        ReactionType type = reaction.getType();

        // Get or create message map
        Map<UUID, Map<ReactionType, Reaction>> messageMap =
                reactionStore.computeIfAbsent(messageId, k -> new ConcurrentHashMap<>());

        // Get or create user map
        Map<ReactionType, Reaction> userMap =
                messageMap.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        // Check if reaction already exists
        if (userMap.containsKey(type)) {
            return false;
        }

        // Add the reaction
        userMap.put(type, reaction);

        // Update message reactions list
        List<Reaction> reactions = messageReactions.computeIfAbsent(messageId, k -> new ArrayList<>());
        reactions.add(reaction);

        return true;
    }

    /**
     * Removes a reaction from the store.
     *
     * @param userId    the user who made the reaction
     * @param messageId the message that was reacted to
     * @param type      the type of reaction
     * @return true if removed successfully, false if not found
     */
    public boolean removeReaction(UUID userId, UUID messageId, ReactionType type) {
        if (userId == null || messageId == null || type == null) {
            return false;
        }

        Map<UUID, Map<ReactionType, Reaction>> messageMap = reactionStore.get(messageId);
        if (messageMap == null) {
            return false;
        }

        Map<ReactionType, Reaction> userMap = messageMap.get(userId);
        if (userMap == null) {
            return false;
        }

        Reaction removed = userMap.remove(type);
        if (removed == null) {
            return false;
        }

        // Clean up empty maps
        if (userMap.isEmpty()) {
            messageMap.remove(userId);
        }
        if (messageMap.isEmpty()) {
            reactionStore.remove(messageId);
        }

        // Update message reactions list
        List<Reaction> reactions = messageReactions.get(messageId);
        if (reactions != null) {
            reactions.removeIf(r -> r.getUserId().equals(userId) &&
                    r.getMessageId().equals(messageId) &&
                    r.getType() == type);
            if (reactions.isEmpty()) {
                messageReactions.remove(messageId);
            }
        }

        return true;
    }

    /**
     * Gets all reactions by a specific user on a specific message, sorted by timestamp.
     *
     * @param userId    the user UUID
     * @param messageId the message UUID
     * @return list of reaction types in chronological order, or empty list if none found
     */
    public List<ReactionType> getReactionsByUser(UUID userId, UUID messageId) {
        if (userId == null || messageId == null) {
            return new ArrayList<>();
        }

        Map<UUID, Map<ReactionType, Reaction>> messageMap = reactionStore.get(messageId);
        if (messageMap == null) {
            return new ArrayList<>();
        }

        Map<ReactionType, Reaction> userMap = messageMap.get(userId);
        if (userMap == null) {
            return new ArrayList<>();
        }

        // Sort reactions by timestamp
        List<Reaction> reactions = new ArrayList<>(userMap.values());
        reactions.sort(Comparator.comparingLong(Reaction::getTimestamp));

        List<ReactionType> result = new ArrayList<>();
        for (Reaction reaction : reactions) {
            result.add(reaction.getType());
        }

        return result;
    }

    /**
     * Gets all reactions for a specific message.
     *
     * @param messageId the message UUID
     * @return list of all reactions for the message
     */
    public List<Reaction> getAllReactionsForMessage(UUID messageId) {
        if (messageId == null) {
            return new ArrayList<>();
        }

        List<Reaction> reactions = messageReactions.get(messageId);
        return reactions != null ? new ArrayList<>(reactions) : new ArrayList<>();
    }

    /**
     * Clears all reactions from the store.
     */
    public void clear() {
        reactionStore.clear();
        messageReactions.clear();
    }

    /**
     * Gets all reactions in the system.
     *
     * @return list of all reactions
     */
    public List<Reaction> getAllReactions() {
        List<Reaction> allReactions = new ArrayList<>();
        for (List<Reaction> reactions : messageReactions.values()) {
            allReactions.addAll(reactions);
        }
        return allReactions;
    }
}



