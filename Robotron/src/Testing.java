import java.io.IOException;

import com.greplin.robotron.Browser;
import com.greplin.robotron.Chrome;

public class Testing {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws AWTException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Browser b = new Chrome();
		b.runJs("alert(\"Hello World\");");
	}
}
