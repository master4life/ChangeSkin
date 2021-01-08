package de.kiyan.ChangeSkin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config {

    Plugin plugin = null;

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

        plugin.saveResource( "config.yml", false );
    }

    public Location getLocation()
    {
        World world = Bukkit.getWorld( plugin.getConfig().getString( "teleport.World") );
        Location loc = new Location(
                world,
                plugin.getConfig().getDouble("teleport.X" ),
                plugin.getConfig().getDouble( "teleport.Y" ),
                plugin.getConfig().getDouble( "teleport.Z" )
        );

        return loc;
    }
}
