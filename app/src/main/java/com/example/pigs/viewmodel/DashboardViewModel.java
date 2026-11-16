package com.example.pigs.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pigs.model.Vaccination;
import com.example.pigs.model.VaccinationLog;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Vaccination>> vaccinations =
            new MutableLiveData<>();

    private final MutableLiveData<List<VaccinationLog>> logs =
            new MutableLiveData<>();

    private final MutableLiveData<String> searchQuery =
            new MutableLiveData<>("");

    public DashboardViewModel() {
        loadVaccinations();
        loadLogs();
    }

    public LiveData<List<Vaccination>> getVaccinations() {
        return vaccinations;
    }

    public LiveData<List<VaccinationLog>> getLogs() {
        return logs;
    }

    private void loadVaccinations() {

        List<Vaccination> list = new ArrayList<>();

        list.add(new Vaccination(
                "003", "1", "15 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        list.add(new Vaccination(
                "004", "1", "16 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        list.add(new Vaccination(
                "064", "5", "17 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        vaccinations.setValue(list);
    }

    private void loadLogs() {

        List<VaccinationLog> list = new ArrayList<>();

        list.add(new VaccinationLog(
                "06-10-26", "008",
                "PCV2 Vaccine", 1, "20001"
        ));

        list.add(new VaccinationLog(
                "06-09-26", "016",
                "PCV2 Vaccine", 2, "20001"
        ));

        list.add(new VaccinationLog(
                "06-07-26", "020",
                "PCV2 Vaccine", 1, "20001"
        ));

        logs.setValue(list);
    }

    public void searchVaccination(String query) {

        searchQuery.setValue(query);

        List<Vaccination> all = getOriginalVaccinations();
        List<Vaccination> filtered = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            vaccinations.setValue(all);
            return;
        }

        for (Vaccination item : all) {
            if (item.getTagNumber().toLowerCase()
                    .contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }

        vaccinations.setValue(filtered);
    }

    private List<Vaccination> getOriginalVaccinations() {

        List<Vaccination> list = new ArrayList<>();

        list.add(new Vaccination(
                "003", "1", "15 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        list.add(new Vaccination(
                "004", "1", "16 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        list.add(new Vaccination(
                "064", "5", "17 Jun",
                "PCV2 Vaccine", "Due Today"
        ));

        return list;
    }
}
