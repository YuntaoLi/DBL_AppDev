package group14.foodfetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s141680 on 27-3-2017.
 */

abstract class User {

    private String usrname;
    private String usrid;
    private boolean isDoner;
    private List<Publish> publishList = new ArrayList<Publish>();


    //set functions should be filled

    protected void setDoner(boolean doner) {
        isDoner = doner;
    }

     protected void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    protected void setUsrname(String usrname) {
        this.usrname = usrname;
    }

    protected void setPublish(Publish publish) {
        publishList.add(publish);
    }

    //get functions should be filled
    protected String getUsrid() {
        return usrid;
    }

    protected String getUsrname() {
        return usrname;
    }

    protected boolean isDoner(){
        return isDoner;
    };

    protected List<Publish> getPublish() {
        return publishList;
    }
}
