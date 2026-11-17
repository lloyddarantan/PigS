
package com.example.pigs.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pigs.model.Pig;
import com.example.pigs.model.Vaccine;
import com.example.pigs.model.Vaccination;
import com.example.pigs.model.VaccinationLog;
import com.example.pigs.network.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Pig>> pigs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Vaccine>> vaccines = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Vaccination>> vaccinations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<VaccinationLog>> logs = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private List<Vaccination> allVaccinations = new ArrayList<>();

    private final MutableLiveData<Integer> immunityPercentage =
            new MutableLiveData<>(0);

    private final MutableLiveData<Integer> protectedPigs =
            new MutableLiveData<>(0);

    private final MutableLiveData<Integer> totalPigs =
            new MutableLiveData<>(0);

    public LiveData<Integer> getImmunityPercentage() {
        return immunityPercentage;
    }

    public LiveData<Integer> getProtectedPigs() {
        return protectedPigs;
    }

    public LiveData<Integer> getTotalPigs() {
        return totalPigs;
    }

    public DashboardViewModel() {
        loadPigs();
        loadVaccines();
        loadVaccinations();
        loadLogs();
        loadHerdImmunity();
    }

    public LiveData<List<Pig>> getPigs() {
        return pigs;
    }

    public LiveData<List<Vaccine>> getVaccines() {
        return vaccines;
    }

    public LiveData<List<Vaccination>> getVaccinations() {
        return vaccinations;
    }

    public LiveData<List<VaccinationLog>> getLogs() {
        return logs;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


    public void loadPigs() {

        ApiClient.get("get_pigs.php", new ApiClient.ApiCallback() {

            @Override
            public void onSuccess(JSONObject response) {

                android.util.Log.d("GET_PIGS", "Response: " + response);

                List<Pig> result = new ArrayList<>();
                JSONArray array = response.optJSONArray("pigs");

                if (array != null) {

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject item = array.optJSONObject(i);

                        if (item != null) {
                            result.add(new Pig(
                                    item.optInt("pig_id"),
                                    item.optString("tag_number"),
                                    item.optString("pen_number")
                            ));
                        }
                    }
                }

                pigs.setValue(result);

                android.util.Log.d(
                        "GET_PIGS",
                        "Number of pigs retrieved: " + result.size()
                );
            }

            @Override
            public void onError(String message) {

                android.util.Log.e("GET_PIGS", "Error: " + message);

                errorMessage.setValue(message);
            }
        });
    }


    public void loadVaccines() {
        ApiClient.get("get_vaccines.php", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<Vaccine> result = new ArrayList<>();
                JSONArray array = response.optJSONArray("vaccines");

                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);

                        if (item != null) {
                            result.add(new Vaccine(
                                    item.optInt("vaccine_id"),
                                    item.optString("vaccine_name"),
                                    item.optString("description", "")
                            ));
                        }
                    }
                }

                vaccines.setValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void loadVaccinations() {
        ApiClient.get("get_vaccinations.php", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<Vaccination> result = new ArrayList<>();
                JSONArray array = response.optJSONArray("vaccinations");

                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);

                        if (item != null) {
                            result.add(new Vaccination(
                                    item.optInt("vaccination_id"),
                                    item.optInt("pig_id"),
                                    item.optString("tag_number"),
                                    item.optString("pen_number"),
                                    item.optString("scheduled_date"),
                                    item.optString("vaccine_name"),
                                    item.optInt("dose_number"),
                                    (float) item.optDouble("dose", 1.0),
                                    item.optString("status")
                            ));
                        }
                    }
                }

                allVaccinations = result;
                vaccinations.setValue(new ArrayList<>(result));
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void loadLogs() {
        ApiClient.get("get_vaccination_logs.php", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<VaccinationLog> result = new ArrayList<>();
                JSONArray array = response.optJSONArray("logs");

                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);

                        if (item != null) {
                            result.add(new VaccinationLog(
                                    item.optString("administered_date"),
                                    item.optString("tag_number"),
                                    item.optString("vaccine_name"),
                                    (float) item.optDouble("dose", 0),
                                    item.optString("staff_id")
                            ));
                        }
                    }
                }

                logs.setValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void searchVaccination(String query) {
        List<Vaccination> filtered = new ArrayList<>();
        String search = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        for (Vaccination item : allVaccinations) {
            if (item.getTagNumber().toLowerCase(Locale.ROOT).contains(search)
                    || item.getVaccineName().toLowerCase(Locale.ROOT).contains(search)
                    || item.getPenNumber().toLowerCase(Locale.ROOT).contains(search)) {
                filtered.add(item);
            }
        }

        vaccinations.setValue(filtered);
    }

    public void addPig(String tagNumber, String penNumber,
                       ApiClient.ApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("tag_number", tagNumber);
            data.put("pen_number", penNumber);

            ApiClient.post("add_pig.php", data, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    loadPigs();
                    loadHerdImmunity();
                    callback.onSuccess(response);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }


            });

        } catch (Exception e) {
            callback.onError("Unable to prepare pig information.");
        }
    }

    public void scheduleVaccination(int pigId, int vaccineId,
                                    int doseNumber, double dose,
                                    String scheduledDate,
                                    ApiClient.ApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("pig_id", pigId);
            data.put("vaccine_id", vaccineId);
            data.put("dose_number", doseNumber);
            data.put("dose", dose);
            data.put("scheduled_date", scheduledDate);

            ApiClient.post("add_vaccination.php", data, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    loadVaccinations();
                    callback.onSuccess(response);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });

        } catch (Exception e) {
            callback.onError("Unable to prepare vaccination information.");
        }
    }

    public void administerVaccination(int vaccinationId,
                                      ApiClient.ApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("vaccination_id", vaccinationId);

            ApiClient.post("administer_vaccine.php", data, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    loadVaccinations();
                    loadLogs();
                    loadHerdImmunity();
                    callback.onSuccess(response);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });

        } catch (Exception e) {
            callback.onError("Unable to prepare administration request.");
        }
    }

    public void loadHerdImmunity() {

        ApiClient.get("get_herd_immunity.php", new ApiClient.ApiCallback() {

            @Override
            public void onSuccess(JSONObject response) {

                immunityPercentage.setValue(
                        response.optInt("percentage", 0)
                );

                protectedPigs.setValue(
                        response.optInt("protected_pigs", 0)
                );

                totalPigs.setValue(
                        response.optInt("total_pigs", 0)
                );
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }
}
