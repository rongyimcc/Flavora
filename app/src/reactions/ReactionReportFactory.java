package reactions;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class ReactionReportFactory {
	private static final Map<String, Supplier<IReactionReporter>> REPORTERS = Map.of(
		"oldest", OldestReactionReporter::new,
		"overview", OverviewReactionReporter::new
	);

	private ReactionReportFactory() {
	}

	public static IReactionReporter buildReporter(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Report type cannot be null");
		}

		String normalizedType = type.trim().toLowerCase(Locale.ROOT);
		Supplier<IReactionReporter> supplier = REPORTERS.get(normalizedType);
		if (supplier == null) {
			throw new IllegalArgumentException(
				"Unknown reporter type: " + type + ". Valid types are: 'oldest', 'overview'"
			);
		}
		return supplier.get();
	}
}
