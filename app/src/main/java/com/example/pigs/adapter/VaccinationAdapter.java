
package com.example.pigs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.R;
import com.example.pigs.model.Vaccination;

import java.util.ArrayList;
import java.util.List;

public class VaccinationAdapter extends RecyclerView.Adapter<VaccinationAdapter.VaccinationViewHolder> {

    public interface OnAdministerClickListener {
        void onAdministerClick(Vaccination vaccination);
    }

    private final List<Vaccination> vaccinations = new ArrayList<>();
    private final OnAdministerClickListener listener;

    public VaccinationAdapter(OnAdministerClickListener listener) {
        this.listener = listener;
    }

    public void setVaccinations(List<Vaccination> list) {
        vaccinations.clear();

        if (list != null) {
            vaccinations.addAll(list);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VaccinationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaccination, parent, false);

        return new VaccinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VaccinationViewHolder holder, int position) {

        Vaccination vaccination = vaccinations.get(position);

        holder.tvTag.setText(
                "Tag: " + vaccination.getTagNumber()
                        + "\nPen: " + vaccination.getPenNumber()
                        + "\nDue: " + vaccination.getDate()
        );

        holder.tvVaccine.setText(vaccination.getVaccineName());
        holder.tvStatus.setText(vaccination.getStatus());

        holder.btnAdminister.setOnClickListener(v ->
                listener.onAdministerClick(vaccination)
        );
    }

    @Override
    public int getItemCount() {
        return vaccinations.size();
    }

    static class VaccinationViewHolder extends RecyclerView.ViewHolder {

        TextView tvTag;
        TextView tvVaccine;
        TextView tvStatus;
        Button btnAdminister;

        VaccinationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTag = itemView.findViewById(R.id.tvTag);
            tvVaccine = itemView.findViewById(R.id.tvVaccine);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAdminister = itemView.findViewById(R.id.btnAdminister);
        }
    }
}
