package com.example.quickconnect;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.OnClickInterface;
import com.example.quickconnect.databinding.ChatItemLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final OnClickInterface onClickInterface;
    private List<Chat> dataList;

    public ChatAdapter(OnClickInterface onClickInterface, List<Chat> dataList) {
        this.onClickInterface = onClickInterface;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        return new ChatViewHolder(view, onClickInterface);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = dataList.get(position);

        if (chat.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.binding.chatItemTitle.setText(chat.getSupportName());
        } else {
            holder.binding.chatItemTitle.setText(chat.getCustomerName());
        }

        holder.binding.chatItemCategory.setText("Category: " + chat.getCategory());
        SimpleDateFormat formatter = new SimpleDateFormat("E dd HH:mm");

        if (chat.getMessages() != null && !chat.getMessages().isEmpty())
        {
            holder.binding.chatItemLastmsg.setText(chat.getMessages().get(chat.getMessages().size() - 1).getText());
        }
        else
        {
            holder.binding.chatItemLastmsg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ChatItemLayoutBinding binding;

        public ChatViewHolder(@NonNull View itemView, OnClickInterface onClickInterface) {
            super(itemView);
            binding = ChatItemLayoutBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickInterface != null)
                    {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        {
                            onClickInterface.onClick(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }
}
