package com.example.quickconnect;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.OnClickInterface;
import com.example.quickconnect.databinding.ChatItemLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final OnClickInterface onClickInterface;
    private static List<ChatRequestItem> chatRequestItemList;

    public ChatAdapter(OnClickInterface onClickInterface, List<ChatRequestItem> chatRequestItemList) {
        this.onClickInterface = onClickInterface;
        this.chatRequestItemList = chatRequestItemList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        return new ChatViewHolder(view, onClickInterface);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatRequestItem item = chatRequestItemList.get(position);
        if (item.isChat())
        {
            Chat chat = item.getChat();

            if (chat.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.binding.chatItemTitle.setText(chat.getSupportName());
            } else {
                holder.binding.chatItemTitle.setText(chat.getCustomerName());
            }

            if (chat.getClosed())
            {
                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#949494"));
            }

            holder.binding.chatItemCategory.setText("Category: " + chat.getCategory());
            SimpleDateFormat formatter = new SimpleDateFormat("E HH:mm");

            if (chat.getMessages() != null && !chat.getMessages().isEmpty())
            {
                holder.binding.chatItemLastmsg.setText(chat.getMessages().get(chat.getMessages().size() - 1).getText());
                holder.binding.chatItemTime.setText(formatter.format(chat.getMessages().get(chat.getMessages().size() - 1).getTimestamp()));
            }
            else
            {
                holder.binding.chatItemLastmsg.setVisibility(View.INVISIBLE);
                holder.binding.chatItemTime.setText(formatter.format(chat.getTimestamp()));
            }

            holder.binding.chatItemIcon.setImageResource(R.drawable.nav_message);
        }
        else if (item.isCallRequest())
        {
            CallRequest callRequest = item.getCallRequest();

            if (callRequest.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.binding.chatItemTitle.setText(callRequest.getSupportName());
            } else {
                holder.binding.chatItemTitle.setText(callRequest.getCustomerName());
            }
            holder.binding.chatItemCategory.setText("Category: " + callRequest.getCategory());
            holder.binding.chatItemLastmsg.setText("Call Request");


//                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#057af7"));


            if (callRequest.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.binding.chatItemTitle.setText(callRequest.getSupportName());
            } else {
                holder.binding.chatItemTitle.setText(callRequest.getCustomerName());
            }


            holder.binding.chatItemCategory.setText("Category: " + callRequest.getCategory());
            SimpleDateFormat formatter = new SimpleDateFormat("E HH:mm");

            if (callRequest.getAccepted())
            {
                holder.binding.chatItemLastmsg.setText("Accepted");
                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#949494"));
            }
            else
            {
                holder.binding.chatItemLastmsg.setText("Queue number: "+ callRequest.getQueueNo());
            }

            holder.binding.chatItemTime.setText(formatter.format(callRequest.getRequestDate()));

            holder.binding.chatItemIcon.setImageResource(R.drawable.nav_call);
        }
    }

    @Override
    public int getItemCount() {
        return chatRequestItemList.size();
    }

    public void setChatRequestItemList(List<ChatRequestItem> chatRequestItemList) {
        this.chatRequestItemList = chatRequestItemList;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ChatItemLayoutBinding binding;

        public ChatViewHolder(@NonNull View itemView, OnClickInterface onClickInterface) {
            super(itemView);
            binding = ChatItemLayoutBinding.bind(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickInterface != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    {
                        ChatRequestItem item = chatRequestItemList.get(getBindingAdapterPosition());
                        onClickInterface.onClick(getBindingAdapterPosition(), item);
                    }
                }
            });
        }
    }
}
