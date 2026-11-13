package com.example.tarea2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Maneja el archivo de preferencias (SharedPreferences) donde se guarda
 * la sesión del usuario: nombre y clave.
 */
public class SesionManager {

    // Nombre del archivo: /data/data/com.example.tarea2/shared_prefs/sesion_faena.xml
    private static final String ARCHIVO     = "sesion_faena";
    private static final String KEY_USUARIO = "usuario";
    private static final String KEY_CLAVE   = "clave";
    private static final String TAG         = "SESION";

    private final SharedPreferences prefs;

    public SesionManager(Context context) {
        // MODE_PRIVATE: solo nuestra app puede leer el archivo
        prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    /** Graba el nombre y la clave en el SharedPreference. */
    public void guardarSesion(String usuario, String clave) {
        prefs.edit()
                .putString(KEY_USUARIO, usuario)
                .putString(KEY_CLAVE, clave)
                .apply();
        Log.i(TAG, "Sesión guardada para el usuario: " + usuario);
    }

    /** true si el SharedPreference ya tiene una sesión guardada. */
    public boolean haySesion() {
        return prefs.contains(KEY_USUARIO) && prefs.contains(KEY_CLAVE);
    }

    public String obtenerUsuario() {
        return prefs.getString(KEY_USUARIO, "");
    }

    public String obtenerClave() {
        return prefs.getString(KEY_CLAVE, "");
    }

    /** Elimina todo el contenido del SharedPreference. */
    public void borrarSesion() {
        prefs.edit().clear().apply();
        Log.i(TAG, "Datos de sesión eliminados");
    }
}
