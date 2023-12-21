package com.imgBb.demo.service;

import com.imgBb.demo.model.Car;
import com.imgBb.demo.model.ImgCar;
import com.imgBb.demo.repository.CarRepository;
import com.imgBb.demo.repository.ImgCarRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class ImgBBService {

    @Value("${imgbb.api.key}")
    private String apiKey;

    private static final String IMG_BB_API_URL = "https://api.imgbb.com/1/upload";

    private final CarRepository carRepository;
    private final ImgCarRepository imgCarRepository;

    public ImgBBService(CarRepository carRepository, ImgCarRepository imgCarRepository) {
        this.carRepository = carRepository;
        this.imgCarRepository = imgCarRepository;
    }

    // Méto do para subir imágenes y guardar el carro con las imágenes
    public Car uploadImagesAndSaveCar(List<MultipartFile> files, String marca) {
        if (files.size() > 4) {
            throw new IllegalArgumentException("No se pueden cargar más de 4 imágenes por carro.");
        }

        try {
            Car car = new Car();
            car.setMarca(marca);
            car = carRepository.save(car); // Guarda la marca del auto en base datos y genera id desde el modelo
            // Subir las imágenes y guardarlas en ImgCar
            for (MultipartFile file : files) {
                // Transformar cada imagen de Binario a Base64 o cadena de texto ASCII
                // Es mejor transportrar los archivos en codigo Base64 que en bytes
                String encodedImage = encodeToBase64(file);
                String imageUrl = uploadToImgBB(encodedImage);

                if (imageUrl != null) {
                    // Limpiar la URL
                    String cleanedUrl = imageUrl.replace("\\/", "/");

                    ImgCar imgCar = new ImgCar();
                    imgCar.setUrlImg(cleanedUrl);
                    imgCar.setCar(car);

                    imgCarRepository.save(imgCar);
                }
            }

            return car;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    // Méto do para codificar la imagen en Base64
    private String encodeToBase64(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
    // Subir imagen al repositorio de imagenes ImgBB y obtener la URL de cada imagen
    private String uploadToImgBB(String encodedImage) { // Imagen o archivo en Codigo Base64 como parametro
        RestTemplate restTemplate = new RestTemplate(); // Objeto encargado de enviar una solicitud post a imgBB
        // Preprar el cuerpo que se enviara en la solicutd http
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("image", encodedImage);
        // Configuracion de encabezados de la solicitud http
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Establece de que tipo de contenido es la solicitud, MULTIPART_FROM_DATA
        // Creacion de la entidad de la solicitud http que contendra el cuerpo y el encabezado
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // Ejecutar la solocitud post. Envia y recibe una respuesta
        ResponseEntity<String> response = restTemplate.exchange(IMG_BB_API_URL, HttpMethod.POST, requestEntity, String.class);
        // Procesar la respuesta recibida de la API imgBB
        String responseBody = response.getBody(); // Informacion en formato json sobre la imagen
        if (responseBody != null && responseBody.contains("url")) { // busca la url de la imgen en el json
            int start = responseBody.indexOf("\"url\":\"") + 7; // "  "url:" "  extraer el numero de posicion donde empieza la url
            int end = responseBody.indexOf("\"", start); // extraer el numero de posicion donde termina la url
            return responseBody.substring(start, end); // estra url http:// imagen.jpg
        }

        return null;
    }
    // Obtener lista de los coches
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
    // Obtener imágenes asociadas a un carro por su ID
    public List<ImgCar> getImagesByCarId(Long idCar) {
        return imgCarRepository.findByCar_IdCar(idCar);
    }

    // Méto do para actualizar la imagen
    public ImgCar updateImage(Long imgCarId, MultipartFile file) {
        try {
            // Buscar la imagen existente en la base de datos
            ImgCar imgCar = imgCarRepository.findById(imgCarId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));

            // Codificar la nueva imagen en base64
            String encodedImage = encodeToBase64(file);

            // Subir la nueva imagen a ImgBB
            String imageUrl = uploadToImgBB(encodedImage);

            // Actualizar la URL de la imagen
            imgCar.setUrlImg(imageUrl);

            // Guardar la imagen actualizada en la base de datos
            imgCarRepository.save(imgCar);

            return imgCar; // Retorna la imagen actualizada
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}


