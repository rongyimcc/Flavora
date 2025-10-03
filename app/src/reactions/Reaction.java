package reactions;

import java.util.UUID;

/**
 * Represents a single reaction made by a user on a message.
 * Each reaction has a unique combination of user, message, and reaction type.
 */
public class Reaction implements Comparable<Reaction> {
    private final UUID userId;
    private final UUID messageId;
    private final ReactionType type;
    private final long timestamp;

    /**
     * Creates a new Reaction instance.
     *
     * @param userId    the UUID of the user who made the reaction
     * @param messageId the UUID of the message being reacted to
     * @param type      the type of reaction
     * @param timestamp the time when the reaction was created
     */
    public Reaction(UUID userId, UUID messageId, ReactionType type, long timestamp) {
        this.userId = userId;
        this.messageId = messageId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public UUID getUserId() {
    return userId;}

    public UUID getMessageId() {
    return messageId;}

    public ReactionType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Reaction other) {
        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Reaction)) return false;
        Reaction other = (Reaction) obj;
        return userId.equals(other.userId) &&
                messageId.equals(other.messageId) &&
                type == other.type;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + messageId.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}