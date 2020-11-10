import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * führt eine gamma-korrektur mir dem vom nutzer eingegebenen wert aus
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class GammaKorrektur_LKO_FL_JM extends AbstractFilter {

	public GammaKorrektur_LKO_FL_JM() {
		properties.addDoubleProperty("γ", 1);
	}

	@Override
	public Image filter(Image input) {
		
		Image output = ImageFactory.getPrecision(input).gray(input.getSize());
		
		for (int col = 0; col < input.getWidth(); col++) {
			for (int row = 0; row < input.getHeight(); row++) {
				output.setValue(col, row, Math.toIntExact(Math.round(256 * Math.pow((input.getValue(col, row, GrayscaleImage.GRAYSCALE) / 255), properties.getDoubleProperty("γ")))));
			}
		}
		return output;
	}
}