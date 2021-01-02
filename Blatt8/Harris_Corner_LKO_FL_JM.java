import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import itb2.filter.AbstractFilter;
import itb2.filter.Filter;
import itb2.filter.RequireImageType;
import itb2.image.Channel;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;


public class Harris_Corner_LKO_FL_JM extends AbstractFilter {
    public Harris_Corner_LKO_FL_JM(){
        properties.addDoubleProperty("kappa", 0.1);
        properties.addDoubleProperty("sigma", 1);
        properties.addDoubleProperty("t", 1E-3);
    }

    private double[] sobel(int x, int y,Image input) {
        int xdim = input.getWidth();
        int ydim = input.getHeight();
		double v00=0,v01=0,v02=0,v10=0,v12=0,v20=0,v21=0,v22=0;

		int x0 = x-1, x1 = x, x2 = x+1;
		int y0 = y-1, y1 = y, y2 = y+1;
		if (x0<0) x0=0;
		if (y0<0) y0=0;
		if (x2>=xdim) x2=xdim-1;
		if (y2>=ydim) y2=ydim-1;
        
		v00=input.getValue(x0, y0,0); v10=input.getValue(x1, y0,0); v20=input.getValue(x2, y0,0);
		v01=input.getValue(x0, y1,0);                    			v21=input.getValue(x2, y1,0);
		v02=input.getValue(x0, y2,0); v12=input.getValue(x1, y2,0); v22=input.getValue(x2, y2,0);

		double sx = ((v20+2*v21+v22)-(v00+2*v01+v02))/(4*255f);
		double sy = ((v02+2*v12+v22)-(v00+2*v10+v20))/(4*255f);
		return new double[] {sx,sy};
    }
    
    private double gauss(double x, double y) {
        double sigma = properties.getDoubleProperty("sigma");
		double sigma2 = sigma*sigma;
		double t = (x*x+y*y)/(2*sigma2);
		double u = 1.0/(2*Math.PI*sigma2);
		double e = u*Math.exp( -t );
		return e;
    }
    
    private double harrisMeasure(int x, int y,double[][] Lx2,double[][] Lxy,double[][] Ly2) {
		// matrix elements (normalized)
		double m00 = Lx2[x][y]; 
		double m01 = Lxy[x][y];
		double m10 = Lxy[x][y];
		double m11 = Ly2[x][y];

		// Harris corner measure = det(M)-k.trace(M)^2
		return m00*m11 - m01*m10 - properties.getDoubleProperty("kappa")*(m00+m11)*(m00+m11);
	}

    private boolean isSpatialMaxima(double[][] hmap, int x, int y) {
		int n=8;
		int[] dx = new int[] {-1,0,1,1,1,0,-1,-1};
		int[] dy = new int[] {-1,-1,-1,0,1,1,1,0};
		double w =  hmap[x][y];
		for(int i=0;i<n;i++) {
			double wk = hmap[x+dx[i]][y+dy[i]];
			if (wk>=w) return false;
		}
		return true;
	}

    @Override
	public Image filter(Image input) {
        int xdim = input.getWidth();
        int ydim = input.getHeight();

        Image output = ImageFactory.bytePrecision().rgb(input.getSize());
        
        for (int y=0; y<ydim; y++)
        for (int x=0; x<xdim; x++)
        output.setValue(x, y, input.getValue(x, y));

        ImageConverter.convert(input, GrayscaleImage.class);
        
        double[][] Lx2,Ly2,Lxy;
        Lx2 = new double[xdim][ydim];
		Ly2 = new double[xdim][ydim];
		Lxy = new double[xdim][ydim];

		// gradient values: Gx,Gy
		double[][][] grad = new double[xdim][ydim][];
		for (int y=0; y<ydim; y++)
			for (int x=0; x<xdim; x++)
				grad[x][y] = sobel(x,y,input);

		// precompute the coefficients of the gaussian filter
		int radius = (int)(2*properties.getDoubleProperty("sigma"));
		int window = 1+2*radius;
		double[][] gaussian = new double[window][window];
		for(int j=-radius;j<=radius;j++)
			for(int i=-radius;i<=radius;i++)
				gaussian[i+radius][j+radius]=(double)gauss(i,j);

		// Convolve gradient with gaussian filter:
		//
		// Ix2 = (F) * (Gx^2)
		// Iy2 = (F) * (Gy^2)
		// Ixy = (F) * (Gx.Gy)
		//
		for (int y=0; y<ydim; y++) {
			for (int x=0; x<xdim; x++) {

				for(int dy=-radius;dy<=radius;dy++) {
					for(int dx=-radius;dx<=radius;dx++) {
						int xk = x + dx;
						int yk = y + dy;
						if (xk<0 || xk>=xdim) continue;
						if (yk<0 || yk>=ydim) continue;

						// gaussian weight
						double gw = gaussian[dx+radius][dy+radius];

						// convolution
						Lx2[x][y]+=gw*grad[xk][yk][0]*grad[xk][yk][0];
						Ly2[x][y]+=gw*grad[xk][yk][1]*grad[xk][yk][1];
						Lxy[x][y]+=gw*grad[xk][yk][0]*grad[xk][yk][1];
					}
				}
			}
		}
        double[][] harrismap = new double[xdim][ydim];

		// for each pixel in the image
		for (int y=0; y<ydim; y++) {
			for (int x=0; x<xdim; x++) {
				// compute the harris measure
				double h =  harrisMeasure(x,y,Lx2,Lxy,Ly2);
                if (h<=0) continue;
				// log scale
				h = 255 * Math.log(1+h) / Math.log(1+255);
				// store
				harrismap[x][y]=(float)h;
			}
		}

        ArrayList<double[]> corners = new ArrayList<double[]>();

        for (int y=1; y<ydim-1; y++) {
			for (int x=1; x<xdim-1; x++) {
				// thresholding : harris measure > epsilon
				double h = harrismap[x][y];
				if (h<=properties.getDoubleProperty("t")) continue;
				// keep only a local maxima
				if (!isSpatialMaxima(harrismap, x, y)) continue;
                // add the corner to the list
                double[] corner = {x,y,h};
				corners.add(corner);
			}
		}

        /*Iterator<double[]> iter = corners.iterator();
		while(iter.hasNext()) {
			double[] p = iter.next();
			for(double[] n:corners) {
				if (n==p) continue;
				int dist = (int)Math.sqrt( (p[0]-n[0])*(p[0]-n[0])+(p[1]-n[1])*(p[1]-n[1]) );
				if(dist>minDistance) continue;
				if (n[2]<p[2]) continue;
				iter.remove();
				break;
			}
        }*/
        
		ArrayList<int[]> keypoints = new ArrayList<int[]>();
		for (double[] p:corners) {
            int[] keypoint = {(int)p[0],(int)p[1]};
			keypoints.add(keypoint);
		}

        for (int[] p:keypoints)
            for (int x=-1;x<=1;x++)
                for (int y=-1;y<=1;y++)
                    output.setValue(p[0]+x, p[1]+y, 0, 255);

        return output;
    }
}
