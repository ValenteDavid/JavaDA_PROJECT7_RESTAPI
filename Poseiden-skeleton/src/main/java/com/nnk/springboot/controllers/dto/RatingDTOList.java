package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.nnk.springboot.domain.Rating;

public class RatingDTOList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotEmpty(message = "Moodys Rating is mandatory")
	private String moodysRating;
	private String sandPRating;
	private String fitchRating;
	private Integer orderNumber;

	public RatingDTOList() {
	}

	public RatingDTOList(String moodysRating, String sandPRating, String fitchRating) {
		super();
		this.moodysRating = moodysRating;
		this.sandPRating = sandPRating;
		this.fitchRating = fitchRating;
	}

	public RatingDTOList(String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
		this.moodysRating = moodysRating;
		this.sandPRating = sandPRating;
		this.fitchRating = fitchRating;
		this.orderNumber = orderNumber;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMoodysRating() {
		return moodysRating;
	}

	public void setMoodysRating(String moodysRating) {
		this.moodysRating = moodysRating;
	}

	public String getSandPRating() {
		return sandPRating;
	}

	public void setSandPRating(String sandPRating) {
		this.sandPRating = sandPRating;
	}

	public String getFitchRating() {
		return fitchRating;
	}

	public void setFitchRating(String fitchRating) {
		this.fitchRating = fitchRating;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public static Rating DTOToDomain(Rating rating, RatingDTOList ratingDTOList) {
		rating.setMoodysRating(ratingDTOList.getMoodysRating());
		rating.setSandPRating(ratingDTOList.getSandPRating());
		rating.setFitchRating(ratingDTOList.getFitchRating());
		rating.setOrderNumber(ratingDTOList.getOrderNumber());
		return rating;
	}

	public static RatingDTOList DomainToDTO(Rating rating) {
		return new RatingDTOList(rating.getMoodysRating(), rating.getSandPRating(), rating.getFitchRating(),
				rating.getOrderNumber());
	}

	@Override
	public String toString() {
		return "Rating [id=" + id + ", moodysRating=" + moodysRating + ", sandPRating=" + sandPRating + ", fitchRating="
				+ fitchRating + ", orderNumber=" + orderNumber + "]";
	}

}
