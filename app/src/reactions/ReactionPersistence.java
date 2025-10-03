package reactions;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * Handles persistence of reaction data to and from files. Uses a compact CSV format for space efficiency.
 */
public class ReactionPersistence {
    private static final String REACTIONS_FILE = "data/reactions.csv";
    private static final String DELIMITER = ",";

    /**
     * Saves all reactions to persistent storage. Uses BufferedWriter for efficient I/O.
     */
    public static void saveReactions() {
        List<Reaction> reactions = ReactionDAO.getInstance().getAllReactions();

        if (reactions.isEmpty()) {
            // If no reactions, just return
            return;
        }

        try {
            // Ensure data directory exists
            File file = new File(REACTIONS_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Write reactions to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Reaction reaction : reactions) {
                    String line = formatReaction(reaction);
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving reactions: " + e.getMessage());
        }
    }

    /**
     * Loads all reactions from persistent storage. Uses BufferedReader for efficient I/O.
     */
    public static void loadReactions() {
        File file = new File(REACTIONS_FILE);

        if (!file.exists()) {
            // No reactions file exists yet, return
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Reaction reaction = parseReaction(line);
                    if (reaction != null) {
                        ReactionDAO.getInstance().addReaction(reaction);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing reaction line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading reactions: " + e.getMessage());
        }
    }

    /**
     * Formats a reaction as a CSV line.
     */
    private static String formatReaction(Reaction reaction) {
        return reaction.getMessageId() + DELIMITER +
                reaction.getUserId() + DELIMITER +
                reaction.getType().name() + DELIMITER +
                reaction.getTimestamp();
    }

    /**
     * Parses a CSV line into a Reaction object.
     */
    private static Reaction parseReaction(String line) {
        String[] parts = line.split(DELIMITER);

        if (parts.length != 4) {
            return null;
        }

        try {
            UUID messageId = UUID.fromString(parts[0].trim());
            UUID userId = UUID.fromString(parts[1].trim());
            ReactionType type = ReactionType.valueOf(parts[2].trim());
            long timestamp = Long.parseLong(parts[3].trim());

            return new Reaction(userId, messageId, type, timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clears the reactions file.
     */
    public static void clearPersistedReactions() {
        File file = new File(REACTIONS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}

