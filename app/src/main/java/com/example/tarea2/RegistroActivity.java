package com.example.tarea2;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class RegistroActivity extends AppCompatActivity
        implements DatePickerFragment.OnFechaSeleccionadaListener {

    // Etiqueta para filtrar en el Logcat
    private static final String TAG = "REGISTRO";

    private TextInputLayout tilCedula, tilNombres, tilApellidos, tilEdad, tilFechaNac;
    private TextInputEditText etCedula, etNombres, etApellidos, etEdad, etFechaNac;
    private Spinner spNacionalidad, spGenero;
    private RadioGroup rgEstadoCivil;
    private RatingBar rbIngles;
    private Button btnRegistrar, btnBorrar, btnCancelar;
    private Button btnVerRegistros;
    private ArchivoRegistros archivo;

    // Etiquetas y mensajes de error de los campos que no tienen setError
    private TextView lblNacionalidad, lblGenero, lblEstadoCivil, lblNivelIngles;
    private TextView tvErrNacionalidad, tvErrGenero, tvErrEstadoCivil, tvErrIngles;

    // Colores
    private int colorError, colorTextoNormal, colorPrimario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        colorError       = ContextCompat.getColor(this, R.color.color_error);
        colorTextoNormal = ContextCompat.getColor(this, R.color.color_texto_oscuro);
        colorPrimario    = ContextCompat.getColor(this, R.color.color_primario);

        enlazarVistas();
        aplicarFiltrosSoloTexto();
        quitarErrorAlCorregir();

        // Al tocar la fecha se abre el calendario (Picker Fragment)
        etFechaNac.setOnClickListener(v -> mostrarCalendario());
        tilFechaNac.setStartIconOnClickListener(v -> mostrarCalendario());

        btnRegistrar.setOnClickListener(v -> registrar());
        btnBorrar.setOnClickListener(v -> borrar());
        btnCancelar.setOnClickListener(v -> cancelar());

        archivo = new ArchivoRegistros(this);
        btnVerRegistros = findViewById(R.id.btnVerRegistros);
        btnVerRegistros.setOnClickListener(v -> verRegistros());
    }

    /** Obtiene las referencias de los componentes del layout. */
    private void enlazarVistas() {
        tilCedula      = findViewById(R.id.tilCedula);
        tilNombres     = findViewById(R.id.tilNombres);
        tilApellidos   = findViewById(R.id.tilApellidos);
        tilEdad        = findViewById(R.id.tilEdad);
        tilFechaNac    = findViewById(R.id.tilFechaNac);

        etCedula       = findViewById(R.id.etCedula);
        etNombres      = findViewById(R.id.etNombres);
        etApellidos    = findViewById(R.id.etApellidos);
        etEdad         = findViewById(R.id.etEdad);
        etFechaNac     = findViewById(R.id.etFechaNac);

        spNacionalidad = findViewById(R.id.spNacionalidad);
        spGenero       = findViewById(R.id.spGenero);
        rgEstadoCivil  = findViewById(R.id.rgEstadoCivil);
        rbIngles       = findViewById(R.id.rbIngles);

        lblNacionalidad   = findViewById(R.id.lblNacionalidad);
        lblGenero         = findViewById(R.id.lblGenero);
        lblEstadoCivil    = findViewById(R.id.lblEstadoCivil);
        lblNivelIngles    = findViewById(R.id.lblNivelIngles);
        tvErrNacionalidad = findViewById(R.id.tvErrNacionalidad);
        tvErrGenero       = findViewById(R.id.tvErrGenero);
        tvErrEstadoCivil  = findViewById(R.id.tvErrEstadoCivil);
        tvErrIngles       = findViewById(R.id.tvErrIngles);

        btnRegistrar   = findViewById(R.id.btnRegistrar);
        btnBorrar      = findViewById(R.id.btnBorrar);
        btnCancelar    = findViewById(R.id.btnCancelar);
    }

    /** Nombres y Apellidos: solo se aceptan letras (con tildes y ñ) y espacios. */
    private void aplicarFiltrosSoloTexto() {
        InputFilter soloLetras = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
                    return "";   // rechaza números y símbolos
                }
            }
            return null;         // acepta el texto
        };
        InputFilter[] filtros = { soloLetras, new InputFilter.LengthFilter(50) };
        etNombres.setFilters(filtros);
        etApellidos.setFilters(filtros);
    }

    /** Cuando el usuario corrige un campo marcado en rojo, el error desaparece. */
    private void quitarErrorAlCorregir() {
        quitarErrorAlEscribir(etCedula, tilCedula);
        quitarErrorAlEscribir(etNombres, tilNombres);
        quitarErrorAlEscribir(etApellidos, tilApellidos);
        quitarErrorAlEscribir(etEdad, tilEdad);

        AdapterView.OnItemSelectedListener alElegir = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion > 0) {
                    if (parent == spNacionalidad) quitarError(spNacionalidad, lblNacionalidad, tvErrNacionalidad);
                    else quitarError(spGenero, lblGenero, tvErrGenero);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
        spNacionalidad.setOnItemSelectedListener(alElegir);
        spGenero.setOnItemSelectedListener(alElegir);

        rgEstadoCivil.setOnCheckedChangeListener((grupo, idMarcado) -> {
            if (idMarcado != -1) quitarErrorEstadoCivil();
        });

        rbIngles.setOnRatingBarChangeListener((barra, estrellas, delUsuario) -> {
            if (estrellas > 0) quitarErrorIngles();
        });
    }

    private void quitarErrorAlEscribir(TextInputEditText campo, TextInputLayout contenedor) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                contenedor.setError(null);
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void mostrarCalendario() {
        new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
    }

    /** Recibe la fecha elegida en el calendario. */
    @Override
    public void onFechaSeleccionada(int anio, int mes, int dia) {
        // mes viene de 0 a 11, por eso se suma 1
        etFechaNac.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio));
        tilFechaNac.setError(null);
    }

    // ===================== BOTÓN REGISTRAR =====================
    private void registrar() {
        if (!validarFormulario()) {
            Toast.makeText(this, R.string.msg_revise_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        String cedula       = texto(etCedula);
        String nombres      = texto(etNombres);
        String apellidos    = texto(etApellidos);
        String edad         = texto(etEdad);
        String nacionalidad = spNacionalidad.getSelectedItem().toString();
        String genero       = spGenero.getSelectedItem().toString();
        RadioButton rbSel   = findViewById(rgEstadoCivil.getCheckedRadioButtonId());
        String estadoCivil  = rbSel.getText().toString();
        String fechaNac     = texto(etFechaNac);
        int nivelIngles     = (int) rbIngles.getRating();

        // Mostrar toda la información registrada en el LOG
        Log.i(TAG, "========== NUEVO REGISTRO ==========");
        Log.i(TAG, "Cédula:              " + cedula);
        Log.i(TAG, "Nombres:             " + nombres);
        Log.i(TAG, "Apellidos:           " + apellidos);
        Log.i(TAG, "Edad:                " + edad);
        Log.i(TAG, "Nacionalidad:        " + nacionalidad);
        Log.i(TAG, "Género:              " + genero);
        Log.i(TAG, "Estado civil:        " + estadoCivil);
        Log.i(TAG, "Fecha de nacimiento: " + fechaNac);
        Log.i(TAG, "Nivel de inglés:     " + nivelIngles + " de 5 estrellas");
        Log.i(TAG, "====================================");

        // Guardar el registro en la memoria externa, campos separados por ";"
        boolean guardado = archivo.guardar(cedula, nombres, apellidos, edad, nacionalidad,
                genero, estadoCivil, fechaNac, String.valueOf(nivelIngles));

        Toast.makeText(this, guardado ? R.string.msg_guardado_archivo : R.string.msg_error_archivo,
                Toast.LENGTH_LONG).show();

        // Luego de "guardar", el formulario queda en blanco para un nuevo registro
        limpiarFormulario();
    }

    /** Revisa TODOS los campos y marca en rojo cada uno que esté vacío o mal. */
    private boolean validarFormulario() {
        limpiarErrores();
        boolean valido = true;

        if (texto(etCedula).length() != 10) {
            tilCedula.setError(getString(R.string.error_cedula));
            valido = false;
        }
        if (TextUtils.isEmpty(texto(etNombres))) {
            tilNombres.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (TextUtils.isEmpty(texto(etApellidos))) {
            tilApellidos.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }

        String edad = texto(etEdad);
        if (TextUtils.isEmpty(edad) || Integer.parseInt(edad) < 1 || Integer.parseInt(edad) > 120) {
            tilEdad.setError(getString(R.string.error_edad));
            valido = false;
        }
        if (TextUtils.isEmpty(texto(etFechaNac))) {
            tilFechaNac.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }

        // Spinner, RadioGroup y RatingBar: se pintan de rojo con un mensaje debajo
        if (spNacionalidad.getSelectedItemPosition() == 0) {
            marcarErrorSpinner(spNacionalidad, lblNacionalidad, tvErrNacionalidad,
                    R.string.error_seleccione_nacionalidad);
            valido = false;
        }
        if (spGenero.getSelectedItemPosition() == 0) {
            marcarErrorSpinner(spGenero, lblGenero, tvErrGenero, R.string.error_seleccione_genero);
            valido = false;
        }
        if (rgEstadoCivil.getCheckedRadioButtonId() == -1) {
            mostrarError(lblEstadoCivil, tvErrEstadoCivil, R.string.error_seleccione_estado_civil);
            pintarRadioButtons(colorError);
            valido = false;
        }
        if (rbIngles.getRating() == 0) {
            mostrarError(lblNivelIngles, tvErrIngles, R.string.error_seleccione_nivel_ingles);
            rbIngles.setProgressBackgroundTintList(ColorStateList.valueOf(colorError));
            valido = false;
        }
        return valido;
    }

    // ===================== BOTÓN BORRAR =====================
    private void borrar() {
        limpiarFormulario();
        Toast.makeText(this, R.string.msg_formulario_borrado, Toast.LENGTH_SHORT).show();
    }

    /** Deja en blanco cada EditText y reinicia el resto del formulario. */
    private void limpiarFormulario() {
        etCedula.setText("");
        etNombres.setText("");
        etApellidos.setText("");
        etEdad.setText("");
        etFechaNac.setText("");

        spNacionalidad.setSelection(0);
        spGenero.setSelection(0);
        rgEstadoCivil.clearCheck();
        rbIngles.setRating(0);

        limpiarErrores();
        etCedula.requestFocus();
    }

    // ===================== BOTÓN CANCELAR =====================
    /** Cierra esta pantalla y regresa al Login. */
    private void cancelar() {
        finish();
    }

    // ===================== BOTÓN VER REGISTROS =====================
    /** Recupera los datos del archivo y los muestra en una ventana modal. */
    private void verRegistros() {
        new RegistrosDialogFragment().show(getSupportFragmentManager(), "registros");
    }

    // ===================== Marcar y quitar errores =====================
    private void mostrarError(TextView etiqueta, TextView mensaje, int textoError) {
        etiqueta.setTextColor(colorError);
        mensaje.setText(textoError);
        mensaje.setVisibility(View.VISIBLE);
    }

    private void ocultarError(TextView etiqueta, TextView mensaje) {
        etiqueta.setTextColor(colorTextoNormal);
        mensaje.setVisibility(View.GONE);
    }

    private void marcarErrorSpinner(Spinner spinner, TextView etiqueta, TextView mensaje, int textoError) {
        mostrarError(etiqueta, mensaje, textoError);
        ViewCompat.setBackgroundTintList(spinner, ColorStateList.valueOf(colorError));
    }

    private void quitarError(Spinner spinner, TextView etiqueta, TextView mensaje) {
        ocultarError(etiqueta, mensaje);
        ViewCompat.setBackgroundTintList(spinner, null);
    }

    private void quitarErrorEstadoCivil() {
        ocultarError(lblEstadoCivil, tvErrEstadoCivil);
        pintarRadioButtons(colorPrimario);
    }

    private void quitarErrorIngles() {
        ocultarError(lblNivelIngles, tvErrIngles);
        rbIngles.setProgressBackgroundTintList(null);
    }

    /** Cambia el color del círculo de cada RadioButton del grupo. */
    private void pintarRadioButtons(int color) {
        for (int i = 0; i < rgEstadoCivil.getChildCount(); i++) {
            View hijo = rgEstadoCivil.getChildAt(i);
            if (hijo instanceof CompoundButton) {
                CompoundButtonCompat.setButtonTintList((CompoundButton) hijo, ColorStateList.valueOf(color));
            }
        }
    }

    private void limpiarErrores() {
        tilCedula.setError(null);
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilEdad.setError(null);
        tilFechaNac.setError(null);

        quitarError(spNacionalidad, lblNacionalidad, tvErrNacionalidad);
        quitarError(spGenero, lblGenero, tvErrGenero);
        quitarErrorEstadoCivil();
        quitarErrorIngles();
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
