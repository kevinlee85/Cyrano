/**
 * CLASS: Item
 *   A troubleshooting item. This class represents various attributes of a troubleshooting
 *   item, as well as what is allowed to do to it. There are no setters, because it should
 *   be initialized directly from the database.
 */

package com.cjcornell.cyrano;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.cjcornell.cyrano.data.AppSettings;

public class Command {
    // Basic attributes of a troubleshooting attribute
    private int id, scriptId, commandId, number;
    private String name, description, type, image, fileName, url;
    private double delay; // Time delay
    private int triggerFlag, stopEnabled, pauseEnabled, next, previous;
    private String branchToRec1, branchToLabel1, branchToRec2, branchToLabel2, branchToRec3, branchToLabel3,
            branchToRec4, branchToLabel4;
    private int labelOrientation;
    private int other;
    private ScriptItem parent;
    private boolean first;
    private boolean last;

    /*
     * "id": 1, "scriptId": 23, "command_id": null, "command_num": 5,
     * "command_name": "gg", "command_desc": "hgj ghjg jjgh", "command_type":
     * "gff", "command_image": "uyy.img", "command_filename": "yttre.txt",
     * "command_url": "http:\/\/rrtt.com", "command_delay": 34,
     * "command_trigger_flag": 1, "command_stop_enabled": 1,
     * "command_pause_enabled": 1, "cfNext": 1, "cfPrevious": 1,
     * "command_branchto_rec_1": "1", "command_branchto_label_1": "1",
     * "command_branchto_rec_2": "1", "command_branchto_label_2": "1",
     * "command_branchto_rec_3": "1", "command_branchto_label_3": "1",
     * "command_branchto_rec_4": "1", "command_branchto_label_4": "1",
     * "command_label_orientation": 1, "other": 3
     */

    /** Constructor */
    public Command(JSONObject json, boolean isLast, ScriptItem parent) throws JSONException {

        // Initialize the basic attributes
        try {
            this.id = json.getInt(Constants.KEY_COMMAND_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            this.id = 0;
        }

        try {
            this.triggerFlag = json.getInt(Constants.KEY_COMMAND_TRIGGER_FLAG);
        } catch (JSONException e) {
            e.printStackTrace();
            this.triggerFlag = 0;
        }

        try {
            this.stopEnabled = json.getInt(Constants.KEY_COMMAND_STOP_ENABLED);
        } catch (JSONException e) {
            e.printStackTrace();
            this.stopEnabled = 0;
        }

        try {
            this.pauseEnabled = json.getInt(Constants.KEY_COMMAND_PAUSE_ENABLED);
        } catch (JSONException e) {
            e.printStackTrace();
            this.pauseEnabled = 0;
        }

        try {
            this.next = json.getInt(Constants.KEY_COMMAND_NEXT);
        } catch (JSONException e) {
            e.printStackTrace();
            this.next = 0;
        }

        try {
            this.previous = json.getInt(Constants.KEY_COMMAND_PREVIOUS);
        } catch (JSONException e) {
            e.printStackTrace();
            this.previous = 0;
        }

        try {
            this.scriptId = json.getInt(Constants.KEY_COMMAND_SCRIPT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            this.scriptId = 0;
        }

        try {
            this.commandId = json.getInt(Constants.KEY_COMMAND_COMMAND_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            this.commandId = 0;
        }

        try {
            this.number = json.getInt(Constants.KEY_COMMAND_NUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
            this.number = 0;
        }

        try {
            this.other = json.getInt(Constants.KEY_COMMAND_OTHER);
        } catch (JSONException e) {
            e.printStackTrace();
            this.other = 0;
        }

        try {
            this.labelOrientation = json.getInt(Constants.KEY_COMMAND_LABEL_ORIENTATION);
        } catch (JSONException e) {
            e.printStackTrace();
            this.labelOrientation = 0;
        }

        this.name = json.getString(Constants.KEY_COMMAND_NAME);
        this.description = json.getString(Constants.KEY_COMMAND_DESCRIPTION);
        this.type = json.getString(Constants.KEY_COMMAND_TYPE);
        this.image = json.getString(Constants.KEY_COMMAND_IMAGE);
        this.fileName = json.getString(Constants.KEY_COMMAND_FILENAME);
        this.url = json.getString(Constants.KEY_COMMAND_URL);
        this.delay = json.getDouble(Constants.KEY_COMMAND_DELAY);
        this.branchToRec1 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_REC_1);
        this.branchToLabel1 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_LABEL_1);
        this.branchToRec2 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_REC_2);
        this.branchToLabel2 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_LABEL_2);
        this.branchToRec3 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_REC_3);
        this.branchToLabel3 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_LABEL_3);
        this.branchToRec4 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_REC_4);
        this.branchToLabel4 = json.getString(Constants.KEY_COMMAND_BRANCH_TO_LABEL_4);

        // Initialize the control flag variables - this is first if we don't
        // have a parent
        this.first = (number == 1);
        this.last = isLast;

        /*
         * this.stoppable = json.getInt("cfStop"); this.pausable =
         * json.getInt("cfPause"); this.canAdvance = json.getInt("cfNext");
         * this.canGoBack = json.getInt("cfPrevious");
         */

        this.parent = parent;

        // Add the branches
        /*
         * this.branches = new ArrayList<Branch>(); JSONArray branching =
         * json.getJSONArray("branching"); for (int i = 0; i <
         * branching.length(); ++i) { try { Branch tmp = new
         * Branch(branching.getJSONObject(i)); if (tmp.label != null) {
         * this.branches.add(tmp); } else { this.branches.add(null); } } catch
         * (JSONException e) { this.branches.add(null); } }
         */
    }

    /** Getters */
    public int getId() {
        return id;
    }

    public int getScriptId() {
        return scriptId;
    }

    public int getCommandId() {
        return commandId;
    }

    public int getNumber() {
        return number;
    }

    public String getImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public boolean getTriggerFlag() {
        return triggerFlag > 0;
    }

    public boolean getStopEnabled() {
        return stopEnabled > 0;
    }

    public boolean getPauseEnabled() {
        return pauseEnabled > 0;
    }

    public boolean getNext() {
        return next > 0;
    }

    public boolean getPrevious() {
        return previous > 0;
    }

    public String getBranchToRec1() {
        return branchToRec1;
    }

    public String getBranchToLabel1() {
        return branchToLabel1;
    }

    public String getBranchToRec2() {
        return branchToRec2;
    }

    public String getBranchToLabel2() {
        return branchToLabel2;
    }

    public String getBranchToRec3() {
        return branchToRec3;
    }

    public String getBranchToLabel3() {
        return branchToLabel3;
    }

    public String getBranchToRec4() {
        return branchToRec4;
    }

    public String getBranchToLabel4() {
        return branchToLabel4;
    }

    public int getLabelOrientation() {
        return labelOrientation;
    }

    public int getOther() {
        return other;
    }

    public ScriptItem getParent() {
        return parent;
    }

    public boolean getFirst() {
        return first;
    }

    public String getGroupName() {
        return parent.getName();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getFilename() {
        return fileName;
    }

    public String getURL() {
        return url;
    }

    /*
     * public List<Branch> getBranches() { return branches; } public boolean
     * hasBranches() { return branches.size() > 0; }
     */

    public double getDelay() {
        return delay;
    }

    /** Play controls methods */
    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isStoppable() {
        return stopEnabled > 0;
    }

    public boolean isPausable() {
        return pauseEnabled > 0;
    }

    public boolean isPlayable() {
        return isStoppable() || isPausable();
    }

    public boolean canGoBack() {
        return previous > 0;
    }

    public boolean canAdvance() {
        // can't advance if you're last
        Log.e("command", "canadvance");
        if (isLast()){
            Log.i("command", "canadvance return false");
            return false;
        }
        return next > 0;
       
    }

    /*
     * public boolean canAdvance() { // can't advance if you're last if
     * (isLast()) return false; if (canAdvance < 0) return parent.canAdvance();
     * return canAdvance > 0; } public boolean canGoBack() { // can't go back if
     * you're first if (isFirst()) return false; if (canGoBack < 0) return
     * parent.canGoBack(); return canGoBack > 0; }
     */

    public void play(ActivityCyrano activity) {
        // Do not play any audio if the tsAudio setting is off
        if (AppSettings.tsAudio) {
            if (this.type.equals("1")) {
                // notify upon completion so we can go to the next item, if
                // delay is 0.
                // we can check == 0, because we aren't doing any calculations
                // with it.
                 //Intent ii = new Intent(activity, TextToSpeachService.class);
                 //ii.putExtra("speach",description);
                // activity.startService(ii);
                // AudioMethods.textToSpeech(activity, description, (delay == 0)
                // ? activity : null);
                new TextToSpeachService().getInstance().textToSpeech(activity, description, (delay == 0)
                 ? activity : null);
                new TextToSpeachService().getInstance().commandcall=true;
                new TextToSpeachService().getInstance().Delay=delay;
                Log.e("CALL", "METOD CALL HO GIYA");
               // new TextToSpeachService().playtts();
            } else if (this.type.equals("2")) {
                AudioMethods.streamAudio(fileName, (delay == 0) ? activity : null);
            }
        }
    }

    public void stop() {
        if (this.type.equals("1")) {
            AudioMethods.stopTextToSpeech();
        } else if (this.type.equals("2")) {
            AudioMethods.shutdownMediaPlayer();
        }
    }

    public void pause() {
        AudioMethods.pauseMediaPlayer();
    }

}
