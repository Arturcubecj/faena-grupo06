package com.example.tarea2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/** Ventana modal "Acerca de" con la información y la foto del grupo. */
public class AcercaDeDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);   // es modal: solo se cierra con el botón Regresar
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.dialog_acerca_de, container, false);

        // Regresa a la actividad que abrió la ventana
        Button btnRegresar = vista.findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> dismiss());

        return vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialogo = getDialog();
        if (dialogo != null && dialogo.getWindow() != null) {
            // 90 % del ancho de la pantalla y fondo transparente para ver las esquinas redondeadas
            int ancho = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialogo.getWindow().setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}