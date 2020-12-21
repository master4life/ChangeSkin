package de.kiyan.ChangeSkin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

        plugin.saveResource( "config.yml", true );
    }

    public Location getLocation()
    {
        World world = Bukkit.getWorld(plugin.getConfig().getString( "teleport.world") );
        Location loc = new Location(
                world,
                plugin.getConfig().getDouble("teleport.x"),
                plugin.getConfig().getDouble( "teleport.y"),
                plugin.getConfig().getDouble( "teleport.z" )
        );

        return loc;
    }
}
