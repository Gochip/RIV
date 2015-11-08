package reconocedor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.opencv.core.CvType.CV_8U;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.warpAffine;
import org.opencv.objdetect.CascadeClassifier;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

// Esta clase usa OPENCV para el reconocimiento de rostros.

public class Reconocedor {

    private final CascadeClassifier clasificadorCara;
    private final CascadeClassifier clasificadorOjos;

    public Reconocedor() {
        clasificadorCara = new CascadeClassifier("src/clasificadores/haarcascade_frontalface_alt2.xml");
        clasificadorOjos = new CascadeClassifier("src/clasificadores/haarcascade_eye.xml");
       }

    //Retorna un vector de imagenes con caras reconocidas en una imagen Mat
    public Image[] reconocerCara(Mat imagen) {
        //Lista guardas las caras reconocidas correctamente
        LinkedList<Image> caras = new LinkedList<>();

        //Pregunta si la matriz posee elementos
        if (!imagen.empty()) {

            Mat aux = new Mat();
            
            //Convierto la imagen a escala de grises
            Imgproc.cvtColor(imagen, aux, Imgproc.COLOR_BGR2GRAY);
            //Aplico la ecuacion de histograma a la imagen para estandaraziar el contraste y el brillo
            Imgproc.equalizeHist(aux, aux);

            //Estructura que guarda las caras detectadas
            MatOfRect carasDetectadas = new MatOfRect();
            //Detecta las caras 
            clasificadorCara.detectMultiScale(aux, carasDetectadas, 1.3, 2,
                    0 | CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(aux.width(), aux.height()));

            //Estructura para guardar los ojos detectados
            MatOfRect ojosDetectados = new MatOfRect();
            Mat caraGris;

            //Recorro las caras detectadas
            for (Rect rect : carasDetectadas.toArray()) {
                //Obtengo las coordenadas de la parte superior de la cara deteactada
                Rect recta  = new Rect(rect.x,rect.y, rect.width, (rect.height/2));
                //Obtengo la imagen de la parte superior de la cara detectada
                caraGris = aux.submat(recta);
                
                //Reconocozco los ojos
                clasificadorOjos.detectMultiScale(caraGris, ojosDetectados, 1.3, 2,
                    0 | CASCADE_SCALE_IMAGE, new Size(0, 0), 
                    new Size(caraGris.width(), caraGris.height()));
                //Vector con coordenadas de los ojos detectados
                
                
                //Descomentar para marcar los ojos con un cuadrado
//                for(Rect rectO : ojosDetectados.toArray()){
//                    rectangle(imagen.submat(rect), new Point(rectO.x, rectO.y),
//                            new Point(rectO.x + rectO.width, rectO.y + rectO.height),
//                            new Scalar(0, 255, 0));
//                }
                
                //Si los ojos detectados son dos la imagen es valida y se agrega
                if (ojosDetectados.toArray().length == 2) {
                    caras.add(convertir(imagen.submat(rect)));
                }
            }
        }

        return caras.toArray(new Image[caras.size()]);
    }
    
    //Convierte una imagen Mat(formato de opencv) a Image(formato de java)
    public Image convertir(Mat imagen) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imagen, matOfByte);

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;

        try {

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            System.out.println(e);
        }
        return (Image) bufImage;
    }
    
    //Funciona que rota y estabiliza una imagen para su reconocimiento-NO PROBADA
    private Image estabilizarImagen(Mat cara, Rect ojoIzquierdo, Rect ojoDerecho) {
        Mat imagen;

        Point izquierda = new Point(ojoIzquierdo.x + ojoIzquierdo.width / 2, ojoIzquierdo.y + ojoIzquierdo.height / 2);
        Point derecha = new Point(ojoDerecho.x + ojoDerecho.width / 2, ojoDerecho.y + ojoDerecho.height / 2);
        Point centroOjos = new Point((izquierda.x + derecha.x) * 0.5f, (izquierda.y + derecha.y) * 0.5f);

        // Obtiene el angulo entre los dos ojos
        double dy = (derecha.y - izquierda.y);
        double dx = (derecha.x - izquierda.x);
        double largo = Math.sqrt(dx * dx + dy * dy);
        double angulo = Math.atan2(dy, dx) * 180.0 / Math.PI;

        // Mediciones a mano muestran que el centro del ojo izquierdo debe ser idealmente en 
        //aproximadamente ( 0.19 , 0.14 ) de una imagen de la cara a escala
        final double DESEADO_OJO_IZQUIERDO_X = 0.16;
        final double DESEADO_OJO_IZQUIERDO_Y = 0.14;
        final double DESADO_OJO_DERECHO_X = (1 - DESEADO_OJO_IZQUIERDO_X);

        // Obtener la cantidad que necesitamos para escalar la imagen para ser el tamaño fijo 
        //deseado que queremos
        double deseadoLargo = (DESADO_OJO_DERECHO_X - DESEADO_OJO_IZQUIERDO_X) * cara.width();
        double escala = deseadoLargo / largo;

        // Obtener la matriz de transformación para rotar y escalar la cara al ángulo y tamaño deseado
        Mat rot_mat = getRotationMatrix2D(centroOjos, angulo, escala);

        // Desplazar el centro de los ojos para ser el centro deseado entre los ojos 
        rot_mat.put(0, 2, cara.width() * 0.5f - centroOjos.x);
        rot_mat.put(1, 2, cara.height() * DESEADO_OJO_IZQUIERDO_Y - centroOjos.y);

        imagen = new Mat(cara.height(), cara.width(), CV_8U, new Scalar(128));

        warpAffine(cara, imagen, rot_mat, imagen.size());

        return convertir(imagen);
    }

    //Redondea al int mas cercano
    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

}
