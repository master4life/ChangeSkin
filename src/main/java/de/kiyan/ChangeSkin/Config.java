package de.kiyan.ChangeSkin;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config {

    Plugin plugin;

    public Config( )
    {
        this.plugin = Main.getInstance();
    }

    public void prepareConfig()
    {
        if( !(new File( plugin.getDataFolder(), "guard.png" ).exists() ) )
        {
            plugin.saveResource( "guard.png", false);
        }

        if( !(new File( plugin.getDataFolder(), "prisoner.png" ).exists() ) )
        {
            plugin.saveResource( "prisoner.png", false);
        }
    }
}
