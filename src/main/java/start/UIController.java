package start;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import pojos.Numbers;

@RestController
public class UIController {
	
	private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    MustacheFactory mFactory;
    String path;
    Greetings gr;
    
    public UIController() {
    	mFactory = new DefaultMustacheFactory();
    	path = "musta\\";
    	path = path.replace("\\", "/");
    	gr = greeting("Avik");
	}

	@GetMapping("/")
    String home() throws IOException{
  	  StringWriter strOut = new StringWriter();
  	  
	  Mustache mustache = mFactory.compile(path+"index.mustache");
	  mustache.execute(strOut, gr).flush();
	  String output = strOut.toString();
	  return output;
    }
	
	@RequestMapping("/greeting")
    public Greetings greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greetings(counter.incrementAndGet(),
                String.format(template, name));
    }
	
	@RequestMapping(value = "/average", method=RequestMethod.POST, consumes = "application/json")
    String getAverage(@RequestBody Numbers numbers) throws IOException{
		StringWriter strOut = new StringWriter();
		
    	Map<String, Double> stat = (new MLController()).getAverage(numbers);
    	
    	Mustache mustache = mFactory.compile(path+"result.mustache");
  	  	mustache.execute(strOut, stat).flush();
  	  	String output = strOut.toString();
  	  	return output;
    	
    }
}
