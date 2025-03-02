package com.example.omniledger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    TextView bank1,amnt1,itm1,trns1,dt1,lctn,url;
    ImageView dImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        bank1 = findViewById(R.id.bankdetails);
        amnt1 = findViewById(R.id.amountdetails);
        itm1 = findViewById(R.id.itemdetails);
        trns1 = findViewById(R.id.transdetails);
        dt1 = findViewById(R.id.datedetails);
        lctn = findViewById(R.id.locdetails);
        dImg = findViewById(R.id.detailImage);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            bank1.setText(bundle.getString("bank"));
            amnt1.setText(bundle.getString("amount"));
            itm1.setText(bundle.getString("item"));
            trns1.setText(bundle.getString("transaction"));
            String dt = bundle.getString("date");
            String d = dt.substring(6,8)+"/"+dt.substring(4,6)+"/"+dt.substring(0,4) +" at " +dt.substring(9,11) +"-"+dt.substring(11,13)+"-"+dt.substring(13);
            dt1.setText(d);
            lctn.setText(bundle.getString("location"));
            Glide.with(this).load(bundle.getString("url")).into(dImg);
        }


    }
}