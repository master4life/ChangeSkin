package de.kiyan.ChangeSkin.Util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.kiyan.ChangeSkin.Main;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SkinManager {

/*
    public void reloadSkinForSelf( Player player, String value, String signature ) {
        CraftPlayer p = (CraftPlayer) player;
        GameProfile gProfile = ( ( CraftPlayer ) p ).getProfile();

        // Reset everything except the textures.
        gProfile.getProperties().removeAll( "textures" );
        gProfile.getProperties().put("texture", new Property( "textures", value, signature ));

        EnumWrappers.NativeGameMode gamemode = EnumWrappers.NativeGameMode.fromBukkit( p.getGameMode() );
        WrappedChatComponent displayName = WrappedChatComponent.fromText( p.getPlayerListName() );
        PlayerInfoData playerInfoData = new PlayerInfoData( WrappedGameProfile.fromPlayer( p ), 0, gamemode, displayName );

        // Remove the old skin - client updates it only on a complete remove and add.
        PacketContainer removeInfo = new PacketContainer( PacketType.Play.Server.PLAYER_INFO );
        removeInfo.getPlayerInfoAction().write( 0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER );
        removeInfo.getPlayerInfoDataLists().write( 0, Collections.singletonList( playerInfoData ) );

        // Add info containing the skin data.
        PacketContainer addInfo = removeInfo.deepClone();
        addInfo.getPlayerInfoAction().write( 0, EnumWrappers.PlayerInfoAction.ADD_PLAYER );

        // Respawn packet - notify the client that it should update the own skin.
        EnumWrappers.Difficulty difficulty = EnumWrappers.getDifficultyConverter().getSpecific( p.getWorld().getDifficulty() );

        PacketContainer respawn = new PacketContainer( PacketType.Play.Server.RESPAWN );
        respawn.getIntegers().write( 0, p.getWorld().getEnvironment().getId() );
        respawn.getDifficulties().write( 0, difficulty );
        respawn.getGameModes().write( 0, gamemode );
        respawn.getWorldTypeModifier().write( 0, p.getWorld().getWorldType() );

        Location location = p.getLocation().clone();

        // Prevent the moved too quickly message.
        PacketContainer teleport = new PacketContainer( PacketType.Play.Server.POSITION );
        teleport.getModifier().writeDefaults();
        teleport.getDoubles().write( 0, location.getX() );
        teleport.getDoubles().write( 1, location.getY() );
        teleport.getDoubles().write( 2, location.getZ() );
        teleport.getFloat().write( 0, location.getYaw() );
        teleport.getFloat().write( 1, location.getPitch() );
        // Send an invalid teleport id in order to let Bukkit ignore the incoming confirm packet.
        teleport.getIntegers().writeSafely( 0, -1337 );

        sendPackets( player, removeInfo, addInfo, respawn, teleport );
    }

    private void sendPackets( Player player, PacketContainer... packets ) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for( PacketContainer packet : packets ) {
            try {
                protocolManager.sendServerPacket( player, packet );
            } catch( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
    }

 */

    private void reloadSkinForSelf( Player player, String value, String signature ) {
        GameProfile gProfile = ( ( CraftPlayer ) player ).getProfile();
        gProfile.getProperties().removeAll( "textures" );
        gProfile.getProperties().put( "textures", new Property( "textures", value, signature ) );

        EntityPlayer entityPlayer = ( ( CraftPlayer ) player ).getHandle();
        Location location = player.getLocation();

        // Recreate player
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer );
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy( entityPlayer.getId() );
        PacketPlayOutNamedEntitySpawn playOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn( entityPlayer );
        PacketPlayOutPlayerInfo playOutPlayerInfoAdd = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer );

        // World
        PlayerInteractManager playerInteractManager = entityPlayer.playerInteractManager;
        EnumGamemode enumGamemode = playerInteractManager.getGameMode();

        PacketPlayOutRespawn packetPlayOutRespawn = new PacketPlayOutRespawn( entityPlayer.getWorld().getDimensionManager(), entityPlayer.getWorld().getDimensionKey(), player.getWorld().getSeed(), enumGamemode, enumGamemode, false, false, true );
        PacketPlayOutPosition packetPlayOutPosition = new PacketPlayOutPosition( location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), new HashSet<>(), 0 );

        // Inventory
        List< Pair< EnumItemSlot, ItemStack > > playerInventory = new ArrayList<>();
        playerInventory.add( new Pair<>( EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy( player.getInventory().getItemInMainHand() ) ) );
        playerInventory.add( new Pair<>( EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy( player.getInventory().getItemInOffHand() ) ) );
        playerInventory.add( new Pair<>( EnumItemSlot.HEAD, CraftItemStack.asNMSCopy( player.getInventory().getHelmet() ) ) );
        playerInventory.add( new Pair<>( EnumItemSlot.CHEST, CraftItemStack.asNMSCopy( player.getInventory().getChestplate() ) ) );
        playerInventory.add( new Pair<>( EnumItemSlot.LEGS, CraftItemStack.asNMSCopy( player.getInventory().getLeggings() ) ) );
        playerInventory.add( new Pair<>( EnumItemSlot.FEET, CraftItemStack.asNMSCopy( player.getInventory().getBoots() ) ) );

        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment( player.getEntityId(), playerInventory );
        PacketPlayOutHeldItemSlot packetPlayOutHeldItemSlot = new PacketPlayOutHeldItemSlot( player.getInventory().getHeldItemSlot() );

        // Update player for all players
        for( Player onlinePlayer : Bukkit.getServer().getOnlinePlayers() ) {
            CraftPlayer craftPlayer = ( ( CraftPlayer ) onlinePlayer );
            PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;

            if( onlinePlayer.equals( player ) ) {
                playerConnection.sendPacket( packetPlayOutPlayerInfo );
                playerConnection.sendPacket( playOutPlayerInfoAdd );
                playerConnection.sendPacket( packetPlayOutRespawn );
                playerConnection.sendPacket( packetPlayOutPosition );
                playerConnection.sendPacket( packetPlayOutHeldItemSlot );

                craftPlayer.updateScaledHealth();
                entityPlayer.updateInventory( craftPlayer.getHandle().activeContainer );
            } else if( onlinePlayer.getWorld().equals( player.getWorld() ) && onlinePlayer.canSee( player ) && player.isOnline() ) {
                playerConnection.sendPacket( packetPlayOutEntityDestroy );
                playerConnection.sendPacket( packetPlayOutPlayerInfo );
                playerConnection.sendPacket( playOutPlayerInfoAdd );
                playerConnection.sendPacket( playOutNamedEntitySpawn );
                playerConnection.sendPacket( packetPlayOutEntityEquipment );
            } else {
                playerConnection.sendPacket( packetPlayOutPlayerInfo );
                playerConnection.sendPacket( playOutPlayerInfoAdd );
            }
        }
    }

    public void applySkin( Player player, int type, int number ) {
        try {
            new MineskinUtil().mineskinUpload( changeSkin( player, type, number ) ).whenComplete( ( JSONObject, throwable ) -> {

                String value_base64 = "";
                String signature = "";
                String string = JSONObject.get( "data" ).toString();

                JSONObject texture = new JSONObject( string );
                String string2 = texture.get( "texture" ).toString();

                JSONObject value = new JSONObject( string2 );
                value_base64 = value.get( "value" ).toString();
                signature = value.get( "signature" ).toString();

                reloadSkinForSelf( player, value_base64, signature );

            } );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public BufferedImage changeSkin( Player p, int type, int number ) throws IOException {
        BufferedImage originalSkin = getPlayerSkin( p );
        BufferedImage biOverlay = null;
        try {
            if( type == 0 )
                biOverlay = ImageIO.read( new File( Main.getInstance().getDataFolder(), "prisoner.png" ) );
            else
                biOverlay = ImageIO.read( new File( Main.getInstance().getDataFolder(), "guard.png" ) );
        } catch( IOException e ) {
            e.printStackTrace();
        }

        // Combine both skins
        BufferedImage newSkin = joinSkins( originalSkin, biOverlay, number );

        return newSkin;
    }

    public BufferedImage getPlayerSkin( Player player ) {
        EntityPlayer p = ( ( CraftPlayer ) player ).getHandle();
        GameProfile profile = p.getProfile();
        Property property = profile.getProperties().get( "textures" ).iterator().next();
        String texture = property.getValue();

        byte[] test = Base64.getDecoder().decode( texture );
        JsonObject skin = new JsonParser().parse( new String( test ) ).getAsJsonObject().get( "textures" )
                .getAsJsonObject().get( "SKIN" ).getAsJsonObject();

        BufferedImage originalSkin = null;
        // Go to the URL and get the skin
        try {
            originalSkin = ImageIO.read( new URL( skin.get( "url" ).getAsString() ) );
        } catch( Exception e ) {
            // URL Invalid
            e.printStackTrace();
        }

        return originalSkin;
    }

    // Get the joined skin
    public BufferedImage joinSkins( BufferedImage biSkin, BufferedImage biOverlay, int numbers ) {
        int width = Math.max( biSkin.getWidth(), biOverlay.getWidth() );
        int height = Math.max( biSkin.getHeight(), biOverlay.getHeight() );

        // The combined image
        BufferedImage combinedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        combinedImage = new Digits().drawNumber( combinedImage, new Digits().getNumbers( numbers ) );

        // Do the combining and stuff
        Graphics graph = combinedImage.getGraphics();

        graph.drawImage( biSkin, 0, 0, null );
        graph.drawImage( biOverlay, 0, 0, null );
        graph.dispose();
        return combinedImage;
    }
}
