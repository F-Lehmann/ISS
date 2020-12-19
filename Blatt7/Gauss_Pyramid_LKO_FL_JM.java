import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;

@RequireImageType(GrayscaleImage.class)
public class Gauss_Pyramid_LKO_FL_JM extends AbstractFilter {
    public Gauss_Pyramid_LKO_FL_JM() {
        properties.addDoubleProperty("sigma", 1);
    }

    private double[][] getgaussfilter2D(int size,double sigma){
        double[] filter1D = new double[size];
        for (int i = 0; i < size; i++) {
            filter1D[i] = (1 / Math.sqrt(2*Math.PI*sigma)) * Math.pow(Math.E, -Math.pow(i-((size-1)/2),2)/(2*Math.pow(sigma, 2)));
        }
        double[][] filter2D = new double[size][size];

        double abs = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                filter2D[i][j]=filter1D[i]*filter1D[j];
                abs+=filter2D[i][j];
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; i < size; i++) {
                filter2D[i][j]/=abs;
            }
        }

        return filter2D;
    }

    public Image[] filter(Image[] input){
        double[][] filtermask = getgaussfilter2D(5, properties.getDoubleProperty("sigma"));

        GrayscaleImage[] output = new GrayscaleImage[6];
        output[0] = ImageConverter.convert(input[0], GrayscaleImage.class);
        for (int i = 1; i < output.length; i++) {
            output[i] = ImageFactory.bytePrecision().gray(output[i-1].getWidth()/2,output[i-1].getHeight()/2);

            for (int x = 0; x < output[i].getHeight(); x++) {
                for (int y = 0; y < output[i].getWidth(); y++) {
                    double filterval = 0;
                    for (int mx = 0; mx < filtermask.length; mx++) {
                        for (int my = 0; my < filtermask.length; my++) {
                            try {
                                filterval += output[i-1].getValue((x*2)-2+mx, (y*2)-2+my, 0) * filtermask[mx][my];
                            } catch (Exception e) {
                                
                            }
                        }
                    }
                    output[i].setValue(x, y, filterval);
                }
            }
        }

        return output;
    }
}