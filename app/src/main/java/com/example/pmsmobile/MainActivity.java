package com.example.pmsmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.pmsmobile.Models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRoot = firebaseDatabase.getReference();
    DatabaseReference mRootReferance;

    EditText txtpinnumber;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtpinnumber=findViewById(R.id.txtpinnumber);
        btnLogin=findViewById(R.id.btnlogin);
        mRootReferance=mRoot.child("Companies").child(Common.CompanyID);
        Common._currentUser=null;
        btnLogin.setEnabled(false);
        LoadUsers();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sPinNumnber=txtpinnumber.getText().toString();
                for(User _user :Common.entUsers)
                {
                    if(_user.getPINNumber().equals(sPinNumnber))
                    {
                        Common._currentUser=_user;
                        Toast.makeText(getApplicationContext(),"User Verified!",Toast.LENGTH_LONG).show();
                        Intent inReceiptList=new Intent(MainActivity.this,ReceiptListActivity.class);
                        startActivity(inReceiptList);

                        return;
                    }


                }
                Toast.makeText(getApplicationContext(),"Invalid User!",Toast.LENGTH_LONG).show();

            }
        });


    }
    private  void LoadUsers()
    {
        Common.entUsers=new ArrayList<>();
        mRootReferance.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Common.entUsers=new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    User _user=new User();
                    _user.setId(String.valueOf(ds.child("id").getValue()));
                    _user.setUserName(String.valueOf(ds.child("UserName").getValue()));
                    _user.setPassword(String.valueOf(ds.child("Password").getValue()));
                    _user.setPINNumber(String.valueOf(ds.child("PINNumber").getValue()));
                    Common.entUsers.add(_user);
                }
                btnLogin.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}