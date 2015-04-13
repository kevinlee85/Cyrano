/**
 * CLASS: ItemGroup
 *   A group of troubleshooting items.
 */

package com.cjcornell.cyrano;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cjcornell.cyrano.data.AppSettings;

public class ScriptItem {
     int id;
     String type;
     String name;
     String description;
     String privacyLevel;
     String sharingType;
     String image;
     String createDate;
     String updateDate;
     String preloadFlag;
    
    /** Constructor */
    public ScriptItem(JSONObject json) throws JSONException {
       
        this.id = json.getInt(Constants.KEY_SCRIPT_ID);
        this.type = json.getString(Constants.KEY_SCRIPT_TYPE);
        this.name = json.getString(Constants.KEY_SCRIPT_NAME);
        this.description = json.getString(Constants.KEY_SCRIPT_DESCRIPTION);
        this.privacyLevel = json.getString(Constants.KEY_SCRIPT_PRIVACY_LEVEL);
        this.sharingType = json.getString(Constants.KEY_SCRIPT_SHARING_TYPES);
        this.image = json.getString(Constants.KEY_SCRIPT_IMAGE);
        this.createDate = json.getString(Constants.KEY_SCRIPT_CREATE_DATE);
        this.updateDate = json.getString(Constants.KEY_SCRIPT_UPDATE_DATE);
        this.preloadFlag = json.getString(Constants.KEY_SCRIPT_PRELOAD_FLAG);
        Log.v("BHUPINDER", "BHUPINDER ID"+id);
    }


    public int getId() {
       
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrivacyLevel() {
        return privacyLevel;
    }

    public String getSharingType() {
        return sharingType;
    }

    public String getImage() {
        return image;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getPreloadFlag() {
        return preloadFlag;
    }
    
    public void play(ActivityCyrano activity) {
        // Do not play any audio if the tsAudio setting is off
        if (AppSettings.tsAudio){ 
            AudioMethods.textToSpeech(activity, description, activity);
        }
    }
    
    public void stop() {
        AudioMethods.stopTextToSpeech();
    }
    
    public void pause() {
        AudioMethods.pauseMediaPlayer();
    }
    
}
