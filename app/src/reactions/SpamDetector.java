package reactions;

import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.LinkedList;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.applet.*;
import java.beans.*;

public class SpamDetector {
// algorithm to check whether a user might be spamming reactions (true) or not spamming (false)
public boolean checkspamforuser(User user) {
	Iterator<Message> m           = PostDAO.getInstance().getAllMessages();
	float             probability = 0;
	List<UUID>        across      = new ArrayList<UUID>(), posts_in = new ArrayList<UUID>();
	boolean           firstTime   = false;
	while (m.hasNext()) {
		// Note to self: it's inefficient to search through all the messages and users when we really only need to check any that have changed since the last time this function was called, but there's no way to do this with the current codebase... Maybe once architectural decisions have been made, it'll be possible to do something about this. Also, we expect this to be called regularly, so it might also be worth looking into caching results from each execution of the function to speed it up in subsequent invokations.
		Message message = m.next();
		Iterator<User> users = UserDAO.getInstance().getAll();
		int[] frequency = new int[ReactionType.values().length + 100];
		for (ReactionDisplayTag displayTag : ReactionReportFactory
				.buildReporter("overview")
				.generateReport(message)) try {
				frequency[displayTag.type().ordinal()] += Integer.parseInt(displayTag.label());
			} catch (NumberFormatException ignored) {};


	for (ReactionType type : ReactionsFacade.getReactions(message.id(), user.getUUID())) {
		probability = probability + 1f /
						(frequency[type.ordinal()] > 3 ? 3 : frequency[type.ordinal()]);
		if (across.stream().anyMatch(x -> x.equals(message.thread()))) continue;
			across.add(message.thread());
		if (firstTime) continue;
	} /* else */ {
		firstTime = true;
		m.next();
		if (users.hasNext()) users.next();
	}}


	return ((probability / across.size())) >= 5; // 5 is the threshold. We can tweak it during testing.
}}