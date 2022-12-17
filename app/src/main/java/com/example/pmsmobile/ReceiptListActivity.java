package com.example.pmsmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;

import com.example.pmsmobile.Models.ReceiptList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ReceiptListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRoot = firebaseDatabase.getReference();
    DatabaseReference mRootReferance;
    Button btnNewReceipt;
    ArrayList<ReceiptList> entReceipts;
    GridView gridView;
    Button searchbtn;
    SearchView txtsearchview;
    Button btnoption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);
        btnNewReceipt=findViewById(R.id.btnnewReceipt);
        gridView=findViewById(R.id.gridView);
        searchbtn=findViewById(R.id.searchbtn);
        txtsearchview=findViewById(R.id.txtsearchview);
        btnoption=findViewById(R.id.btnoption);
        btnNewReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intReceipt=new Intent(ReceiptListActivity.this,NewReceiptActivity.class);
                startActivity(intReceipt);
            }
        });
        mRootReferance=mRoot.child("Companies").child(Common.CompanyID);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbtn.setVisibility(View.GONE);
                txtsearchview.setVisibility(View.VISIBLE);
                txtsearchview.setIconified(false);
            }
        });
        btnoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intReport=new Intent(ReceiptListActivity.this,ReportsActivity.class);
                startActivity(intReport);
            }
        });

        txtsearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchbtn.setVisibility(View.VISIBLE);
                txtsearchview.setVisibility(View.GONE);
                return true;
            }
        });
        txtsearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    gridView.clearTextFilter();
                }else{
                    gridView.setFilterText(newText);
                }


                return true;
            }
        });
        PopulateAllReceipts();
    }
    private void PopulateAllReceipts()
    {
        entReceipts=new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        final String DayNode = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mRootReferance.child("Receipt").child(DayNode).child(Common._currentUser.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                entReceipts=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    ReceiptList lst=new ReceiptList();
                    lst.setMobileVNo(String.valueOf(ds.child("MobileVNo").getValue()));
                    lst.setTenantName(String.valueOf(ds.child("TenantName").getValue()));
                    lst.setAmount(String.valueOf(ds.child("PaymentAmount").getValue()));
                    lst.setMonthYear(String.valueOf(ds.child("MonthName").getValue()));
                    entReceipts.add(lst);
                }
                ReceiptAdapter mainAdapter=new ReceiptAdapter(ReceiptListActivity.this,entReceipts);
                gridView.setAdapter(mainAdapter);
                gridView.setTextFilterEnabled(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}