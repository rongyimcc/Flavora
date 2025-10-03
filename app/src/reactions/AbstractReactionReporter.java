package reactions;

import dao.model.Message;

import java.util.List;
import java.util.Objects;

public abstract class AbstractReactionReporter implements IReactionReporter {
	private static final int MAX_DISPLAY_COUNT = 5;
	private static final ReactionDisplayTag[] EMPTY_REPORT = new ReactionDisplayTag[0];

	@Override
	public ReactionDisplayTag[] generateReport(Message message) {
		if (message == null) {
			return EMPTY_REPORT;
		}

		List<Reaction> reactions = ReactionDAO.getInstance().getAllReactionsForMessage(message.id());
		if (reactions.isEmpty()) {
			return EMPTY_REPORT;
		}

		List<ReactionDisplayTag> processedTags = Objects.requireNonNull(processReactions(reactions, message));
		return processedTags.stream()
			.limit(MAX_DISPLAY_COUNT)
			.toArray(ReactionDisplayTag[]::new);
	}

	protected abstract List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message);

	protected int getMaxDisplayCount() {
		return MAX_DISPLAY_COUNT;
	}
}
