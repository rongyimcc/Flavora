import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import reactions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ReactionReportOverviewTests {

    private IReactionReporter reporter;

    public ReactionReportOverviewTests(IReactionReporter reporter) {
        this.reporter = reporter;
    }

    @Parameters
    public static Collection<Object[]> getReporters() {
        ReportSources sources = new ReportSources();
        IReactionReporter[] reporters = sources.getReporters();

        // Convert to collection of single-element arrays
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < reporters.length; i++) {
            data.add(new Object[]{reporters[i]});
        }

        return data;
    }

    @Before
    public void setUp() {
        RandomContentGenerator.populateRandomData();
        // Clear reactions before each test
        ReactionDAO.getInstance().clear();
    }


    @Test
    public void testLessThanFiveTypes() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        // Add reactions: HAPPY x2, ANGRY x1
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.HAPPY, 1);
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.ANGRY, 2);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(2, result.length);
        assertEquals(ReactionType.HAPPY, result[0].type());
        assertEquals("2", result[0].label());
        assertEquals(ReactionType.ANGRY, result[1].type());
        assertEquals("1", result[1].label());
    }


    @Test
    public void testExactlyFiveTypes() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 1);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LAUGH, 2);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.SAD, 3);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LIKE, 4);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(5, result.length);
    }


    @Test
    public void testMoreThanFiveTypes() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 1);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LAUGH, 2);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.SAD, 3);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LIKE, 4);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LOVE, 5);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.SURPRISE, 6);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(5, result.length);
    }


    @Test
    public void testSortedByFrequency() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();
        User user3 = getRandomUser();
        User user4 = getRandomUser();

        // HAPPY x4, ANGRY x2, LAUGH x2
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.ANGRY, 1);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.HAPPY, 3);
        ReactionsFacade.addReaction(user3.getUUID(), message.id(), ReactionType.LAUGH, 4);
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.LAUGH, 5);
        ReactionsFacade.addReaction(user3.getUUID(), message.id(), ReactionType.HAPPY, 6);
        ReactionsFacade.addReaction(user3.getUUID(), message.id(), ReactionType.ANGRY, 7);
        ReactionsFacade.addReaction(user4.getUUID(), message.id(), ReactionType.HAPPY, 8);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(ReactionType.HAPPY, result[0].type());
        assertEquals("4", result[0].label());
        // ANGRY and LAUGH both have 2, check both appear
        assertTrue(result[1].type() == ReactionType.ANGRY || result[1].type() == ReactionType.LAUGH);
        assertTrue(result[2].type() == ReactionType.ANGRY || result[2].type() == ReactionType.LAUGH);
    }


    @Test
    public void testEqualFrequencyOlderFirst() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        // HAPPY first at time 0, ANGRY later at time 2, both appear twice
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.HAPPY, 1);
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.ANGRY, 2);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.ANGRY, 3);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(2, result.length);
        assertEquals(ReactionType.HAPPY, result[0].type());
        assertEquals("2", result[0].label());
        assertEquals(ReactionType.ANGRY, result[1].type());
        assertEquals("2", result[1].label());
    }


    @Test
    public void testDeletedReactionsNotShown() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.HAPPY, 1);
        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.ANGRY, 2);

        // Remove one HAPPY reaction
        ReactionsFacade.removeReaction(user1.getUUID(), message.id(), ReactionType.HAPPY);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(2, result.length);
        // HAPPY should now have count 1
        assertEquals(ReactionType.HAPPY, result[0].type());
        assertEquals("1", result[0].label());
        assertEquals(ReactionType.ANGRY, result[1].type());
        assertEquals("1", result[1].label());
    }


    @Test
    public void testAllDeletedTypeNotShown() {
        Message message = getRandomMessage();
        User user1 = getRandomUser();
        User user2 = getRandomUser();

        ReactionsFacade.addReaction(user1.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user2.getUUID(), message.id(), ReactionType.ANGRY, 1);

        // Remove HAPPY
        ReactionsFacade.removeReaction(user1.getUUID(), message.id(), ReactionType.HAPPY);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(1, result.length);
        assertEquals(ReactionType.ANGRY, result[0].type());
    }


    @Test
    public void testEmptyMessage() {
        Message message = getRandomMessage();

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertNotNull(result);
        assertEquals(0, result.length);
    }


    @Test
    public void testSingleReaction() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 0);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(1, result.length);
        assertEquals(ReactionType.HAPPY, result[0].type());
        assertEquals("1", result[0].label());
    }


    @Test
    public void testMultipleReactionsFromSameUser() {
        Message message = getRandomMessage();
        User user = getRandomUser();

        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.HAPPY, 0);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.ANGRY, 1);
        ReactionsFacade.addReaction(user.getUUID(), message.id(), ReactionType.LAUGH, 2);

        ReactionDisplayTag[] result = reporter.generateReport(message);

        assertEquals(3, result.length);
        // Each should have count of 1
        for (ReactionDisplayTag tag : result) {
            assertEquals("1", tag.label());
        }
    }

    // Helper methods
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


