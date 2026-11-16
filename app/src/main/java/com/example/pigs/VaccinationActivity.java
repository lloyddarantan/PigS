package com.example.pigs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.adapter.VaccinationAdapter;
import com.example.pigs.view.VaccinationChartView;
import com.example.pigs.viewmodel.DashboardViewModel;

public class VaccinationActivity extends AppCompatActivity {

    private DashboardViewModel viewModel;

    private RecyclerView rvVaccinations;
    private EditText etSearch;
    private TextView btnBack;
    private Button btnContinue;

    private VaccinationAdapter adapter;

    private VaccinationChartView vaccinationChart;

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
        btnContinue = findViewById(R.id.btnContinue);

        vaccinationChart = findViewById(R.id.vaccinationChart);
    }

    private void setupViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(DashboardViewModel.class);
    }

    private void setupRecyclerView() {

        adapter = new VaccinationAdapter();

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

            vaccinationChart.setLogs(logs);

        });
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

        btnBack.setOnClickListener(v -> {

            finish();

        });

        btnContinue.setOnClickListener(v -> {

            Toast.makeText(
                    VaccinationActivity.this,
                    "Continue button clicked!",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }
}