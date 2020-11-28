import java.util.Arrays;
import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Blatt 4 Aufgabe 2
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class MinimumMaximumFilter_LKO_FL_JM extends AbstractFilter {

	public MinimumMaximumFilter_LKO_FL_JM() {
		properties.addOptionProperty("Filter", "Minimum / Erosion", "Minimum / Erosion", "Maximum / Dilatation");
	}

	@Override
	public Image filter(Image input) {

		GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getWidth() - 2, input.getHeight() - 2);

		for (int col = 1; col < input.getWidth() - 1; col++) {
			for (int row = 1; row < input.getHeight() - 1; row++) {
				int[] intensities = { -1, -1, -1, -1, -1 };
				intensities[0] = (int) input.getValue(col, row - 1, GrayscaleImage.GRAYSCALE);
				intensities[1] = (int) input.getValue(col - 1, row, GrayscaleImage.GRAYSCALE);
				intensities[2] = (int) input.getValue(col, row, GrayscaleImage.GRAYSCALE);
				intensities[3] = (int) input.getValue(col + 1, row, GrayscaleImage.GRAYSCALE);
				intensities[4] = (int) input.getValue(col, row + 1, GrayscaleImage.GRAYSCALE);
				Arrays.sort(intensities);
				System.out.println(intensities[0]);
				if (properties.getOptionProperty("Filter") == "Minimum / Erosion")
					output.setValue(col - 1, row - 1, intensities[0]);
				else if (properties.getOptionProperty("Filter") == "Maximum / Dilatation")
					output.setValue(col - 1, row - 1, intensities[4]);
			}
		}
		return output;
	}
}