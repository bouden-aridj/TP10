package com.example.tp10.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.tp10.R;
import com.example.tp10.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    public interface OnAppointmentDeleteListener {
        void onDelete(Appointment appointment);
    }

    private final Context context;
    private final OnAppointmentDeleteListener deleteListener;

    public AppointmentAdapter(Context context, List<Appointment> appointments, OnAppointmentDeleteListener deleteListener) {
        super(context, 0, appointments);
        this.context = context;
        this.deleteListener = deleteListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        }

        Appointment app = getItem(position);
        if (app != null) {
            TextView tvPatientName = convertView.findViewById(R.id.tvPatientName);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            TextView tvPatientInfo = convertView.findViewById(R.id.tvPatientInfo);
            TextView tvDoctorSpecialty = convertView.findViewById(R.id.tvDoctorSpecialty);
            TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);
            TextView tvNotes = convertView.findViewById(R.id.tvNotes);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

            tvPatientName.setText(app.getFirstName() + " " + app.getLastName());
            
            String info = app.getEmail() + " • " + app.getPhone() + " • " + app.getAge() + " yrs (" + app.getSex() + ")";
            tvPatientInfo.setText(info);

            tvDoctorSpecialty.setText(app.getDoctorName() + " — " + app.getSpecialty());
            tvDateTime.setText("📅 " + app.getAppointmentDate() + "  🕒 " + app.getAppointmentTime());

            if (app.getNotes() != null && !app.getNotes().trim().isEmpty()) {
                tvNotes.setText("Notes: " + app.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Status Badge Formatting
            String status = app.getStatus() != null ? app.getStatus() : "Scheduled";
            tvStatus.setText(status);

            if ("Confirmed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_confirmed));
                tvStatus.setTextColor(Color.parseColor("#00390A"));
            } else if ("Completed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_completed));
                tvStatus.setTextColor(Color.parseColor("#2D126B"));
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_cancelled));
                tvStatus.setTextColor(Color.parseColor("#690005"));
            } else {
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_scheduled));
                tvStatus.setTextColor(Color.parseColor("#381E72"));
            }

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(app);
                }
            });
        }

        return convertView;
    }
}
