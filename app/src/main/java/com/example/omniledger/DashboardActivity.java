package com.example.omniledger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DashboardActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 101;
    FloatingActionButton camera;
    TextView tview;
    List<Users> dataList;
    ValueEventListener eventListener;
    DatabaseReference db;
    RecyclerView rview;

    FirebaseFirestore fstore;

    TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String email = getIntent().getStringExtra("Email");
        rview = findViewById(R.id.recycler1);
        tview = findViewById(R.id.helloView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DashboardActivity.this,1);
        rview.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();



        dataList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(DashboardActivity.this,dataList);
        rview.setAdapter(transactionAdapter);
        String em = email.replace("@gmail.com","");
        fstore = FirebaseFirestore.getInstance();
        DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(email);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    tview.setText("Hello "+documentSnapshot.getString("Name"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
            }
        });

        db = FirebaseDatabase.getInstance().getReference("Transactions").child(em);
        dialog.show();

        eventListener = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                    Users users = itemSnapshot.getValue(Users.class);
                    dataList.add(users);
                }
                transactionAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
dialog.dismiss();
            }
        });





        camera = findViewById(R.id.camFab);

        camera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent
                                = new Intent(DashboardActivity.this,
                                CameraActivity.class).putExtra("Email",email);
                        startActivity(intent);

                    }
                });





    }


}