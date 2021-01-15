package de.kiyan.ChangeSkin.Util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.kiyan.ChangeSkin.Main;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.mineskin.MineskinClient;
import org.mineskin.Model;
import org.mineskin.SkinOptions;
import org.mineskin.Visibility;
import org.mineskin.data.Skin;
import org.mineskin.data.SkinCallback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

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
    private void updateSkin( Player player, String value, String signature ) {
        try {
            Object profile = player.getClass().getMethod( "getProfile" ).invoke( player );
            GameProfile gameProfile = ( GameProfile ) profile;
            gameProfile.getProperties().removeAll( "textures" );
            gameProfile.getProperties().put( "textures", new Property( "textures", value, signature ) );

            // Get entityPlayer
            Object entityPlayer = player.getClass().getMethod( "getHandle" ).invoke( player );
            // Get the PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER as object
            Object removeEnum = getNMSClass( "PacketPlayOutPlayerInfo$EnumPlayerInfoAction" ).getEnumConstants()[ 4 ];
            Object addEnum = getNMSClass( "PacketPlayOutPlayerInfo$EnumPlayerInfoAction" ).getEnumConstants()[ 0 ];
            Object classArray = Array.newInstance( getNMSClass( "EntityPlayer" ), 1 );
            Object[] objArray = ( Object[] ) classArray;
            objArray[ 0 ] = entityPlayer;

            // Get the constructor
            Constructor< ? > playerInfo = getNMSClass( "PacketPlayOutPlayerInfo" ).getConstructor( getNMSClass( "PacketPlayOutPlayerInfo$EnumPlayerInfoAction" ), objArray.getClass() );
            // Apply the Enum & EntityPlayer to the instance
            Object playerConnection = entityPlayer.getClass().getField( "playerConnection" ).get( entityPlayer );
            Method sendPacket = playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) );
            sendPacket.invoke( playerConnection, playerInfo.newInstance( removeEnum, objArray ) );
            sendPacket.invoke( playerConnection, playerInfo.newInstance( addEnum, objArray ) );

            Object world = entityPlayer.getClass().getMethod( "getWorld" ).invoke( entityPlayer );
            Object dimensionManager = world.getClass().getMethod( "getDimensionManager" ).invoke( world );
            Object dimensionKey = world.getClass().getMethod( "getDimensionKey" ).invoke( world );
            Object world2 = world.getClass().getMethod( "getWorld" ).invoke( world );
            Object seed = world2.getClass().getMethod( "getSeed" ).invoke( world2 );
            String stringGameMode = player.getGameMode().name();
            Method enumGamemode = getNMSClass( "EnumGamemode" ).getMethod( "valueOf", String.class );
            Object gameMode = enumGamemode.invoke( getNMSClass( "EnumGamemode" ), stringGameMode );
            Constructor< ? > OutRespawn = getNMSClass( "PacketPlayOutRespawn" ).getConstructor( dimensionManager.getClass(), dimensionKey.getClass(), long.class, getNMSClass( "EnumGamemode" ), getNMSClass( "EnumGamemode" ), boolean.class, boolean.class, boolean.class );

            sendPacket.invoke( playerConnection, OutRespawn.newInstance( dimensionManager, dimensionKey, (long) seed, gameMode, gameMode, false, false, true ) );
            player.updateInventory();
            player.getClass().getMethod( "sendHealthUpdate" ).invoke( player ); // ( ( CraftPlayer ) player ).sendHealthUpdate();
        } catch( NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException e ) {
            e.printStackTrace();
        }
    }

    public Class< ? > getNMSClass( String name ) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
        try {
            return Class.forName( "net.minecraft.server." + version + "." + name );
        } catch( ClassNotFoundException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public void applySkin( Player player, int type, int number ) {
        mineskinUpload( player, changeSkin( player, type, number ) );
    }

    public void mineskinUpload( Player player, File skinFile ) {
        MineskinClient skinClient = new MineskinClient();

        String[] skinValues = new String[ 2 ];
        skinClient.generateUpload( skinFile, SkinOptions.create( skinFile.getName().replace( ".png", "" ), Model.DEFAULT, Visibility.PRIVATE ), new SkinCallback() {
            @Override
            public void waiting( long l ) {
                player.sendMessage( "§7Waiting " + ( l / 1000D ) + "s to upload skin..." );
            }

            @Override
            public void uploading() {
                player.sendMessage( "§eYour skin is generating..." );
            }

            @Override
            public void done( Skin skin ) {
                skinValues[ 0 ] = skin.data.texture.value;
                skinValues[ 1 ] = skin.data.texture.signature;

                if( skinFile.exists() )
                    skinFile.delete();

                updateSkin( player, skinValues[ 0 ], skinValues[ 1 ] );
            }
        } );
    }

    public File changeSkin( Player p, int type, int number ) {
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

        File skin = null;
        try {
            skin = File.createTempFile( "mineskin-file", ".png" );
        } catch( IOException e ) {
            e.printStackTrace();
        }
        BufferedImage newSkin = joinSkins( originalSkin, biOverlay, number );

        try {
            ImageIO.write( newSkin, "png", skin );
        } catch( IOException e ) {
            e.printStackTrace();
        }

        return skin;
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
