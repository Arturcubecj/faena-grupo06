package com.example.tarea2;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
/**
 * Picker Fragment que muestra un calendario (CalendarView) para elegir
 * la fecha de nacimiento y devuelve la fecha a la Activity que lo abrió.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /** La Activity que abre el calendario debe implementar esta interfaz. */
    public interface OnFechaSeleccionadaListener {
        void onFechaSeleccionada(int anio, int mes, int dia);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Fecha inicial: la fecha actual
        Calendar hoy = Calendar.getInstance();
        int anio = hoy.get(Calendar.YEAR);
        int mes  = hoy.get(Calendar.MONTH);
        int dia  = hoy.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogo = new DatePickerDialog(requireContext(), this, anio, mes, dia);

        // No se permite elegir una fecha de nacimiento futura
        dialogo.getDatePicker().setMaxDate(System.currentTimeMillis());
        return dialogo;
    }

    @Override
    public void onDateSet(DatePicker view, int anio, int mes, int dia) {
        if (getActivity() instanceof OnFechaSeleccionadaListener) {
            ((OnFechaSeleccionadaListener) getActivity()).onFechaSeleccionada(anio, mes, dia);
        }
    }
}