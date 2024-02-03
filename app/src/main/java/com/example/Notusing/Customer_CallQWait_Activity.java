//package com.example.customer;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.telecom.Call;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import com.example.quickconnect.CallRequest;
//import com.example.quickconnect.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class Customer_CallQWait_Activity extends AppCompatActivity {
//    private DatabaseReference dbRef;
//    private CallRequest callRequest;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customer_callqwait);
//
//        dbRef = FirebaseDatabase.getInstance().getReference();
//        callRequest = getIntent().getParcelableExtra("callRequest");
//
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Waiting for Support");
//
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);// set drawable icon
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.queuemenu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        int itemId = menuItem.getItemId();
//        if (itemId == android.R.id.home) {
//            finish();
//        } else if (itemId == R.id.cancel_call_request) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Cancel Call Request");
//            builder.setMessage("Are you sure you want to cancel the call request?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    cancelRequest();
//                    finish();
//                }});
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//
//            builder.create().show();
//        }
//
//        return super.onOptionsItemSelected(menuItem);
//    }
//
//    public void cancelRequest(){
//
//        dbRef.child("Requests").child(callRequest.getRequestId()).removeValue();
//        dbRef.child("Chats").child(callRequest.getRequestId()).removeValue();
//    }
//}