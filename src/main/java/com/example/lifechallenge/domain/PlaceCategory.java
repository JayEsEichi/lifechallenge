package com.example.lifechallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PlaceCategory {

    cafe("카페", 1),
    convenience("편의점", 2),
    accommodation("숙박", 3),
    restaurant("음식점", 4),
    mart("마트", 5),
    touristAttraction("관광명소", 6);

    private String category;
    private int categoryNum;

    public static PlaceCategory inputCategory(String category) throws NullPointerException{
        for(PlaceCategory placeCategory : PlaceCategory.values()){
            if(placeCategory.category.equals(category)){
                return placeCategory;
            }
        }
        return null;
    }
}
