package reactions;

import dao.model.Message;

import java.util.List;

public abstract class AbstractReactionReporter implements IReactionReporter{
    private static final int MAX_DISPLAY_COUNT = 5;


    @Override
    public ReactionDisplayTag[] generateReport(Message message) {
        if(message == null){
            return new ReactionDisplayTag[0];
        }
        // Get all reactions for the message
        List<Reaction> reactions = ReactionDAO.getInstance().getAllReactionsForMessage(message.id());

        if (reactions == null || reactions.isEmpty()) {
            return new ReactionDisplayTag[0];
        }

        // Process reactions according to specific algorithm (implemented by subclasses)
        List<ReactionDisplayTag> processedTags = processReactions(reactions, message);

        // Limit to maximum display count
        int resultSize = Math.min(processedTags.size(), MAX_DISPLAY_COUNT);
        ReactionDisplayTag[] result = new ReactionDisplayTag[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = processedTags.get(i);
        }

        return result;
    }

    protected abstract List<ReactionDisplayTag> processReactions(List<Reaction> reactions, Message message);
    protected int getMaxDisplayCount() {
        return MAX_DISPLAY_COUNT;
    }
}
