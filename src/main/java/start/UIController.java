package start;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	
	@RequestMapping(value = "/average", method=RequestMethod.POST)
    String getAverage(@RequestBody String numbers) throws IOException{
		System.out.println("Rest received");
		System.out.println(numbers);
		StringWriter strOut = new StringWriter();
		numbers = numbers.replace(" ", "");
		Pattern pattern = Pattern.compile(",");
		List<Double> values = pattern.splitAsStream(numbers)
		                            .map(Double::valueOf)
		                            .collect(Collectors.toList());
    	Map<String, Double> stat = (new MLController()).getAverage(new Numbers(values));
    	
    	Mustache mustache = mFactory.compile(path+"result.mustache");
  	  	mustache.execute(strOut, stat).flush();
  	  	String output = strOut.toString();
  	  	System.out.println(output);
  	  	return output;
    	
    }
}
