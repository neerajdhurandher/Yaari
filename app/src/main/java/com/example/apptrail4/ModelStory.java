package com.example.apptrail4;

public class ModelStory {

    private String image_url;
    private long time_start;
    private long time_end;
    private String story_Id;
    private String user_Id;

    public ModelStory(String image_url, long time_start, long time_end, String story_Id, String user_Id) {
        this.image_url = image_url;
        this.time_start = time_start;
        this.time_end = time_end;
        this.story_Id = story_Id;
        this.user_Id = user_Id;
    }

    public ModelStory() {
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getTime_start() {
        return time_start;
    }

    public void setTime_start(long time_start) {
        this.time_start = time_start;
    }

    public long getTime_end() {
        return time_end;
    }

    public void setTime_end(long time_end) {
        this.time_end = time_end;
    }

    public String getStory_Id() {
        return story_Id;
    }

    public void setStory_Id(String story_Id) {
        this.story_Id = story_Id;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }
}
