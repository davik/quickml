package start;

/**
 * Created by avik on 20-04-2017.
 */
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuickML {
    private static JavaSparkContext javaSparkContext;
    private static SparkSession spark;

    public static void main(String[] args) {
        

        
        
        SparkConf sparkConf = new SparkConf().setAppName("Quick ML");

        sparkConf.setMaster("local[4]");
        sparkConf.set("spark.cleaner.ttl", "0");
        sparkConf.set("spark.driver.maxResultSize", "0");
        
        javaSparkContext = new JavaSparkContext(sparkConf);

//        sqlContext = new SQLContext(javaSparkContext);
        SpringApplication.run(QuickML.class, args);

    }

    public static JavaSparkContext getJavaSparkContext() {
        return javaSparkContext;
    }

	public static SparkSession getSpark() {
		return spark;
	}


    
}
