package com.example.pigs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.R;
import com.example.pigs.model.Vaccination;

import java.util.ArrayList;
import java.util.List;

public class VaccinationAdapter
        extends RecyclerView.Adapter<VaccinationAdapter.ViewHolder> {

    private List<Vaccination> vaccinationList = new ArrayList<>();

    public void setVaccinations(List<Vaccination> list) {
        vaccinationList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaccination, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        Vaccination item = vaccinationList.get(position);

        holder.tvTag.setText(
                "Tag " + item.getTagNumber()
                        + ", Pen " + item.getPenNumber()
                        + ", " + item.getDate()
        );

        holder.tvVaccine.setText(
                "[" + item.getVaccineName() + "]"
        );

        holder.tvStatus.setText(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return vaccinationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTag, tvVaccine, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTag = itemView.findViewById(R.id.tvTag);
            tvVaccine = itemView.findViewById(R.id.tvVaccine);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
