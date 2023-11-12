package com.example.quickconnect;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickconnect.databinding.ChatItemLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> dataList;
    private final String userId;

    public ChatAdapter(List<Chat> dataList) {
        this.dataList = dataList;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = dataList.get(position);

        if (userId == chat.getCustomerId()) {
            holder.binding.chatItemTitle.setText(chat.getSupportName());
        } else {
            holder.binding.chatItemTitle.setText(chat.getCustomerName());
        }
        holder.binding.chatItemLastmsg.setText(chat.getCategory());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddd hh:mm");
        holder.binding.chatItemTime.setText(chat.getMessages().get(chat.getMessages().size() - 1).getTimestamp().format(formatter));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ChatItemLayoutBinding binding;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemLayoutBinding.bind(itemView);
        }
    }
}
