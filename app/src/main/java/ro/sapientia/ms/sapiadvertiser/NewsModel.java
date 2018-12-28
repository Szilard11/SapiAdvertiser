package ro.sapientia.ms.sapiadvertiser;

public class NewsModel {

    private String mTitle;
    private String mDescription;
    private Integer mCounter;
    private String mProfileImage;
    private String mImage;
    private String mUserId;
    private String mNewsId;

    public NewsModel(String title, String description, Integer counter, String profileImage, String image, String userId, String newsId) {
        this.mTitle = title;
        this.mDescription = description;
        this.mCounter = counter;
        this.mProfileImage = profileImage;
        this.mImage = image;
        this.mUserId = userId;
        this.mNewsId = newsId;
    }
    public NewsModel()
    {
        
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmCounter(Integer mCounter) {
        this.mCounter = mCounter;
    }

    public void setmProfileImage(String mProfileImage) {
        this.mProfileImage = mProfileImage;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmNewsId() {
        return mNewsId;
    }

    public void setmNewsId(String mNewsId) {
        this.mNewsId = mNewsId;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public Integer getmCounter() {
        return mCounter;
    }

    public String getmProfileImage() {
        return mProfileImage;
    }

    public String getmImage() {
        return mImage;
    }
}
