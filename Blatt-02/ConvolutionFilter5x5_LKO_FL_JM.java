import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;

/**
 * ConvolutionFilter 5x5 for Blatt 2.5.B
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */


public class ConvolutionFilter5x5_LKO_FL_JM extends ConvolutionFilter_LKO_FL_JM {
    @Override
    public GrayscaleImage getKernel(){
        GrayscaleImage kernel = ImageFactory.doublePrecision().gray(5, 5);
        for (int col = 0; col < kernel.getWidth(); col++) {
			for (int row = 0; row < kernel.getHeight(); row++) {
                kernel.setValue(col, row, 1.0/25.0);
            }
        }
        return kernel;
    }
}