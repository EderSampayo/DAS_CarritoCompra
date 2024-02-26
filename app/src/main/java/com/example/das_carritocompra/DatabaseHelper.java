package com.example.das_carritocompra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "carrito_database";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper miDatabaseHelper;

    public static synchronized DatabaseHelper getMiDatabaseHelper(Context context) {
        if (miDatabaseHelper == null) {
            miDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return miDatabaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla para el carrito
        String createCarritoTable = "CREATE TABLE carrito_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, producto TEXT)";
        db.execSQL(createCarritoTable);

        // Crear la tabla para productos
        String createProductosTable = "CREATE TABLE productos_table (nombre TEXT PRIMARY KEY, tipo TEXT)";
        db.execSQL(createProductosTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas antiguas si existen
        db.execSQL("DROP TABLE IF EXISTS carrito_table");
        db.execSQL("DROP TABLE IF EXISTS productos_table");

        // Crear tablas nuevas
        onCreate(db);
    }

    // Método para agregar un elemento al carrito
    public boolean anadirAlCarrito(String producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("producto", producto);

        long result = db.insert("carrito_table", null, contentValues);

        return result != -1;
    }

    // Método para eliminar un elemento del carrito por nombre
    public boolean borrarDelCarrito(String producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("carrito_table", "producto = ?", new String[]{producto});

        return result > 0;
    }

    // Método para agregar un producto a la tabla productos
    public boolean anadirProducto(String nombre, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nombre", nombre);
        contentValues.put("tipo", tipo);

        long result = db.insert("productos_table", null, contentValues);

        return result != -1;
    }

    // Método para borrar un producto de la tabla productos por nombre
    public boolean borrarProducto(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("productos_table", "nombre = ?", new String[]{nombre});

        return result > 0;
    }

    // Método para obtener todos los productos
    public Cursor getTodosLosProductos() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM productos_table", null);
    }

    // Agrega este método a tu clase DatabaseHelper
    public Cursor obtenerCarrito() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM carrito_table", null);
    }

}
