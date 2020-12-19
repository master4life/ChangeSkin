package de.kiyan.ChangeSkin.Events;

import de.kiyan.ChangeSkin.Util.SkinManager;
import org.behindbars.core.util.handler.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventJoin implements Listener
{
    @EventHandler
    public void onJoin( PlayerJoinEvent event  )
    {
        Player player = event.getPlayer();

        if( new PlayerHandler().getRank( player) < 5 ) {
            new SkinManager().applySkin(player);
        }
    }
}
