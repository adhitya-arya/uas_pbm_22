package com.example.firestore.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firestore.MainActivity;
import com.example.firestore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private EditText namaMatkul;
    private Button btnSimpan, btnKembali;
    private ImageView foto;
    private TextView pilihJamMatkul;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerHari, spinnerKelas;
    private ProgressDialog progressDialog;

    private String id = ""; // menyimpan id dari firestore
    private String hariMatkul = ""; // menyimpan hari dari spinner
    private String kelasMatkul = ""; // menyimpan kelas dari spinner
    private String jamMatkul = ""; // menyimpan jam dari time picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // INISIALISASI VARIABEL DARI VIEW
        // NAMA MATKUL
        namaMatkul = findViewById(R.id.nama_matkul);

        // JAM MATKUL
        pilihJamMatkul = findViewById(R.id.jam_kelas);
        // JAM MATKUL KETIKA DI KLIK
        pilihJamMatkul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar waktuSekarang = Calendar.getInstance();
                int hour = waktuSekarang.get(Calendar.HOUR_OF_DAY);
                int minute = waktuSekarang.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InputActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // ISI TEXTVIEW DENGAN DATA JAM:MENIT
                        pilihJamMatkul.setText(selectedHour + ":" + selectedMinute);
                        jamMatkul = ""+selectedHour+":"+selectedMinute; // SIMPAN JAM:MENIT KE VARIABEL jamMatkul
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        // TOMBOL SIMPAN
        btnSimpan = findViewById(R.id.btn_simpan);
        // FOTO
        foto = findViewById(R.id.foto);
        // TOMBOL KEMBALI
        btnKembali = findViewById(R.id.btn_kembali);

        //SPINNER HARI
        spinnerHari =  findViewById(R.id.spinner_hari);
        // ISI LIST SPINNER DARI FILE string.xml di FOLDER VALUES
        ArrayAdapter<CharSequence> adapterSpinnerHari = ArrayAdapter.createFromResource(this, R.array.array_hari, android.R.layout.simple_spinner_item);
        adapterSpinnerHari.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHari.setAdapter(adapterSpinnerHari);
        // SPINNER KETIKA DI KLIK
        spinnerHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // VARIABEL hariMatkul DI ISI DARI LIST ARRAY HARI
                hariMatkul = adapterSpinnerHari.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // SPINNER KELAS
        spinnerKelas =  findViewById(R.id.spinner_kelas);
        ArrayAdapter<CharSequence> adapterSpinnerKelas = ArrayAdapter.createFromResource(this, R.array.array_kelas, android.R.layout.simple_spinner_item);
        adapterSpinnerKelas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKelas.setAdapter(adapterSpinnerKelas);
        // SPINNER KETIKA DI KLIK
        spinnerKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kelasMatkul = adapterSpinnerKelas.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // ANIMASI LOADING
        progressDialog = new ProgressDialog(InputActivity.this);
        progressDialog.setTitle("Loading ");
        progressDialog.setMessage("Menyimpan data ");

        // TOMBOL SIMPAN DATA
        btnSimpan.setOnClickListener(view -> {
            // CEK JIKA NAMA MATKUL KOSONG
           if (namaMatkul.getText().length() > 0){
               // PROSES SIMPAN DATA
               saveData(namaMatkul.getText().toString(), hariMatkul, kelasMatkul, jamMatkul);
           }else{
               Toast.makeText(getApplicationContext(), "Wajib Di Isi", Toast.LENGTH_SHORT).show();
           }
        });

        // TOMBOL KEMBALI KETIKA DI KLIK MENGARAH KE MainActivity / Halaman awal aplikasi
        btnKembali.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // AMBIL DATA INTENT DARI DaftarMatkulFragment UNTUK PROSES EDIT
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            namaMatkul.setText(intent.getStringExtra("nama_matkul"));
        }
    }

    // TAMBAH & UPDATE
    private void saveData(String namaMatkul, String hariMatkul, String kelasMatkul, String jamMatkul){
        Map<String, Object> user = new HashMap<>();
        // Deklarasi variable parameter ke user sebelum disimpan ke firestore
        user.put("nama_matkul", namaMatkul);
        user.put("hari_matkul", hariMatkul);
        user.put("kelas_matkul", kelasMatkul);
        user.put("jam_matkul", jamMatkul);

        if (id != null){
            // UPDATE DATA
            db.collection("daftar_matkul").document(id).set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            // TAMBAH DATA
            progressDialog.show();
            db.collection("daftar_matkul").add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

}