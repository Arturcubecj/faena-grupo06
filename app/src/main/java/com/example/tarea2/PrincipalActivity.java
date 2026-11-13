package com.example.tarea2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class PrincipalActivity extends AppCompatActivity {

    private SesionManager sesion;
    private TextView tvUsuarioGuardado, tvEstadoSesion;
    private Button btnBorrarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        configurarToolbar();

        sesion = new SesionManager(this);

        tvUsuarioGuardado = findViewById(R.id.tvUsuarioGuardado);
        tvEstadoSesion    = findViewById(R.id.tvEstadoSesion);
        btnBorrarDatos    = findViewById(R.id.btnBorrarDatos);

        mostrarDatosSesion();
        btnBorrarDatos.setOnClickListener(v -> borrarDatos());
    }

    /** (Anderson) Usa la Toolbar del layout como barra de la app, para que muestre el menú. */
    private void configurarToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // ===================== MENÚ =====================

    /** Carga las opciones del menú en la Toolbar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    /** Qué hace cada opción del menú. */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_registrar) {
            startActivity(new Intent(this, RegistroActivity.class));
            return true;
        } else if (id == R.id.action_ver_registros) {
            new RegistrosDialogFragment().show(getSupportFragmentManager(), "registros");
            return true;
        } else if (id == R.id.action_acerca_de) {
            new AcercaDeDialogFragment().show(getSupportFragmentManager(), "acercaDe");
            return true;
        } else if (id == R.id.action_borrar_datos) {
            borrarDatos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===================== SESIÓN (T1) =====================

    /** Lee el SharedPreference y muestra si hay una sesión guardada. */
    private void mostrarDatosSesion() {
        if (sesion.haySesion()) {
            tvUsuarioGuardado.setText(getString(R.string.lbl_usuario_guardado, sesion.obtenerUsuario()));
            tvEstadoSesion.setText(R.string.estado_sesion_guardada);
        } else {
            tvUsuarioGuardado.setText(R.string.lbl_sin_datos);
            tvEstadoSesion.setText(R.string.estado_sesion_no_guardada);
        }
    }

    /** Botón "Borrar datos": elimina el contenido del SharedPreference y vuelve al Login. */
    private void borrarDatos() {
        sesion.borrarSesion();
        Toast.makeText(this, R.string.msg_datos_borrados, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        // Limpia la pila: no se puede volver a la principal con "Atrás"
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
