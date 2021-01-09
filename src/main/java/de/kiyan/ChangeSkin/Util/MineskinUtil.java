package de.kiyan.ChangeSkin.Util;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class MineskinUtil {
    public CompletableFuture< JSONObject > mineskinUpload( BufferedImage skinImage ) throws IOException {
        return CompletableFuture.supplyAsync( () -> {
            byte[] imageBuffer = new byte[ 0 ];

            try( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
                ImageIO.write( skinImage, "png", baos );

                baos.flush();
                imageBuffer = baos.toByteArray();
            } catch( IOException e ) {
                e.printStackTrace();
            }
            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost( "https://api.mineskin.org/generate/upload?visibility=1" );
            post.setEntity( MultipartEntityBuilder.create().addBinaryBody( "file", imageBuffer, ContentType.create( "image/png" ), "" ).build() );

            JSONObject object = null;
            try {
                object = new JSONObject( EntityUtils.toString( client.execute( post ).getEntity() ) );
                client.close();
            } catch( IOException e ) {
                e.printStackTrace();
            }
            return object;
        } );
    }
}
