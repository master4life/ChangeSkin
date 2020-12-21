package de.kiyan.ChangeSkin.Util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.kiyan.ChangeSkin.Config;
import de.kiyan.ChangeSkin.Main;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class SkinManager {
    public static void reloadSkinForSelf(Player player, String value, String signature) {
        GameProfile gProfile = ((CraftPlayer) player).getProfile();
        gProfile.getProperties().removeAll("textures");
        gProfile.getProperties().put("textures", new Property("textures", value, signature));

        for (Player bystander : Bukkit.getOnlinePlayers())
            ((CraftPlayer) bystander).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));

        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        Location loc = player.getLocation().clone();
        ep.playerConnection.sendPacket(removeInfo);
        ep.playerConnection.sendPacket(addInfo);
        player.teleport( new Config().getLocation() );
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                ep.playerConnection.sendPacket(new PacketPlayOutRespawn(
                        ep.getWorld().getDimensionManager(),
                        ep.getWorld().getDimensionKey(),
                        Bukkit.getWorld( ep.getWorld().getWorld().getName()).getSeed(),
                        ep.playerInteractManager.getGameMode(),
                        ep.playerInteractManager.getGameMode(),
                        false,
                        false,
                        true));
                player.updateInventory();
            }
        }.runTaskLater( Main.getInstance(), 4L);
    }

    /*public void reloadSkinForSelf( Player player, String value, String signature ) {
        GameProfile gProfile = ( ( CraftPlayer ) player ).getProfile();
        gProfile.getProperties().removeAll("textures" );
        gProfile.getProperties().put( "textures", new Property("textures", value, signature ) );

        final EntityPlayer ePlayer = ( ( CraftPlayer ) player).getHandle();
        final PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ePlayer );
        final PacketPlayOutEntityDestroy removeEntity = new PacketPlayOutEntityDestroy( new int[] { player.getEntityId() } );
        final PacketPlayOutNamedEntitySpawn addNamed = new PacketPlayOutNamedEntitySpawn( ePlayer );
        final PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ePlayer );
        final PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
                ePlayer.getWorld().getDimensionManager(),
                ePlayer.getWorld().getDimensionKey(),
                Bukkit.getWorld( ePlayer.getWorld().getWorld().getName()).getSeed(),
                ePlayer.playerInteractManager.getGameMode(),
                ePlayer.playerInteractManager.getGameMode(),
                false,
                false,
                true);
        ePlayer.playerConnection.sendPacket( removeInfo );
        ePlayer.playerConnection.sendPacket( removeEntity );
        ePlayer.playerConnection.sendPacket( addNamed );
        ePlayer.playerConnection.sendPacket( addInfo );
        ePlayer.playerConnection.sendPacket( respawn );
        player.updateInventory();
    }*/

    public void applySkin( Player player, int type )
    {
        JSONObject data = null;
        String value_base64 = "";
        String signature = "";
        try
        {
            data = MineskinUtil.mineskinUpload( changeSkin( player, type ) );
            String string = data.get( "data" ).toString( );

            JSONObject texture = new JSONObject( string );
            String string2 = texture.get( "texture" ).toString( );

            JSONObject value = new JSONObject( string2 );
            value_base64 = value.get( "value" ).toString( );
            signature = value.get( "signature" ).toString( );

        } catch( IOException e ) { e.printStackTrace( ); }
        reloadSkinForSelf( player, value_base64, signature );
    }

    public BufferedImage changeSkin(Player p, int type ) throws IOException
    {
        BufferedImage originalSkin = getPlayerSkin( p );
        BufferedImage biOverlay = null;
        try
        {
            if( type == 0)
                biOverlay = ImageIO.read( new File( Main.getInstance().getDataFolder(), "prisoner.png") );
            else
                biOverlay = ImageIO.read( new File( Main.getInstance().getDataFolder(), "guard.png") );
        } catch( IOException e )
        {
            e.printStackTrace( );
        }

        // Combine both skins
        BufferedImage newSkin = joinSkins( originalSkin, biOverlay );

        return newSkin;
    }

    public BufferedImage getPlayerSkin( Player player )
    {
        EntityPlayer p = ( ( CraftPlayer ) player ).getHandle( );
        GameProfile profile = p.getProfile( );
        Property property = profile.getProperties( ).get( "textures" ).iterator( ).next( );
        String texture = property.getValue( );

        byte[] test = Base64.getDecoder( ).decode( texture );
        JsonObject skin = new JsonParser( ).parse( new String( test ) ).getAsJsonObject( ).get( "textures" )
                .getAsJsonObject( ).get( "SKIN" ).getAsJsonObject( );

        BufferedImage originalSkin = null;
        // Go to the URL and get the skin
        try
        {
            originalSkin = ImageIO.read( new URL( skin.get( "url" ).getAsString( ) ) );
        } catch( MalformedURLException e )
        {
            // URL Invalid
            e.printStackTrace( );
        } catch( IOException e )
        {
            // Failed
            e.printStackTrace( );
        }

        return originalSkin;
    }

    // Get the joined skin
    public BufferedImage joinSkins( BufferedImage biSkin, BufferedImage biOverlay )
    {

        int width = Math.max( biSkin.getWidth( ), biOverlay.getWidth( ) );
        int height = Math.max( biSkin.getHeight( ), biOverlay.getHeight( ) );

        // The combined image
        BufferedImage combinedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        // Do the combining and stuff
        Graphics graph = combinedImage.getGraphics( );
        graph.drawImage( biSkin, 0, 0, null );
        graph.drawImage( biOverlay, 0, 0, null );
        graph.dispose( );
        return combinedImage;
    }
}
