package reactions;

import dao.UserDAO;

import dao.model.Message;

import dao.model.User;

import java.util.*;

public class OldestReactionReporter extends AbstractReactionReporter {

    @Override
    protected List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message) {
        // Sort reactions by timestamp (oldest first)
        List<Reaction> sortedReactions = new ArrayList<>(reactions);
        sortedReactions.sort(Comparator.comparingLong(Reaction::getTimestamp));

        // Track which users we've already included
        Set<UUID> includedUsers = new HashSet<>();
        List<ReactionDisplayTag> result = new ArrayList<>();

        // Process reactions in chronological order
        for (Reaction reaction : sortedReactions) {
            UUID userId = reaction.getUserId();

            // Only include the first (oldest) reaction per user
            if (!includedUsers.contains(userId)) {
                includedUsers.add(userId);

                // Get the username
                String username = getUsernameById(userId);

                // Add to result
                result.add(new ReactionDisplayTag(reaction.getType(), username));

                // Stop if we've reached the maximum display count
                if (result.size() >= getMaxDisplayCount()) {
                    break;
                }
            }
        }

        return result;
    }


    private String getUsernameById(UUID userId) {
        try {
            // Try to find user by iterating through all users
            Iterator<User> users = UserDAO.getInstance().getAll();
            while (users.hasNext()) {
                User user = users.next();
                if (user != null && user.getUUID().equals(userId)) {
                    return user.username() != null ? user.username() : userId.toString();
                }
            }
        } catch (Exception e) {
            // If any error occurs, return UUID as string

        }
        return userId.toString();
    }
}
