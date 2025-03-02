package com.example.omniledger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Users> dataList;

    public TransactionAdapter(Context context, List<Users> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modellayout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.amount.setText("₹"+dataList.get(position).getAmount());
        holder.trns.setText(dataList.get(position).getTransaction()+"ed");
        holder.bank.setText(dataList.get(position).getBank());
        String dt = dataList.get(position).getDatetym();
        String d = dt.substring(6,8)+"/"+dt.substring(4,6)+"/"+dt.substring(0,4) +" at " +dt.substring(9,11) +"-"+dt.substring(11,13)+"-"+dt.substring(13);
        holder.date.setText(d);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("bank",dataList.get(holder.getAbsoluteAdapterPosition()).getBank());
                intent.putExtra("amount",dataList.get(holder.getAbsoluteAdapterPosition()).getAmount());
                intent.putExtra("item",dataList.get(holder.getAbsoluteAdapterPosition()).getItem());
                intent.putExtra("date",dataList.get(holder.getAbsoluteAdapterPosition()).getDatetym());
                intent.putExtra("location",dataList.get(holder.getAbsoluteAdapterPosition()).getLocation());
                intent.putExtra("transaction",dataList.get(holder.getAbsoluteAdapterPosition()).getTransaction());
                intent.putExtra("url",dataList.get(holder.getAbsoluteAdapterPosition()).getFilename());
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
class MyViewHolder extends  RecyclerView.ViewHolder{

    TextView amount,trns,bank,date;

    CardView cardView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        amount = itemView.findViewById(R.id.amount);
        trns = itemView.findViewById(R.id.tvDebtorCrd);
        bank = itemView.findViewById(R.id.Bank);
        date = itemView.findViewById(R.id.Date);
        cardView = itemView.findViewById(R.id.cview);
    }
}
