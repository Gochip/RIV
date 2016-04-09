package guardador;

import java.sql.DriverManager;

/**
 * Esta clase guarda los datos a una base de datos.
 *
 * @author Parisi Germán
 */
public class Guardador {

    public Guardador() {
        //Comprobamos que el driver se inicializa y se regiistra
        try {
            Class.forName("com.mysql.jdbc.Driver");

            // Establecemos la conexión con la base de datos.
            java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/prueba",
                    "root", "soyunacontraseña");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
