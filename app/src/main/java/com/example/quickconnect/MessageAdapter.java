package com.example.quickconnect;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.AppPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEWTYPE_SENT = 1;
    private static final int VIEWTYPE_RECIEVE = 2;

    private static OnFileClickListener onFileClickListener;

    private List<Message> messageList = new ArrayList<>();

    private AppPreferences appPreferences;

    private Context context;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.appPreferences = new AppPreferences(context);
        this.context = context;
    }

    public MessageAdapter(List<Message> messageList, OnFileClickListener onFileClickListener, Context context) {
        this.messageList = messageList;
        this.onFileClickListener = onFileClickListener;
        this.appPreferences = new AppPreferences(context);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messageList.get(position);
        Log.d("SENDERID", "getItemViewType: " + message.getSenderId() + " " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        if (message.getSenderId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:ss");

        Message message = messageList.get(position);

        String formattedTime = timeFormat.format(message.getTimestamp());
        String formattedDate = dateFormat.format(message.getTimestamp());

        if (holder.getItemViewType() == VIEWTYPE_SENT) {
            MeViewHolder newHolder = (MeViewHolder) holder;
            newHolder.message.setText(message.getText());
            newHolder.time.setText(formattedTime);

            newHolder.image.setVisibility(View.GONE);
            newHolder.file.setVisibility(View.GONE);
            newHolder.message.setVisibility(View.VISIBLE);

            if (position - 1 >= 0 && areDatesEqual(message.getTimestamp(), messageList.get(position - 1).getTimestamp())) {
                newHolder.date.setVisibility(View.GONE);
            } else {
                newHolder.date.setText(formattedDate);
            }

            if (message.getImage() != null && !message.getImage().isEmpty() ) {
                newHolder.image.setVisibility(View.VISIBLE);
                Glide.with(newHolder.image.getContext()).load(message.getImage()).into(newHolder.image);
            }


            if (message.getFile() != null && !message.getFile().isEmpty() ) {
                newHolder.file.setVisibility(View.VISIBLE);
                newHolder.file.setText(message.getFile().substring(message.getFile().lastIndexOf('/') + 1));
            }


            if (message.getText().isEmpty())
            {
                newHolder.message.setVisibility(View.GONE);
            }
            else
            {
                newHolder.message.setVisibility(View.VISIBLE);
            }


        } else if (holder.getItemViewType() == VIEWTYPE_RECIEVE) {
            ThemViewHolder newHolder = (ThemViewHolder) holder;
            newHolder.message.setText(message.getText());
            newHolder.time.setText(formattedTime);

            newHolder.image.setVisibility(View.GONE);
            newHolder.file.setVisibility(View.GONE);
            newHolder.message.setVisibility(View.VISIBLE);

            if (position - 1 >= 0 && areDatesEqual(message.getTimestamp(), messageList.get(position - 1).getTimestamp())) {
                newHolder.date.setVisibility(View.GONE);
            } else {
                newHolder.date.setText(formattedDate);
            }

            if (message.getImage() != null && !message.getImage().isEmpty() ) {
                newHolder.image.setVisibility(View.VISIBLE);
                Glide.with(newHolder.image.getContext()).load(message.getImage()).into(newHolder.image);
            }


            if (message.getFile() != null && !message.getFile().isEmpty() ) {
                newHolder.file.setVisibility(View.VISIBLE);
                newHolder.file.setText(message.getFile().substring(message.getFile().lastIndexOf('/') + 1));
            }


            if (message.getText().isEmpty())
            {
                newHolder.message.setVisibility(View.GONE);
            }
            else
            {
                newHolder.message.setVisibility(View.VISIBLE);
            }

            if (message.getMessageId().equals("1"))
            {
                newHolder.progressLottie.setVisibility(View.VISIBLE);
            }
            else
            {
                newHolder.progressLottie.setVisibility(View.GONE);
            }

            if (!appPreferences.getChatLanguage().equals("None") && !appPreferences.getChatLanguage().isEmpty() && !message.getText().isEmpty()) {
                newHolder.message.setText(message.getText());
                newHolder.progressLottie.setVisibility(View.VISIBLE);
                newHolder.message.setVisibility(View.GONE);
                newHolder.progressLottie.setVisibility(View.VISIBLE);

                TranslationService translationService = new TranslationService();
                translationService.translate(message.getText(), appPreferences.getChatLanguage(), new TranslationService.TranslationCallback() {
                    @Override
                    public String onTranslationSuccess(String translatedText) {
                        newHolder.message.setText(translatedText);
                        newHolder.progressLottie.setVisibility(View.GONE);
                        newHolder.message.setVisibility(View.VISIBLE);
                        return translatedText;
                    }

                    @Override
                    public void onTranslationFailure(String errorMessage) {
                        newHolder.progressLottie.setVisibility(View.GONE);
                        newHolder.message.setVisibility(View.VISIBLE);
                        Toast.makeText(newHolder.message.getContext(), "Translation failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        if (messageList !=null)
        {
            return messageList.size();
        }
        return 0;
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public static boolean areDatesEqual(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        // Compare year, month, and day
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    public static class MeViewHolder extends RecyclerView.ViewHolder {
        public TextView message, date, time;
        public ImageView image;
        public Button file;
        public LottieAnimationView progressLottie;

        public MeViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_me_text);
            date = itemView.findViewById(R.id.message_me_date);
            time = itemView.findViewById(R.id.message_me_time);
            image = itemView.findViewById(R.id.message_me_image);
            file = itemView.findViewById(R.id.message_me_file);
            progressLottie = itemView.findViewById(R.id.me_loadingMsg);

            file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onFileClickListener != null) {
                        onFileClickListener.onFileClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public static class ThemViewHolder extends RecyclerView.ViewHolder {
        public TextView message,date, time;
        public ImageView image;
        public Button file;
        public LottieAnimationView progressLottie;

        public ThemViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_them_text);
            date = itemView.findViewById(R.id.message_them_date);
            time = itemView.findViewById(R.id.message_them_time);
            image = itemView.findViewById(R.id.message_them_image);
            file = itemView.findViewById(R.id.message_them_file);
            progressLottie = itemView.findViewById(R.id.other_loadingMsg);

            file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onFileClickListener != null) {
                        onFileClickListener.onFileClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    interface  OnFileClickListener{
        void onFileClick(int position);
    }
}
