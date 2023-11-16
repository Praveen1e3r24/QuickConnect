package com.example.quickconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList = new ArrayList<>();
    private Context context;

    public TransactionAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.textViewTransactionId.setText("Transaction ID: " + transaction.getTransactionId());
        holder.textViewDescription.setText(transaction.getDescription());
        holder.textViewAmount.setText("Amount: $ " + transaction.getAmount());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(transaction.getTransactionDateTime());

        holder.textViewDateTime.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTransactionId;
        public TextView textViewDescription;
        public TextView textViewAmount;
        public TextView textViewDateTime;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTransactionId = itemView.findViewById(R.id.textViewTransactionId);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
        }
    }
}
