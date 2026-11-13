package com.example.tarea2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

/** Ventana modal que muestra los registros recuperados del archivo (no usa Toast). */
public class RegistrosDialogFragment extends DialogFragment {

    private static final String[] ETIQUETAS = {
            "Cédula", "Nombres", "Apellidos", "Edad", "Nacionalidad",
            "Género", "Estado civil", "Fecha de nacimiento", "Nivel de inglés"
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);   // solo se cierra con el botón
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.dialog_registros, container, false);

        TextView tvRuta      = vista.findViewById(R.id.tvRutaArchivo);
        TextView tvRegistros = vista.findViewById(R.id.tvRegistros);
        Button btnCerrar     = vista.findViewById(R.id.btnCerrarRegistros);

        ArchivoRegistros archivo = new ArchivoRegistros(requireContext());
        tvRuta.setText(archivo.getRuta());
        tvRegistros.setText(formatear(archivo.leer()));

        btnCerrar.setOnClickListener(v -> dismiss());
        return vista;
    }

    /** Convierte cada línea "a;b;c..." en un bloque legible con sus etiquetas. */
    private String formatear(List<String[]> registros) {
        if (registros.isEmpty()) {
            return getString(R.string.msg_sin_registros);
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < registros.size(); i++) {
            String[] campos = registros.get(i);
            texto.append("Registro ").append(i + 1).append("\n");
            for (int j = 0; j < ETIQUETAS.length && j < campos.length; j++) {
                texto.append("• ").append(ETIQUETAS[j]).append(": ").append(campos[j]).append("\n");
            }
            texto.append("\n");
        }
        return texto.toString().trim();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialogo = getDialog();
        if (dialogo != null && dialogo.getWindow() != null) {
            int ancho = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialogo.getWindow().setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
