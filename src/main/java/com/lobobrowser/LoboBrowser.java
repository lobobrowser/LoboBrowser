package com.lobobrowser;
import java.util.logging.Logger;

public class LoboBrowser {

    private static LoboBrowser instance;
    private Logger logger;

    public LoboBrowser() {
        instance = this;
        logger = Logger.getLogger("LoboBrowser");
    }

    public static LoboBrowser getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

}
