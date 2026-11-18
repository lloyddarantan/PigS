package com.example.pigs.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.R;
import com.example.pigs.model.Pig;

import java.util.ArrayList;
import java.util.List;

public class ThermalPigAdapter extends RecyclerView.Adapter<ThermalPigAdapter.PigViewHolder> {

    private final List<Pig> pigList = new ArrayList<>();

    public void setPigs(List<Pig> pigs) {
        pigList.clear();
        if (pigs != null) {
            pigList.addAll(pigs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pig_thermal, parent, false);
        return new PigViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PigViewHolder holder, int position) {
        Pig pig = pigList.get(position);

        holder.tvPigBadgeId.setText(pig.getBadgeId());
        holder.tvPigTitle.setText(pig.getFormattedTitle());
        holder.tvPigTemp.setText(pig.getTemperature());
        holder.tvPigDetails.setText(pig.getRfidDetails());

        String status = pig.getStatus();
        holder.tvPigStatus.setText(status);

        double tempVal = pig.getTemperatureValue();

        if (tempVal >= 40.0 || (status != null && (status.toLowerCase().contains("asf") || status.toLowerCase().contains("flag")))) {
            holder.cardPig.setCardBackgroundColor(Color.parseColor("#FFF5F5"));
            holder.badgeContainer.setBackgroundResource(R.drawable.bg_badge_red);
            holder.tvPigBadgeId.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvPigTemp.setBackgroundResource(R.drawable.bg_badge_red);
            holder.tvPigTemp.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvPigStatus.setTextColor(Color.parseColor("#D32F2F"));
        } else if (tempVal >= 39.2 || (status != null && (status.toLowerCase().contains("elevated") || status.toLowerCase().contains("warning")))) {
            holder.cardPig.setCardBackgroundColor(Color.parseColor("#FFFDF5"));
            holder.badgeContainer.setBackgroundResource(R.drawable.bg_badge_orange);
            holder.tvPigBadgeId.setTextColor(Color.parseColor("#E65100"));
            holder.tvPigTemp.setBackgroundResource(R.drawable.bg_badge_orange);
            holder.tvPigTemp.setTextColor(Color.parseColor("#E65100"));
            holder.tvPigStatus.setTextColor(Color.parseColor("#E65100"));
        } else {
            holder.cardPig.setCardBackgroundColor(Color.parseColor("#F9FBF9"));
            holder.badgeContainer.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvPigBadgeId.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvPigTemp.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvPigTemp.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvPigStatus.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return pigList.size();
    }

    static class PigViewHolder extends RecyclerView.ViewHolder {

        CardView cardPig;
        LinearLayout badgeContainer;
        TextView tvPigBadgeId;
        TextView tvPigTitle;
        TextView tvPigTemp;
        TextView tvPigDetails;
        TextView tvPigStatus;

        PigViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPig = itemView.findViewById(R.id.cardPig);
            badgeContainer = itemView.findViewById(R.id.badgeContainer);
            tvPigBadgeId = itemView.findViewById(R.id.tvPigBadgeId);
            tvPigTitle = itemView.findViewById(R.id.tvPigTitle);
            tvPigTemp = itemView.findViewById(R.id.tvPigTemp);
            tvPigDetails = itemView.findViewById(R.id.tvPigDetails);
            tvPigStatus = itemView.findViewById(R.id.tvPigStatus);
        }
    }
}
