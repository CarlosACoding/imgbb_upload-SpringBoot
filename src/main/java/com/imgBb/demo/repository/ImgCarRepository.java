package com.imgBb.demo.repository;

import com.imgBb.demo.model.Car;
import com.imgBb.demo.model.ImgCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImgCarRepository extends JpaRepository<ImgCar, Long> {
    // Puedes agregar métodos personalizados si es necesario
    // Buscar las imágenes de un carro por su ID
    List<ImgCar> findByCar_IdCar(Long idCar);

    // Mét odo para obtener todas las imágenes asociadas a un carro
    List<ImgCar> findByCar(Car car);

}
