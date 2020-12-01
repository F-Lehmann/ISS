import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import itb2.engine.Controller;
import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.BinaryImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Blatt 5 Aufgabe 3 Connected Component Labeling
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(BinaryImage.class)
public class ConnectedComponentLabeling_LKO_FL_JM extends AbstractFilter {
	
	@Override
	public Image filter(Image input) {
		
		Image helperimage = ImageFactory.bytePrecision().rgb(input.getSize());
		int label = 0;
		int progress = 0;
		double pixel = input.getHeight() * input.getWidth();
		for (int row = 0; row < input.getHeight(); row++) {
			for (int col = 0; col < input.getWidth(); col++) {
				if (input.getValue(col, row, BinaryImage.BINARY) == 0) {
					List<Integer> neighborLabels = new ArrayList<Integer>();
					for (int irow = -1; irow < 2; irow++) {
						for (int icol = -1; icol < 2; icol++) {
							try {
								if (helperimage.getValue(col + icol, row + irow, 1) != 0) {
									neighborLabels.add((int) helperimage.getValue(col + icol, row + irow, 1));
								}
							} catch (Exception e) {
							}
						}
					}
					if (!neighborLabels.isEmpty()) {
						Collections.sort(neighborLabels);
						if (neighborLabels.get(0) != 0) {
							helperimage.setValue(col, row, 1, neighborLabels.get(0));
							// store the equivalence between neighboring labels in a list or an array
							// of label equivalences
							while (neighborLabels.size() > 1) {
								for (int erow = 0; erow < helperimage.getHeight(); erow++) {
									for (int ecol = 0; ecol < helperimage.getWidth(); ecol++) {
										if (helperimage.getValue(ecol, erow, 1) == neighborLabels.get(1)) {
											helperimage.setValue(ecol, erow, 1, neighborLabels.get(0));
										}
									}
								}
								neighborLabels.remove(1);
							}
						}
					} else {
						label++;
						helperimage.setValue(col, row, 1, label);
					}
				}
				progress++;
				Controller.getCommunicationManager().inProgress((progress / pixel));
			}
		}
		Image output = ImageFactory.bytePrecision().rgb(input.getSize());
		for (int row = 0; row < output.getHeight(); row++) {
			for (int col = 0; col < output.getWidth(); col++) {
				output.setValue(col, row, 255, 255, 255);
			}
		}
		for (int labels = 1; labels <= label; labels++) {
			int r = ThreadLocalRandom.current().nextInt(0, 256);
			int g = ThreadLocalRandom.current().nextInt(0, 256);
			int b = ThreadLocalRandom.current().nextInt(0, 256);
			for (int row = 0; row < helperimage.getHeight(); row++) {
				for (int col = 0; col < helperimage.getWidth(); col++) {
					if (helperimage.getValue(col, row, 1) == labels) {
						output.setValue(col, row, r, g, b);
					}
				}
			}
		}
		return output;
	}
}