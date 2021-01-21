package de.kiyan.ChangeSkin.Events;

import de.kiyan.ChangeSkin.Util.SkinManager;
import org.behindbars.gamecore.core.handlers.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;


public class EventJoin implements Listener {
    int counter = 0;

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();
        /*int rank = new PlayerHandler( player ).getRank();

        if( rank < 5 )
        {
            counter++;
            new SkinManager().applySkin(player, 0, counter);
            if( counter == 99 ) counter = 0;
        }

        if ( rank == 6 || rank == 7)
            new SkinManager().applySkin(player, 1, -1);*/

        int i = new Random().nextInt( 2 );
        new SkinManager().applySkin( player, i, i == 0 ? i + counter : -1 );
        counter++;
    }
}
