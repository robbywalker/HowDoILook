import com.greplin.robotron.Browser;
import com.greplin.robotron.Chrome;

public class Testing {
	public static void main(String[] args) throws Exception {
		Browser b = new Chrome();
		b.start();
		b.goToUrl("http://greplin.com");
		b.runJs("alert(\"Hello World\");");
	}
}
