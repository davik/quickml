package start;

/**
 * Created by avik  on 20-04-2017.
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.mapper.Mapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.clustering.GaussianMixture;
import org.apache.spark.mllib.clustering.GaussianMixtureModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import pojos.Numbers;

import static org.springframework.util.ResourceUtils.getFile;

@RestController
public class MLController {
	
	ObjectMapper mapper;
	
	public MLController() {
		// TODO Auto-generated constructor stub
		mapper = new ObjectMapper();
	}

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileName") String fileName){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + fileName;
            } catch (Exception e) {
                return "You failed to upload " + fileName + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + fileName + " because the file was empty.";
        }
    }

    @RequestMapping(value = "/gmmSpark")
    public Map<String, List<Double>> runGMM(
            @RequestParam(value = "fileName") String fileName) {
        if (fileName != null) {
            File file;
            BufferedReader br;
            HashMap<String, List<Double>> model = new HashMap<>();
            List<String> list = new ArrayList<>();
            try {
                file = getFile(fileName);
                br = new BufferedReader(new FileReader(file));
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
                for (CSVRecord record: records) {
                    list.add(record.get("data"));
                }
                List<Vector> dataPointList = new ArrayList<>();
                double data;

                for(String s: list){
                    data = Double.parseDouble(s);
                    dataPointList.add(Vectors.dense(data).copy());
                }
                JavaRDD<Vector> parsedDataPoints = QuickML.getJavaSparkContext().parallelize(dataPointList);
                GaussianMixtureModel gmmModel = new GaussianMixture().setK(2).setMaxIterations(1000).run(parsedDataPoints);

                List<Double> mu = new ArrayList<>();
                List<Double> sigma = new ArrayList<>();
                for(int i = 0; i< gmmModel.k(); i++){
                    mu.add(gmmModel.gaussians()[i].mu().toArray()[0]);
                    sigma.add(Math.sqrt(gmmModel.gaussians()[i].sigma().toArray()[0]));
                }
                model.put("mu",mu);
                model.put("sigma",sigma);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return model;
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    @RequestMapping(value = "/gmmSparkFrame")
    public Map<String, List<Double>> runGMMFrame(
            @RequestParam(value = "fileName") String fileName) {
        if (fileName != null) {
            File file;
            BufferedReader br;
            HashMap<String, List<Double>> model = new HashMap<>();
            List<String> list = new ArrayList<>();
            try {
                file = getFile(fileName);
                br = new BufferedReader(new FileReader(file));
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
                for (CSVRecord record: records) {
                    list.add(record.get("data"));
                }
                List<Vector> dataPointList = new ArrayList<>();
                double data;

                for(String s: list){
                    data = Double.parseDouble(s);
                    dataPointList.add(Vectors.dense(data).copy());
                }
                JavaRDD<Vector> parsedDataPoints = QuickML.getJavaSparkContext().parallelize(dataPointList);
                GaussianMixtureModel gmmModel = new GaussianMixture().setK(2).setMaxIterations(1000).run(parsedDataPoints);

                List<Double> mu = new ArrayList<>();
                List<Double> sigma = new ArrayList<>();
                for(int i = 0; i< gmmModel.k(); i++){
                    mu.add(gmmModel.gaussians()[i].mu().toArray()[0]);
                    sigma.add(Math.sqrt(gmmModel.gaussians()[i].sigma().toArray()[0]));
                }
                model.put("mu",mu);
                model.put("sigma",sigma);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return model;
        } else {
            return Collections.EMPTY_MAP;
        }
    }
    
    @RequestMapping(value = "/ml/average", method=RequestMethod.POST, consumes = "application/json")
    public Map<String, Double> getAverage(@RequestBody Numbers numbers) {
    	Map<String, Double> stat = new HashMap<String, Double>();
    	
    	List<Double> numbersz =  numbers.getNumbers();
    	Double avg = 0.0;
    	Double sum = 0.0;
    	for(Double number: numbersz) {
    		sum = sum + number;
    	}
    	try {
    	avg = sum / numbersz.size();
    	} catch(ArithmeticException e) {
    		stat.put("Error", 0.0);
    		return stat;
    	}
    	stat.put("average", avg);
    	return stat;
    }
    
   
}
