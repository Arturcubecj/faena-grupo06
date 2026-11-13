package com.example.tarea2;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y recupera los registros del formulario en un archivo de texto
 * en la memoria externa de la app. Cada registro es una línea y cada campo
 * va separado por ";".
 */
public class ArchivoRegistros {

    private static final String NOMBRE_ARCHIVO = "registros_faena.txt";
    private static final String SEPARADOR      = ";";
    private static final String TAG            = "ARCHIVO";

    private final File archivo;

    public ArchivoRegistros(Context context) {
        // Memoria externa propia de la app (no necesita permisos):
        // /storage/emulated/0/Android/data/com.example.tarea2/files/registros_faena.txt
        File carpeta = context.getExternalFilesDir(null);
        archivo = new File(carpeta, NOMBRE_ARCHIVO);
    }

    /** true si la memoria externa está montada y se puede escribir. */
    public boolean memoriaDisponible() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** Agrega un registro al final del archivo: campo1;campo2;...;campoN */
    public boolean guardar(String... campos) {
        if (!memoriaDisponible()) {
            Log.e(TAG, "La memoria externa no está disponible");
            return false;
        }
        String linea = TextUtils.join(SEPARADOR, campos);

        // true = modo "append": agrega la línea sin borrar las anteriores
        try (OutputStreamWriter escritor = new OutputStreamWriter(
                new FileOutputStream(archivo, true), StandardCharsets.UTF_8)) {
            escritor.write(linea + "\n");
            Log.i(TAG, "Guardado en " + archivo.getAbsolutePath() + ": " + linea);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error al guardar el registro", e);
            return false;
        }
    }

    /** Lee todas las líneas del archivo y separa cada una por ";". */
    public List<String[]> leer() {
        List<String[]> registros = new ArrayList<>();
        if (!archivo.exists()) {
            return registros;
        }
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(
                new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    registros.add(linea.split(SEPARADOR, -1));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error al leer el archivo", e);
        }
        return registros;
    }

    public String getRuta() {
        return archivo.getAbsolutePath();
    }
}
