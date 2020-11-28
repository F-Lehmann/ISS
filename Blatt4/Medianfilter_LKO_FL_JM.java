import java.util.Arrays;

import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * 3x3 Medianfilter
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class Medianfilter_LKO_FL_JM extends AbstractFilter {

	@Override
	public Image filter(Image input) {

		GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getWidth() - 4, input.getHeight() - 4);

		for (int col = 2; col < input.getWidth() - 2; col++) {
			for (int row = 2; row < input.getHeight() - 2; row++) {
				int[] intensities = { -1, -1, -1, -1, -1, -1, -1, -1, -1 };
				for (int filtercol = -1; filtercol <= 1; filtercol++) {
					for (int filterrow = -1; filterrow <= 1; filterrow++) {
						int i = 0;
						while (intensities[i] > 0)
							i++;
						intensities[i] = (int) input.getValue(col + filtercol, row + filterrow, GrayscaleImage.GRAYSCALE);
					}
				}
				Arrays.sort(intensities);
				output.setValue(col - 2, row - 2, intensities[4]);
			}
		}

		return output;
	}
}