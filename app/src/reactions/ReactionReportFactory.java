package reactions;

public class ReactionReportFactory {
	public static IReactionReporter buildReporter(String type) {
		if(type == null){
			throw new IllegalArgumentException("the Report type can not be bull");
		}

		String normalizedType = type.toLowerCase().trim(); //去除空行

		switch (normalizedType) {
			case "oldest":
				return new OldestReactionReporter();
			case "overview":
				return new OverviewReactionReporter();
			default:
				throw new IllegalArgumentException("Unknown reporter type:" + type + ". Valid types are: 'oldest', 'overview'");
		}
	}
}
