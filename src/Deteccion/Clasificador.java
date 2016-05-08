/*
 * 
 */
package Deteccion;

import Guardador.Guardador;
import java.util.ArrayList;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_32S;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.ml.Ml.ROW_SAMPLE;
import org.opencv.ml.SVM;

/**
 *
 * @author gastr
 */
public class Clasificador {

    private ArrayList<Cara> imagenes;
    SVM svm;
    private static Clasificador clasificador;

    public static Clasificador getSingletonInstance() {
        if (clasificador == null) {
            clasificador = new Clasificador();
        }

        return clasificador;
    }

    /*
        Obtienes las imagenes para entrenar el clasificador de la base de datos
     */
    private Clasificador() {
        Guardador conexion = new Guardador();
        imagenes = conexion.getCarasClasificador(new Size(100, 100));
        //Creo el clasificador para entrenar 
        svm = SVM.create();
    }

    /*
        Entrena una Red para detectar las diferentes caras
     */
    public boolean entrenar() {
        boolean entrenado = false;
        if (imagenes.size() > 0) {
            //Configuro el clasificador
            svm.setType(SVM.C_SVC);
            svm.setKernel(SVM.LINEAR);
            svm.setC(1);
            svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, (int) 1E7, 1e-6));

            //Estructuras que van a contener el conjunto de entrenamiento
            int largoEntrada = (imagenes.get(0).getImagen().cols() * imagenes.get(0).getImagen().rows());
            Mat salidas = new Mat(0, 1, CV_32S);
            Mat entradas = new Mat(0, largoEntrada, CV_32FC1);

            Cara c;
            Mat imagenCara = new Mat();
            //Recorro todas las caras para armar el conjunto de entrenamiento
            for (int i = 0; i < imagenes.size(); i++) {
                //Obtengo la cara
                c = imagenes.get(i);
                //Convierto la imagen al tipo aceptado por el clasificador
                c.getImagen().convertTo(imagenCara, CV_32FC1);
                
                //Asigno imagen como un vector a la matriz que va a contener todas las imagenes
                entradas.push_back(imagenCara.reshape(0, 1));
                //Asigno como salida el legajo
                salidas.push_back(new Mat(1, 1, CV_32S, new Scalar(c.getLegajo())));
            }

            //Entrena la red
            entrenado = svm.train(entradas, ROW_SAMPLE, salidas);

            Mat salidasRed = new Mat();
            int cont = 0;
            svm.predict(entradas, salidasRed, 0);
            for (int i = 0; i < imagenes.size(); i++) {
                if (salidasRed.get(0, 0)[0] == salidas.get(0, 0)[0]) {
                    cont++;
                }
            }
            System.out.println((double) cont / imagenes.size());
        }
        return entrenado;
    }

    public void getLegajo(ArrayList<Cara> caras) {
        Mat imagenDetectar = new Mat();
        for (Cara cara : caras) {
            //Convierto la imagen a binario
            threshold(cara.getImagen(), cara.getImagen(), 100, 255, THRESH_BINARY);

            cara.getImagen().convertTo(imagenDetectar, CV_32FC1);
            cara.setLegajo((int) this.svm.predict(imagenDetectar.reshape(0, 1)));
        }
    }
}
