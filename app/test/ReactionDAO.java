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

public class ReactionDAO {

    @Before
    public void setUp() {
        RandomContentGenerator.populateRandomData();
        reactions.ReactionDAO.getInstance().clear();
    }

    @Test
    public void testAddReactionSuccess() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean result = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);

        assertTrue(result);
        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());
        assertEquals(1, reactions.size());
        assertEquals(ReactionType.HAPPY, reactions.get(0));
    }

    @Test
    public void testAddDuplicateReaction() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean first = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        boolean second = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 200);

        assertTrue(first);
        assertFalse(second); // Should fail as duplicate

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());
        assertEquals(1, reactions.size()); // Only one reaction should exist
    }

    @Test
    public void testAddReactionNullUser() {
        Message message = getRandomMessage();

        boolean result = ReactionsFacade.addReaction(null, message.id(), ReactionType.HAPPY, 100);

        assertFalse(result);
    }

    @Test
    public void testAddReactionNullMessage() {
        User user = getRandomUser();

        boolean result = ReactionsFacade.addReaction(user.getUUID(), null, ReactionType.HAPPY, 100);

        assertFalse(result);
    }

    @Test
    public void testAddReactionNullType() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean result = ReactionsFacade.addReaction(user.getUUID(), message.id(), null, 100);

        assertFalse(result);
    }

    @Test
    public void testAddMultipleTypesFromSameUser() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean r1 = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        boolean r2 = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 200);
        boolean r3 = ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LAUGH, 300);

        assertTrue(r1);
        assertTrue(r2);
        assertTrue(r3);

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());
        assertEquals(3, reactions.size());
    }

    @Test
    public void testRemoveReactionSuccess() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        boolean result = ReactionsFacade.removeReaction(user.getUUID(), message.id(), ReactionType.HAPPY);

        assertTrue(result);
        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());
        assertEquals(0, reactions.size());
    }

    @Test
    public void testRemoveNonExistentReaction() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean result = ReactionsFacade.removeReaction(user.getUUID(), message.id(), ReactionType.HAPPY);

        assertFalse(result);
    }

    @Test
    public void testRemoveReactionNullUser() {
        Message message = getRandomMessage();

        boolean result = ReactionsFacade.removeReaction(null, message.id(), ReactionType.HAPPY);

        assertFalse(result);
    }

    @Test
    public void testRemoveReactionNullMessage() {
        User user = getRandomUser();

        boolean result = ReactionsFacade.removeReaction(user.getUUID(), null, ReactionType.HAPPY);

        assertFalse(result);
    }

    @Test
    public void testRemoveReactionNullType() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        boolean result = ReactionsFacade.removeReaction(user.getUUID(), message.id(), null);

        assertFalse(result);
    }

    @Test
    public void testRemoveOneKeepOthers() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 200);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LAUGH, 300);

        boolean result = ReactionsFacade.removeReaction(user.getUUID(), message.id(), ReactionType.ANGRY);

        assertTrue(result);
        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());
        assertEquals(2, reactions.size());
        assertTrue(reactions.contains(ReactionType.HAPPY));
        assertTrue(reactions.contains(ReactionType.LAUGH));
        assertFalse(reactions.contains(ReactionType.ANGRY));
    }

    @Test
    public void testGetReactionsChronologicalOrder() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.SAD, 300);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 200);

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());

        assertEquals(3, reactions.size());
        assertEquals(ReactionType.HAPPY, reactions.get(0)); // timestamp 100
        assertEquals(ReactionType.ANGRY, reactions.get(1)); // timestamp 200
        assertEquals(ReactionType.SAD, reactions.get(2));   // timestamp 300
    }

    @Test
    public void testGetReactionsNullUser() {
        Message message = getRandomMessage();

        List<ReactionType> reactions = ReactionsFacade.getReactions(null, message.id());

        assertNull(reactions);
    }

    @Test
    public void testGetReactionsNullMessage() {
        User user = getRandomUser();

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), null);

        assertNull(reactions);
    }

    @Test
    public void testGetReactionsEmpty() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());

        assertNotNull(reactions);
        assertEquals(0, reactions.size());
    }

    @Test
    public void testGetReactionsSpecificUser() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 100);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.ANGRY, 200);
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.LAUGH, 300);

        List<ReactionType> reactions1 = ReactionsFacade.getReactions(user1.getUUID(), message.id());
        List<ReactionType> reactions2 = ReactionsFacade.getReactions(user2.getUUID(), message.id());

        assertEquals(2, reactions1.size());
        assertEquals(1, reactions2.size());
        assertTrue(reactions1.contains(ReactionType.HAPPY));
        assertTrue(reactions1.contains(ReactionType.LAUGH));
        assertTrue(reactions2.contains(ReactionType.ANGRY));
    }

    @Test
    public void testGetReactionsAfterRemoveAll() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 100);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 200);

        ReactionsFacade.removeReaction(user.getUUID(), message.id(), ReactionType.HAPPY);
        ReactionsFacade.removeReaction(user.getUUID(), message.id(), ReactionType.ANGRY);

        List<ReactionType> reactions = ReactionsFacade.getReactions(user.getUUID(), message.id());

        assertNotNull(reactions);
        assertEquals(0, reactions.size());
    }

    @Test
    public void testComplexScenario() {
        Message msg1 = getRandomMessage();
        Message msg2 = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        // User1 reacts to msg1
        ReactionsFacade.addReaction(user1.getUUID(), msg1.id(), ReactionType.HAPPY, 100);
        ReactionsFacade.addReaction(user1.getUUID(), msg1.id(), ReactionType.ANGRY, 200);

        // User2 reacts to msg1
        ReactionsFacade.addReaction(user2.getUUID(), msg1.id(), ReactionType.HAPPY, 150);

        // User1 reacts to msg2
        ReactionsFacade.addReaction(user1.getUUID(), msg2.id(), ReactionType.LAUGH, 300);

        // Verify
        assertEquals(2, ReactionsFacade.getReactions(user1.getUUID(), msg1.id()).size());
        assertEquals(1, ReactionsFacade.getReactions(user2.getUUID(), msg1.id()).size());
        assertEquals(1, ReactionsFacade.getReactions(user1.getUUID(), msg2.id()).size());
        assertEquals(0, ReactionsFacade.getReactions(user2.getUUID(), msg2.id()).size());
    }

    private Message getRandomMessage() {
        Message message = null;
        while (message == null) {
            Post post = PostDAO.getInstance().getRandom();
            if (post != null) {
                message = post.messages.getRandom();
            }
        }
        return message;
    }

    private User getRandomUser() {
        User user = UserDAO.getInstance().getRandom();
        while (user == null) {
            user = UserDAO.getInstance().getRandom();
        }
        return user;
    }


}
