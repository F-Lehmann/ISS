import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

/**
 * verstärkt einen RGB-kanal um einen wert zwischen 0 und 10
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(RgbImage.class)
public class SelektiveFarbverbesserung_LKO_FL_JM extends AbstractFilter {

	public SelektiveFarbverbesserung_LKO_FL_JM() {
		properties.addOptionProperty("Kanal", "R", "R", "G", "B");
		properties.addRangeProperty("Faktor", 5, 0, 1, 10);
	}

	@Override
	public Image filter(Image input) {
		Image output = ImageFactory.getPrecision(input).rgb(input.getSize());

		if (properties.getOptionProperty("Kanal") == "R") {
			for (int col = 0; col < input.getWidth(); col++) {
				for (int row = 0; row < input.getHeight(); row++) {
					output.setValue(col, row, 
							properties.getRangeProperty("Faktor") * input.getValue(col, row, RgbImage.RED),
							input.getValue(col, row, RgbImage.GREEN),
							input.getValue(col, row, RgbImage.BLUE));
				}
			}
		} else if (properties.getOptionProperty("Kanal") == "G") {
			for (int col = 0; col < input.getWidth(); col++) {
				for (int row = 0; row < input.getHeight(); row++) {
					output.setValue(col, row, 
							input.getValue(col, row, RgbImage.RED),
							properties.getRangeProperty("Faktor") * input.getValue(col, row, RgbImage.GREEN),
							input.getValue(col, row, RgbImage.BLUE));
				}
			}
		} else if (properties.getOptionProperty("Kanal") == "B") {
			for (int col = 0; col < input.getWidth(); col++) {
				for (int row = 0; row < input.getHeight(); row++) {
					output.setValue(col, row, 
							input.getValue(col, row, RgbImage.RED),
							input.getValue(col, row, RgbImage.GREEN),
							properties.getRangeProperty("Faktor") * input.getValue(col, row, RgbImage.BLUE));
				}
			}
		}
		return output;
	}
}