package com.imgBb.demo.controller;

import com.imgBb.demo.model.Car;
import com.imgBb.demo.model.ImgCar;
import com.imgBb.demo.repository.CarRepository;
import com.imgBb.demo.repository.ImgCarRepository;
import com.imgBb.demo.service.ImgBBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ImgBBController {

    @Autowired
    private ImgBBService imgBBService;

    @Autowired
    private  CarRepository carRepository;

    @Autowired
    private ImgCarRepository imgCarRepository;

    @GetMapping("/")
    public String showUploadForm() {
        return "upload"; // Vista para cargar la imagen
    }

    // Endpoint para ver las imágenes por idCar
    @GetMapping("/{idCar}/images")
    public String getImagesByCarId(@PathVariable Long idCar, Model model) {
        // Obtener las imágenes asociadas al idCar
        List<ImgCar> imgCars = imgBBService.getImagesByCarId(idCar);

        // Agregar las imágenes al modelo
        model.addAttribute("imgCars", imgCars);
        return "carImages";  // Nombre de la vista (plantilla Thymeleaf)
    }

    // Mostrar las imágenes de un carro
    @GetMapping("/images/{carId}")
    public String showImages(@PathVariable Long carId, Model model) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Carro no encontrado"));

        List<ImgCar> imgCars = imgCarRepository.findByCar(car);

        model.addAttribute("car", car);
        model.addAttribute("imgCars", imgCars);

        return "car_images"; // Nombre de la vista HTML
    }

    @PostMapping("/upload")
    public String uploadImages(@RequestParam("files") List<MultipartFile> files,
                               @RequestParam("marca") String marca, Model model) {
        try {
            // Llamar al servicio para subir las imágenes y guardar el carro
            Car car = imgBBService.uploadImagesAndSaveCar(files, marca);
            model.addAttribute("car", car); // Pasar el carro a la vista
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Error al subir las imágenes");
        }

        return "upload"; // Mostrar la vista con el resultado
    }

    @GetMapping("/cars")
    public String listCars(Model model) {
        model.addAttribute("cars", imgBBService.getAllCars()); // Obtener todos los coches
        return "cars"; // Vista para listar los coches
    }
    // actualizar imagenes por separado, cada una
    @PostMapping("/update-image/{imgCarId}")
    public String updateImage(@PathVariable Long imgCarId, @RequestParam("file") MultipartFile file) {
        ImgCar updatedImgCar = imgBBService.updateImage(imgCarId, file);

        if (updatedImgCar != null) {
            return "redirect:/cars/images/" + updatedImgCar.getCar().getIdCar(); // Redirige a la vista de imágenes del coche
        } else {
            return "error"; // O una página de error si algo salió mal
        }
    }
}
