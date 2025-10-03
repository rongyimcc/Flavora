import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import org.junit.Before;
import org.junit.Test;
import reactions.ReactionDAO;
import reactions.ReactionType;
import reactions.ReactionsFacade;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for ReactionDAO achieving statement-complete coverage.
 * Covers addReaction, removeReaction, and getReactions methods.
 */
