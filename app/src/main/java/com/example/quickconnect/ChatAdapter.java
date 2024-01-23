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
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final OnClickInterface onClickInterface;
    private List<Chat> chatList;
    private List<CallRequest> callRequestList;

    public ChatAdapter(OnClickInterface onClickInterface,List<Chat> chatList, List<CallRequest> callRequestList) {
        this.onClickInterface = onClickInterface;
        this.callRequestList = callRequestList;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        if (chatList == null)
        {
            CallRequest request = new CallRequest();
            return new ChatViewHolder(view, onClickInterface,request);
        }
        else
        {
            Chat chat = new Chat();
            return new ChatViewHolder(view, onClickInterface,chat);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (callRequestList == null)
        {
            Chat chat = chatList.get(position);

            if (chat.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.binding.chatItemTitle.setText(chat.getSupportName());
            } else {
                holder.binding.chatItemTitle.setText(chat.getCustomerName());
            }

            if (chat.getClosed())
            {
                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#c40000"));
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
        else if (chatList == null)
        {
            CallRequest callRequest = callRequestList.get(position);

            if (callRequest.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.binding.chatItemTitle.setText(callRequest.getSupportName());
            } else {
                holder.binding.chatItemTitle.setText(callRequest.getCustomerName());
            }
            holder.binding.chatItemCategory.setText("Category: " + callRequest.getCategory());
            holder.binding.chatItemLastmsg.setText("Call Request");


                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#fa5a5a"));


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
                holder.binding.chatItemCard.setCardBackgroundColor(Color.parseColor("#a63c3c"));
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
        if (chatList!=null)
        {
            return chatList.size();
        }
        else
        {
            return callRequestList.size();
        }
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    public void setCallRequestList(List<CallRequest> callRequestList) {
        this.callRequestList = callRequestList;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ChatItemLayoutBinding binding;

        public ChatViewHolder(@NonNull View itemView, OnClickInterface onClickInterface, Object o) {
            super(itemView);
            binding = ChatItemLayoutBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickInterface != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    {
                        onClickInterface.onClick(getAdapterPosition(), o);
                    }
                }
            });
        }
    }
}
