/**
 * CLASS: FacebookProfileDownloader
 *   This class is an asynchronous task used to download Facebook profile pictures
 */

package com.cjcornell.cyrano;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


public class FacebookProfileDownloader extends AsyncTask<String, Void, Bitmap>
{
 
    private static final String TAG = "Facebook downloader:";

    /**
     * The task to run in the background - returns a Bitmap of the facebook picture
     */
    @Override
    protected Bitmap doInBackground(String... params) 
    {
        Bitmap picture = null;
        String id = params[0];
        URL imgURL= null;
       
        Log.v("image url.....", id+"this is a url");
        try {
            imgURL = new URL("https://graph.facebook.com/"+id+"/picture?type=large");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
         try {
            picture = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }
             
        return picture;
    }
    
}
