import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.ImageConverter;

/**
 * Diffusionsfilter for Blatt 4.3
 * 
 * @author Leo Kyster Oerter, Felix Lehmann, Jan Manhillen
 */

@RequireImageType(GrayscaleImage.class)
public class Diffusion_LKO_FL_JM extends AbstractFilter{
    
    public Diffusion_LKO_FL_JM(){
        properties.addDoubleProperty("e0", 1.0);
        properties.addIntegerProperty("Iterations", 500);
    }

    private double norm(double[] vec){
        double sum = 0;
        for (double d : vec) {
            sum += Math.pow(d, 2);
        }
        return Math.sqrt(sum);
    }

    private double[] jxy(double[][] diffusionstensor, double[] du){
        double[] result = new double[2];
        
        result[0] = - diffusionstensor[0][0] * du[0] - diffusionstensor[0][1] * du[1];
        result[1] = - diffusionstensor[1][0] * du[0] - diffusionstensor[1][1] * du[1];
        
        return result;
    }

    @Override
	public Image filter(Image input) {
        Image temp = ImageFactory.doublePrecision().gray(input.getSize());

        for (int i = 0; i < properties.getIntegerProperty("Iterations"); i++) {
            double[][][] flussmap = new double[input.getWidth()][input.getHeight()][2];

        //// A. Intensitätsgradient ////

            for(int x = 0; x < input.getWidth(); x++) {
                for(int y = 0; y < input.getHeight(); y++) {
                    double di_dx = 0;
                    try {
                        di_dx -= input.getValue(x-1, y, 0); 
                    } catch (Exception e) {}
                    try {
                        di_dx += input.getValue(x+1, y, 0); 
                    } catch (Exception e) {}
                    double di_dy = 0;
                    try {
                        di_dy -= input.getValue(x, y-1, 0); 
                    } catch (Exception e) {}
                    try {
                        di_dy += input.getValue(x, y+1, 0); 
                    } catch (Exception e) {}
                
        //// B. Diffusionstensor ////

                    double du[] = {di_dx,di_dy};
                    double lambda = 1;
                    double epsilon = properties.getDoubleProperty("e0") * (Math.pow(lambda, 2)/(Math.pow(norm(du),2)+Math.pow(lambda, 2)));
                    double[][] diffusionstensor = {{epsilon,0},{0,epsilon}};
                    flussmap[x][y] = jxy(diffusionstensor,du);                    
                }
            }
                
        //// C. Flussgradient ////

            for(int x = 0; x < input.getWidth(); x++) {
                for(int y = 0; y < input.getHeight(); y++) {
                    double djx_dx = 0;
                    try {
                        djx_dx -= flussmap[x-1][y][0]; 
                    } catch (Exception e) {}
                    try {
                        djx_dx += flussmap[x+1][y][0];  
                    } catch (Exception e) {}
                    double djy_dy = 0;
                    try {
                        djy_dy -= flussmap[x][y-1][1]; 
                    } catch (Exception e) {}
                    try {
                        djy_dy += flussmap[x][y+1][1]; 
                    } catch (Exception e) {}

        //// D. Divergenz ////
        
                    double div_jxy = djx_dx + djy_dy;

        //// E. Ergebnisintensität ////
        
                    temp.setValue(x, y, input.getValue(x, y, 0) - div_jxy);
                }
            }
            input = temp;
        }
        Image output = ImageConverter.convert(temp, ImageFactory.bytePrecision().gray());
        return output;
    }
}
