package com.imgBb.demo.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCar;

    private String marca;

    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY)
    private List<ImgCar> imgCars;

    public Car() {
    }

    public Long getIdCar() {
        return idCar;
    }

    public void setIdCar(Long idCar) {
        this.idCar = idCar;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


    public List<ImgCar> getImgCars() {
        return imgCars;
    }

    public void setImgCars(List<ImgCar> imgCars) {
        this.imgCars = imgCars;
    }
}
