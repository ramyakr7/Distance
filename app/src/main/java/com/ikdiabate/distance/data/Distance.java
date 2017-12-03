package com.ikdiabate.distance.data;

/**
 * Model class for video retrieval.
 *
 * @Author: Ibrahim Diabate
 * @Version: November 2017
 */
public class Distance {

    //Video id
    private String mvideoid;
    // Title of the video
    private String mtitle;
    //Description of the video
    private String mdescription;
    // Date video was published
    private String mPublishDate;
    // Video thumbnail
    private String mthumbnail;
    //Title of video's channel
    private String mChannelTitle;

    //For viewcount -- Added by Ramya Kumar - 11/27/17
    private Integer mViewCount;

    // Constructor
    public Distance(String videoid, String title, String description, String publishDate, String thumbnail, String channelTitle) {
        mvideoid = videoid;
        mtitle = title;
        mdescription = description;
        mPublishDate = publishDate;
        mthumbnail = thumbnail;
        mChannelTitle = channelTitle;
    }

    public Distance (String videoid, Integer viewcount){
        this.mvideoid = videoid;
        this.mViewCount = viewcount;
    }
    public Integer getViewcount(){
        return mViewCount;
    }
     //Getters and setters
    public  String getVideoid(){
        return mvideoid;
    }
    public String getTitle() {return mtitle;}
    public String getDescription() {
        return mdescription;
    }
    public String getPublishedDate() {return mPublishDate;}
    public String getThumbnail() {return mthumbnail;}
    public String getChannelTitle() {
        return mChannelTitle;
    }
}
