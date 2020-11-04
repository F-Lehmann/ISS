import itb2.filter.AbstractFilter;
import itb2.image.BinaryImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

/**
 * 2 bilder als input, das erste wird mit den zweiten maskiert
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

public class Maskierung_LKO_FL_JM extends AbstractFilter {

	@Override
	public Image[] filter(Image[] input) {
		if (input.length != 2) {
			throw new IllegalArgumentException("Input must be exactly 2 images");
		}
	
		Image[] output = new Image[1];
		output[0] = ImageFactory.getPrecision(input[0]).rgb(input[0].getSize());

		RgbImage inputBild = ImageConverter.convert(input[0], RgbImage.class);
		BinaryImage inputMaske = ImageConverter.convert(input[1], BinaryImage.class);

		// verhindert abbruch des programms falls maske kleiner als bild durch skalierung
		BinaryImage newMask = ImageFactory.getPrecision(inputMaske).binary(inputBild.getSize());
		for (int col = 0; col < inputMaske.getWidth(); col++) {
			for (int row = 0; row < inputMaske.getHeight(); row++) {
				newMask.setValue(col, row, BinaryImage.BINARY, inputMaske.getValue(col, row, BinaryImage.BINARY));
			}
		}

		for (int col = 0; col < inputBild.getWidth(); col++) {
			for (int row = 0; row < inputBild.getHeight(); row++) {
				if (newMask.getValue(col, row, BinaryImage.BINARY) == 1) {
					output[0].setValue(col, row, inputBild.getValue(col, row));
				} else {
					output[0].setValue(col, row, 0, 0, 0);
				}
			}
		}
		return output;
	}
}