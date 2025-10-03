package reactions;

import dao.UserDAO;
import dao.model.Message;
import dao.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OldestReactionReporter extends AbstractReactionReporter {
	private final UserDAO userDAO = UserDAO.getInstance();

	@Override
	protected List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message) {
		List<Reaction> sortedReactions = new ArrayList<>(reactions);
		sortedReactions.sort(Comparator.comparingLong(Reaction::getTimestamp));

		Set<UUID> includedUsers = new HashSet<>();
		Map<UUID, String> usernameCache = new HashMap<>();
		List<ReactionDisplayTag> result = new ArrayList<>();

		for (Reaction reaction : sortedReactions) {
			UUID userId = reaction.getUserId();
			if (!includedUsers.add(userId)) {
				continue;
			}

			String username = usernameCache.computeIfAbsent(userId, this::resolveUsername);
			result.add(new ReactionDisplayTag(reaction.getType(), username));

			if (result.size() >= getMaxDisplayCount()) {
				break;
			}
		}

		return result;
	}

	private String resolveUsername(UUID userId) {
		User user = userDAO.getByUUID(userId);
		if (user == null) {
			return userId.toString();
		}

		String username = user.username();
		return (username == null || username.isBlank()) ? userId.toString() : username;
	}
}
