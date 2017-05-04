package start;

/**
 * Created by avik on 20-04-2017.
 */
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuickML {
    private static JavaSparkContext javaSparkContext;
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("Aegis ML");
        sparkConf.setMaster("local[4]");
        sparkConf.set("spark.cleaner.ttl", "0");
        sparkConf.set("spark.driver.maxResultSize", "0");

        javaSparkContext = new JavaSparkContext(sparkConf);

        SpringApplication.run(QuickML.class, args);
    }

    public static JavaSparkContext getJavaSparkContext() {
        return javaSparkContext;
    }
}
