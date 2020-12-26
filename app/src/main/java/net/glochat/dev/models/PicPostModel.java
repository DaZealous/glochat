package net.glochat.dev.models;

import java.util.ArrayList;
//import com.google.gson.annotations.SerializedName;

public class PicPostModel {
    //@SerializedName("postid")
    private String postId;
    //@SerializedName("postImage")
    private ArrayList<String> postImage;
    //@SerializedName("postbody")
    private String postBody;
    //@SerializedName("postedbyname")
    private String posterName;
    //@SerializedName("postedbyprofileimage")
    private String posterProfileImage;
   // @SerializedName("postedbyverified")
    private String posterVerified;
    //@SerializedName("postDate")
    private Long postDate;
   // @SerializedName("like_status")
    private String likeStatus;
   // @SerializedName("Like")
    private String likes;
  //  @SerializedName("postcommentNum")
    private String postcommentNum;

    public PicPostModel(String postId, ArrayList<String> postImage, String postBody, String posterName, String posterProfileImage, String posterVerified, Long postDate, String likeStatus, String likes, String postcommentNum) {
        this.postId = postId;
        this.postImage = postImage;
        this.postBody = postBody;
        this.posterName = posterName;
        this.posterProfileImage = posterProfileImage;
        this.posterVerified = posterVerified;
        this.postDate = postDate;
        this.likeStatus = likeStatus;
        this.likes = likes;
        this.postcommentNum = postcommentNum;
    }

    public String getPostcommentNum() {
        return postcommentNum;
    }

    public void setPostcommentNum(String postcommentNum) {
        this.postcommentNum = postcommentNum;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPostBody() {
        return postBody;
    }

    public String getPosterName() {
        return posterName;
    }

    public String getPosterProfileImage() {
        return posterProfileImage;
    }

    public String getPosterVerified() {
        return posterVerified;
    }

    public Long getPostDate() {
        return postDate;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public String getLikes() {
        return likes;
    }

    public String getPostId() {
        return postId;
    }

    public ArrayList<String> getPostImage() {
        return postImage;
    }

}