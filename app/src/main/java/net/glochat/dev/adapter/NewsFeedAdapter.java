package net.glochat.dev.adapter;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.codemybrainsout.onboarder.views.CircleIndicatorView;

import net.glochat.dev.R;
import net.glochat.dev.models.PicPostModel;
import net.glochat.dev.utils.NumberFormatter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.viewHolder> {

    private List<PicPostModel> list;
    private Context context;

    public NewsFeedAdapter(List<PicPostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsFeedAdapter.viewHolder(LayoutInflater.from(context).inflate(R.layout.single_feed_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final PicPostModel post = list.get(position);
        if (post != null) {

            holder.textUsername.setText(post.getPosterName());

            if (Long.parseLong(post.getPostcommentNum()) <= 0)
                holder.textCommentCap.setText(context.getString(R.string.be_the_first_to_comment));
            else
                holder.textCommentCap.setText(context.getString(R.string.comments));

            if (post.getPostBody().equals(" "))
                holder.textPostBody.setVisibility(View.GONE);
            else {
                holder.textPostBody.setVisibility(View.VISIBLE);
                holder.setTextViewHTML(holder.textPostBody, post.getPostBody());
            }

            //***setup viewpager***
            holder.postImageView.setVisibility(View.VISIBLE);
            holder.postImageView.setAdapter(new NewsFeedImageAdapter(post.getPostImage(), context));

            if (post.getPostImage().size() <= 1)
                holder.circleIndicatorView.setVisibility(View.GONE);
            else {
                holder.circleIndicatorView.setVisibility(View.VISIBLE);
                holder.circleIndicatorView.setPageIndicators(post.getPostImage().size());
                holder.circleIndicatorView.setActiveIndicatorColor(R.color.colorPrimary);
                holder.circleIndicatorView.setInactiveIndicatorColor(R.color.grey);
               // holder.postImageView.setPageTransformer(new HingeTransformation());
                holder.postImageView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        holder.circleIndicatorView.setCurrentPage(position);
                    }
                });
            }
            //***ends here***

            //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).
            try {
                String postedOn = DateUtils.getRelativeTimeSpanString(post.getPostDate()).toString();
                //String postedOn = "posted : "+ post.getPostDate();
                holder.textPostTime.setText(postedOn);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Glide.with(context).clear(holder.profileImage);

            Glide.with(context).load(post.getPosterProfileImage()).placeholder(R.drawable.profile_placeholder).into(holder.profileImage);

            holder.isVerifiedImage.setVisibility(post.getPosterVerified().equals("0") ? View.GONE : View.VISIBLE);
            holder.textNumLikes.setText(NumberFormatter.format(Long.parseLong(post.getLikes())));
            holder.textNumComments.setText(NumberFormatter.format(Long.parseLong(post.getPostcommentNum())));

            holder.btnLike.setImageResource((post.getLikeStatus().equals("true")) ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_black_24dp);
            holder.btnLike.setOnClickListener(view -> {
                if (post.getLikeStatus().equals("true")) {
                    post.setLikeStatus("false");
                    String likes = Long.toString(Long.parseLong(post.getLikes()) - 1);
                    post.setLikes(likes);
                    holder.textNumLikes.setText(NumberFormatter.format(Long.parseLong(likes)));
                    holder.btnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    post.setLikeStatus("true");
                    String likes = Long.toString(Long.parseLong(post.getLikes()) + 1);
                    post.setLikes(likes);
                    holder.textNumLikes.setText(NumberFormatter.format(Long.parseLong(likes)));
                    holder.btnLike.setImageResource(R.drawable.ic_favorite_red_24dp);
                }
              //  holder.doLikes(post);
            });

            holder.btnComments.setOnClickListener(view -> {

            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        ViewPager2 postImageView;
        ImageView isVerifiedImage;
        TextView textUsername, textPostBody, textNumLikes, textNumComments, textPostTime, textCommentCap;
        CircleImageView profileImage;
        ImageButton btnLike, btnComments;
        CircleIndicatorView circleIndicatorView;

        viewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.single_feed_img_video_view);
            textUsername = itemView.findViewById(R.id.single_feed_user_name);
            textPostBody = itemView.findViewById(R.id.single_feed_text_post_body);
            textNumLikes = itemView.findViewById(R.id.single_feed_text_likes);
            textNumComments = itemView.findViewById(R.id.single_feed_text_comments);
            profileImage = itemView.findViewById(R.id.single_feed_user_profile_pic);
            isVerifiedImage = itemView.findViewById(R.id.single_feed_img_is_verified);
            btnComments = itemView.findViewById(R.id.single_feed_img_btn_comments);
            btnLike = itemView.findViewById(R.id.single_feed_img_btn_likes);
            textPostTime = itemView.findViewById(R.id.single_feed_post_time);
            textCommentCap = itemView.findViewById(R.id.single_feed_text_be_first_to_comment);
            circleIndicatorView = itemView.findViewById(R.id.single_feed_circle_indicator_view);

        }

        void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
            int start = strBuilder.getSpanStart(span);
            int end = strBuilder.getSpanEnd(span);
            int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(@NonNull View view) {
                    Toast.makeText(context, span.getURL(), Toast.LENGTH_SHORT).show();
                }
            };
            strBuilder.setSpan(clickable, start, end, flags);
            strBuilder.removeSpan(span);
        }

        void setTextViewHTML(TextView text, String html) {
            CharSequence sequence = Html.fromHtml(html);
            SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
            URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
            for (URLSpan span : urls) {
                makeLinkClickable(strBuilder, span);
            }
            text.setText(strBuilder);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        /*void doLikes(final PicPostModel post) {
            if ((post.getLikeStatus().equals("true"))) {
                post.setLikeStatus("false");
                String likes = Long.toString(Long.parseLong(post.getLikes()) - 1);
                post.setLikes(likes);
                textNumLikes.setText(net.glochat.dev.utils.NumberFormatter.format(Long.parseLong(likes)));
                btnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            } else {
                post.setLikeStatus("true");
                String likes = Long.toString(Long.parseLong(post.getLikes()) + 1);
                post.setLikes(likes);
                textNumLikes.setText(NumberFormatter.format(Long.parseLong(likes)));
                btnLike.setImageResource(R.drawable.ic_favorite_red_24dp);
            }
        }*/

       /* public void addNewData(ArrayList<PicPostModel> posts) {
            list.addAll(posts);
            notifyDataSetChanged();
        }*/
    }
}