package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.Actividad
import com.example.myapplication.model.NoSocio
import com.example.myapplication.model.Socio
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminSQLiteOpenHelper(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {

        // ---------- TABLA PROFESORES ----------
        db.execSQL(
            """
            CREATE TABLE profesores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                especialidad TEXT
            )
            """
        )

        // ---------- TABLA SOCIOS ----------
        db.execSQL(
            """
            CREATE TABLE socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                fecha_nacimiento TEXT NOT NULL,
                celular TEXT NOT NULL,
                domicilio TEXT NOT NULL,
                email TEXT NOT NULL,
                apto_fisico INTEGER NOT NULL,
                fecha_inscripcion TEXT DEFAULT CURRENT_TIMESTAMP
            )
            """
        )

        // ---------- TABLA USUARIOS ----------
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                nombre_usuario TEXT
            )
            """
        )

        // ---------- TABLA NO SOCIOS ----------
        db.execSQL(
            """
            CREATE TABLE no_socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo_actividad TEXT NOT NULL,
                importe REAL NOT NULL,
                forma_pago TEXT NOT NULL,
                apto_fisico INTEGER NOT NULL,
                fecha_inscripcion TEXT DEFAULT CURRENT_TIMESTAMP
            )
            """
        )

        // ---------- TABLA ACTIVIDADES ----------
        db.execSQL(
            """
            CREATE TABLE actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                profesor TEXT,
                horario TEXT,
                cupos INTEGER DEFAULT 0
            )
            """
        )

        // ---------- PAGOS SOCIO ----------
        db.execSQL(
            """
            CREATE TABLE pagosSocio(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idSocio INTEGER,
                cuotas INTEGER,
                importe REAL,
                formaPago TEXT,
                periodo TEXT,
                fechaPago TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(idSocio) REFERENCES socios(id)
            )
            """
        )

        // ---------- PAGOS ACTIVIDADES ----------
        db.execSQL(
            """
            CREATE TABLE pagos_actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_socio INTEGER,
                id_no_socio INTEGER,
                id_actividad INTEGER NOT NULL,
                importe REAL NOT NULL,
                forma_pago TEXT NOT NULL,
                fecha TEXT DEFAULT CURRENT_TIMESTAMP
            )
            """
        )

        // ---------- INSCRIPCIONES ----------
        db.execSQL(
            """
            CREATE TABLE inscripciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_socio INTEGER,
                id_no_socio INTEGER,
                id_actividad INTEGER NOT NULL,
                fecha TEXT DEFAULT CURRENT_TIMESTAMP
            )
            """
        )

        // ---------- INSERTS INICIALES ----------
        db.execSQL(
            """
            INSERT INTO socios 
            (nombre, apellido, dni, fecha_nacimiento, celular, domicilio, email, apto_fisico)
            VALUES ('Carlos', 'Ramírez', '55111222', '1990-01-01', '1111111111', 'Calle Falsa 123', 'carlos@mail.com', 1)
                            """
        )

        db.execSQL(
            """
            INSERT INTO pagosSocio (idSocio, cuotas, importe, formaPago, periodo)
            VALUES ((SELECT id FROM socios WHERE dni='55111222'), 1, 4000, 'Efectivo', '16/11/2025 ➜ 20/11/2025')
            """
        )

        db.execSQL(
            """
            INSERT INTO actividades (nombre, descripcion, precio, profesor, horario, cupos)
            VALUES ('Fútbol', 'Deporte', 5000, 'Juan Pérez', 'Lunes 17:00', 20)
            """
        )
        db.execSQL(
            """
            INSERT INTO actividades (nombre, descripcion, precio, profesor, horario, cupos)
            VALUES ('Básquet', 'Deporte', 4500, 'Sofía Gómez', 'Martes 19:00', 0)
            """
        )
        db.execSQL(
            """
            INSERT INTO actividades (nombre, descripcion, precio, profesor, horario, cupos)
            VALUES ('Zumba', 'Baile', 3000, 'Ana Torres', 'Viernes 18:00', 40)
            """
        )
        db.execSQL(
            """
            INSERT INTO pagosSocio (idSocio, cuotas, importe, formaPago, periodo)
            VALUES (2, 2, 10000, 'Efectivo', '16/11/2025 ➜ 17/11/2025')
            """
        )
        db.execSQL(
            """
            INSERT INTO pagosSocio (idSocio, cuotas, importe, formaPago, periodo)
            VALUES (1, 1, 5000, 'Transferencia', '16/11/2025 ➜ 18/11/2025')
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE actividades ADD COLUMN profesor TEXT")
            db.execSQL("ALTER TABLE actividades ADD COLUMN horario TEXT")
            db.execSQL("ALTER TABLE actividades ADD COLUMN cupos INTEGER DEFAULT 0")
        }
    }

    // ---------------------------------------------------------
    //  SOCIOS
    // ---------------------------------------------------------

    fun agregarSocio(socio: Socio): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("dni", socio.dni)
            put("fecha_nacimiento", socio.fechaNacimiento)
            put("celular", socio.celular)
            put("domicilio", socio.domicilio)
            put("email", socio.email)
            put("apto_fisico", if (socio.aptoFisico) 1 else 0)
        }
        val id = db.insert("socios", null, cv)
        db.close()
        return id
    }
      fun buscarSocioPorId(idSocio: Int): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM socios WHERE id = ?", arrayOf(idSocio.toString()))

        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return null
        }

        val socio = Socio(
            id = cursor.getInt(0),
            nombre = cursor.getString(1),
            apellido = cursor.getString(2),
            dni = cursor.getString(3),
            fechaNacimiento = cursor.getString(4),
            celular = cursor.getString(5),
            domicilio = cursor.getString(6),
            email = cursor.getString(7),
            aptoFisico = cursor.getInt(8) == 1,
            fechaInscripcion = cursor.getString(9)
        )

        cursor.close()
        db.close()
        return socio
    }

    fun buscarSocioPorNombre(query: String): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM socios WHERE nombre || ' ' || apellido LIKE ?",
            arrayOf("%$query%")
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return null
        }

        val socio = Socio(
            id = cursor.getInt(0),
            nombre = cursor.getString(1),
            apellido = cursor.getString(2),
            dni = cursor.getString(3),
            fechaNacimiento = cursor.getString(4),
            celular = cursor.getString(5),
            domicilio = cursor.getString(6),
            email = cursor.getString(7),
            aptoFisico = cursor.getInt(8) == 1,
            fechaInscripcion = cursor.getString(9)
        )

        cursor.close()
        db.close()
        return socio
    }

    // ---------------------------------------------------------
    //  LISTAR TODOS LOS SOCIOS
    // ---------------------------------------------------------
    fun obtenerTodosLosSocios(): ArrayList<Socio> {
        val lista = ArrayList<Socio>()
        val db = readableDatabase

        // Ordenado por apellido y nombre
        val cursor = db.rawQuery(
            "SELECT * FROM socios ORDER BY apellido, nombre",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val socio = Socio(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    apellido = cursor.getString(2),
                    dni = cursor.getString(3),
                    fechaNacimiento = cursor.getString(4),
                    celular = cursor.getString(5),
                    domicilio = cursor.getString(6),
                    email = cursor.getString(7),
                    aptoFisico = cursor.getInt(8) == 1,
                    fechaInscripcion = cursor.getString(9)
                )
                lista.add(socio)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun existeSocio(idSocio: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM socios WHERE id = ?", arrayOf(idSocio.toString()))
        val ok = cursor.moveToFirst()
        cursor.close()
        db.close()
        return ok
    }
    fun buscarSocioPorDni(dni: String): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM socios WHERE dni = ?", arrayOf(dni))

        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return null
        }

        val socio = Socio(
            id = cursor.getInt(0),
            nombre = cursor.getString(1),
            apellido = cursor.getString(2),
            dni = cursor.getString(3),
            fechaNacimiento = cursor.getString(4),
            celular = cursor.getString(5),
            domicilio = cursor.getString(6),
            email = cursor.getString(7),
            aptoFisico = cursor.getInt(8) == 1,
            fechaInscripcion = cursor.getString(9)
        )

        cursor.close()
        db.close()
        return socio
    }

    // ---------------------------------------------------------
    //  NO SOCIO
    // ---------------------------------------------------------
    fun agregarNoSocio(noSocio: NoSocio): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("tipo_actividad", noSocio.tipoActividad)
            put("importe", noSocio.importe)
            put("forma_pago", noSocio.formaPago)
            put("apto_fisico", if (noSocio.aptoFisico) 1 else 0)
        }
        val id = db.insert("no_socios", null, cv)
        db.close()
        return id
    }
    // ---------------------------------------------------------
    //  PAGOS CUOTA SOCIO
    // ---------------------------------------------------------
    fun registrarPagoCuotaSocio(
        idSocio: Int,
        cuotas: Int,
        importe: Double,
        formaPago: String,
        periodo: String
    ): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("idSocio", idSocio)
            put("cuotas", cuotas)
            put("importe", importe)
            put("formaPago", formaPago)
            put("periodo", periodo)
        }
        val id = db.insert("pagosSocio", null, cv)
        db.close()
        return id
    }

    // ---------------------------------------------------------
    //  ACTIVIDADES
    // ---------------------------------------------------------

    fun obtenerActividades(): ArrayList<Actividad> {
        val lista = ArrayList<Actividad>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Actividad(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        descripcion = cursor.getString(2),
                        precio = cursor.getDouble(3),
                        profesor = cursor.getString(4),
                        horario = cursor.getString(5),
                        cupos = cursor.getInt(6)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun actividadTieneCupos(idActividad: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT cupos FROM actividades WHERE id = ?", arrayOf(idActividad.toString()))
        val ok = cursor.moveToFirst() && cursor.getInt(0) > 0
        cursor.close()
        db.close()
        return ok
    }
    fun existeSocioPorId(idSocio: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM socios WHERE id = ?",
            arrayOf(idSocio.toString())
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    // ---------------------------------------------------------
    //  PAGOS ACTIVIDAD + INSCRIPCIÓN
    // ---------------------------------------------------------
    fun registrarPagoNoSocioActividad(
        idNoSocio: Int,
        idActividad: Int,
        importe: Double,
        formaPago: String
    ): Long {

        val db = writableDatabase

        val cv = ContentValues().apply {
            put("id_no_socio", idNoSocio)
            put("id_actividad", idActividad)
            put("importe", importe)
            put("forma_pago", formaPago)
        }

        val idPago = db.insert("pagos_actividades", null, cv)

        // registrar inscripción
        val cvIns = ContentValues().apply {
            put("id_no_socio", idNoSocio)
            put("id_actividad", idActividad)
        }

        db.insert("inscripciones", null, cvIns)

        db.close()
        return idPago
    }

    fun registrarPagoSocioActividad(
        idSocio: Int,
        idActividad: Int,
        importe: Double,
        formaPago: String
    ): Long {

        val db = writableDatabase

        val cv = ContentValues().apply {
            put("id_socio", idSocio)
            put("id_actividad", idActividad)
            put("importe", importe)
            put("forma_pago", formaPago)
        }

        val idPago = db.insert("pagos_actividades", null, cv)

        // registrar inscripción
        val cvIns = ContentValues().apply {
            put("id_socio", idSocio)
            put("id_actividad", idActividad)
        }
        db.insert("inscripciones", null, cvIns)

        db.close()
        return idPago
    }
    fun obtenerPagosProximosAVencer(idSocio: Int): ArrayList<Map<String, String>> {
        val db = this.readableDatabase
        val lista = ArrayList<Map<String, String>>()

        val sql = """
        SELECT cuotas, importe, formaPago, periodo, fechaPago
        FROM pagosSocio
        WHERE idSocio = ?
        ORDER BY id DESC
    """

        val cursor = db.rawQuery(sql, arrayOf(idSocio.toString()))

        val formatoUser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = Calendar.getInstance()

        if (cursor.moveToFirst()) {
            do {
                val periodo = cursor.getString(3) // "01/10/2024 ➜ 01/11/2024"
                val importe = cursor.getString(1)

                val partes = periodo.split("➜")
                if (partes.size == 2) {
                    val fechaFin = partes[1].trim() // "01/11/2024"

                    try {
                        val fechaVenc = formatoUser.parse(fechaFin)

                        if (fechaVenc != null) {
                            val calVenc = Calendar.getInstance()
                            calVenc.time = fechaVenc

                            val diff = calVenc.timeInMillis - hoy.timeInMillis
                            val diasRestantes = (diff / (1000 * 60 * 60 * 24)).toInt()

                            // Solo mostrar pagos próximos a vencer (< 30 dias)
                            if (diasRestantes <= 30) {
                                val map = mapOf(
                                    "periodo" to periodo,
                                    "importe" to importe,
                                    "diasRestantes" to diasRestantes.toString()
                                )
                                lista.add(map)
                            }
                        }
                    } catch (_: Exception) { }
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        // Ordenar por menor cantidad de días
        lista.sortBy { it["diasRestantes"]?.toIntOrNull() ?: 999 }

        return lista
    }
    fun obtenerPagosProximosAVencerGlobal(): ArrayList<Map<String, String>> {
        val lista = ArrayList<Map<String, String>>()
        val db = readableDatabase

        val sql = """
        SELECT p.idSocio, s.nombre, s.apellido, p.periodo, p.importe
        FROM pagosSocio p
        JOIN socios s ON s.id = p.idSocio
        ORDER BY p.fechaPago DESC
    """

        val cursor = db.rawQuery(sql, null)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = Date()

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(1)
                val apellido = cursor.getString(2)
                val periodo = cursor.getString(3)
                val importe = cursor.getString(4)

                // Ejemplo periodo → "01/02/2025 ➜ 01/03/2025"
                val partes = periodo.split("➜")
                if (partes.size != 2) continue

                val fechaFinStr = partes[1].trim()

                try {
                    val fechaFin = sdf.parse(fechaFinStr)
                    if (fechaFin != null) {
                        val diff = fechaFin.time - hoy.time
                        val dias = (diff / (1000 * 60 * 60 * 24)).toInt()

                        if (dias in 0..7) {
                            lista.add(
                                mapOf(
                                    "socio" to "$nombre $apellido",
                                    "periodo" to periodo,
                                    "importe" to importe,
                                    "diasRestantes" to dias.toString()
                                )
                            )
                        }
                    }
                } catch (_: Exception) {}
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        // Ordenar por menor cantidad de días
        lista.sortBy { it["diasRestantes"]?.toIntOrNull() ?: 999 }

        return lista
    }


}
