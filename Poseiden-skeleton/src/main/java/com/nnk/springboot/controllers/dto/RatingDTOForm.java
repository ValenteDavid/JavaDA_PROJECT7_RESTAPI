package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.nnk.springboot.domain.Rating;

public class RatingDTOForm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotEmpty(message = "Moodys Rating is mandatory")
	private String moodysRating;
	private String sandPRating;
	private String fitchRating;
	
	public RatingDTOForm() {
	}
	
	public RatingDTOForm(String moodysRating, String sandPRating, String fitchRating) {
		super();
		this.moodysRating = moodysRating;
		this.sandPRating = sandPRating;
		this.fitchRating = fitchRating;
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
	
	public static Rating DTOToDomain(Rating rating, RatingDTOForm ratingDTOForm) {
		rating.setMoodysRating(ratingDTOForm.getMoodysRating());
		rating.setSandPRating(ratingDTOForm.getSandPRating());
		rating.setFitchRating(ratingDTOForm.getFitchRating());
		return rating;
	}
	
	public static RatingDTOForm DomainToDTO(Rating rating) {
		return new RatingDTOForm(rating.getMoodysRating(),rating.getSandPRating(), rating.getFitchRating());
	}

	@Override
	public String toString() {
		return "RatingDTOForm [id=" + id + ", moodysRating=" + moodysRating + ", sandPRating=" + sandPRating
				+ ", fitchRating=" + fitchRating + "]";
	}
	
}
