package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.EventLog;

import java.time.LocalDateTime;

public class EventLogger {

    public static void log(String action) {

        try {

            String username = "SYSTEM";

            if (SessionManager.getInstance().isLoggedIn()) {
                username =
                        SessionManager.getInstance()
                                .getLoggedInUser()
                                .getUsername();
            }

            EventLog event =
                    new EventLog(
                            username,
                            action,
                            LocalDateTime.now().toString()
                    );

            DaoFactory.getDaoFactory()
                    .createEventLogDao()
                    .create(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}