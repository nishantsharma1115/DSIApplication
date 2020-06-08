package com.application.dsi.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.dsi.R;
import com.application.dsi.applyForDigitalCardActivity;
import com.application.dsi.dataClass.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.application.dsi.common.Constants.SR;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Customer> customerList;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<Customer> customerList) {
        this.customerList = customerList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int position) {
        final Customer currentCustomer = customerList.get(position);

        holder.tv_name.setText(currentCustomer.getName());
        holder.tv_email.setText(currentCustomer.getEmail());
        holder.tv_phone.setText(currentCustomer.getMobile());

        SR.child("Profile Picture").child("Customers").child(currentCustomer.getCustomerId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .into(holder.customerProfile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.customerProfile.setImageResource(R.drawable.profile_picture);
                    }
                });

        holder.applyDigitalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), applyForDigitalCardActivity.class);
                intent.putExtra("customerId", currentCustomer.getCustomerId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_phone, tv_email;
        CircleImageView customerProfile;
        Button applyDigitalCard;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_mobileNumber);
            tv_email = itemView.findViewById(R.id.tv_email);
            customerProfile = itemView.findViewById(R.id.profileImage);
            applyDigitalCard = itemView.findViewById(R.id.applyForDigitalCard);
        }
    }
}
