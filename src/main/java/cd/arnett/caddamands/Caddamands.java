package cd.arnett.caddamands;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Caddamands extends JavaPlugin {

    /**
     * Logger used for errors & testing or other things
     */
    public static Logger logger;


    @Override
    public void onEnable()
    {
        //set the logger for easy logging
        logger = getLogger();
    }

}
