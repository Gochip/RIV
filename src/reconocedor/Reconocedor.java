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
import static org.opencv.objdetect.Objdetect.CASCADE_DO_ROUGH_SEARCH;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

public class Reconocedor {

    private final CascadeClassifier clasificadorCara;
    private final CascadeClassifier clasificadorOjos;

    private final int caraAncho;
    private final int caraAlto;
    private final double DESEADO_OJO_IZQUIERDO_X;
    private final double DESEADO_OJO_IZQUIERDO_Y;
    private final double EYE_SX;
    private final double EYE_SY;
    private final double EYE_SW;
    private final double EYE_SH;

    public Reconocedor() {
        //Usados para estabilizar la imagen
        this.caraAncho = 100;
        this.caraAlto = 100;
        this.DESEADO_OJO_IZQUIERDO_X = 0.19;
        this.DESEADO_OJO_IZQUIERDO_Y = 0.14;

        //Usados para detectar las coordenadas de los ojos
        this.EYE_SX = 0.16;
        this.EYE_SY = 0.26;
        this.EYE_SW = 0.30;
        this.EYE_SH = 0.28;

        //Clasificadores para la cara y los ojos
        clasificadorCara = new CascadeClassifier("src/clasificadores/haarcascade_frontalface_alt2.xml");
        clasificadorOjos = new CascadeClassifier("src/clasificadores/haarcascade_eye.xml");
    }

    //Retorna un vector de imagenes con caras reconocidas en una imagen Mat
    public LinkedList<Image> reconocerCaraValida(Mat imagen, MatOfRect carasDetectadas) {
        //Lista guardas las caras reconocidas correctamente
        LinkedList<Image> caras = new LinkedList<>();

        //Pregunta si la matriz posee elementos
        Mat aux = new Mat();

        //Convierto la imagen a escala de grises
        Imgproc.cvtColor(imagen, aux, Imgproc.COLOR_BGR2GRAY);
        //Aplico la ecuacion de histograma a la imagen para estandaraziar el contraste y el brillo
        Imgproc.equalizeHist(aux, aux);

        //Recorro las caras detectadas
        for (Rect rect : carasDetectadas.toArray()) {

            //Cara detectada
            Mat cara = imagen.submat(rect);
            //Obtengo las coordenadas de los ojos detectados
            Rect[] ojos = detectarOjos2(cara);

            //Pregunto si se obtuvieron correctamenta 2 ojos
            if (ojos != null) {
                //Cara detectada a color
                Mat img = aux.submat(rect);

                //Descomentar para marcar los ojos con un cuadrado
                for (Rect rectO : ojos) {
                    rectangle(img, new Point(rectO.x, rectO.y),
                            new Point(rectO.x + rectO.width, rectO.y + rectO.height),
                            new Scalar(0, 255, 0));
                }

                //Estabilizo la imagen
                //img = estabilizarImagen(img, ojos[1], ojos[0]);
                caras.add(convertir(img));
            }
        }

        return caras;
    }

    //Retorna un vector de imagenes con caras reconocidas en una imagen Mat
    public MatOfRect reconocerCara(Mat imagen) {
        //Estructura que guarda las caras detectadas
        MatOfRect carasDetectadas = new MatOfRect();

        //Pregunta si la matriz posee elementos
        if (!imagen.empty()) {

            Mat aux = new Mat();

            //Convierto la imagen a escala de grises
            Imgproc.cvtColor(imagen, aux, Imgproc.COLOR_BGR2GRAY);
            //Aplico la ecuacion de histograma a la imagen para estandaraziar el contraste y el brillo
            Imgproc.equalizeHist(aux, aux);

            //Detecta las caras 
            clasificadorCara.detectMultiScale(aux, carasDetectadas, 1.3, 2,
                    0 | CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(aux.width(), aux.height()));

            //Recorro las caras detectadas
            for (Rect rect : carasDetectadas.toArray()) {

                //Descomentar para marcar los ojos con un cuadrado
                rectangle(imagen, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }
        }
        return carasDetectadas;
    }

    //Retorna un vector con las coordenadas de los ojos si los encuentra si no retorna null
    public Rect[] detectarOjos(Mat cara) {

        //Obtiene las regiones donde detectar el ojo
        int izquierdaX = round(cara.cols() * EYE_SX);
        int arribaY = round(cara.rows() * EYE_SY);
        int anchoX = round(cara.cols() * EYE_SW);
        int altoY = round(cara.rows() * EYE_SH);
        int derechaX = round(cara.cols() * (1.0 - EYE_SX - EYE_SW));

        //Obtengo las imagenes de las partes de la cara donde se ecnuentran los ojos
        Mat arribaIzquierdaCara = cara.submat(new Rect(izquierdaX, arribaY, anchoX, altoY));
        Mat arribaDerechaCara = cara.submat(new Rect(derechaX, arribaY, anchoX, altoY));

        MatOfRect ojoIzquierdo = new MatOfRect();
        MatOfRect ojoDerecho = new MatOfRect();

        //Detecto los ojos en cada region
        clasificadorOjos.detectMultiScale(arribaIzquierdaCara, ojoIzquierdo,
                1.1, 3, CASCADE_DO_ROUGH_SEARCH, new Size(0, 0),
                new Size(arribaIzquierdaCara.width(), arribaDerechaCara.height()));
        clasificadorOjos.detectMultiScale(arribaDerechaCara, ojoDerecho,
                1.1, 3, CASCADE_DO_ROUGH_SEARCH, new Size(0, 0),
                new Size(arribaIzquierdaCara.width(), arribaDerechaCara.height()));

        Rect[] ojos = null;
        //Pregunto si detecto correctamente los ojos
        if (ojoDerecho.toArray().length == 1 && ojoIzquierdo.toArray().length == 1) {
            ojos = new Rect[2];
            //Obtengo las coordenadas de los ojos
            Rect ojoDCoor = ojoDerecho.toArray()[0];
            Rect ojoICoor = ojoIzquierdo.toArray()[0];

            //Modifico las coordenadas conrespecto a la cara
            ojoDCoor.x += izquierdaX;
            ojoDCoor.y += arribaY;

            ojoICoor.x += derechaX;
            ojoICoor.y += arribaY;

            //Pongo las coordenadas correctas de los ojos en el vector
            ojos[0] = ojoDCoor;
            ojos[1] = ojoICoor;
        }

        return ojos;
    }

    //Devuelve un vector si encontro los ojos si no devuelve null
    public Rect[] detectarOjos2(Mat cara) {
        //Coordenadas para recortar la mitad superior de la cara
        Rect caraSup = new Rect(0, 0, cara.width(), cara.height() / 2);

        MatOfRect ojosDetectados = new MatOfRect();
        Rect[] ojos = null;

        //Detecto los ojos
        clasificadorOjos.detectMultiScale(cara.submat(caraSup), ojosDetectados);

        //Si se detectaron los ojos se devuelve el vector
        if (ojosDetectados.toArray().length == 2) {
            if (ojosDetectados.toArray()[0].x > ojosDetectados.toArray()[1].x) {
                ojos = ojosDetectados.toArray();
            } else {
                ojos = new Rect[2];
                ojos[0] = ojosDetectados.toArray()[1];
                ojos[1] = ojosDetectados.toArray()[0];
            }
        }
        return ojos;
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

    //Funciona que rota y estabiliza una imagen para su reconocimiento
    private Mat estabilizarImagen(Mat cara, Rect ojoIzquierdo, Rect ojoDerecho) {
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
        final double DESADO_OJO_DERECHO_X = (1 - DESEADO_OJO_IZQUIERDO_X);

        // Obtener la cantidad que necesitamos para escalar la imagen para ser el tamaño fijo 
        //deseado que queremos
        double deseadoLargo = (DESADO_OJO_DERECHO_X - DESEADO_OJO_IZQUIERDO_X) * caraAncho;
        double escala = deseadoLargo / largo;

        // Obtener la matriz de transformación para rotar y escalar la cara al ángulo y tamaño deseado
        Mat rot_mat = getRotationMatrix2D(centroOjos, angulo, escala);

        // Desplazar el centro de los ojos para ser el centro deseado entre los ojos 
        rot_mat.put(0, 2, caraAncho * 0.5f - centroOjos.x);
        rot_mat.put(1, 2, caraAlto * DESEADO_OJO_IZQUIERDO_Y - centroOjos.y);

        imagen = new Mat(caraAlto, caraAncho, CV_8U, new Scalar(128));

        warpAffine(cara, imagen, rot_mat, imagen.size());

        return imagen;
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
