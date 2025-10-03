package reactions;

import java.util.List;
import java.util.UUID;

public class ReactionsFacade {
	/**
	 * Adds a reaction by a particular user of a particular type to a particular message.
	 * Returns true if the reaction was successfully added, and false otherwise.
	 * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
	 */
	public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
		// TODO (task 1)
		return false;
	}

	/**
	 * Removes a reaction by a particular user of a particular type to a particular message.
	 * Returns true if the reaction was successfully removed, and false otherwise.
	 */
	public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
		// TODO (task 1)
		return false;
	}

	/**
	 * Fetches all reactions made by a particular user on a particular message.
	 * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
	 * They must be returned in chronological (time-based) order, from oldest to newest.
	 */
	public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
		// TODO (task 1)
		return null;
	}

	/**
	 * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
	 */
	public static void loadPersistentData() {
		// TODO (task 3)
	}
}
