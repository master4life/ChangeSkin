package de.kiyan.ChangeSkin.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.util.HashSet;

public class Skin1_5 implements Skin {

    @Override
    public void refreshPlayer( Player player, String value, String signature ) {
        GameProfile gameProfile = ( ( CraftPlayer ) player ).getProfile();
        gameProfile.getProperties().removeAll( "textures" );
        gameProfile.getProperties().put( "textures", new Property( "textures", value, signature ) );

        Location location = player.getLocation();
        EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();

        // Remove
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer );
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy( entityPlayer.getId() );

        // Add
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn( entityPlayer );
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer );

        World world = entityPlayer.getWorld();
        WorldData worldData = entityPlayer.getWorld().worldData;
        WorldType worldType = worldData.getType();
        long worldTime = worldData.getTime();
        PlayerInteractManager playerInteractManager = entityPlayer.playerInteractManager;
        EnumGamemode enumGamemode = playerInteractManager.getGameMode();

        PacketPlayOutRespawn playOutRespawn = new PacketPlayOutRespawn(world.getWorldProvider().getDimensionManager(), worldTime, worldType, enumGamemode);;
        PacketPlayOutPosition playOutPosition = new PacketPlayOutPosition( location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), new HashSet<>(), 0 );

        for( Player target : Bukkit.getOnlinePlayers() ) {
            CraftPlayer craftHandle = ( ( CraftPlayer ) target );
            PlayerConnection playerConnection = craftHandle.getHandle().playerConnection;

            if( target.equals( player ) ) {
                playerConnection.sendPacket( remove );
                playerConnection.sendPacket( add );
                playerConnection.sendPacket( playOutRespawn );

                playerConnection.sendPacket( playOutPosition );

                craftHandle.updateScaledHealth();
                target.updateInventory();
                continue;
            }
            if( target.getWorld().equals( player.getWorld() ) && target.canSee( player ) && player.isOnline() ) {
                playerConnection.sendPacket( destroy );
                playerConnection.sendPacket( remove );
                playerConnection.sendPacket( add );
                playerConnection.sendPacket( spawn );
            } else {
                playerConnection.sendPacket( remove );
                playerConnection.sendPacket( add );
            }
        }

    }
}
