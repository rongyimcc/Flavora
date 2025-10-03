package reactions;

import dao.PostDAO;
import dao.model.Message;
import dao.model.User;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SpamDetector {
	private static final int CONTRIBUTION_CAP = 3;
	private static final float SPAM_THRESHOLD = 5f;

	private final PostDAO postDAO;

	public SpamDetector() {
		this(PostDAO.getInstance());
	}

	SpamDetector(PostDAO postDAO) {
		this.postDAO = Objects.requireNonNull(postDAO);
	}

	// algorithm to check whether a user might be spamming reactions (true) or not spamming (false)
	public boolean checkSpamForUser(User user) {
		if (user == null) {
			return false;
		}

		Iterator<Message> messages = postDAO.getAllMessages();
		float probability = 0f;
		Set<UUID> threadsReacted = new HashSet<>();

		while (messages.hasNext()) {
			Message message = messages.next();
			Map<ReactionType, Integer> frequency = countReactionsForMessage(message.id());
			List<ReactionType> userReactions = safeGetReactions(user.getUUID(), message.id());
			if (userReactions.isEmpty()) {
				continue;
			}

			threadsReacted.add(message.thread());
			for (ReactionType type : userReactions) {
				int occurrences = Math.max(1, frequency.getOrDefault(type, 0));
				probability += 1f / Math.min(CONTRIBUTION_CAP, occurrences);
			}
		}

		if (threadsReacted.isEmpty()) {
			return false;
		}

		return probability / threadsReacted.size() >= SPAM_THRESHOLD; // 5 is the threshold. We can tweak it during testing.
	}

	private Map<ReactionType, Integer> countReactionsForMessage(UUID messageId) {
		EnumMap<ReactionType, Integer> counts = new EnumMap<>(ReactionType.class);
		for (Reaction reaction : ReactionDAO.getInstance().getAllReactionsForMessage(messageId)) {
			counts.merge(reaction.getType(), 1, Integer::sum);
		}
		return counts;
	}

	private List<ReactionType> safeGetReactions(UUID userId, UUID messageId) {
		List<ReactionType> reactions = ReactionsFacade.getReactions(userId, messageId);
		return reactions == null ? Collections.emptyList() : reactions;
	}
}
