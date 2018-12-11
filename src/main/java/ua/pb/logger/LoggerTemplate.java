package ua.pb.logger;

import org.apache.logging.log4j.Logger;

public interface LoggerTemplate {
    Logger getLogger();

    default void logError(Exception e) {
        getLogger().error("Message: " + e.getMessage() + "\tCause:" + e.getCause());
    }

    default void logErrorMessage(String message) {
        getLogger().error(message);
    }

    default void logWarn(String warning) {
        getLogger().error("Warning: " + warning);
    }
}
