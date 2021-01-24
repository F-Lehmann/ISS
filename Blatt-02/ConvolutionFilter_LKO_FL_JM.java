import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;


/**
 * ConvolutionFilter for Blatt 2.5.A
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public abstract class ConvolutionFilter_LKO_FL_JM extends AbstractFilter {

	public abstract GrayscaleImage getKernel();

	@Override
	public Image filter(Image input){
		Image mask = getKernel();
		int deltaSize = mask.getHeight() / 2;
		Image output = ImageFactory.getPrecision(input).gray(input.getWidth()-deltaSize*2,input.getHeight()-deltaSize*2);
		for (int outcol = 0; outcol < output.getWidth(); outcol++) {
			for (int outrow = 0; outrow < output.getHeight(); outrow++) {
				double result = 0;
				for (int maskcol = 0; maskcol < mask.getWidth(); maskcol++) {
					for (int maskrow = 0; maskrow < mask.getHeight(); maskrow++) {
						int incol = outcol+maskcol;
						int inrow = outrow+maskrow;
						result = result + (input.getValue(incol, inrow, GrayscaleImage.GRAYSCALE) * mask.getValue(maskcol, maskrow, GrayscaleImage.GRAYSCALE));
					}
				}
				output.setValue(outcol, outrow, result);
			}
		}
		return output;
	}
}