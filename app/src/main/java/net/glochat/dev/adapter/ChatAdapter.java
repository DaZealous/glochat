package net.glochat.dev.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import net.glochat.dev.R;
import net.glochat.dev.models.ChatDao;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {

    private Context context;
    private List<ChatDao> list;

    public ChatAdapter(Context context, List<ChatDao> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chats_layout, parent, false);
        return new ChatAdapter.viewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {
            ChatDao dao = list.get(position);
            holder.checkUser(dao.getFrom());
            holder.checkType(dao.getMsg_type(), dao.getMsg_body(), dao.getMsg_name());
            holder.setTextAdmin(dao.getMsg_body());
            holder.setTextUser(dao.getMsg_body());
            holder.setTextUserTime(Long.toString(dao.getTime_stamp()));
            holder.setTextAdminTime(Long.toString(dao.getTime_stamp()));

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        LinearLayout adminLinear;
        RelativeLayout userLinear;
        CardView textAdminCard, textUserCard;
        TextView textUser, textAdmin, adminTime, userTime;

        private viewHolder(@NonNull View itemView) {
            super(itemView);
            userLinear = itemView.findViewById(R.id.chat_layout_user_layout);
            adminLinear = itemView.findViewById(R.id.chat_layout_admin_layout);
            textUser = itemView.findViewById(R.id.chat_user_text);
            textAdmin = itemView.findViewById(R.id.chat_admin_text);
            textAdminCard = itemView.findViewById(R.id.chat_admin_text_card);
            textUserCard = itemView.findViewById(R.id.chat_user_text_card);
            adminTime = itemView.findViewById(R.id.chat_admin_text_time);
            userTime = itemView.findViewById(R.id.chat_user_text_time);
        }

        void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
            int start = strBuilder.getSpanStart(span);
            int end = strBuilder.getSpanEnd(span);
            int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
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

        private void setTextUser(String text) {
            setTextViewHTML(textUser, text);
        }

        private void setTextAdmin(String text) {
            setTextViewHTML(textAdmin, text);
        }

        private void setTextUserTime(String text) {
            userTime.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(text)));
        }

        private void setTextAdminTime(String time) {
            adminTime.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(time)));
        }

        private void checkUser(String user) {
            if (user.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                adminLinear.setVisibility(View.GONE);
                userLinear.setVisibility(View.VISIBLE);
            } else {
                userLinear.setVisibility(View.GONE);
                adminLinear.setVisibility(View.VISIBLE);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void checkType(String msg_type, String img, String msg_name) {
            if (msg_type.equalsIgnoreCase("text")) {
                textAdminCard.setVisibility(View.VISIBLE);
                textUserCard.setVisibility(View.VISIBLE);
            }
        }

    }
}
