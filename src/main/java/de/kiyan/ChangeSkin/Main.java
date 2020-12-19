package de.kiyan.ChangeSkin;

import de.kiyan.ChangeSkin.Events.EventJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    private static Main instance;

    @Override
    public void onEnable( )
    {
        instance = this;

        Bukkit.getServer( ).getConsoleSender( ).sendMessage( "§2Enabling ChangeSkin" );

        saveDefaultConfig( );
        reloadConfig( );

        new Config().AssignVar();

         PluginManager plr = Bukkit.getPluginManager( ); plr.registerEvents( new EventJoin(), instance );
    }

    public static Main getInstance( )
    {
        return instance;
    }
}
