
package srctest; 
 
import java.awt.BorderLayout; 
import java.io.File; 
import java.io.IOException; 
 
import javax.swing.JFrame; 
import javax.swing.UIManager; 
import javax.swing.UnsupportedLookAndFeelException; 
 
import com.rapidminer.RapidMiner; 
import com.rapidminer.gui.look.RapidLookAndFeel; 
import com.rapidminer.gui.processeditor.ResultDisplay; 
import com.rapidminer.operator.IOContainer; 
import com.rapidminer.operator.OperatorException; 
import com.rapidminer.tools.XMLException; 
 
/** 
*  
* @author Ingo Mierswa 
* @version $Id$ 
*/ 
public class Test1 { 

	public static void main(String[] argv) { 
		RapidMiner.init(false,false,false,false); 

		com.rapidminer.Process process = null; 
		try { 
			process = new com.rapidminer.Process(new File("D:\\ws\\QS\\test\\Discretization\\Bin1.xml")); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (XMLException e) { 
			e.printStackTrace(); 
		} 

		if (process != null) { 
			IOContainer result = null; 
			try { 
				result = process.run(); 
			} catch (OperatorException e) { 
				e.printStackTrace(); 
			} 

			if (result != null) { 
				ResultDisplay display = new ResultDisplay(); 

				display.setData(result, "Results"); 
				display.showSomething(); 

				JFrame frame = new JFrame(); 
				frame.getContentPane().add(display, BorderLayout.CENTER); 
				frame.setSize(800, 600); 
				frame.setLocationRelativeTo(null); 
				frame.setVisible(true); 
			} 
		} 
	} 
} 
