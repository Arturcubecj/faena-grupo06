package com.example.tarea2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    // ===== Credenciales válidas definidas en el código =====
    private static final String USUARIO_VALIDO = "admin";
    private static final String CLAVE_VALIDA   = "admin123";

    private TextInputLayout tilUsuario, tilClave;
    private TextInputEditText etUsuario, etClave;
    private Button btnIniciarSesion;
    private Button btnCrearCuenta;
    private CheckBox cbMantenerSesion;
    private SesionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si ya existe el SharedPreference, se entra directo a la pantalla principal
        sesion = new SesionManager(this);
        if (sesion.haySesion()) {
            irAPantallaPrincipal();
            return;   // no se carga el diseño del Login
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Evita que el contenido quede debajo de la barra de estado/navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Referencias a los componentes del layout
        tilUsuario       = findViewById(R.id.tilUsuario);
        tilClave         = findViewById(R.id.tilClave);
        etUsuario        = findViewById(R.id.etUsuario);
        etClave          = findViewById(R.id.etClave);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        cbMantenerSesion = findViewById(R.id.cbMantenerSesion);

        btnIniciarSesion.setOnClickListener(v -> validarIngreso());

        // Botón "Crear cuenta": abre la pantalla de registro
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));
    }

    /** Valida los datos ingresados por el usuario y muestra el Toast correspondiente. */
    private void validarIngreso() {
        String usuario = textoDe(etUsuario).trim();
        String clave   = textoDe(etClave);

        // Limpiar errores anteriores
        tilUsuario.setError(null);
        tilClave.setError(null);

        // 1) Validación de campos vacíos
        boolean hayVacios = false;
        if (TextUtils.isEmpty(usuario)) {
            tilUsuario.setError(getString(R.string.error_usuario_vacio));
            hayVacios = true;
        }
        if (TextUtils.isEmpty(clave)) {
            tilClave.setError(getString(R.string.error_password_vacio));
            hayVacios = true;
        }
        if (hayVacios) {
            return;
        }

        // 2) Comparación con las credenciales definidas en el código
        if (usuario.equals(USUARIO_VALIDO) && clave.equals(CLAVE_VALIDA)) {
            Toast.makeText(this, getString(R.string.msg_acceso),
                    Toast.LENGTH_SHORT).show();

            // Si el usuario marcó "Mantener sesión", se graba nombre y clave
            if (cbMantenerSesion.isChecked()) {
                sesion.guardarSesion(usuario, clave);
            }

            irAPantallaPrincipal();
        } else {
            Toast.makeText(this, getString(R.string.msg_datos_inco),
                    Toast.LENGTH_SHORT).show();
            etClave.setText("");      // limpia la contraseña tras un intento fallido
            etClave.requestFocus();
        }
    }

    /** Abre la pantalla principal y cierra el Login para que "Atrás" no regrese a él. */
    private void irAPantallaPrincipal() {
        startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
        finish();
    }

    private String textoDe(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}