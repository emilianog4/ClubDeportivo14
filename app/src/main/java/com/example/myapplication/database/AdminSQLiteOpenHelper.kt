package com.example.myapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import java.util.ArrayList
import com.example.myapplication.model.Socio


class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {


    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla para almacenar los datos de los socios:
        db?.execSQL("CREATE TABLE socios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellido TEXT NOT NULL, " +
                "dni TEXT NOT NULL UNIQUE, " +
                "fecha_nacimiento DATE NOT NULL, " +
                "celular TEXT NOT NULL, " +
                "domicilio TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "apto_fisico INTEGER NOT NULL, " + // Se usa INTEGER (0 para false, 1 para true)
                "fecha_inscripcion TEXT DEFAULT CURRENT_TIMESTAMP)")

        // Tabla para el login:
        db?.execSQL("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "nombre_usuario TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS socios")
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    /**
     * Agrega un nuevo socio a la tabla 'socios'.
     * @param socio El objeto Socio con todos los datos a guardar.
     * @return El ID de la nueva fila insertada, o -1 si ocurrió un error.
     */
    fun agregarSocio(socio: Socio): Long {
        // Obtenemos una referencia a la base de datos en modo escritura.
        val db = this.writableDatabase

        // ContentValues se usa para almacenar un conjunto de pares clave-valor.
        val contentValues = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("dni", socio.dni)
            put("fecha_nacimiento", socio.fechaNacimiento)
            put("celular", socio.celular)
            put("domicilio", socio.domicilio)
            put("email", socio.email)
            // Convertimos el Boolean 'aptoFisico' a un Integer (1 o 0) para la base de datos.
            put("apto_fisico", if (socio.aptoFisico) 1 else 0)
            // La 'fecha_inscripcion' se establece por defecto en la tabla,
            // por lo que no es estrictamente necesario añadirla aquí, a menos que
            // quieras especificar una fecha diferente a la actual.
        }

        // Insertamos la nueva fila. El método insert() devuelve el id de la fila o -1 si falla.
        val id = db.insert("socios", null, contentValues)

        // Es una buena práctica cerrar la conexión a la base de datos cuando terminas.
        db.close()

        return id
    }

    /**
     * Obtiene todos los socios de la base de datos.
     * @return Una lista de objetos Socio.
     */
    fun obtenerTodosLosSocios(): ArrayList<Socio> {
        val listaSocios = ArrayList<Socio>()
        // Sentencia SQL para seleccionar todos los registros de la tabla socios
        val selectQuery = "SELECT * FROM socios"
        // Obtenemos una referencia a la base de datos en modo lectura
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            db.execSQL(selectQuery) // Intenta ejecutar de nuevo si falla
            return ArrayList()
        }

        // Variables para almacenar los datos de cada socio
        var id: Int
        var nombre: String
        var apellido: String
        var dni: String
        var fechaNac: String
        var celular: String
        var domicilio: String
        var email: String
        var aptoFisico: Int // Leemos como Int desde la BD
        var fechaInscripcion: String

        // Recorremos el cursor fila por fila
        if (cursor.moveToFirst()) {
            do {
                // Obtenemos el índice de cada columna
                val idIndex = cursor.getColumnIndex("id")
                val nombreIndex = cursor.getColumnIndex("nombre")
                val apellidoIndex = cursor.getColumnIndex("apellido")
                val dniIndex = cursor.getColumnIndex("dni")
                val fechaNacIndex = cursor.getColumnIndex("fecha_nacimiento")
                val celularIndex = cursor.getColumnIndex("celular")
                val domicilioIndex = cursor.getColumnIndex("domicilio")
                val emailIndex = cursor.getColumnIndex("email")
                val aptoFisicoIndex = cursor.getColumnIndex("apto_fisico")
                val fechaInscIndex = cursor.getColumnIndex("fecha_inscripcion")

                // Extraemos los valores de la fila actual
                id = cursor.getInt(idIndex)
                nombre = cursor.getString(nombreIndex)
                apellido = cursor.getString(apellidoIndex)
                dni = cursor.getString(dniIndex)
                fechaNac = cursor.getString(fechaNacIndex)
                celular = cursor.getString(celularIndex)
                domicilio = cursor.getString(domicilioIndex)
                email = cursor.getString(emailIndex)
                aptoFisico = cursor.getInt(aptoFisicoIndex)
                fechaInscripcion = cursor.getString(fechaInscIndex)

                // Creamos un objeto Socio con los datos
                val socio = Socio(
                    id = id,
                    nombre = nombre,
                    apellido = apellido,
                    dni = dni,
                    fechaNacimiento = fechaNac,
                    celular = celular,
                    domicilio = domicilio,
                    email = email,
                    aptoFisico = (aptoFisico == 1), // Convertimos de Int a Boolean
                    fechaInscripcion = fechaInscripcion
                )

                // Añadimos el socio a la lista
                listaSocios.add(socio)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaSocios
    }

}
