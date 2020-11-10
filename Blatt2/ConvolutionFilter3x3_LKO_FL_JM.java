import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;

/**
 * ConvolutionFilter 3x3 for Blatt 2.5.B
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */


public abstract class ConvolutionFilter3x3_LKO_FL_JM extends ConvolutionFilter_LKO_FL_JM {
    @Override
    public GrayscaleImage getKernel(){
        GrayscaleImage kernel = ImageFactory.doublePrecision().gray(3, 3);
        for (int col = 0; col < kernel.getWidth(); col++) {
			for (int row = 0; row < kernel.getHeight(); row++) {
                kernel.setValue(col, row, 1.0/9.0);
            }
        }
        return kernel;
    }
}