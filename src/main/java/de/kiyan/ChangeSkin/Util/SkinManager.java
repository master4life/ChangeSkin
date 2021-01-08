package de.kiyan.ChangeSkin.Util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.kiyan.ChangeSkin.Config;
import de.kiyan.ChangeSkin.Main;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R2.PacketPlayOutRespawn;
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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SkinManager {

    public static void reloadSkinForSelf( Player player, String value, String signature ) {
        GameProfile gProfile = ( ( CraftPlayer ) player ).getProfile();
        gProfile.getProperties().removeAll( "textures" );
        gProfile.getProperties().put( "textures", new Property( "textures", value, signature ) );

        for( Player bystander : Bukkit.getOnlinePlayers() )
            ( ( CraftPlayer ) bystander ).getHandle().playerConnection.sendPacket( new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ( ( CraftPlayer ) player ).getHandle() ) );

        EntityPlayer ep = ( ( CraftPlayer ) player ).getHandle();
        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep );
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo( PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep );
        ep.playerConnection.sendPacket( removeInfo );
        ep.playerConnection.sendPacket( addInfo );

    }

    public void applySkin( Player player, int type, int number ) {
        JSONObject data;
        String value_base64 = "";
        String signature = "";
        try {
            data = new MineskinUtil().mineskinUpload( changeSkin( player, type, number ) ).get();
            String string = data.get( "data" ).toString();

            JSONObject texture = new JSONObject( string );
            String string2 = texture.get( "texture" ).toString();

            JSONObject value = new JSONObject( string2 );
            value_base64 = value.get( "value" ).toString();
            signature = value.get( "signature" ).toString();

        } catch( Exception e ) {
            e.printStackTrace();
        }
        reloadSkinForSelf( player, value_base64, signature );
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
