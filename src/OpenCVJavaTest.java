import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OpenCVJavaTest extends JPanel{

    private BufferedImage mImg;

    private BufferedImage mat2BI(Mat mat) {
        int dataSize = mat.cols() * mat.rows() * (int)mat.elemSize();
        byte[] data = new byte[dataSize];
        mat.get(0,0,data);
        int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0;i< dataSize; i+=3){
                byte blue = data[i+0];
                data[i+0] = data[i+2];
                data[i+2] = blue;
            }
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }

    public void paintComponent(Graphics g) {
        if(mImg != null) {
            g.drawImage(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), this);
        }
    }

//    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    static {System.load("D:\\opencv\\build\\java\\x64\\opencv_java341.dll");}

    public static void main(String[] args) throws Exception {
        Mat capImg = new Mat();
        VideoCapture capture = new VideoCapture(0);
        int height = (int)capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int width = (int)capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        if(height == 0 || width==0) {
            throw new Exception("camera not found");
        }

        JFrame frame = new JFrame("camera");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        OpenCVJavaTest panel=new OpenCVJavaTest();

        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setSize(width + frame.getInsets().left+ frame.getInsets().right, height + frame.getInsets().top+frame.getInsets().bottom);
        int n = 0;
        Mat temp = new Mat();
        while(frame.isShowing() && n < 500) {
            capture.read(capImg);
            Imgproc.cvtColor(capImg, temp, Imgproc.COLOR_RGB2GRAY);
            panel.mImg=panel.mat2BI(detectFace(capImg));
            panel.repaint();
            n ++;
        }
        capture.release();
        frame.dispose();
    }

    public static Mat detectFace(Mat img) {
        CascadeClassifier faceDetector = new CascadeClassifier("C:\\Users\\17990\\Desktop\\shexiangtou\\src\\haarcascade_frontalface_alt.xml");
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);
        Rect[] rects = faceDetections.toArray();
        if(rects != null && rects.length >= 1){
            for (Rect rect : rects) {
                Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0,0,255),2);

            }
        }
        return img;
    }
}
