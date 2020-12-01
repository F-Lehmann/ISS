import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Blatt 5 Aufgabe 1 interaktive Binarisierung
 * 
 * negative Schwellwerte werden ignoriert
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class Binarisierung_LKO_FL_JM extends AbstractFilter {

	public Binarisierung_LKO_FL_JM() {
		properties.addIntegerProperty("Schwellwert 1", 127);
		properties.addIntegerProperty("Schwellwert 2", -1);
		properties.addIntegerProperty("Schwellwert 3", -1);
		properties.addIntegerProperty("Schwellwert 4", -1);
		properties.addIntegerProperty("Schwellwert 5", -1);
		properties.addIntegerProperty("Schwellwert 6", -1);
		properties.addIntegerProperty("Schwellwert 7", -1);
		properties.addIntegerProperty("Schwellwert 8", -1);
		properties.addIntegerProperty("Schwellwert 9", -1);
	}

	@Override
	public Image filter(Image input) {

		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());

		int[] inputSchwellwerte = { properties.getIntegerProperty("Schwellwert 1"), properties.getIntegerProperty("Schwellwert 2"), properties.getIntegerProperty("Schwellwert 3"), properties.getIntegerProperty("Schwellwert 4"), properties.getIntegerProperty("Schwellwert 5"), properties.getIntegerProperty("Schwellwert 6"), properties.getIntegerProperty("Schwellwert 7"), properties.getIntegerProperty("Schwellwert 8"), properties.getIntegerProperty("Schwellwert 9"), 255 };
		List<Integer> schwellwerte = new ArrayList<Integer>();
		for (int i = 0; i <= 9; i++) {
			if (inputSchwellwerte[i] > 255)
				inputSchwellwerte[i] = 255;
			if (inputSchwellwerte[i] > -1 && !schwellwerte.contains(inputSchwellwerte[i]))
				schwellwerte.add(inputSchwellwerte[i]);
		}
		Collections.sort(schwellwerte);
		int anzahlSchwellwerte = schwellwerte.size() - 1;

		for (int col = 0; col < input.getWidth(); col++) {
			for (int row = 0; row < input.getHeight(); row++) {
				int bin = 0;
				while (input.getValue(col, row, GrayscaleImage.GRAYSCALE) > schwellwerte.get(bin)) {
					bin++;
				}
				try {
					output.setValue(col, row, bin * (255 / anzahlSchwellwerte));
				} catch (Exception e) {
					output.setValue(col, row, 0);
				}
			}
		}
		return output;
	}
}