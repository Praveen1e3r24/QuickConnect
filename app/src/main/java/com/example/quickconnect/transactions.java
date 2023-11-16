package com.example.quickconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.example.customer.Transaction;
import com.example.utilities.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class transactions extends AppCompatActivity {

    List<Transaction> transactions = new ArrayList<>();

    private Context getContext() {
        return this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        CallRequest request = getIntent().getParcelableExtra("callRequest");
        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(request.getCustomerId()).child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()){
                    transactions.add(s.getValue(Transaction.class));
                }
                RecyclerView rv = findViewById(R.id.trans_rv);
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(new TransactionAdapter(transactions, getContext()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        findViewById(R.id.transBack).setOnClickListener(v -> {
            finish();
        });
    }
}