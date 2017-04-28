package start;

/**
 * Created by avik  on 20-04-2017.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.util.ResourceUtils.getFile;

@RestController
public class MLController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greetings greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greetings(counter.incrementAndGet(),
                String.format(template, name));
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
    public List<String> runGMM(
            @RequestParam(value = "fileName") String fileName) {
        if (fileName != null) {
            File file;
            BufferedReader br;
            List<String> list = new ArrayList<>();
            try {
                file = getFile(fileName);
                br = new BufferedReader(new FileReader(file));
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
                for (CSVRecord record: records) {
                    list.add(record.get("data"));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
