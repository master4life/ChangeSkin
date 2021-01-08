package de.kiyan.ChangeSkin.Events;

import de.kiyan.ChangeSkin.Main;
import de.kiyan.ChangeSkin.Util.SkinManager;
import org.behindbars.core.util.handler.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EventJoin implements Listener
{
    int counter = 0;

    @EventHandler
    public void onJoin( PlayerJoinEvent event  )
    {
        Player player = event.getPlayer();
        /*int rank = new PlayerHandler().getRank( player);

        if( rank < 5 )
        {
            counter++;
            new SkinManager().applySkin(player, 0, counter);
            if( counter == 99 ) counter = 0;
        }

        if ( rank == 6 || rank == 7)
            new SkinManager().applySkin(player, 1, -1);*/
        new BukkitRunnable() {
            @Override
            public void run() {
                new SkinManager().applySkin( player, 0, 2 );
                System.out.println( " balbal ");
            }
        }.runTaskLater( Main.getInstance(), 100L );

    }

}
