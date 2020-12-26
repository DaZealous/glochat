package net.glochat.dev.utils;

import net.glochat.dev.models.PicPostModel;

import java.util.ArrayList;

public class FetchAllPosts {
    public static ArrayList<PicPostModel> picPost = getPosts();

    private static ArrayList<PicPostModel> getPosts() {
        ArrayList<PicPostModel> post = new ArrayList<>();
        ArrayList<String> ne = new ArrayList<>();
        ne.add("add");
        ne.add("add");
        ne.add("add");
        ne.add("add");
        post.add(new PicPostModel("a", ne, "hello guys, i welcome you all to GloChat", "zealous", "image", "0", System.currentTimeMillis(), "liked", "2400", "278"));
        post.add(new PicPostModel("b", ne, "Hi, so nice to be here.", "adewale", "image", "1", System.currentTimeMillis(), "liked", "3500000", "278000"));
      //  post.add(new PicPostModel(new SharedPref(this).getUserId(), selectedImages, editCaption.getText().toString(), new SharedPref(PostPicWithCap.this).getUsername(), new SharedPref(PostPicWithCap.this).getImage_url(), "1", System.currentTimeMillis(), "false", "2400", "278"));
        return post;
    }
}
