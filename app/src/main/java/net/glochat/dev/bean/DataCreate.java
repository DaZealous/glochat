package net.glochat.dev.bean;


import net.glochat.dev.R;

import java.util.ArrayList;


public class DataCreate {
    public static ArrayList<VideoBe> datas = new ArrayList<>();
    public static ArrayList<VideoBe.UserBean> userList = new ArrayList<>();

    public void initData() {

        VideoBe videoBeanOne = new VideoBe();
        //videoBeanOne.setCoverRes(R.drawable.cover1);
        videoBeanOne.setContent("Heads up! watch this video");
        videoBeanOne.setVideoRes(R.raw.video1);
        videoBeanOne.setDistance(7.9f);
        videoBeanOne.setFocused(false);
        videoBeanOne.setLiked(true);
        videoBeanOne.setLikeCount(226823);
        videoBeanOne.setCommentCount(3480);
        videoBeanOne.setShareCount(4252);

        VideoBe.UserBean userBeanOne = new VideoBe.UserBean();
        userBeanOne.setUid(1);
        userBeanOne.setHead(R.drawable.head1);
        userBeanOne.setNickName("Kumasi");
        userBeanOne.setSign("");
        userBeanOne.setSubCount(119323);
        userBeanOne.setFocusCount(482);
        userBeanOne.setFansCount(32823);
        userBeanOne.setWorkCount(42);
        userBeanOne.setDynamicCount(42);
        userBeanOne.setLikeCount(821);

        userList.add(userBeanOne);
        videoBeanOne.setUserBean(userBeanOne);

        VideoBe videoBeanTwo = new VideoBe();
       // videoBeanTwo.setCoverRes(R.drawable.cover2);
        videoBeanTwo.setContent("This is what i do every day");
        videoBeanTwo.setVideoRes(R.raw.video2);
        videoBeanTwo.setDistance(19.7f);
        videoBeanTwo.setFocused(true);
        videoBeanTwo.setLiked(false);
        videoBeanTwo.setLikeCount(1938230);
        videoBeanTwo.setCommentCount(8923);
        videoBeanTwo.setShareCount(5892);

        VideoBe.UserBean userBeanTwo = new VideoBe.UserBean();
        userBeanTwo.setUid(2);
        userBeanTwo.setHead(R.drawable.head2);
        userBeanTwo.setNickName("beloi");
        userBeanTwo.setSign("");
        userBeanTwo.setSubCount(20323234);
        userBeanTwo.setFocusCount(244);
        userBeanTwo.setFansCount(1938232);
        userBeanTwo.setWorkCount(123);
        userBeanTwo.setDynamicCount(123);
        userBeanTwo.setLikeCount(344);

        userList.add(userBeanTwo);
        videoBeanTwo.setUserBean(userBeanTwo);

        VideoBe videoBeanThree = new VideoBe();
      //  videoBeanThree.setCoverRes(R.drawable.cover3);
        videoBeanThree.setContent("Hi, i'm beloi");
        videoBeanThree.setVideoRes(R.raw.video3);
        videoBeanThree.setDistance(15.9f);
        videoBeanThree.setFocused(false);
        videoBeanThree.setLiked(false);
        videoBeanThree.setLikeCount(592032);
        videoBeanThree.setCommentCount(9221);
        videoBeanThree.setShareCount(982);

        VideoBe.UserBean userBeanThree = new VideoBe.UserBean();
        userBeanThree.setUid(3);
        userBeanThree.setHead(R.drawable.head3);
        userBeanThree.setNickName("Dreacot");
        userBeanThree.setSign("");
        userBeanThree.setSubCount(1039232);
        userBeanThree.setFocusCount(159);
        userBeanThree.setFansCount(29232323);
        userBeanThree.setWorkCount(171);
        userBeanThree.setDynamicCount(173);
        userBeanThree.setLikeCount(1724);

        userList.add(userBeanThree);
        videoBeanThree.setUserBean(userBeanThree);

        VideoBe videoBeanFour = new VideoBe();
      //  videoBeanFour.setCoverRes(R.drawable.cover4);
        videoBeanFour.setContent("Dreacot here... watch my video");
        videoBeanFour.setVideoRes(R.raw.video4);
        videoBeanFour.setDistance(25.2f);
        videoBeanFour.setFocused(false);
        videoBeanFour.setLiked(false);
        videoBeanFour.setLikeCount(887232);
        videoBeanFour.setCommentCount(2731);
        videoBeanFour.setShareCount(8924);

        VideoBe.UserBean userBeanFour = new VideoBe.UserBean();
        userBeanFour.setUid(4);
        userBeanFour.setHead(R.drawable.head4);
        userBeanFour.setNickName("oshorefuel");
        userBeanFour.setSign("");
        userBeanFour.setSubCount(2689424);
        userBeanFour.setFocusCount(399);
        userBeanFour.setFansCount(360829);
        userBeanFour.setWorkCount(562);
        userBeanFour.setDynamicCount(570);
        userBeanFour.setLikeCount(4310);

        userList.add(userBeanFour);
        videoBeanFour.setUserBean(userBeanFour);

       /* VideoBean videoBeanFive = new VideoBean();
        videoBeanFive.setCoverRes(R.drawable.cover5);
        videoBeanFive.setContent("");
        videoBeanFive.setVideoRes(R.raw.video5);
        videoBeanFive.setDistance(9.2f);
        videoBeanFive.setFocused(false);
        videoBeanFive.setLiked(false);
        videoBeanFive.setLikeCount(8293241);
        videoBeanFive.setCommentCount(982);
        videoBeanFive.setShareCount(8923);

        VideoBean.UserBean userBeanFive = new VideoBean.UserBean();
        userBeanFive.setUid(5);
        userBeanFive.setHead(R.drawable.head5);
        userBeanFive.setNickName("Adelaide");
        userBeanFive.setSign("");
        userBeanFive.setSubCount(1002342);
        userBeanFive.setFocusCount(87);
        userBeanFive.setFansCount(520232);
        userBeanFive.setWorkCount(89);
        userBeanFive.setDynamicCount(122);
        userBeanFive.setLikeCount(9);

        userList.add(userBeanFive);
        videoBeanFive.setUserBean(userBeanFive);

        VideoBean videoBeanSix = new VideoBean();
        videoBeanSix.setCoverRes(R.drawable.cover6);
        videoBeanSix.setContent("");
        videoBeanSix.setVideoRes(R.raw.video6);
        videoBeanSix.setDistance(16.4f);
        videoBeanSix.setFocused(true);
        videoBeanSix.setLiked(true);
        videoBeanSix.setLikeCount(2109823);
        videoBeanSix.setCommentCount(9723);
        videoBeanFive.setShareCount(424);

        VideoBean.UserBean userBeanSix = new VideoBean.UserBean();
        userBeanSix.setUid(6);
        userBeanSix.setHead(R.drawable.head6);
        userBeanSix.setNickName("sirmorris");
        userBeanSix.setSign("");
        userBeanSix.setSubCount(29342320);
        userBeanSix.setFocusCount(67);
        userBeanSix.setFansCount(7028323);
        userBeanSix.setWorkCount(5133);
        userBeanSix.setDynamicCount(5159);
        userBeanSix.setLikeCount(0);

        userList.add(userBeanSix);
        videoBeanSix.setUserBean(userBeanSix);

        VideoBean videoBeanSeven = new VideoBean();
        videoBeanSeven.setCoverRes(R.drawable.cover7);
        videoBeanSeven.setContent("");
        videoBeanSeven.setVideoRes(R.raw.video7);
        videoBeanSeven.setDistance(16.4f);
        videoBeanSeven.setFocused(false);
        videoBeanSeven.setLiked(false);
        videoBeanSeven.setLikeCount(185782);
        videoBeanSeven.setCommentCount(2452);
        videoBeanSeven.setShareCount(3812);

        VideoBean.UserBean userBeanSeven = new VideoBean.UserBean();
        userBeanSeven.setUid(7);
        userBeanSeven.setHead(R.drawable.head7);
        userBeanSeven.setNickName("Sean");
        userBeanSeven.setSign("");
        userBeanSeven.setSubCount(471932);
        userBeanSeven.setFocusCount(482);
        userBeanSeven.setFansCount(371423);
        userBeanSeven.setWorkCount(242);
        userBeanSeven.setDynamicCount(245);
        userBeanSeven.setLikeCount(839);

        userList.add(userBeanSeven);
        videoBeanSeven.setUserBean(userBeanSeven);

        VideoBean videoBeanEight = new VideoBean();
        videoBeanEight.setCoverRes(R.drawable.cover8);
        videoBeanEight.setContent("");
        videoBeanEight.setVideoRes(R.raw.video8);
        videoBeanEight.setDistance(8.4f);
        videoBeanEight.setFocused(false);
        videoBeanEight.setLiked(false);
        videoBeanEight.setLikeCount(1708324);
        videoBeanEight.setCommentCount(8372);
        videoBeanEight.setShareCount(982);

        VideoBean.UserBean userBeanEight = new VideoBean.UserBean();
        userBeanEight.setUid(8);
        userBeanEight.setHead(R.drawable.head8);
        userBeanEight.setNickName("readah");
        userBeanEight.setSign("");
        userBeanEight.setSubCount(1832342);
        userBeanEight.setFocusCount(397);
        userBeanEight.setFansCount(1394232);
        userBeanEight.setWorkCount(164);
        userBeanEight.setDynamicCount(167);
        userBeanEight.setLikeCount(0);

        userList.add(userBeanEight);
        videoBeanEight.setUserBean(userBeanEight);*/

        datas.add(videoBeanOne);
        datas.add(videoBeanTwo);
        datas.add(videoBeanThree);
        datas.add(videoBeanFour);
/*        datas.add(videoBeanFive);
        datas.add(videoBeanSix);
        datas.add(videoBeanSeven);
        datas.add(videoBeanEight);*/


       /* datas.add(videoBeanOne);
        datas.add(videoBeanTwo);
        datas.add(videoBeanThree);
        datas.add(videoBeanFour);*/


  /*      datas.add(videoBeanFive);
        datas.add(videoBeanSix);
        datas.add(videoBeanSeven);
        datas.add(videoBeanEight);*/

    }
}
