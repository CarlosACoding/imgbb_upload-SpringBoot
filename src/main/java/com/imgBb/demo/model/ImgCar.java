package com.imgBb.demo.model;

import jakarta.persistence.*;

@Entity
public class ImgCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImgCar;

    private String urlImg;

    @ManyToOne
    @JoinColumn(name = "idCar")
    private Car car;

    public ImgCar() {
    }

    public Long getIdImgCar() {
        return idImgCar;
    }

    public void setIdImgCar(Long idImgCar) {
        this.idImgCar = idImgCar;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
