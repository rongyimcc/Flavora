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
		if (userUUID == null || messageUUID == null || type == null) {
			return false;
		}

		Reaction reaction = new Reaction(userUUID, messageUUID, type, timestamp);

		boolean result = ReactionDAO.getInstance().addReaction(reaction);

		// Save to persistent storage after modification
		if (result) {
			ReactionPersistence.saveReactions();
		}

		return result;

	}

	/**
	 * Removes a reaction by a particular user of a particular type to a particular message.
	 * Returns true if the reaction was successfully removed, and false otherwise.
	 */
	public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
		if (userUUID == null || messageUUID == null || type == null) {
			return false;
		}

		boolean result = ReactionDAO.getInstance().removeReaction(userUUID, messageUUID, type);

		// Save to persistent storage after modification
		if (result) {
			ReactionPersistence.saveReactions();
		}

		return result;
	}

	/**
	 * Fetches all reactions made by a particular user on a particular message.
	 * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
	 * They must be returned in chronological (time-based) order, from oldest to newest.
	 */
	public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
			if (userUUID == null || messageUUID == null) {
				return null;
			}

			return ReactionDAO.getInstance().getReactionsByUser(userUUID, messageUUID);
		}


	/**
	 * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
	 */
	public static void loadPersistentData() {
		ReactionPersistence.loadReactions();
	}
}
