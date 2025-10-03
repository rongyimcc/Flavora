package reactions;

import dao.model.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewReactionReporter extends AbstractReactionReporter {
	private static final Comparator<ReactionTypeStats> STATS_ORDER =
		Comparator.comparingInt(ReactionTypeStats::count)
			.reversed()
			.thenComparingLong(ReactionTypeStats::firstTimestamp);

	@Override
	protected List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message) {
		Map<ReactionType, Stats> statsByType = new EnumMap<>(ReactionType.class);

		for (Reaction reaction : reactions) {
			statsByType.compute(reaction.getType(), (type, existing) -> {
				long timestamp = reaction.getTimestamp();
				if (existing == null) {
					return new Stats(1, timestamp);
				}
				int updatedCount = existing.count() + 1;
				long earliest = Math.min(existing.firstTimestamp(), timestamp);
				return new Stats(updatedCount, earliest);
			});
		}

		List<ReactionTypeStats> stats = new ArrayList<>(statsByType.size());
		for (Map.Entry<ReactionType, Stats> entry : statsByType.entrySet()) {
			Stats value = entry.getValue();
			stats.add(new ReactionTypeStats(entry.getKey(), value.count(), value.firstTimestamp()));
		}
		stats.sort(STATS_ORDER);

		List<ReactionDisplayTag> result = new ArrayList<>();
		int limit = Math.min(stats.size(), getMaxDisplayCount());
		for (int i = 0; i < limit; i++) {
			ReactionTypeStats stat = stats.get(i);
			result.add(new ReactionDisplayTag(stat.type(), Integer.toString(stat.count())));
		}
		return result;
	}

	private record Stats(int count, long firstTimestamp) { }

	private record ReactionTypeStats(ReactionType type, int count, long firstTimestamp) { }
}
