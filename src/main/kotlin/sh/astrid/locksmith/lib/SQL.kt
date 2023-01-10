package sh.astrid.locksmith.lib

import sh.astrid.locksmith.Locksmith
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

// Yoinked from -> https://github.com/UwUAroze/TarnaIsBad

object SQL {
    private val conn: Connection

    init {
        val plugin = Locksmith.instance;

        conn = DriverManager.getConnection("jdbc:sqlite:${plugin.dataFolder}/data.db")

        execute("""CREATE TABLE IF NOT EXISTS locked_containers(
            location TEXT NOT NULL PRIMARY KEY UNIQUE,
            owner TEXT NOT NULL,
            keyId TEXT NOT NULL
        );""".trimIndent())

        execute("""CREATE TABLE IF NOT EXISTS keys(
            keyId TEXT NOT NULL PRIMARY KEY UNIQUE,
            owner TEXT NOT NULL,
            createdAt TEXT NOT NULL         
        );""".trimIndent())
    }

    fun execute(sql: String, vararg values: Any) {
        prepare(sql, *values).execute()
    }

    fun query(sql: String, vararg values: Any): ResultSet {
        return prepare(sql, *values).executeQuery()
    }

    fun close() {
        conn.close()
    }

    private fun prepare(sql: String, vararg values: Any): PreparedStatement {
        val stmt = conn.prepareStatement(sql)

        values.forEachIndexed { index, value ->
            stmt.setObject(index + 1, value)
        }

        return stmt
    }
}