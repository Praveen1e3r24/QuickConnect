package com.example.palmchatbot;

public class chatbotAdapter {
//
//    private static final int VIEWTYPE_SENT = 1;
//    private static final int VIEWTYPE_RECIEVE = 2;
//
//    private List<Message> messageList = new ArrayList<>();
//
//    public MessageAdapter(List<Message> messageList) {
//        this.messageList = messageList;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message message = messageList.get(position);
//        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//            return VIEWTYPE_SENT;
//        } else {
//            return VIEWTYPE_RECIEVE;
//        }
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == VIEWTYPE_SENT)
//        {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_me, parent, false);
//            return new MessageAdapter.MeViewHolder(view);
//        }
//        else if (viewType == VIEWTYPE_RECIEVE)
//        {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_other, parent, false);
//            return new MessageAdapter.ThemViewHolder(view);
//        }
//        return null;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:ss");
//
//        Message message = messageList.get(position);
//
//        String formattedTime = timeFormat.format(message.getTimestamp());
//        String formattedDate = dateFormat.format(message.getTimestamp());
//
//        if (holder.getItemViewType() == VIEWTYPE_SENT) {
//            MessageAdapter.MeViewHolder newHolder = (MessageAdapter.MeViewHolder) holder;
//            newHolder.message.setText(message.getText());
//            newHolder.time.setText(formattedTime);
//
//            if (position - 1 >= 0 && areDatesEqual(message.getTimestamp(), messageList.get(position - 1).getTimestamp())) {
//                newHolder.date.setVisibility(View.GONE);
//            } else {
//                newHolder.date.setText(formattedDate);
//            }
//
//            if (message.getImage() != null) {
//                newHolder.image.setVisibility(View.VISIBLE);
//                Picasso.get().load(message.getImage()).fit().centerCrop().into(newHolder.image);
//            }
//
//        } else if (holder.getItemViewType() == VIEWTYPE_RECIEVE) {
//            MessageAdapter.ThemViewHolder newHolder = (MessageAdapter.ThemViewHolder) holder;
//            newHolder.message.setText(message.getText());
//            newHolder.time.setText(formattedTime);
//
//            if (position - 1 >= 0 && areDatesEqual(message.getTimestamp(), messageList.get(position - 1).getTimestamp())){
//                newHolder.date.setVisibility(View.GONE);
//            } else {
//                newHolder.date.setText(formattedDate);
//            }
//
//            if (message.getImage() != null) {
//                newHolder.image.setVisibility(View.VISIBLE);
//                Picasso.get().load(message.getImage()).fit().centerCrop().into(newHolder.image);
//            }
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        if (messageList !=null)
//        {
//            return messageList.size();
//        }
//        return 0;
//    }
//
//    public void addMessage(Message message) {
//        messageList.add(message);
//        notifyItemInserted(messageList.size() - 1);
//    }
//
//    public static boolean areDatesEqual(Date date1, Date date2) {
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(date1);
//
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(date2);
//
//        // Compare year, month, and day
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
//                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
//                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
//    }
//
//
//    public static class MeViewHolder extends RecyclerView.ViewHolder {
//        public TextView message, date, time;
//        public ImageView image;
//
//        public MeViewHolder(View itemView) {
//            super(itemView);
//            message = itemView.findViewById(R.id.message_me_text);
//            date = itemView.findViewById(R.id.message_me_date);
//            time = itemView.findViewById(R.id.message_me_time);
//            image = itemView.findViewById(R.id.message_me_image);
//        }
//    }
//
//    public static class ThemViewHolder extends RecyclerView.ViewHolder {
//        public TextView message,date, time;
//        public ImageView image;
//
//        public ThemViewHolder(View itemView) {
//            super(itemView);
//            message = itemView.findViewById(R.id.message_them_text);
//            date = itemView.findViewById(R.id.message_them_date);
//            time = itemView.findViewById(R.id.message_them_time);
//            image = itemView.findViewById(R.id.message_them_image);
//        }
//    }
}
