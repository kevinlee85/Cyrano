package com.cjcornell.cyrano;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjcornell.cyrano.ImageDownload.ImageLoader;

public class Customadaptor extends BaseAdapter
{
   
List<BluetoothFriend> f1=new ArrayList<BluetoothFriend>();
ImageLoader img;
private static LayoutInflater inflater=null;
Context c;
    public Customadaptor(ActivityCyrano activityCyrano, List<BluetoothFriend> friends) {
        // TODO Auto-generated constructor stub
        f1.addAll(friends);
        c=activityCyrano;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        img=new ImageLoader(activityCyrano);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return f1.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi=convertView;
        
        /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
        vi = inflater.inflate(R.layout.layout_list2, null);
         
        /****** View Holder Object to contain tabitem.xml file elements ******/
        TextView text = (TextView) vi.findViewById(R.id.listTextView);
        ImageView im=(ImageView)vi.findViewById(R.id.imageoffriends);
        text.setText(f1.get(position)+"");
        //  picture = new FacebookProfileDownloader().execute(f1.get(position).getId()).get();
         // im.setImageBitmap(picture);
       String Url="https://graph.facebook.com/"+f1.get(position).getId()+"/picture?type=large";
        img.DisplayImage(Url, im);
      
   
    //DisplayImage function from ImageLoader Class
    
    
    /******** Set Item Click Listner for LayoutInflater for each row ***********/
    return vi;
    }

}
