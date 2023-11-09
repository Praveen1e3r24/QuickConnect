package com.example.quickconnect;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int VIEWTYPE_SENT = 1;
    private static final int VIEWTYPE_RECIEVE = 2;
    private String userId;

    private final List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemViewType(int position) {
	        Message message = messageList.get(position);

	        if (message.getSenderId() == userId) {
	            return VIEWTYPE_SENT;
	        } else {
	            return VIEWTYPE_RECIEVE;
	        }
	    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEWTYPE_SENT)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_me, parent, false);
            return new MeViewHolder(view);
        }
        else if (viewType == VIEWTYPE_RECIEVE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_other, parent, false);
            return new ThemViewHolder(view);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:ss");

        Message message = messageList.get(position);
        LocalDateTime messageTimestamp = message.getTimestamp();
        String formattedTime = messageTimestamp.format(timeFormat);
        String formattedDate = messageTimestamp.format(dateFormat);

        if (holder.getItemViewType() == VIEWTYPE_SENT) {
            MeViewHolder newHolder = (MeViewHolder) holder;
            newHolder.message.setText(message.getText());
            newHolder.time.setText(formattedTime);

            if (position - 1 >= 0 && messageTimestamp.toLocalDate().isEqual(messageList.get(position - 1).getTimestamp().toLocalDate())) {
                newHolder.date.setText(formattedDate);
            } else {
                newHolder.date.setVisibility(View.GONE);
            }

            if (message.getImage() != null) {
                newHolder.image.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getImage()).fit().centerCrop().into(newHolder.image);
            }
        } else {
            ThemViewHolder newHolder = (ThemViewHolder) holder;
            newHolder.message.setText(message.getText());
            newHolder.time.setText(formattedTime);

            if (position - 1 >= 0 && messageTimestamp.toLocalDate().isEqual(messageList.get(position - 1).getTimestamp().toLocalDate())) {
                newHolder.date.setText(formattedDate);
            } else {
                newHolder.date.setVisibility(View.GONE);
            }

            if (message.getImage() != null) {
                newHolder.image.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getImage()).fit().centerCrop().into(newHolder.image);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MeViewHolder extends RecyclerView.ViewHolder {
        public TextView message, date, time;
        public ImageView image;

        public MeViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_me_text);
            date = itemView.findViewById(R.id.message_me_date);
            time = itemView.findViewById(R.id.message_me_time);
            image = itemView.findViewById(R.id.message_me_image);
        }
    }

    public static class ThemViewHolder extends RecyclerView.ViewHolder {
        public TextView message,date, time;
        public ImageView image;

        public ThemViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_them_text);
            date = itemView.findViewById(R.id.message_them_date);
            time = itemView.findViewById(R.id.message_them_time);
            image = itemView.findViewById(R.id.message_them_image);
        }
    }
}
