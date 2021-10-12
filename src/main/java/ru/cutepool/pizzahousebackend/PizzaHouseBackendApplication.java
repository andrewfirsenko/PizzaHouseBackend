package ru.cutepool.pizzahousebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Controller
public class PizzaHouseBackendApplication {

    //Save the uploaded file to this folder
    private static String FILES_FOLDER = "//Users//andrew//PizzaHouseDB//";

    public static void main(String[] args) {

        SpringApplication.run(PizzaHouseBackendApplication.class, args);
    }

    @GetMapping("/")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
//        return String.format("Hello %s!", name);
        return "redirect:addPromoCode";
    }

    // Add PromoCode
    @GetMapping("/addPromoCode")
    public String addCategory() {
        return "add-promo-code";
    }

    @PostMapping("/addPromoCode")
    public String addPromoCode(
            @RequestParam("image") MultipartFile file,
            @RequestParam("status") Boolean status,
            RedirectAttributes redirectAttributes
    ) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatus";
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILES_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error upload file");
            return "redirect:/uploadStatus";
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "upload-status";
    }


    // Download File
    @GetMapping(value = "/file/{fileName}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String fileName) throws IOException {
        File file = new File(FILES_FOLDER + File.separator + fileName);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
