/*
 * Acceso a la base de datos para guardar y obtener datos.
 */
package Guardador;

import Deteccion.Cara;
import Deteccion.Persona;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC2;
import static org.opencv.core.CvType.CV_8UC3;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 *
 * @author gastr
 */
public class Guardador {

    Connection conexion;
    String servidor;
    String usuario;
    String contraseña;
    PreparedStatement ps;

    public Guardador() {
        servidor = "jdbc:mysql://localhost:3306/baseriv";
        usuario = "root";
        contraseña = "";
    }

    /**
     * Carga el driver para hacer una conexion MySQL
     *
     * @return
     */
    private String cargarDriver() {
        String cargado = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException ex) {
            cargado = ex.toString();
        }
        return cargado;
    }

    /**
     * Crea una conexion con MySQL
     *
     * @return
     */
    private String conectarConMySQL() {
        String conectado = "";
        try {
            conexion = (Connection) DriverManager.getConnection(servidor, usuario, contraseña);
        } catch (SQLException ex) {
            conectado = ex.toString();
        }
        return conectado;
    }

    public boolean guardarCaraDetectada(Cara cara) {
        boolean guardado = false;
        File midir = new File(".");
        File imagen;
        try {
            imwrite("imagen.jpg", cara.getImagen());
            imagen = new File(midir.getCanonicalPath());
            if (imagen.exists()) {

                if ("".equals(this.cargarDriver())) {
                    if ("".equals(this.conectarConMySQL())) {
                        ps = conexion.prepareStatement("INSERT INTO carasdetectadas(legajo,imagen,fecha) "
                                + "VALUES(?,?,NOW())");

                        ps.setInt(1, cara.getLegajo());
                        ps.setBinaryStream(2, new FileInputStream(imagen), imagen.length());
                        guardado = ps.execute();
                        ps.close();
                        conexion.close();
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return guardado;
    }

    public boolean guardarCarasClasificador(ArrayList<Cara> imagenes) {
        boolean guardado = true;
        try {
            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {
                    conexion.setAutoCommit(false);
                    File midir = new File(".");
                    File imagen;
                    for (Cara cara : imagenes) {
                        imwrite("imagen.jpg", cara.getImagen());
                        imagen = new File(midir.getCanonicalPath() + "\\imagen.jpg");
                        if (imagen.exists()) {
                            ps = conexion.prepareStatement("INSERT INTO "
                                    + "carasclasificador(legajo,imagen) "
                                    + "VALUES(?,?)");

                            ps.setInt(1, cara.getLegajo());
                            ps.setBinaryStream(2, new FileInputStream(imagen), imagen.length());
                            ps.executeUpdate();
                        }
                    }

                    conexion.commit();
                    conexion.setAutoCommit(true);
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            try {
                conexion.rollback();
                conexion.close();
            } catch (SQLException ex1) {
                Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return guardado;
    }

    public ArrayList<Cara> getCarasClasificador(Size tamaño) {
        ArrayList<Cara> imagenes = new ArrayList<>();
        try {
            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {

                    ps = conexion.prepareStatement("SELECT legajo,imagen FROM carasclasificador");
                    ResultSet rs = ps.executeQuery();

                    Cara C;
                    Blob blob;
                    Mat m;
                    while (rs.next()) {
                        blob = rs.getBlob(2);
                        byte[] data = blob.getBytes(1, (int) blob.length());
                        BufferedImage img = null;
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        Image imagen = (Image)img;
                        
                        m = new Mat(tamaño, CV_8UC1);
                        m.put(0, 0, );

                        C = new Cara(m, rs.getInt(1));
                        imagenes.add(C);
                    }

                    rs.close();
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imagenes;
    }

    public boolean existeLegajo(int legajo) {
        boolean existe = false;
        try {

            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {
                    ps = conexion.prepareStatement("SELECT legajo FROM personas WHERE legajo=?");

                    ps.setInt(1, legajo);
                    ResultSet rs = ps.executeQuery();
                    existe = rs.first();
                    rs.close();
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public int[][] getCantidadDeImagenesLegajo() {
        int[][] tabla = null;
        try {
            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {
                    ps = conexion.prepareStatement("SELECT COUNT(*) FROM personas");
                    ResultSet rs = ps.executeQuery();
                    rs.next();

                    int cantLeg = rs.getInt(1);
                    if (cantLeg > 0) {
                        ps = conexion.prepareStatement("SELECT legajo, COUNT(*) FROM "
                                + "carasclasificador GROUP BY 1");

                        rs = ps.executeQuery();
                        int i = 0;
                        tabla = new int[cantLeg][2];
                        while (rs.next()) {
                            tabla[i][0] = rs.getInt(1);
                            tabla[i][1] = rs.getInt(2);
                            i++;
                        }
                    }
                    rs.close();
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tabla;
    }

    private InputStream convertir(Mat imagen) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imagen, matOfByte);
        InputStream in = null;
        byte[] byteArray = matOfByte.toArray();

        try {
            in = new ByteArrayInputStream(byteArray);
        } catch (Exception e) {
            System.out.println(e);
        }
        return in;
    }

    public boolean eliminarCarasClasificador(int legajo) {
        boolean eliminado = false;
        try {
            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {
                    PreparedStatement ps = conexion.prepareStatement("DELETE FROM carasclasificador "
                            + "WHERE legajo=?");
                    ps.setInt(1, legajo);
                    eliminado = ps.execute();
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eliminado;
    }

    public void insertarPersona(Persona persona) {
        try {
            if ("".equals(this.cargarDriver())) {
                if ("".equals(this.conectarConMySQL())) {
                    PreparedStatement ps = conexion.prepareStatement("INSERT INTO personas(legajo,nombre,apellido) "
                            + "VALUES(?,?,?)");
                    ps.setInt(1, persona.getLegajo());
                    ps.setString(2, persona.getNombre());
                    ps.setString(3, persona.getApellido());
                    ps.executeUpdate();
                    ps.close();
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Persona> getPersonas() {
        ArrayList<Persona> listaPersonas = new ArrayList<>();
        Persona persona;
        if ("".equals(this.cargarDriver())) {
            if ("".equals(this.conectarConMySQL())) {
                try {
                    ps = conexion.prepareStatement("SELECT legajo,nombre,apellido FROM personas");
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        persona = new Persona(rs.getInt(1), rs.getString(2), rs.getString(3));
                        listaPersonas.add(persona);
                    }
                    rs.close();
                    ps.close();
                    conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listaPersonas;
    }

    public void eliminarPersona(Integer legajo) {
        if ("".equals(this.cargarDriver())) {
            if ("".equals(this.conectarConMySQL())) {
                try {
                    PreparedStatement ps = conexion.prepareStatement("DELETE FROM personas WHERE legajo=?");
                    ps.setInt(1, legajo);
                    ps.executeUpdate();
                    ps.close();
                    conexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Guardador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
