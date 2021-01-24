import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.ImageUtils;

/**
 * SobelOperator for Blatt 3.2
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class SobelOperator_LKO_FL_JM extends AbstractFilter {
    
    private static final String A = "Gradientenbetrag", B = "Gradientenrichtung", C = "Gradientenbetrag und Richtung";

    public SobelOperator_LKO_FL_JM(){
        properties.addOptionProperty("Variante", A, A, B, C);
    }

    private double[] Colorbinning(double gradientenrichtung){
        double[] color = new double[3];
        gradientenrichtung = gradientenrichtung <= -22.5 ? gradientenrichtung + 180 : gradientenrichtung;

        if (-22.5 < gradientenrichtung && gradientenrichtung <= 22.5)
            color = new double[]{0,0,1};
        else if (45-22.5 < gradientenrichtung && gradientenrichtung <= 45+22.5)
            color = new double[]{1,0,0};
        else if (90-22.5 < gradientenrichtung && gradientenrichtung <= 90+22.5)
            color = new double[]{1,1,0};
        else if (135-22.5 < gradientenrichtung && gradientenrichtung <= 135+22.5)
            color = new double[]{0,1,0};
        return color;
    }
    
    @Override
	public Image filter(Image input) {
        Image output = ImageFactory.getPrecision(input).rgb(input.getSize());
		
        double[][] sobel_x = {{-1,0,1},{-2,0,2},{-1,0,1}};
        double[][] sobel_y = {{1,2,1},{0,0,0},{-1,-2,-1}};
        
        double sum_x;
        double sum_y;
        double gradientenbetrag;
        double gradientenrichtung;

		for(int col = 0; col < input.getWidth(); col++) {
			for(int row = 0; row < input.getHeight(); row++) {
                sum_x = 0;
                sum_y = 0;
				for(int u = 0; u < 3; u++) {
					for(int v = 0; v < 3; v++) {
						try {						
                            sum_x += input.getValue(col - 1 + u, row - 1 + v, 0) * sobel_x[u][v];
                        } catch(Exception e) {}
                        try {						
							sum_y += input.getValue(col - 1 + u, row - 1 + v, 0) * sobel_y[u][v]; 
						} catch(Exception e) {}
					}
                }

                gradientenbetrag = Math.sqrt(Math.pow(sum_x, 2) + Math.pow(sum_y, 2));
                gradientenrichtung = (sum_x != 0) ? Math.toDegrees(Math.atan(sum_y/sum_x)) : 90.0;
                double[] color;

                switch (properties.getOptionProperty("Variante").toString()) {
                    case A:
                        output.setValue(col, row, gradientenbetrag, gradientenbetrag, gradientenbetrag);
                        break;
                    case B:
                        color = Colorbinning(gradientenrichtung);
                        output.setValue(col, row, color[0]*255, color[1]*255, color[2]*255);
                        break;
                    case C:
                        color = Colorbinning(gradientenrichtung);
                        output.setValue(col, row, color[0]*gradientenbetrag, color[1]*gradientenbetrag, color[2]*gradientenbetrag);
                        break;
                }
			}
        }       
        
        ImageUtils.scaleLinearly(output); 
        return output;
    }
}
