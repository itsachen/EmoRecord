import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CustomerSecurity extends Library  
{
	CustomerSecurity INSTANCE = (CustomerSecurity)
            Native.loadLibrary("CustomerSecurity",
            		CustomerSecurity.class);
    	
	double emotiv_func(double x);
}