import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.ImageUtils;

/**
 * LaplaceOperator for Blatt 3.4
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class LaplaceOperator_LKO_FL_JM extends AbstractFilter {

    public LaplaceOperator_LKO_FL_JM(){
        properties.addOptionProperty("Variante", "L4", "L4", "L8");
    }

    public double[][] getKernel() {
        double[][] kernel = {};
        switch (properties.getOptionProperty("Variante").toString()) {
            case "L4":
                kernel = new double[][]{{0,1,0},{1,-4,1},{0,1,0}};
                break;
            case "L8":
                kernel = new double[][]{{1,1,1},{1,-8,1},{1,1,1}};
                break;
        }
        return kernel;
    }

    @Override
	public Image filter(Image input) {
        Image output = ImageFactory.getPrecision(input).gray(input.getSize());
		
		double[][] filter = getKernel();
		int len = filter[0].length;
		int r = (len - 1) / 2;
		double sum = 0;
	    
		for(int col = 0; col < input.getWidth(); col++) {
			for(int row = 0; row < input.getHeight(); row++) {
				sum = 0;
				for(int u = 0; u < len; u++) {
					for(int v = 0; v < len; v++) {
						try {						
							sum += input.getValue(col - r + u, row - r + v, 0) * filter[u][v]; 
						} catch(Exception e) {}
					}
				}
				output.setValue(col, row, sum);
			}
        }       
        
        ImageUtils.scaleLinearly(output); 
        return output;
    }
}