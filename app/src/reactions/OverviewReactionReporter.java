package reactions;

import dao.model.Message;

import java.util.*;

public class OverviewReactionReporter extends AbstractReactionReporter {

    @Override
    protected List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message) {
        // Track frequency and first occurrence timestamp for each reaction type
        Map<ReactionType, Integer> frequencyMap = new HashMap<>();
        Map<ReactionType, Long> firstOccurrenceMap = new HashMap<>();

        // Count reactions and track first occurrence
        for (Reaction reaction : reactions) {
            ReactionType type = reaction.getType();
            long timestamp = reaction.getTimestamp();

            // Update frequency
            frequencyMap.put(type, frequencyMap.getOrDefault(type, 0) + 1);

            // Update first occurrence (keep the earliest timestamp)
            if (!firstOccurrenceMap.containsKey(type) ||
                    timestamp < firstOccurrenceMap.get(type)) {
                firstOccurrenceMap.put(type, timestamp);
            }
        }

        // Create list of reaction type statistics
        List<ReactionTypeStats> statsList = new ArrayList<>();
        for (Map.Entry<ReactionType, Integer> entry : frequencyMap.entrySet()) {
            ReactionType type = entry.getKey();
            int count = entry.getValue();
            long firstTimestamp = firstOccurrenceMap.get(type);

            statsList.add(new ReactionTypeStats(type, count, firstTimestamp));
        }

        // Sort: first by count (descending), then by timestamp (ascending)
        statsList.sort((a, b) -> {
            int countCompare = Integer.compare(b.count, a.count);
            if (countCompare != 0) {
                return countCompare;
            }
            return Long.compare(a.firstTimestamp, b.firstTimestamp);
        });

        // Build result with counts as labels
        List<ReactionDisplayTag> result = new ArrayList<>();
        int limit = Math.min(statsList.size(), getMaxDisplayCount());

        for (int i = 0; i < limit; i++) {
            ReactionTypeStats stats = statsList.get(i);
            result.add(new ReactionDisplayTag(stats.type, String.valueOf(stats.count)));
        }

        return result;
    }

    private static class ReactionTypeStats {
        final ReactionType type;
        final int count;
        final long firstTimestamp;

        ReactionTypeStats(ReactionType type, int count, long firstTimestamp) {
            this.type = type;
            this.count = count;
            this.firstTimestamp = firstTimestamp;
        }
    }
}
