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

public class MineskinUtil
{
	public static JSONObject mineskinUpload( BufferedImage skinImage ) throws IOException
	{
		byte[] imageBuffer; // Declare the buffer for the raw image bytes

		try( ByteArrayOutputStream baos = new ByteArrayOutputStream( ) )
		{
			ImageIO.write( skinImage, "png", baos ); // Write the image in PNG format to the OutputStream

			baos.flush( );
			imageBuffer = baos.toByteArray( ); // Initialize the buffer with the raw PNG image data
		}

		CloseableHttpClient client = HttpClientBuilder.create( ).build( ); // Create a new HttpClient

		HttpPost post = new HttpPost( "https://api.mineskin.org/generate/upload?visibility=1" ); // Create the Empty
		// POST request
		// Here we write the image to the Request payload
		post.setEntity( MultipartEntityBuilder.create( )
				// Mineskin looks for the parameter "file". The data is a PNG image. We don't
				// need to supply a filename.
				.addBinaryBody( "file", imageBuffer, ContentType.create( "image/png" ), "" ).build( ) );

		// Execute the POST request and parse the result as a JSON object
		JSONObject object = new JSONObject( EntityUtils.toString( client.execute( post ).getEntity( ) ) );

		client.close( );

		return object;
	}
}
