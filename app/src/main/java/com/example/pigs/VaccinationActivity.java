
package com.example.pigs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.adapter.VaccinationAdapter;
import com.example.pigs.model.Pig;
import com.example.pigs.model.Vaccine;
import com.example.pigs.model.VaccinationLog;
import com.example.pigs.network.ApiClient;
import com.example.pigs.viewmodel.DashboardViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VaccinationActivity extends AppCompatActivity {

    private DashboardViewModel viewModel;

    private RecyclerView rvVaccinations;
    private EditText etSearch;
    private TextView btnBack;
    private Button btnAddPig;
    private Button btnScheduleVaccination;

    private TextView tvImmunityPercentage;
    private TextView tvImmunityCount;
    private TextView tvImmunityStatus;

    private com.example.pigs.view.ImmunityPieView immunityPieView;

    private VaccinationAdapter adapter;
    private TableLayout tableVaccinationLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vaccination);

        initializeViews();
        setupViewModel();
        setupRecyclerView();
        observeData();
        setupListeners();
    }

    private void initializeViews() {

        rvVaccinations = findViewById(R.id.rvVaccinations);
        etSearch = findViewById(R.id.etSearch);

        btnBack = findViewById(R.id.btnBack);

        tableVaccinationLogs = findViewById(R.id.tableVaccinationLogs);
        btnAddPig = findViewById(R.id.btnAddPig);
        btnScheduleVaccination = findViewById(R.id.btnScheduleVaccination);

        tvImmunityPercentage = findViewById(R.id.tvImmunityPercentage);
        tvImmunityCount = findViewById(R.id.tvImmunityCount);
        tvImmunityStatus = findViewById(R.id.tvImmunityStatus);

        immunityPieView = findViewById(R.id.immunityPieView);
    }

    private void setupViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(DashboardViewModel.class);
    }

    private void setupRecyclerView() {

        adapter = new VaccinationAdapter(vaccination ->
                new AlertDialog.Builder(this)
                        .setTitle("Administer Vaccine")
                        .setMessage("Mark " + vaccination.getVaccineName()
                                + " for pig " + vaccination.getTagNumber()
                                + " as administered?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", (dialog, which) -> {

                            viewModel.administerVaccination(
                                    vaccination.getVaccinationId(),
                                    new ApiClient.ApiCallback() {

                                        @Override
                                        public void onSuccess(JSONObject response) {
                                            Toast.makeText(
                                                    VaccinationActivity.this,
                                                    "Vaccination administered successfully.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }

                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(
                                                    VaccinationActivity.this,
                                                    message,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }
                            );
                        })
                        .show()
        );

        rvVaccinations.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvVaccinations.setAdapter(adapter);
        rvVaccinations.setNestedScrollingEnabled(false);
    }

    private void observeData() {

        viewModel.getVaccinations().observe(this, vaccinations -> {
            adapter.setVaccinations(vaccinations);
        });

        viewModel.getLogs().observe(this, logs -> {
            displayVaccinationLogs(logs);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getImmunityPercentage().observe(this, percentage -> {

            int score = percentage == null ? 0 : percentage;

            tvImmunityPercentage.setText(score + "% Protected");

            immunityPieView.setProtectedPercentage(score);

            if (score >= 80) {
                tvImmunityStatus.setText("✔ Good");
                tvImmunityStatus.setTextColor(
                        android.graphics.Color.rgb(0, 155, 53)
                );
            } else {
                tvImmunityStatus.setText("Needs Attention");
                tvImmunityStatus.setTextColor(
                        android.graphics.Color.rgb(217, 94, 104)
                );
            }
        });

        viewModel.getProtectedPigs().observe(this, protectedCount -> {
            updateImmunityCount();
        });

        viewModel.getTotalPigs().observe(this, totalCount -> {
            updateImmunityCount();
        });

        viewModel.getProtectedPigs().observe(this, protectedCount -> {

            Integer total = viewModel.getTotalPigs().getValue();

            int totalCount = total == null ? 0 : total;

            tvImmunityCount.setText(
                    protectedCount + " / " + totalCount + " pigs protected"
            );
        });

        viewModel.getTotalPigs().observe(this, totalCount -> {

            Integer protectedCount = viewModel.getProtectedPigs().getValue();

            int protectedValue = protectedCount == null ? 0 : protectedCount;

            tvImmunityCount.setText(
                    protectedValue + " / " + totalCount + " pigs protected"
            );

            if (totalCount == 0) {
                tvImmunityStatus.setText("No pigs registered");
                tvImmunityStatus.setTextColor(
                        android.graphics.Color.GRAY
                );
            }
        });
    }

    private void displayVaccinationLogs(List<VaccinationLog> logs) {

        // Keep the header row and remove previous records.
        if (tableVaccinationLogs.getChildCount() > 1) {
            tableVaccinationLogs.removeViews(
                    1,
                    tableVaccinationLogs.getChildCount() - 1
            );
        }

        if (logs == null || logs.isEmpty()) {
            return;
        }

        for (VaccinationLog log : logs) {

            TableRow row = new TableRow(this);

            row.addView(createTableCell(log.getDate()));
            row.addView(createTableCell(log.getTagNumber()));
            row.addView(createTableCell(log.getVaccineName()));
            row.addView(createTableCell(String.valueOf(log.getDose())));
            row.addView(createTableCell(log.getStaffId()));

            tableVaccinationLogs.addView(row);
        }
    }

    private TextView createTableCell(String value) {

        TextView textView = new TextView(this);

        textView.setText(value);
        textView.setTextSize(12);
        textView.setTextColor(android.graphics.Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(12, 16, 12, 16);

        return textView;
    }

    private void setupListeners() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                viewModel.searchVaccination(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnAddPig.setOnClickListener(v -> {
            Toast.makeText(this, "Add Pig clicked!", Toast.LENGTH_SHORT).show();
            showAddPigDialog();
        });

        btnScheduleVaccination.setOnClickListener(v -> {
            Toast.makeText(this, "Schedule Vaccination clicked!", Toast.LENGTH_SHORT).show();
            showScheduleVaccinationDialog();
        });

        ;
    }

    // ADD PIG DIALOG


    private void showAddPigDialog() {

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_add_pig, null
        );

        EditText etTagNumber = dialogView.findViewById(
                R.id.etDialogTagNumber
        );

        EditText etPenNumber = dialogView.findViewById(
                R.id.etDialogPenNumber
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Pig")
                .setView(dialogView)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .setPositiveButton("Save Pig", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button saveButton = dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            saveButton.setOnClickListener(v -> {

                String tagNumber = etTagNumber.getText().toString().trim();
                String penNumber = etPenNumber.getText().toString().trim();

                if (tagNumber.isEmpty() || penNumber.isEmpty()) {
                    Toast.makeText(
                            this,
                            "Please enter both tag number and pen number.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                saveButton.setEnabled(false);

                viewModel.addPig(tagNumber, penNumber,
                        new ApiClient.ApiCallback() {

                            @Override
                            public void onSuccess(JSONObject response) {
                                Toast.makeText(
                                        VaccinationActivity.this,
                                        "Pig added successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(String message) {
                                saveButton.setEnabled(true);

                                Toast.makeText(
                                        VaccinationActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
            });
        });

        dialog.show();
    }


    // SCHEDULE VACCINATION DIALOG


    private void showScheduleVaccinationDialog() {

        List<Pig> pigList = viewModel.getPigs().getValue();
        List<Vaccine> vaccineList = viewModel.getVaccines().getValue();

        if (pigList == null || pigList.isEmpty()) {
            Toast.makeText(this,
                    "Please add a pig first or wait for the pig list to load.",
                    Toast.LENGTH_LONG).show();
            viewModel.loadPigs();
            return;
        }

        if (vaccineList == null || vaccineList.isEmpty()) {
            Toast.makeText(this,
                    "No vaccines found. Please check your vaccine records.",
                    Toast.LENGTH_LONG).show();
            viewModel.loadVaccines();
            return;
        }

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_schedule_vaccination, null
        );

        Spinner spinnerPig = dialogView.findViewById(
                R.id.spinnerDialogPig
        );

        Spinner spinnerVaccine = dialogView.findViewById(
                R.id.spinnerDialogVaccine
        );

        EditText etDoseNumber = dialogView.findViewById(
                R.id.etDialogDoseNumber
        );

        EditText etDose = dialogView.findViewById(
                R.id.etDialogDose
        );

        TextView tvDate = dialogView.findViewById(
                R.id.tvDialogScheduledDate
        );

        ArrayAdapter<Pig> pigAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                pigList
        );

        pigAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPig.setAdapter(pigAdapter);

        ArrayAdapter<Vaccine> vaccineAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                vaccineList
        );

        vaccineAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerVaccine.setAdapter(vaccineAdapter);

        Calendar calendar = Calendar.getInstance();

        tvDate.setOnClickListener(v -> {

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {

                        String selectedDate = String.format(
                                Locale.getDefault(),
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayOfMonth
                        );

                        tvDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMinDate(
                    System.currentTimeMillis() - 1000
            );

            datePicker.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Schedule Vaccination")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Schedule", null)
                .create();

        dialog.show();

        Button scheduleButton = dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        );

        scheduleButton.setOnClickListener(v -> {

            Pig selectedPig = (Pig) spinnerPig.getSelectedItem();

            Vaccine selectedVaccine =
                    (Vaccine) spinnerVaccine.getSelectedItem();

            String doseNumberText = etDoseNumber.getText().toString().trim();
            String doseText = etDose.getText().toString().trim();
            String date = tvDate.getText().toString().trim();

            if (selectedPig == null || selectedVaccine == null
                    || doseNumberText.isEmpty()
                    || doseText.isEmpty()
                    || date.equals("Select scheduled date")) {

                Toast.makeText(this,
                        "Please complete all vaccination details.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                int doseNumber = Integer.parseInt(doseNumberText);
                double dose = Double.parseDouble(doseText);

                if (doseNumber < 1 || dose <= 0) {
                    Toast.makeText(this,
                            "Dose number and dose amount must be greater than zero.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                scheduleButton.setEnabled(false);

                viewModel.scheduleVaccination(
                        selectedPig.getPigId(),
                        selectedVaccine.getVaccineId(),
                        doseNumber,
                        dose,
                        date,
                        new ApiClient.ApiCallback() {

                            @Override
                            public void onSuccess(JSONObject response) {
                                Toast.makeText(
                                        VaccinationActivity.this,
                                        "Vaccination scheduled successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(String message) {
                                scheduleButton.setEnabled(true);

                                Toast.makeText(
                                        VaccinationActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Please enter valid dose values.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // DIALOG HELPERS


    private void updateImmunityCount() {

        Integer protectedCount = viewModel.getProtectedPigs().getValue();
        Integer totalCount = viewModel.getTotalPigs().getValue();

        int protectedValue = protectedCount == null ? 0 : protectedCount;
        int totalValue = totalCount == null ? 0 : totalCount;

        tvImmunityCount.setText(
                protectedValue + " / " + totalValue + " pigs protected"
        );
    }
}
